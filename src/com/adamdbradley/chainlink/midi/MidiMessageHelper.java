package com.adamdbradley.chainlink.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

public abstract class MidiMessageHelper {

    public static final byte SOX = -0x10; // Start of Sysex  0xF0
    public static final byte EOX = -0x09; // End of Sysex    0xF7

    public static final byte ALL_CHANNELS = 0x7f;

    /**
     * 
     * @param channel Human enumeration (1-16), or -1 ("all channels")
     * @return
     */
    public static SysexMessage deviceInquiry(int channel) {
        SysexMessage message = new SysexMessage();
        byte[] payload = {
                SOX, // SOX
                0x7e, // non-real-time global inquiry
                (channel == -1) ? ALL_CHANNELS : channelToByte(channel), // channel identifier
                0x06, // general info
                0x01, // identity request
                EOX // EOX
        };
        try {
            message.setMessage(payload, payload.length - 1);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    private static byte channelToByte(int channel) {
        if (channel < 1 || channel > 16) {
            throw new IllegalArgumentException("Channel number must be between 1 and 16, inclusive");
        }
        return ((byte) ((channel - 1) & 0x0f));
    }

    public static String render(MidiMessage message) {
        StringBuilder sb = new StringBuilder("[ ");
        for (byte b:message.getMessage()) {
            sb.append(String.format("%02x ", b));
        }
        return sb.append("]").toString();
    }

}
