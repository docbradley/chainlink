package com.adamdbradley.chainlink.midi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

public class DeviceDiscovery {

    /**
     * 
     * @return a list of descriptors of recognizable controllable devices connected to the MIDI subsystem.
     *   Descriptors are not de-duped across multiple calls.
     * @throws InterruptedException 
     */
    public static List<DeviceDescriptor> discover() throws InterruptedException {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        
        List<MidiDevice> inputs = new LinkedList<MidiDevice>();
        List<MidiDevice> outputs = new LinkedList<MidiDevice>();
        
        for (final MidiDevice.Info info:infos) {
            MidiDevice device;
            try {
                device = MidiSystem.getMidiDevice(info);
            } catch (MidiUnavailableException e) {
                throw new RuntimeException("MidiSystem vended a MidiDevice.Info which it did not recognize", e);
            }
            
            // A "transmitter" is how the software RECEIVED MIDI messages.
            if (device.getMaxTransmitters() != 0) {
                inputs.add(device);
            }
            // A "receiver" is how the software SENDS MIDI messages.
            if (device.getMaxReceivers() != 0) {
                outputs.add(device);
            }
        }
        
        final List<DescriptorListener> listeners = new ArrayList<DescriptorListener>(inputs.size());
        for (final MidiDevice input:inputs) {
            try {
                listeners.add(new DescriptorListener(input));
            } catch (MidiUnavailableException e) {
                System.err.println("Couldn't open input " + input + ", skipping");
            }
        }
        
        final List<DeviceDescriptor> descriptors = new LinkedList<DeviceDescriptor>();
        
        boolean done = false;
        
        for (final MidiDevice output:outputs) {
            if (false) {
                if (output.getDeviceInfo().getName().equals("Port 8 on MXPXT")) {
                    try {
                        output.open();
                        MidiMessage pc = MidiMessageHelper.programChange(16, 3);
                        System.err.println("Sending " + MidiMessageHelper.render(pc));
                        output.getReceiver().send(pc, -1);
                        done = true;
                    } catch (MidiUnavailableException e) {
                        System.err.println("FAIL " + e);
                        throw new RuntimeException(e);
                    } finally {
                        output.close();
                    }
                    
                    
                } else {
                    continue;
                }
            }
            
            
            
            if (!output.getDeviceInfo().getName().contains("MXPXT") || output.getDeviceInfo().getName().contains("Sync")) {
                continue;
            }
            
            
            
            
            if (true) {
                final Receiver send;
                try {
                    output.open();
                    send = output.getReceiver();
                } catch (MidiUnavailableException e) {
                    if (output.isOpen()) {
                        output.close();
                    }
                    System.err.println("Couldn't open output " + output + ", skipping");
                    continue;
                }
                    
                System.err.println("Spamming discovery messages on " + output.getDeviceInfo().getName());
                
                for (int deviceId=0 ; deviceId<128 ; deviceId++) {
                    MidiMessage inquiryRequest = MidiMessageHelper.deviceInquiry(deviceId);
                    send.send(inquiryRequest, -1);
                    Thread.sleep(10);
                }
                
                send.close();
                output.close();
                
                System.err.println("Waiting for responses to DI messages on " + output.getDeviceInfo().getName());
                Thread.sleep(1000);
                
                for (DescriptorListener listener:listeners) {
                    descriptors.addAll(listener.getResponses(output));
                }
            }
        }
        
        for (DescriptorListener listener:listeners) {
            listener.close();
            Thread.sleep(100); // Can throw NullPointerException !?!?
        }
        
        
        if (!done) {
            throw new RuntimeException("Didn't find interface");
        }
        
        return Collections.unmodifiableList(descriptors);
    }








    public static void main(String[] argv) throws Exception {
        List<DeviceDescriptor> descriptors = DeviceDiscovery.discover();
        for (DeviceDescriptor descriptor:descriptors) {
            System.out.println("Found " + descriptor);
        }
    }

}
