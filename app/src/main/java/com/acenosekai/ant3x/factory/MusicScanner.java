package com.acenosekai.ant3x.factory;

import com.acenosekai.ant3x.App;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class MusicScanner {

    private OnScan onScan;
    private final List<String> runningThread;
    private final List<String> finishedThread;


    public static interface OnScan {
        void fileAdded(File file);

        void complete();

        void error(Exception e);
    }

    public MusicScanner(OnScan onScan) {
        this.runningThread = Collections.synchronizedList(new ArrayList<>());
        this.finishedThread = Collections.synchronizedList(new ArrayList<>());
        this.onScan = onScan;
    }

    public void scan(File... files) {
        for (File f : files) {
            runDirScanner(f);
        }
    }

    private void runDirScanner(final File dir) {
        if (dir.isDirectory()) {
            new Worker(() -> {
                synchronized (runningThread) {
                    runningThread.add("z");
                }
                try {
                    File[] listFile = dir.listFiles();
                    if (listFile != null) {
                        for (File aListFile : listFile) {
                            if (!excludedDirectory(aListFile.getAbsolutePath())) {
                                if (aListFile.isDirectory()) {
                                    runDirScanner(aListFile);
                                } else {
                                    if (isSupportedFile(aListFile.getName())) {
                                        onScan.fileAdded(aListFile);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    App.i(e.getMessage());
                    onScan.error(e);
                }
                synchronized (finishedThread) {
                    finishedThread.add("z");
                    if (runningThread.size() == finishedThread.size()) onScan.complete();
                }
            }).start();
        }

    }


    private boolean isSupportedFile(String filename) {
        String[] extList = new String[]{".mp3", ".flac", ".m4a"};
        boolean res = false;
        for (String ext : extList) {
            if (filename.endsWith(ext)) {
                res = true;
                break;
            }
        }
        return res;
    }

    private boolean excludedDirectory(String path) {
        if (path.startsWith("/storage/sdcard0/Android")
                || path.startsWith("/storage/sdcard0/.")
                || path.startsWith("/storage/sdcard0/com.")) {
            return true;
        }
        return false;

    }


}
