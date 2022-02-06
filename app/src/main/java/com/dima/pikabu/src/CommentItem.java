package com.dima.pikabu.src;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.dima.pikabu.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.diskLruImageCache;
import static com.dima.pikabu.src.ActivityComent.comentAdapter;
import static com.dima.pikabu.src.ActivityComent.commentItems;

public class CommentItem implements Html.ImageGetter {
    public int id;
    public int rating;
    public String nick;
    public String date;
    public int answer;

    public String mCommentText;

    public int pLeft=0;

    public HashMap<String,LevelListDrawable> dr=new  HashMap<>();

    public void setView(final ComentsAdapter.PostsViewHolder postViewHolder) {
        if (answer!=0)
        {
            postViewHolder.cvetLayout.setVisibility(View.VISIBLE);
            for(CommentItem c:commentItems)
            {
                if (answer==c.id)
                {
                    pLeft=c.pLeft+5;
                    break;
                }
            }
            int col=0;
            switch(pLeft) {
                case 5:
                    col= Color.BLUE;
                    break;
                case 10:
                    col= Color.GREEN;
                    break;
                case 15:
                    col= Color.YELLOW;
                    break;
                case 20:
                    col= Color.RED;
                    break;
                case 25:
                    col= Color.CYAN;
                    break;
                case 30:
                    col= Color.DKGRAY;
                    break;
                case 35:
                    col= Color.GRAY;
                    break;
                case 40:
                    col= Color.LTGRAY;
                    break;
                case 45:
                    col= Color.MAGENTA;
                    break;
            }
            float scale = activity.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (pLeft*scale + 0.5f);
            postViewHolder.cvetLayout.setBackgroundColor(col);
            postViewHolder.root.setPadding(dpAsPixels,0,0,0);
        }
        else
        {
            postViewHolder.cvetLayout.setVisibility(View.GONE);
            postViewHolder.root.setPadding(0,0,0,0);
        }
        Spanned spanned = Html.fromHtml(mCommentText, this, null);
        postViewHolder.NazvanieText.setText(spanned);
        postViewHolder.NazvanieText.setMovementMethod(LinkMovementMethod.getInstance());
        postViewHolder.TimeText.setText(date);
        postViewHolder.AvtorText.setText(nick);
        postViewHolder.reiteng.setText(String.valueOf(rating));
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable ddr;
        if (!dr.containsKey(source)) {
            ddr = new LevelListDrawable();
            dr.put(source,ddr);

            int nach = source.lastIndexOf("/")+1;
            int con = source.lastIndexOf(".");
            String s = source.substring(nach, con);

            Bitmap bitmap=diskLruImageCache.getBitmap(s);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                ddr.addLevel(1, 1, d);
                ddr.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                ddr.setLevel(1);
            }
            else {
                Drawable empty = activity.getResources().getDrawable(R.drawable.home);
                ddr.addLevel(0, 0, empty);
                ddr.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                new LoadImage().execute(source, ddr);
            }
        }
        else {
            ddr = dr.get(source);
        }
        return ddr;
    }

    private class LoadImage extends AsyncTask<Object, Void, Boolean> {

        private LevelListDrawable mDrawable;
        String source;

        @Override
        protected Boolean doInBackground(Object... params) {
            source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            try {
                InputStream is = new URL(source).openStream();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                byte[] buffer;
                buffer = new byte[8192];
                int len1;
                while ((len1 = is.read(buffer)) != -1) {
                    b.write(buffer, 0, len1);
                }
                buffer=b.toByteArray();
                int nach = source.lastIndexOf("/")+1;
                int con = source.lastIndexOf(".");
                String s = source.substring(nach, con);
                diskLruImageCache.putData(s,buffer);
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                comentAdapter.notifyDataSetChanged();
            }
        }
    }
}
