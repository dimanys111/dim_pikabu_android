package com.dima.pikabu.src;

import android.os.AsyncTask;
import android.widget.Toast;

import com.dima.pikabu.MainActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.dima.pikabu.src.HtmlGetPosts.string_cookie;

/**
 * Created by dima on 30.03.17.
 */

class HtmlPlusPost extends AsyncTask<String, Void, Boolean> {

    private PostItem pi;
    private int vote;

    HtmlPlusPost(PostItem pi_){
        pi=pi_;
    }

    private boolean getInet(String id, String vote) throws IOException {

        String myURL = "https://pikabu.ru/ajax/vote_story.php";
        String parammetrs = "story_id="+id+"&vote="+vote;
        byte[] data;

        URL url = new URL(myURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setRequestProperty("Origin", "http://pikabu.ru");
        conn.setRequestProperty("Cookie", string_cookie);
        OutputStream os = conn.getOutputStream();
        data = parammetrs.getBytes("UTF-8");
        os.write(data);

        conn.connect();
        int responseCode= conn.getResponseCode();

        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            this.vote=Integer.parseInt(params[1]);
            return getInet(params[0],params[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean b)
    {
        if(b)
        {
            if(vote==1) {
                Toast toast = Toast.makeText(MainActivity.activity,
                        "Плюс Посту", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                if(vote==0) {
                    Toast toast = Toast.makeText(MainActivity.activity,
                            "Ноль Посту", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    if (vote == -1) {
                        Toast toast = Toast.makeText(MainActivity.activity,
                                "Минус Посту", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
            pi.setColorPlus(vote);
        }
        else
        {
            Toast toast = Toast.makeText(MainActivity.activity,
                    "Не смог", Toast.LENGTH_SHORT);
            toast.show();
        }
        pi.wait=false;
    }
}