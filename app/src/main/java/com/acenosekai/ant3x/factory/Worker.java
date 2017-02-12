package com.acenosekai.ant3x.factory;

import com.acenosekai.ant3x.App;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class Worker extends Thread {
    private Runnable runnable;

    public Worker(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        super.run();
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
            App.i(e.getMessage());
        }
    }
}
