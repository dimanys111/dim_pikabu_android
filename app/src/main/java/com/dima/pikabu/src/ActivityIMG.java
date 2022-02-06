package com.dima.pikabu.src;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.dima.pikabu.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.diskLruImageCache;


public class ActivityIMG extends AppCompatActivity {

    private class MoveWork extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            myProgressBarCrug.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... arg)
        {
            final String ssilk=arg[0];
            int nach = ssilk.lastIndexOf("/")+1;
            int con = ssilk.lastIndexOf(".");
            String s = ssilk.substring(nach, con);
            if(ssilk.contains("gif"))
                s=s+"gif";
            if (diskLruImageCache.containsKey(s))
            {
                File f=new File(diskLruImageCache.getCacheFolder(),s+".0");
                String html="file:"+f.getAbsolutePath();
                Uri uri = Uri.parse(html);
                web.setGifImageUri(uri);
                return html;
            }
            else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new ParseSite().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ssilk);
                    }
                });
            }
            return "";
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(String html)
        {
            if (!html.equals("")) {
                myProgressBarCrug.setVisibility(View.GONE);
            }
        }
    }

    private class ParseSite extends AsyncTask<String, Void, String> {
        //Фоновая операция
        byte[] DownloadFile(String sss) {
            try {
                URL url = new URL(sss);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                final int file_size = urlConnection.getContentLength();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000 /* milliseconds */);
                conn.setConnectTimeout(35000 /* milliseconds */);

                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                byte[] buffer;
                buffer = new byte[8192];
                int len1;
                while ((len1 = is.read(buffer)) != -1) {
                    b.write(buffer, 0, len1);
                    myHandle.sendEmptyMessage((int) ((float) b.size() / file_size * 100));
                }
                conn.disconnect();
                buffer=b.toByteArray();
                return buffer;
            } catch (Exception e) {
                Log.d("Error....", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            myProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... arg)
        {
            int nach = arg[0].lastIndexOf("/")+1;
            int con = arg[0].lastIndexOf(".");
            String s = arg[0].substring(nach, con);
            if(arg[0].contains("gif"))
                s=s+"gif";
            byte[] b=DownloadFile(arg[0]);
            if (b != null) {
                diskLruImageCache.putData(s, b);
                File f = new File(diskLruImageCache.getCacheFolder(), s + ".0");
                String html=f.getAbsolutePath();
                Uri uri = Uri.parse("file:"+html);
                web.setGifImageUri(uri);
                return html;
            }
            return "";
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(String html)
        {
            if (!html.equals("")) {
                myProgressBar.setVisibility(View.GONE);
                myProgressBarCrug.setVisibility(View.GONE);
            }
        }
    }

    boolean b=false;
    private ProgressBar myProgressBar;
    private ProgressBar myProgressBarCrug;

    Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            myProgressBar.setProgress(msg.what);
        }
    };

    private GifImageView web=null;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        b=true;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_img);

        web = (GifImageView) findViewById(R.id.webViewImage);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myProgressBarCrug = (ProgressBar) findViewById(R.id.progressBar2);
        Intent intent = getIntent();

        String ssilk = intent.getStringExtra("url");
        new MoveWork().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ssilk);
    }
}
