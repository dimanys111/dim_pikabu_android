package com.dima.pikabu.src;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Xml;
import android.widget.AbsListView;
import android.widget.ListView;

import com.dima.pikabu.R;
import com.dima.pikabu.src.api.XmlUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.dima.pikabu.MainActivity.activity;


public class ActivityComent extends AppCompatActivity {

    private ProgressDialog pd;
    private ListView listComent;
    public static ComentsAdapter comentAdapter;
    public static ArrayList<CommentItem> commentItems;

    public CommentItem fromXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        CommentItem item = new CommentItem();
        int id = XmlUtils.readIntAttribute(parser, "id", 0);
        int rating = XmlUtils.readIntAttribute(parser, "rating", 0);
        String nick = XmlUtils.readStringAttribute(parser, "nick");
        int answer = XmlUtils.readIntAttribute(parser, "answer", 0);
        String date=XmlUtils.readStringAttribute(parser, "date");
        String s=XmlUtils.readTagString(parser, "comment");
        item.id=id;
        item.rating=rating;
        item.nick=nick;
        item.date=date;
        item.answer=answer;
        item.mCommentText=s;

        return item;
    }

    ArrayList<CommentItem> parse(String html) {
        ArrayList<CommentItem> list = new ArrayList<>();
        StringReader reader;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            StringReader reader2 = new StringReader(html);
            parser.setInput(reader2);
            parser.nextTag();
            parser.require(2, null, "comments");
            while (parser.next() != 3) {
                if (parser.getEventType() == 2) {
                    if (parser.getName().equals("comment")) {
                        try {
                            CommentItem newComment = fromXml(parser);
                            list.add(newComment);
                        } catch (Exception ignored) {

                        }
                    } else {
                        XmlUtils.skip(parser);
                    }
                }
            }
            reader = reader2;
        } catch (Exception e2) {
            return list;
        }
        if (reader != null) {
            reader.close();
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        commentItems.clear();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_coment);

        listComent = (ListView) findViewById(R.id.listViewComent);
        listComent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        Intent intent = getIntent();
        // Принимаем имя
        String url = intent.getStringExtra("url");

        //Показываем диалог ожидания
        pd = ProgressDialog.show(ActivityComent.this, "Подождете...", "загружаются данные", true, false);
        new DownloadComents().execute(url);

    }

    private class DownloadComents extends AsyncTask<String, Void, Void> {
         @TargetApi(Build.VERSION_CODES.KITKAT)
         protected Void doInBackground(String... arg) {

            String df=arg[0];
            int i=df.lastIndexOf("_");
            String xmlOut="https://pikabu.ru/generate_xml_comm.php?id="+df.substring(i+1,df.length());

             try {
                 xmlOut= HtmlGetPosts.getData(xmlOut);
             } catch (IOException e) {
                 e.printStackTrace();
             }

             try {
                 xmlOut= new String(xmlOut.getBytes("windows-1251"), "UTF-8");
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }

             commentItems=parse(xmlOut);
             return null;
         }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Void v)
        {
            pd.dismiss();
            comentAdapter = new ComentsAdapter(activity, commentItems);
            listComent.setAdapter(comentAdapter);
        }
    }
}
