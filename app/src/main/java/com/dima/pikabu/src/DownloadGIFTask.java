package com.dima.pikabu.src;

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
import static com.dima.pikabu.MainActivity.prerZagAll;
import static com.dima.pikabu.MainActivity.siz;
import static com.dima.pikabu.src.DownloadImageTask.obj_download_imag;

/**
 * Created by dima on 27.12.16.
 */

public class DownloadGIFTask extends AsyncTask<PostItem.Blok, Void, PostItem.Blok> {
    public static ArrayList<PostItem.Blok> spisZagr = new ArrayList<>();
    private static final int CORE_POOL_SIZE = 3;
    private static final int MAXIMUM_POOL_SIZE = 6;
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

    static void nach(PostItem.Blok blok) {
        synchronized (obj_download_imag) {
            if(!spisZagr.contains(blok)) {
                spisZagr.add(blok);
                DownloadGIFTask parseSite = new com.dima.pikabu.src.DownloadGIFTask();
                parseSite.executeOnExecutor(THREAD_POOL_EXECUTOR, blok);
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

    protected PostItem.Blok doInBackground(PostItem.Blok... arg)
    {
        final PostItem.Blok blok = arg[0];
        if (prerZagAll) {
            return blok;
        }
        siz=0;
        for (PostItem post: fpl.getPostItems()){
            for (final PostItem.Blok bl:post.Blocks){
                if(bl.isDownload)
                    siz= siz+bl.len2;
            }
        }
        while (siz>15000000)
        {
            if (prerZagAll) {
                return blok;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            siz=0;
            for (PostItem post: fpl.getPostItems()) {
                for (final PostItem.Blok bl : post.Blocks) {
                    if (bl.isDownload)
                        siz = siz + bl.len2;
                }
            }
        }
        int nach = blok.ssilk.lastIndexOf("/") + 1;
        int con = blok.ssilk.lastIndexOf(".");
        String s = blok.ssilk.substring(nach, con) + "gif";
        if (!diskLruImageCache.containsKey(s)) {
            byte[] b = blok.DownloadFile(blok.ssilk);
            if (b != null) {
                diskLruImageCache.putData(s, b);
            }
        }
        return blok;
    }

    //Событие по окончанию парсинга
    protected void onPostExecute(PostItem.Blok blok)
    {
        synchronized (obj_download_imag) {
            spisZagr.remove(blok);
        }
        if (!DownloadImageTask.spisZagr.isEmpty() || !DownloadGIFTask.spisZagr.isEmpty()) {
            fpl.ProgresLayout.setVisibility(View.VISIBLE);
            fpl.TextProg_Arh.setText("З=" + DownloadImageTask.spisZagr.size() +
                    "+" + DownloadGIFTask.spisZagr.size() +
                    "+" + siz);
        }
        else
            fpl.ProgresLayout.setVisibility(View.GONE);
    }
}
