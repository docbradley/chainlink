package com.adamdbradley.chainlink.art.sgx.nightbass;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import com.adamdbradley.chainlink.MidiControlledProcessor;
import com.adamdbradley.chainlink.exceptions.CommunicationFailureException;

public class SGXProcessor extends MidiControlledProcessor {

    private ConcurrentLinkedQueue<SysexMessage> messageQueue = new ConcurrentLinkedQueue<SysexMessage>();

    private String name;
    private SGXPatch[] patches;

    /**
     * This constructor tries to talk to the SGX, so it should NEVER be called in a
     * thread that blocks interactive user experience.
     * @param transmitPort
     * @param receivePort
     */
    public SGXProcessor(Receiver transmitPort, Transmitter receivePort) {
        this.setTransmitPort(transmitPort);
        this.setReceivePort(receivePort);
        
        try {
            if (transmitPort != null && receivePort != null) {
                name = getSystemMetadata(); // firmware revision, etc
                patches = getAllPatches(); // list of patch names
            }
        } catch (CommunicationFailureException e) {
            if (name == null) {
                name = "ART SGX Nightbass (disconnected)";
            }
            patches = null;
        }
    }

    private String getSystemMetadata() {
        // TODO: is there a sysex to fetch this?
        return "ART SGX Nightbass (unknown details)";
    }

    @Override
    public SysexMessage dumpAllPatches() throws CommunicationFailureException {
        return requestResponse(new byte[] { (byte) 0xf0, 0x1a, (byte) (getChannel()-1), 0x13, 0x4b, (byte) 0xf7 });
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
            getTransmitPort().send(dumpAllRequest, 0);
            try {
                messageQueue.wait(5000);
            } catch (InterruptedException e) {
                throw new CommunicationFailureException("Timed out waiting for response");
            }
            return messageQueue.poll();
        }
    }

    @Override
    public SGXPatch[] getAllPatches() throws CommunicationFailureException {
        SysexMessage message = dumpAllPatches();
        // TODO: parse it
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
            StringBuilder sb = new StringBuilder("[ ");
            for (byte b:sysex.getData()) {
                sb.append(Integer.toHexString(b)).append(' ');
            }
            System.err.println("Adding MIDI SysEx message to queue: " + sb.toString());
            messageQueue.add(sysex);
            synchronized(messageQueue) {
                messageQueue.notify();
            }
        } else {
            StringBuilder sb = new StringBuilder("[ ");
            for (byte b:message.getMessage()) {
                sb.append(Integer.toHexString(b)).append(' ');
            }
            System.err.println("Ignoring MIDI message: " + sb.toString());
        }
    }

}
