package com.dima.pikabu.src;

import com.dima.pikabu.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.dima.pikabu.MainActivity.activity;

public class HtmlGetPosts {

    public static String phpsesstrans="uhtun98149gg0rrsfia2diq74ppp628a";
    public static String phpDug2="a%3A4%3A%7Bs%3A3%3A%22uid%22%3Bi%3A737745%3Bs%3A8%3A%22username%22%3Bs%3A10%3A%22dimanys111%22%3Bs%3A3%3A%22rem%22%3Bs%3A32%3A%22f26ef567f062b756e9f65efb99fa79f9%22%3Bs%3A5%3A%22tries%22%3Bi%3A0%3B%7D";

    public static String string_cookie="PHPSESS="+phpsesstrans+"; " +
            "phpDug2="+phpDug2;


    static String getData(String myurl) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(myurl).openConnection();
        c.setReadTimeout(5000 /* milliseconds */);
        c.setConnectTimeout(5000);
        c.setRequestProperty("Cookie", string_cookie);
        c.connect();
        int responseCode= c.getResponseCode();
        if (responseCode == 200) {
            int file_size = c.getContentLength();
            InputStream is = c.getInputStream();
            ByteArrayOutputStream os=new ByteArrayOutputStream();
            copyStream(is, os,file_size);
            c.disconnect();
            return os.toString("windows-1251");
        }
        c.disconnect();
        return null;
    }

    private static void copyStream(InputStream input, OutputStream output, final int file_size)
            throws IOException
    {
        byte[] buffer = new byte[16384]; // Adjust if you want
        int bytesRead;
        int len=0;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
            len=len+bytesRead;
            final int finalLen = len;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.fpl.myProgressBar.setProgress((int) ((float) finalLen / file_size * 60)+10);
                }
            });
        }
    }

    public static Document getDocument(String url) {
        String html=null;
        try {
            html = getData(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (html != null) {
            return Jsoup.parse(html);
        }
        return null;
    }
}