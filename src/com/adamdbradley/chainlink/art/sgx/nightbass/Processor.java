package com.adamdbradley.chainlink.art.sgx.nightbass;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import com.adamdbradley.chainlink.MidiControlledProcessor;
import com.adamdbradley.chainlink.Patch;

public class Processor extends MidiControlledProcessor {

    @Override
    public SysexMessage dumpAllPatches() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SysexMessage dumpPatch(short patchNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        // TODO Auto-generated method stub

    }

    @Override
    public Patch[] getAllPatches() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Patch getPatch(Short bank, short patchNumber) {
        // TODO Auto-generated method stub
        return null;
    }

}
