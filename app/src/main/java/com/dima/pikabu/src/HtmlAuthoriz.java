package com.dima.pikabu.src;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.dima.pikabu.src.HtmlGetPosts.phpsesstrans;
import static com.dima.pikabu.src.HtmlGetPosts.phpDug2;

/**
 * Created by dima on 30.03.17.
 */

public class HtmlAuthoriz {

    public static String getData(String login, String pasword) throws IOException {

        String myURL = "https://m.pikabu.ru/ajax/auth.php";
        String parammetrs = "username="+login+"&password="+pasword+"&mode=login";
        byte[] data;

        URL url = new URL(myURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setRequestProperty("Origin", "https://m.pikabu.ru");
        conn.setRequestProperty("Cookie", "PHPSESS="+phpsesstrans+"; phpDug2="+phpDug2);
        OutputStream os = conn.getOutputStream();
        data = parammetrs.getBytes("UTF-8");
        os.write(data);

        conn.connect();
        int responseCode= conn.getResponseCode();

        if (responseCode == 200) {
            int g=0;
            Map<String, List<String>> map = conn.getHeaderFields();
            List<String> sl=map.get("Set-Cookie");
            for (String el : sl) {
                int n=el.indexOf("PHPSESS");
                if(n>=0)
                {
                    login=el.split("=")[1].split(";")[0];
                    g++;
                }
                else
                {
                    n=el.indexOf("phpDug2");
                    if(n>=0)
                    {
                        pasword=el.split("=")[1].split(";")[0];
                        g++;
                    }
                }
            }

            {
                return login+"="+pasword;
            }
        }
        return null;
    }
}