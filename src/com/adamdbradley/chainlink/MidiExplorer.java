package com.adamdbradley.chainlink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import com.adamdbradley.chainlink.art.sgx.nightbass.SGXProcessor;

public class MidiExplorer {

    final private List<MidiDevice> inputs;
    final private List<MidiDevice> outputs;

    public MidiExplorer() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        
        List<MidiDevice> tmpInputs = new LinkedList<MidiDevice>();
        List<MidiDevice> tmpOutputs = new LinkedList<MidiDevice>();
        
        for (final MidiDevice.Info info:infos) {
            MidiDevice device;
            try {
                device = MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                throw new RuntimeException("MidiSystem vended a MidiDevice.Info which it did not recognize", e);
            }
            
            // A "transmitter" is how the software RECEIVED MIDI messages.
            if (device.getMaxTransmitters() != 0) {
                tmpInputs.add(device);
            }
            // A "receiver" is how the software SENDS MIDI messages.
            if (device.getMaxReceivers() != 0) {
                tmpOutputs.add(device);
            }
        }
        
        inputs = Collections.unmodifiableList(new ArrayList<MidiDevice>(tmpInputs));
        outputs = Collections.unmodifiableList(new ArrayList<MidiDevice>(tmpOutputs));
    }

    /**
     * @param args
     * @throws MidiUnavailableException 
     * @throws InterruptedException 
     */
    public static void main(final String[] args) throws MidiUnavailableException, InterruptedException {
        final MidiExplorer ex = new MidiExplorer();
        
        MidiDevice sgxIn = null;
        MidiDevice sgxOut = null;
        
        for (final MidiDevice input:ex.inputs) {
            final MidiDevice.Info info = input.getDeviceInfo();
            System.out.println("INPUT: " + info.getDescription() + " // " + info.getName() + " // " + info.getVendor() + " // " + info.getVersion() + " //mR=" + input.getMaxReceivers() + " //mT=" + input.getMaxTransmitters());
            if (info.getName().equals("Port 8 on MXPXT")) {
                sgxIn = input;
            }
        }
        
        for (final MidiDevice output:ex.outputs) {
            final MidiDevice.Info info = output.getDeviceInfo();
            System.out.println("OUTPUT: " + info.getDescription() + " // " + info.getName() + " // " + info.getVendor() + " // " + info.getVersion() + " //mR=" + output.getMaxReceivers() + " //mT=" + output.getMaxTransmitters());
            if (info.getName().equals("Port 8 on MXPXT")) {
                sgxOut = output;
            }
        }
        
        
        if (sgxIn != null && sgxOut != null) {
            SGXProcessor sgx = new SGXProcessor(sgxOut, sgxIn);
        } else {
            System.err.println("Not found");
        }
        
        Thread.sleep(30000L);
        
        System.exit(0);
    }

}
