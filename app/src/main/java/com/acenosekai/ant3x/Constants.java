package com.acenosekai.ant3x;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class Constants {
    public interface ACTION {
        public static String MAIN = "com.acenosekai.ant3x.action.main";
        public static String START = "com.acenosekai.ant3x.action.start";
        public static String STOP = "com.acenosekai.ant3x.action.stop";
        public static String PLAY = "com.acenosekai.ant3x.action.play";
        public static String PAUSE = "com.acenosekai.ant3x.action.pause";
        public static String NEXT = "com.acenosekai.ant3x.action.next";
        public static String PREV = "com.acenosekai.ant3x.action.prev";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
