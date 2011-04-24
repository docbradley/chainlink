package com.adamdbradley.chainlink.art.sgx.nightbass;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import com.adamdbradley.chainlink.MidiControlledProcessor;
import com.adamdbradley.chainlink.exceptions.CommunicationFailureException;
import com.adamdbradley.chainlink.midi.MidiMessageHelper;

public class SGXProcessor extends MidiControlledProcessor {

    private ConcurrentLinkedQueue<SysexMessage> messageQueue = new ConcurrentLinkedQueue<SysexMessage>();

    private String name;
    private SGXPatch[] patches;

    private MidiDevice sgxOut;
    private MidiDevice sgxIn;

    /**
     * This constructor tries to talk to the SGX, so it should NEVER be called in a
     * thread that blocks interactive user experience.
     * @param sgxOut
     * @param sgxIn
     * @throws MidiUnavailableException 
     */
    public SGXProcessor(MidiDevice sgxOut, MidiDevice sgxIn) throws MidiUnavailableException {
        
        super.setChannel((short) 1);
        
        // TODO: handle MidiUnavailableException
        
        this.sgxOut = sgxOut;
        this.sgxIn = sgxIn;
        
        sgxOut.open();
        sgxIn.open();
        
        final Receiver receiver = sgxOut.getReceiver();
        this.setTransmitPort(receiver);
        
        final Transmitter transmitter = sgxIn.getTransmitter();
        this.setReceivePort(transmitter);
        
        
        
        for (byte b:new byte[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0x7f}) {
            try {
                requestResponse(deviceInquiry(b));
            } catch (CommunicationFailureException e) {
                System.err.println("Done waiting for " + b);
            }
        }
        
        try {
            if (sgxOut != null && sgxIn != null) {
                System.err.println("Trying to get SGX metadata");
                name = getSystemMetadata(); // firmware revision, etc
                System.err.println("Trying to get SGX patches");
                patches = getAllPatches(); // list of patch names
            } else {
                System.err.println("Not enough ports defined");
            }
        } catch (CommunicationFailureException e) {
            if (name == null) {
                name = "ART SGX Nightbass (disconnected)";
            }
            patches = null;
            System.err.println("Failed to initialize " + e);
        }
    }

    @Override
    public void finalize() {
        sgxOut.close();
        sgxIn.close();
    }

    private String getSystemMetadata() {
        // TODO: is there a sysex to fetch this?
        return "ART SGX Nightbass (unknown details)";
    }

    protected byte[] deviceInquiry(byte channel) {
        return new byte[] {
                -0x10, // SOX
                0x7e, // non-real-time global inquiry
                channel, // "all channels" 
                0x06, // general info
                0x01, // identity request
                -0x09 // EOX
        };
    }

    protected final byte[] dumpAllPatches = {
            -0x10, // SysEx start status
            0x1a, // ART manufacturer id
            0x00, // channel number, basis-0
            0x13, // product id code
            0x4b, 
            -0x09 // SysEx end status 
    };

    @Override
    public SysexMessage dumpAllPatches() throws CommunicationFailureException {
        return requestResponse(dumpAllPatches);
     }


    @Override
    public SysexMessage dumpPatch(short patchNumber) {
        throw new RuntimeException("not handled yet");
    }

    private SysexMessage requestResponse(byte[] command) throws CommunicationFailureException {
        
        synchronized(messageQueue) {
            while (!messageQueue.isEmpty()) {
                System.err.println("Discarding message " + messageQueue.poll());
            }
            SysexMessage dumpAllRequest = new SysexMessage();
            try {
                dumpAllRequest.setMessage(command, command.length);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
            System.err.println("Sending " + MidiMessageHelper.render(dumpAllRequest) + " to " + getTransmitPort());
            getTransmitPort().send(dumpAllRequest, -1);
            try {
                messageQueue.wait(5000);
            } catch (InterruptedException e) {
                throw new CommunicationFailureException("Timed out waiting for response");
            }
            
            final SysexMessage message = messageQueue.poll();
            if (message == null) {
                throw new CommunicationFailureException("timeout");
            } else {
                return message;
            }
        }
    }

    @Override
    public SGXPatch[] getAllPatches() throws CommunicationFailureException {
        SysexMessage message = dumpAllPatches();
        // TODO: parse it
        
        System.err.println(message);
        
        throw new RuntimeException();
    }

    @Override
    public String getDescription() {
        return "ART SGX Nightbass (Firmware version ?.?)";
    }

    @Override
    public SGXPatch getPatch(Short bank, short patchNumber) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * Called when a MIDI message arrives on the appropriate port.
     * Drops the message onto {@link #messageQueue} and calls {@link #notify()} on it.
     */
    @Override
    public void send(final MidiMessage message, final long timeStamp) {
        if (message instanceof SysexMessage) {
            final SysexMessage sysex = (SysexMessage) message;
            System.err.println("Adding MIDI SysEx message(" + sysex.getMessage().length + ") to queue: " + MidiMessageHelper.render(sysex));
            messageQueue.add(sysex);
            synchronized(messageQueue) {
                messageQueue.notify();
            }
        } else {
            System.err.println("Ignoring MIDI message: " + MidiMessageHelper.render(message));
        }
    }

}
