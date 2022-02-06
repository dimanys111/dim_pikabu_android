package com.dima.pikabu;

import android.app.Application;
import android.content.res.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static com.felipecsl.android.imaging.Utils.getDiskCacheDir;

public class MyApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });
        //LeakCanary.install(this);
    }

    private void handleUncaughtException (Thread thread, Throwable e)
    {
        File f=getDiskCacheDir(this,"log");
        boolean b=f.exists();
        if (!b) {
            f.mkdirs();
        }
        File fileLog = new File(f, "log.log");
        String s=fileLog.getAbsolutePath();
        String s1=e.getMessage()+e.getLocalizedMessage();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(s, true)));
            out.println(s1);
            out.close();
        } catch (IOException v) {
            //exception handling left as an exercise for the reader
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}