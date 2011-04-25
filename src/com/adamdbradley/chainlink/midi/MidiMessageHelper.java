package com.adamdbradley.chainlink.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

public abstract class MidiMessageHelper {

    public static final byte SOX = -0x10; // Start of Sysex  0xF0
    public static final byte EOX = -0x09; // End of Sysex    0xF7

    public static final byte ALL_CHANNELS = 0x7f;

    /**
     * 
     * @param deviceId MIDI device identifier.
     * @return
     */
    public static SysexMessage deviceInquiry(int deviceId) {
        if (deviceId < 0 || deviceId > ALL_CHANNELS) {
            throw new IllegalArgumentException("device identifier must be between 0 and 127 (inclusive)");
        }
        SysexMessage message = new SysexMessage();
        byte[] payload = {
                SOX, // SOX
                0x7e, // non-real-time global inquiry
                (byte) deviceId,
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

    public static byte channelToByte(int channel) {
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

    /**
     * 
     * @param channel (basis-1)
     * @param program (basis-1)
     * @return
     */
    public static MidiMessage programChange(int channel, int program) {
        if (channel < 1 || channel > 16) {
            throw new IllegalArgumentException("Channel must be in [1,16]");
        }
        if (program < 1 || program > 128) {
            throw new IllegalArgumentException("Program must be in [1,128]");
        }
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(ShortMessage.PROGRAM_CHANGE, channel - 1, program - 1, 0xffffffff);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

}
