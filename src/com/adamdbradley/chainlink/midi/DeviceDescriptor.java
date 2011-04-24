package com.adamdbradley.chainlink.midi;

import javax.sound.midi.MidiDevice;

import com.adamdbradley.chainlink.MidiControlledProcessor;

public class DeviceDescriptor {

    private final Class<? extends MidiControlledProcessor> driverClass;
    private final MidiDevice outputMidiDevice;
    private final MidiDevice inputMidiDevice;
    private final Short midiChannel;
    private final Short midiVendorId;
    private final Short midiDeviceId;

    public DeviceDescriptor(Class<? extends MidiControlledProcessor> driverClass,
            MidiDevice outputMidiDevice, MidiDevice inputMidiDevice,
            Short midiChannel, Short midiVendorId, Short midiDeviceId) {
        this.driverClass = driverClass;
        this.outputMidiDevice = outputMidiDevice;
        this.inputMidiDevice = inputMidiDevice;
        this.midiChannel = midiChannel;
        this.midiVendorId = midiVendorId;
        this.midiDeviceId = midiDeviceId;
    }


    /**
     * 
     * @return <code>null</code> if no driver recognizes this device.
     */
    public Class<? extends MidiControlledProcessor> getDriverClass() { return driverClass; }

    /**
     * 
     * @return the {@link MidiDevice} to use to send messages to the Device.
     */
    public MidiDevice getOutputMidiDevice() { return outputMidiDevice; }

    /**
     * 
     * @return the {@link MidiDevice} to use to receive messages from the Device.
     */
    public MidiDevice getInputMidiDevice() { return inputMidiDevice; }

    /**
     * 
     * @return <code>null</code> if the device description didn't specify a particular channel.
     */
    public Short getMidiChannel() { return midiChannel; }

    /**
     * 
     * @return <code>null</code> if the device description didn't specify a vendor Id.
     */
    public Short getMidiVendorId() { return midiVendorId; }

    /**
     * 
     * @return <code>null</code> if the device description didn't speciy a MIDI device Id.
     */
    public Short getMidiDeviceId() { return midiDeviceId; }




    @Override
    public String toString() {
        return ((driverClass==null)?"NoDriver":driverClass.getClass().getSimpleName())
                + "V/D/C=" + this.midiVendorId + "/" + this.midiDeviceId + "/" + this.midiChannel
                + " on I=" + this.inputMidiDevice + "/O=" + this.outputMidiDevice;
    }
}
