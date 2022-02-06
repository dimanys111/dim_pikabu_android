package com.dima.pikabu.src;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dima.pikabu.MainActivity.diskLruImageCache;
import static com.dima.pikabu.MainActivity.isWifi;

public class FindImageTask extends AsyncTask<PostItem.Blok, Void, Void> {
    //Фоновая операция

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAXIMUM_POOL_SIZE = 6;
    private static final int KEEP_ALIVE = 10;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<>(1500);

    private static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    public static void nach(PostItem pos) {
        for (final PostItem.Blok blok:pos.Blocks) {
            if(blok.tip != PostItem.Blok.TipBloka.text) {
                nach(blok);
            }
        }
    }

    public static void nach(PostItem.Blok blok) {
        blok.parentPostItem.ok=true;
        blok.ok_images = true;
        FindImageTask parseSite = new FindImageTask();
        parseSite.executeOnExecutor(THREAD_POOL_EXECUTOR,blok);
    }

    protected Void doInBackground(PostItem.Blok... arg)
    {
        PostItem.Blok blok = arg[0];
        if(blok.key==null) {
            int nach = blok.soderjimoe.lastIndexOf("/") + 1;
            int con = blok.soderjimoe.lastIndexOf(".");
            blok.key = blok.soderjimoe.substring(nach, con);
        }
        if (diskLruImageCache.containsKey(blok.key)) {
            Bitmap bitmap = diskLruImageCache.getBitmap(blok.key);
            if (bitmap != null) {
                DownloadImageTask.set_bitmap(blok,bitmap);
            } else {
                DownloadImageTask.nach(blok);
            }
        } else {
            DownloadImageTask.nach(blok);
        }
        if (isWifi) {
            if (blok.tip == PostItem.Blok.TipBloka.gif) {
                DownloadGIFTask.nach(blok);
            }
        }
        return null;
    }
}