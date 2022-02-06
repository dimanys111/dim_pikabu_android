package com.dima.pikabu.src;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.diskLruImageCache;
import static com.dima.pikabu.MainActivity.fpl;
import static com.dima.pikabu.MainActivity.isWifi;
import static com.dima.pikabu.MainActivity.prerZagAll;

/**
 * Created by Dima on 07.11.2015.
 */
public class DownloadImageTask extends AsyncTask<PostItem.Blok, Void, PostItem.Blok> {
    //Фоновая операция
    public static ArrayList<PostItem.Blok> spisZagr = new ArrayList<>();
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final int KEEP_ALIVE = 1;

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


    static final Object obj_download_imag = new Object();

    static void nach(PostItem.Blok blok) {
        synchronized (obj_download_imag) {
            spisZagr.remove(blok);
            spisZagr.add(0, blok);
            if(isWifi){
                new DownloadImageTask().executeOnExecutor(THREAD_POOL_EXECUTOR,blok);
            }
            if (spisZagr.size() == 1) {
                new DownloadImageTask().execute(blok);
            }
        }
        if (!DownloadImageTask.spisZagr.isEmpty() || !DownloadGIFTask.spisZagr.isEmpty()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fpl.ProgresLayout.setVisibility(View.VISIBLE);
                    fpl.TextProg_Arh.setText("З=" + DownloadImageTask.spisZagr.size() +
                            "+" + DownloadGIFTask.spisZagr.size());
                }
            });
        }
    }

    protected PostItem.Blok doInBackground(PostItem.Blok... arg) {
        PostItem.Blok blok = arg[0];
        if (prerZagAll) {
            return blok;
        }
        byte[] b = blok.DownloadFile(blok.soderjimoe);
        if(b!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            if (bitmap != null) {
                set_bitmap(blok, bitmap);
                diskLruImageCache.putData(blok.key, b);
            } else {
                blok.ok_images = false;
            }
        } else {
            blok.ok_images = false;
        }
        return blok;
    }

    static void set_bitmap(final PostItem.Blok blok, Bitmap bitmap) {
        int g = bitmap.getHeight();
        int n = 0;
        final ArrayList<Bitmap> abm = new ArrayList<>();
        if (g > 2000) {
            blok.is_dlin=true;
            while (g > 0) {
                int l = 2000;
                if (g < l)
                    l = g;
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 2000 * n, bitmap.getWidth(), l);
                abm.add(bitmap1);
                n++;
                g -= l;
            }
        } else {
            abm.add(bitmap);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                blok.setDraw(abm);
            }
        });
    }

    //Событие по окончанию парсинга
    protected void onPostExecute(PostItem.Blok blok) {
        synchronized (obj_download_imag) {
            spisZagr.remove(blok);
            post();
        }
        if (!DownloadImageTask.spisZagr.isEmpty() || !DownloadGIFTask.spisZagr.isEmpty()) {
            fpl.ProgresLayout.setVisibility(View.VISIBLE);
            fpl.TextProg_Arh.setText("З=" + DownloadImageTask.spisZagr.size() +
                    "+" + DownloadGIFTask.spisZagr.size());
        } else
            fpl.ProgresLayout.setVisibility(View.GONE);
    }

    private void post() {
        if (prerZagAll) {
            spisZagr.clear();
            return;
        }
        if (!isWifi && !spisZagr.isEmpty()) {
            PostItem.Blok blok = spisZagr.get(0);
            if (blok != null) {
                new DownloadImageTask().execute(blok);
            }
        }
    }
}
