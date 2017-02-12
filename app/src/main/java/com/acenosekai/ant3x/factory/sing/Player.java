package com.acenosekai.ant3x.factory.sing;

import com.acenosekai.ant3x.App;
import com.un4seen.bass.BASSFLAC;
import com.un4seen.bass.BASS;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class Player {
    private int chan = 0;

    public Player(String dir) {
        App.i("new instance");
        BassInit.getInstance();
        load(dir);
    }

    public boolean load(String dir) {
        App.i("Load " + dir);

        try {
            if (dir.toLowerCase().endsWith(".flac")) {
                if ((this.chan = BASSFLAC.BASS_FLAC_StreamCreateFile(dir, 0, 0, BASS.BASS_SAMPLE_FLOAT | BASS.BASS_STREAM_AUTOFREE)) == 0) {
                    // whatever it is, it ain't playable
                    App.i("Can't play flac " + dir);
                    return false;
                }
            } else {
                if ((this.chan = BASS.BASS_StreamCreateFile(dir, 0, 0, BASS.BASS_SAMPLE_FLOAT | BASS.BASS_STREAM_AUTOFREE)) == 0) {
                    // whatever it is, it ain't playable
                    App.i("Can't play " + dir);
                    return false;
                }
            }
        } catch (Exception e) {
            App.i("Can't play zzz " + dir);
            return false;
        }

        return true;
    }

    public void play(long position) {

        App.i("chan active " + this.chan);
        BASS.BASS_ChannelSetAttribute(this.chan, BASS.BASS_ATTRIB_VOL, 1);
        BASS.BASS_ChannelSetPosition(this.chan, position, BASS.BASS_POS_BYTE);
        BASS.BASS_ChannelPlay(this.chan, false);
    }


    public void close() {
        BASS.BASS_StreamFree(this.chan);
        this.chan = 0;
    }

    public int getSecondsPosition() {
        return (int) BASS.BASS_ChannelBytes2Seconds(this.chan, getBytesPosition());
    }

    public void setSecondsPosition(int position) {
        setBytesPosition(BASS.BASS_ChannelSeconds2Bytes(this.chan, position));
    }

    public long getBytesPosition() {
        return BASS.BASS_ChannelGetPosition(this.chan, BASS.BASS_POS_BYTE);
    }

    public void setBytesPosition(long position) {
        BASS.BASS_ChannelSetPosition(this.chan, position, BASS.BASS_POS_BYTE);
    }

    public int getSecondsTotal() {
        return (int) BASS.BASS_ChannelBytes2Seconds(this.chan, getBytesTotal());
    }

    public long getBytesTotal() {
        return BASS.BASS_ChannelGetLength(this.chan, BASS.BASS_POS_BYTE);
    }
}
