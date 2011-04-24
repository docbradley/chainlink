package com.adamdbradley.chainlink;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

public 
abstract 
class MidiControlledProcessor 
implements ControllableProcessor, Receiver {

    private short midiChannel;
    private Receiver transmitPort;
    private Transmitter receivePort;

    /**
     * MIDI channel number (human readable enumeration): 1-16
     * @return
     */
    public short getChannel() { return midiChannel; }
    public void setChannel(final short channel) { this.midiChannel = channel; }

    /**
     * This is the device with which we transmit controls to the processor.
     * @return
     */
    public Receiver getTransmitPort() { return transmitPort; }
    public void setTransmitPort(final Receiver receiver) { this.transmitPort = receiver; }

    /**
     * This is the device with which we receive messages from the processor.
     * Messages are delivered via {@link #send(MidiMessage, long)}.
     * @return
     */
    public Transmitter getReceivePort() { return receivePort; }
    public void setReceivePort(final Transmitter receivePort) {
        if (this.receivePort != null 
                && this.receivePort.getReceiver() == this) {
            this.receivePort.setReceiver(null);
        }
        this.receivePort = receivePort;
        this.receivePort.setReceiver(this);
    }

    /**
     * Called as part of shutdown (??? maybe ???)
     * @see Receiver#close()
     */
    @Override
    public void close() {
        // will this ever happen?
        
    }

    /**
     * This method gets invoked by the MIDI in port when a message arrives.
     * @see Receiver#send(MidiMessage, long)
     */
    @Override
    public abstract void send(MidiMessage message, long timeStamp);

    public abstract SysexMessage dumpPatch(short patchNumber);
    public abstract SysexMessage dumpAllPatches();






}
