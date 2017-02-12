package com.acenosekai.ant3x.factory.sing;

import com.acenosekai.ant3x.App;
import com.un4seen.bass.BASS;

import java.io.File;


/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */



public class BassInit {

    private static final int SAMPLE44 = 44100;
    private static final int SAMPLE48 = 48000;
    private static final int SAMPLE96 = 96000;
    private static final int SAMPLE192 = 192000;
    private static BassInit instance;

    private BassInit() {

    }

    public static void destroy() {
        BASS.BASS_Free();
        instance = null;
    }

    public static void reset() {
        instance = null;
    }


    public static void configure(int minBuffer) {
        BASS.BASS_SetConfig(BASS.BASS_CONFIG_FLOATDSP, 1);
        BASS.BASS_SetConfig(BASS.BASS_CONFIG_DEV_BUFFER, minBuffer);
        BASS.BASS_SetConfig(BASS.BASS_CONFIG_SRC, 6);
        BASS.BASS_SetConfig(BASS.BASS_CONFIG_SRC_SAMPLE, 6);
    }

    public static synchronized BassInit getInstance() {
        if (instance == null) {
            BASS.BASS_Free();
            instance = new BassInit();
            App.i("init with sample " + SAMPLE192 + "Hz");
            if (!BASS.BASS_Init(-1, SAMPLE192, BASS.BASS_DEVICE_FREQ)) {
                App.i("Can't initialize device");
                App.i("init with sample " + SAMPLE96 + "Hz");
                if (!BASS.BASS_Init(-1, SAMPLE96, BASS.BASS_DEVICE_FREQ)) {
                    App.i("Can't initialize device");
                    App.i("init with sample " + SAMPLE48 + "Hz");
                    if (!BASS.BASS_Init(-1, SAMPLE48, BASS.BASS_DEVICE_FREQ)) {
                        App.i("Can't initialize device");
                        App.i("init with sample " + SAMPLE44 + "Hz");
                        if (!BASS.BASS_Init(-1, SAMPLE44, BASS.BASS_DEVICE_FREQ)) {
                            App.i("Can't initialize device");
                        }
                    }
                }
            }

            BASS.BASS_INFO info = new BASS.BASS_INFO();
            if (BASS.BASS_GetInfo(info)) {
                App.i("Min Buffer :" + info.minbuf);
                App.i( "Direct Sound Ver :" + info.dsver);
                App.i( "Latency :" + info.latency);
                App.i( "speakers :" + info.speakers);
                App.i( "freq :" + info.freq);
            }
            configure(info.minbuf);
            String[] list = new File(App.getInstance().getApplicationInfo().nativeLibraryDir).list();
            for (String s : list) {
                BASS.BASS_PluginLoad(App.getInstance().getApplicationInfo().nativeLibraryDir + "/" + s, 0);
            }
        }

        return instance;
    }
}

