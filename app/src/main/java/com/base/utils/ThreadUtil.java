package com.base.utils;

public class ThreadUtil {

    public static void startThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
