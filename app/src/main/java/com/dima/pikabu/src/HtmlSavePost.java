package com.dima.pikabu.src;

import android.os.AsyncTask;
import android.widget.Toast;

import com.dima.pikabu.MainActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.dima.pikabu.src.HtmlGetPosts.phpsesstrans;
import static com.dima.pikabu.src.HtmlGetPosts.string_cookie;

/**
 * Created by dima on 30.03.17.
 */

class HtmlSavePost extends AsyncTask<String, Void, Boolean> {

    private PostItem pi;

    HtmlSavePost(PostItem pi_) {
        pi = pi_;
    }

    private boolean getInet(String id) throws IOException {
        String parammetrs;
        if (pi.savePost)
            parammetrs = "?action=save_story" + URLEncoder.encode("-", "UTF-8") + "&story_id=" + id + "&dataType=json";
        else
            parammetrs = "?action=save_story" + URLEncoder.encode("+", "UTF-8") + "&story_id=" + id + "&dataType=json" + "&cat_id=0" + "&cat_name=" + URLEncoder.encode("Общее", "UTF-8");
        String myURL = "https://m.pikabu.ru/ajax/stories_actions.php" + parammetrs;

        URL url = new URL(myURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("X-Csrf-Token", phpsesstrans);
        conn.setRequestProperty("Cookie", string_cookie);

        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            return true;
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            return getInet(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean b) {
        if (b) {
            if (pi.savePost) {
                Toast toast = Toast.makeText(MainActivity.activity,
                        "Удалили Пост", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(MainActivity.activity,
                        "Сохранили Пост", Toast.LENGTH_SHORT);
                toast.show();
            }
            pi.setColorSave();
        } else {
            Toast toast = Toast.makeText(MainActivity.activity,
                    "Не смог", Toast.LENGTH_SHORT);
            toast.show();
        }
        pi.wait = false;
    }
}