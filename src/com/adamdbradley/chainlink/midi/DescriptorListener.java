package com.adamdbradley.chainlink.midi;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

/**
 * Listens on a MIDI in port for responses to the "device inquiry" SysEx message.
 */
public class DescriptorListener implements Receiver {

    private final MidiDevice inputDevice;
    private final String description;
    private final Queue<SysexMessage> messages = new ConcurrentLinkedQueue<SysexMessage>();

    public DescriptorListener(MidiDevice input) throws MidiUnavailableException {
        this.inputDevice = input;
        this.description = input.getDeviceInfo().getName();
        inputDevice.open();
        inputDevice.getTransmitter().setReceiver(this);
    }

    @Override
    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public void close() {
        if (inputDevice.isOpen()) {
            inputDevice.close();
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        // Ignore anything that's not a sysex message
        if (message instanceof SysexMessage) {
            System.err.println("Enqueuing message on " + description + ": " + MidiMessageHelper.render(message));
            messages.add((SysexMessage) message);
        } else if (message.getStatus() == 0xf8) { // MIDI clock event, disregard
            // hard ignore
        } else {
            System.err.println("Ignoring message on " + description + ": " + MidiMessageHelper.render(message));
            // ignore
        }
    }

    public List<DeviceDescriptor> getResponses(MidiDevice outputDevice) {
        List<DeviceDescriptor> result = new ArrayList<DeviceDescriptor>(messages.size());
        SysexMessage message;
        while ((message=messages.poll()) != null) {
            System.err.println("Trying to interpret " + MidiMessageHelper.render(message));
            // TODO: try to recognize it
            new DeviceDescriptor(null,  // TODO
                    outputDevice, inputDevice, 
                    null, // midiChannel 
                    null, // midiVendorId 
                    null // midiDeviceId
                    );
        }
        return result;
    }



}
