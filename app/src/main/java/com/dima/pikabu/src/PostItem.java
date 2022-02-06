package com.dima.pikabu.src;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dima.pikabu.Fragment_Pika_List;
import com.dima.pikabu.MainActivity;
import com.dima.pikabu.R;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.dima.pikabu.Fragment_Pika_List.boolArhiv;
import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.fpl;
import static com.dima.pikabu.MainActivity.fragmentManager;
import static com.dima.pikabu.MainActivity.isWifi;
import static com.dima.pikabu.MainActivity.manager;
import static com.dima.pikabu.MainActivity.postItemsFavorit;
import static com.dima.pikabu.MainActivity.prerZag;
import static com.dima.pikabu.MainActivity.prerZagAll;
import static com.dima.pikabu.MainActivity.widthDisplay;
import static com.dima.pikabu.src.DownloadImageTask.obj_download_imag;
import static com.dima.pikabu.src.DownloadImageTask.spisZagr;

public class PostItem implements java.io.Serializable{

    public static class Blok implements java.io.Serializable{

        public enum TipBloka {
            text,
            image,
            gif,
            video
        }

        boolean is_dlin=false;
        TextView text =null;
        LinearLayout fl=null;
        FrameLayout flm=null;
        Integer height=0;
        public String soderjimoe = "";
        public TipBloka tip =TipBloka.text;
        public String ssilk = "";
        public ArrayList<DrawView> draws;
        PostItem parentPostItem;
        String key=null;
        volatile int len2=0;
        volatile boolean isDownload=false;
        volatile boolean ok_images =false;

        Blok(PostItem p)
        {
            parentPostItem=p;
        }

        public void setParent(PostItem p)
        {
            parentPostItem=p;
        }

        void setFL()
        {
            draws=new ArrayList<>();
            fl = new LinearLayout(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 2, 0, 2);
            fl.setLayoutParams(params);

            final Blok blok=this;
            lister = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tip==TipBloka.gif) {
                        Intent intent = new Intent(activity, ActivityIMG.class);
                        intent.putExtra("url", ssilk);
                        activity.startActivity(intent);
                    }
                    if (tip==TipBloka.video) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ssilk));
                        activity.startActivity(browserIntent);
                    }
                    if (tip==TipBloka.image) {
                        activity.zoomImageFromThumb(blok);
                    }
                }
            };

            DrawView draw = new DrawView(activity, this);
            draw.setImageBitmap(null);
            draw.setOnClickListener(lister);
            draws.add(draw);
            add_draw_to_fl(draw);
        }

        void clear_fl() {
            if(fl.getParent()!=null){
                ((ViewGroup) fl.getParent()).removeView(fl);
            }
        }

        void setText()
        {
            text = new TextView(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 2, 0, 2);
            text.setLayoutParams(params);
            text.setText(Html.fromHtml(soderjimoe));
            text.setMovementMethod(LinkMovementMethod.getInstance());
            text.setTextColor(Color.parseColor("#e1e1e1"));
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }

        public void clear_text() {
            if(text.getParent()!=null) {
                ((ViewGroup) text.getParent()).removeView(text);
            }
        }

        View.OnClickListener lister=null;

        void setDraw(ArrayList<Bitmap> Images) {
            if(Images!=null && parentPostItem.ok) {
                if(draws.size()<Images.size()) {
                    while (draws.size() < Images.size()) {
                        DrawView draw = new DrawView(activity, this);
                        draw.setOnClickListener(lister);
                        draws.add(draw);
                    }
                    if(flm!=null){
                        flm.removeAllViews();
                    }
                    fl.removeAllViews();
                    for (DrawView draw : draws) {
                        add_draw_to_fl(draw);
                    }
                }
                int n=0;
                for (Bitmap Image : Images) {
                    draws.get(n).setImageBitmap(Image);
                    n++;
                }
            }
        }

        private void add_draw_to_fl(DrawView draw) {
            if (tip == TipBloka.gif || tip == TipBloka.video) {
                if(flm==null){
                    flm=new FrameLayout(activity);
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        height
                );
                params.setMargins(0, 0, 0, 0);
                draw.setLayoutParams(params);

                fl.addView(flm);
                flm.addView(draw);
                TextView t = new TextView(activity);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                if (tip == TipBloka.gif) {
                    t.setText("GIF");
                } else {
                    t.setText("VIDEO");
                }
                t.setTextColor(Color.WHITE);
                t.setBackgroundColor(Color.parseColor("#99000000"));
                flm.addView(t);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        height
                );
                params.setMargins(0, 0, 0, 0);
                draw.setLayoutParams(params);
                fl.addView(draw);
            }
        }

        private void writeObject(java.io.ObjectOutputStream stream)
                throws IOException {
            stream.writeObject(soderjimoe);
            stream.writeObject(tip);
            stream.writeObject(ssilk);
            stream.writeObject(height);
        }

        private void readObject(java.io.ObjectInputStream stream)
                throws IOException, ClassNotFoundException {
            soderjimoe = (String) stream.readObject();
            tip =(TipBloka) stream.readObject();
            ssilk = (String) stream.readObject();
            height = (Integer) stream.readObject();
        }

        public byte[] DownloadFile(String sss) {
            NetworkInfo ni=manager.getActiveNetworkInfo();
            if(ni!=null) {
                isWifi = ni.getType() == ConnectivityManager.TYPE_WIFI;
                try {
                    URL url = new URL(sss);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(20000 /* milliseconds */);
                    conn.setConnectTimeout(35000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    final int file_size = conn.getContentLength();

                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    byte[] buffer = new byte[32768];
                    int len1;
                    isDownload = true;
                    len2 = 0;

                    if (!isWifi) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (parentPostItem.postViewHolder.progressBar != null) {
                                    parentPostItem.postViewHolder.progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }

                    while ((len1 = is.read(buffer)) != -1) {
                        if (!fpl.boolArhiv) {
                            while (prerZag) {
                                Thread.sleep(100);
                            }
                        }
                        if (prerZagAll) {
                            break;
                        }
                        len2 = len2 + len1;
                        b.write(buffer, 0, len1);
                        final int finalLen = len2;
                        if (!isWifi) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (parentPostItem.postViewHolder.progressBar != null) {
                                        parentPostItem.postViewHolder.progressBar.setProgress((int) ((float) finalLen / file_size * 100));
                                    }
                                    fpl.myProgressBar.setProgress((int) ((float) finalLen / file_size * 100));
                                }
                            });
                        }
                    }
                    if (!isWifi) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (parentPostItem.postViewHolder.progressBar != null) {
                                    parentPostItem.postViewHolder.progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                    isDownload = false;
                    len2 = 0;
                    conn.disconnect();
                    buffer = b.toByteArray();
                    return buffer;
                } catch (Exception e) {
                    Log.d("Error....", e.toString());
                }
                isDownload = false;
                len2 = 0;
            }
            else
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity,
                                "Инета то нет!!!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            return null;
        }
    }
    boolean ok=false;
    boolean savePost=false;
    private int plusPost=0;
    public String Zagolovok="";
    private String Time="";
    private String avtor="";
    private String Tag="";
    private String ssilka="";
    private String coment="";
    private String stor="";

    public ArrayList<Blok> Blocks=new ArrayList<>();

    public Integer id=0;
    private boolean isAuthorPost=false;
    private String  authorProfileUrl="";
    private Integer Points=0;

    private boolean Dlin=false;

    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.writeObject(Zagolovok);
        stream.writeObject(Time);
        stream.writeObject(avtor);
        stream.writeObject(Tag);
        stream.writeObject(ssilka);
        stream.writeObject(coment);
        stream.writeObject(stor);
        stream.writeObject(id);
        stream.writeObject(Points);
        stream.writeObject(Dlin);
        stream.writeObject(Blocks.size());
        for (Blok s:Blocks) {
            stream.writeObject(s);
        }
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        Zagolovok = (String) stream.readObject();
        Time = (String) stream.readObject();
        avtor = (String) stream.readObject();
        Tag = (String) stream.readObject();
        ssilka = (String) stream.readObject();
        coment = (String) stream.readObject();
        stor = (String) stream.readObject();
        id=(Integer) stream.readObject();
        Points=(Integer) stream.readObject();
        Dlin=(Boolean) stream.readObject();
        int n=(int) stream.readObject();
        Blocks =new ArrayList<>();
        for (int i=0;i<n;i++)
        {
            Blok blok=(Blok)stream.readObject();
            blok.setParent(this);
            Blocks.add(blok);
            switch(blok.tip) {
                case image:case gif:case video:
                    blok.setFL();
                    break;
                case text:
                    blok.setText();
                    break;
            }
        }
        Setlister();
    }

    private void Setlister() {
        lister1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                (new Handler()).postDelayed(new Runnable() {
//                    public void run() {
//                        postViewHolder.blockLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//                        postViewHolder.imDlin.setVisibility(View.GONE);
//                    }
//                }, 5);
                postViewHolder.blockLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                postViewHolder.imDlin.setVisibility(View.GONE);
            }
        };
        lister2=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.ContextMenu(v);
            }
        };
        lister3 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ActivityComent.class);
                intent.putExtra("url", ssilka);
                activity.startActivity(intent);
            }
        };
        final PostItem postItemThis=this;
        lister4 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!savePost) {
                    boolean p=true;
                    for (PostItem pi:postItemsFavorit)
                    {
                        if(pi.id.equals(postItemThis.id))
                        {
                            p=false;
                            break;
                        }
                    }
                    if(p)
                        postItemsFavorit.add(postItemThis);
                    Toast toast = Toast.makeText(activity,
                            "Добавленна в Избранное", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if(!wait) {
                    wait = true;
                    HtmlSavePost sp = new HtmlSavePost(postItemThis);
                    sp.execute(String.valueOf(id));
                }
            }
        };
        lister5 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fpl.delFavorit(postItemThis);
                Toast toast = Toast.makeText(MainActivity.activity,
                        "Удалена из Избранного", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        lister6 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wait) {
                    if (plusPost < 1) {
                        wait = true;
                        HtmlPlusPost hp = new HtmlPlusPost(postItemThis);
                        hp.execute(String.valueOf(id), String.valueOf(plusPost+1));
                    }
                }
            }
        };
        lister7 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wait) {
                    if (plusPost > -1) {
                        wait = true;
                        HtmlPlusPost hp = new HtmlPlusPost(postItemThis);
                        hp.execute(String.valueOf(id), String.valueOf(plusPost-1));
                    }
                }
            }
        };
        lister8 =new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fpl=new Fragment_Pika_List();
                Bundle args = new Bundle();
                args.putString("Avtor", authorProfileUrl);
                fpl.setArguments(args);

                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rlf, fpl);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        };
    }

    public PostItem() {
        Setlister();
    }

    boolean wait=false;

    void setColorPlus(int zn) {
        plusPost=zn;
        switch(zn) {
            case -1:
                postViewHolder.minPost.setTextColor(Color.RED);
                break;
            case 0:
                postViewHolder.plusPost.setTextColor(Color.parseColor("#cfcfcf"));
                postViewHolder.minPost.setTextColor(Color.parseColor("#cfcfcf"));
                break;
            case 1:
                postViewHolder.plusPost.setTextColor(Color.GREEN);
                break;
            default:
                postViewHolder.plusPost.setTextColor(Color.parseColor("#cfcfcf"));
                postViewHolder.minPost.setTextColor(Color.parseColor("#cfcfcf"));
                break;
        }
    }

    void setColorSave() {
        savePost=!savePost;
        if(savePost)
            postViewHolder.addFaforit.setTextColor(Color.GREEN);
        else
            postViewHolder.addFaforit.setTextColor(Color.parseColor("#cfcfcf"));
    }

    private View.OnClickListener lister1 = null;
    private View.OnClickListener lister2 = null;
    private View.OnClickListener lister3 = null;
    private View.OnClickListener lister4 = null;
    private View.OnClickListener lister5 = null;
    private View.OnClickListener lister6 = null;
    private View.OnClickListener lister7 = null;
    private View.OnClickListener lister8 = null;

    private PostAdapter.PostsViewHolder postViewHolder = null;

    public void setView(final PostAdapter.PostsViewHolder postViewHolder) {
        if(!this.equals(postViewHolder.pi)) {
            this.postViewHolder = postViewHolder;
            postViewHolder.blockLayout.removeAllViews();
            postViewHolder.pi=this;
            postViewHolder.blockLayout.removeOnLayoutChangeListener(postViewHolder.list);
            if(!Dlin) {
                postViewHolder.list = new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (bottom-top > 1500) {
                            if (!Dlin) {
                                Dlin = true;
                                ViewGroup.LayoutParams params = postViewHolder.blockLayout.getLayoutParams();
                                params.height = 600;
                                postViewHolder.blockLayout.setLayoutParams(params);
                                postViewHolder.imDlin.setVisibility(View.VISIBLE);
                                postViewHolder.imDlin.setOnClickListener(lister1);
                                (new Handler()).postDelayed(new Runnable() {
                                    public void run() {
                                        fpl.postAdapter.notifyDataSetChanged();
                                    }
                                }, 5);
                            }
                        }
                    }
                };
                postViewHolder.blockLayout.addOnLayoutChangeListener(postViewHolder.list);
            }
            addblokView();
            postViewHolder.progressBar.setProgress(0);
            if (stor.equals("")) {
                postViewHolder.storia.setVisibility(View.GONE);
            } else {
                postViewHolder.storia.setVisibility(View.VISIBLE);
                postViewHolder.storia.setText(stor);
            }
            postViewHolder.NazvanieText.setText(Zagolovok);
            postViewHolder.TimeText.setText(Time);
            postViewHolder.AvtorText.setText(avtor);
            postViewHolder.AvtorText.setOnClickListener(lister8);
            postViewHolder.ComentText.setText(coment);
            postViewHolder.tagText.setText(Tag);
            postViewHolder.tagText.setOnClickListener(lister2);
            postViewHolder.reiteng.setText(String.valueOf(Points));
            if (savePost)
                postViewHolder.addFaforit.setTextColor(Color.GREEN);
            else
                postViewHolder.addFaforit.setTextColor(Color.parseColor("#cfcfcf"));
            postViewHolder.addFaforit.setOnClickListener(lister4);
            postViewHolder.delFaforit.setOnClickListener(lister5);
            postViewHolder.plusPost.setTextColor(Color.parseColor("#cfcfcf"));
            postViewHolder.minPost.setTextColor(Color.parseColor("#cfcfcf"));
            if (plusPost == 1)
                postViewHolder.plusPost.setTextColor(Color.GREEN);
            else if (plusPost == -1)
                postViewHolder.minPost.setTextColor(Color.RED);
            postViewHolder.plusPost.setOnClickListener(lister6);
            postViewHolder.minPost.setOnClickListener(lister7);
            postViewHolder.ComentText.setOnClickListener(lister3);
            if (!Dlin) {
                postViewHolder.imDlin.setVisibility(View.GONE);
                postViewHolder.blockLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                ViewGroup.LayoutParams params = postViewHolder.blockLayout.getLayoutParams();
                params.height = 600;
                postViewHolder.blockLayout.setLayoutParams(params);
                postViewHolder.imDlin.setVisibility(View.VISIBLE);
                postViewHolder.imDlin.setOnClickListener(lister1);
            }
            if (fpl.boolFavorit) {
                postViewHolder.addFaforit.setVisibility(View.GONE);
                postViewHolder.delFaforit.setVisibility(View.VISIBLE);
            } else {
                if (savePost)
                    postViewHolder.addFaforit.setTextColor(Color.GREEN);
                postViewHolder.addFaforit.setVisibility(View.VISIBLE);
                postViewHolder.delFaforit.setVisibility(View.GONE);
            }
            obn_image();
        }
    }

    private void addblokView() {
        for (Blok blok:Blocks){
            switch(blok.tip) {
                case image:case gif:case video:
                    blok.clear_fl();
                    postViewHolder.blockLayout.addView(blok.fl);
                    break;
                case text:
                    blok.clear_text();
                    postViewHolder.blockLayout.addView(blok.text);
                    break;
            }
        }
    }

    void obn_image(){
        for (Blok blok : Blocks) {
            if (blok.tip != Blok.TipBloka.text) {
                DrawView draw = blok.draws.get(0);
                Rect startBounds = new Rect();
                draw.getGlobalVisibleRect(startBounds);
                int top=startBounds.top;
                if(blok.draws.size()>1){
                    draw = blok.draws.get(blok.draws.size() - 1);
                    draw.getGlobalVisibleRect(startBounds);
                }
                int bottom=startBounds.bottom;
                if(!blok.ok_images) {
                    if ((top != 0 && bottom != 0) &&
                            (top < MainActivity.heightDisplay + 200 && bottom > -200)) {
                        FindImageTask.nach(blok);
                    }
                } else {
                    if ((top != 0 && bottom != 0) && (
                            (top > MainActivity.heightDisplay + 300) ||
                                    (bottom < -300))) {
                        for (DrawView draw1 : blok.draws) {
                            draw1.setImageBitmap(null);
                        }

                        blok.ok_images=false;
                    }
                }
            }
        }
    }

    void obn_first_image(){
        for (Blok blok : Blocks) {
            if (blok.tip != Blok.TipBloka.text) {
                if (!blok.ok_images) {
                    FindImageTask.nach(blok);
                }
                break;
            }
        }
    }

    void remove_image()
    {
        if(ok) {
            ok = false;
            for (Blok blok : Blocks) {
                if (blok.tip != Blok.TipBloka.text) {
                    if(!boolArhiv) {
                        synchronized (obj_download_imag) {
                            spisZagr.remove(blok);
                        }
                    }
                    blok.ok_images = false;
                    for (DrawView draw : blok.draws) {
                        draw.setImageBitmap(null);
                    }
                }
            }
        }
    }

    public boolean fromJSOUP(Element element) {
        Element story__header=element.child(0);
        Element story__title=story__header.child(0);
        Element story__zagolovok=story__title.child(0);
        Zagolovok = story__zagolovok.ownText();
        ssilka = story__zagolovok.attr("href");
        id = Integer.parseInt(element.parent().attr("data-story-id"));
        Element  story__after_header=element.child(1);
        isAuthorPost = story__after_header.select("a.story__tag-author").size()>0;
        Element autor=story__after_header.select("a.user").first();
        avtor = autor.attr("data-name");
        authorProfileUrl = "https://m.pikabu.ru"+autor.attr("href");
        Elements eee=story__after_header.select("time.story__datetime");
        if(eee.isEmpty()){
            return false;
        } else {
            Time=eee.first().ownText();
        }

        Element TagsElement=element.select("div.story__tags").first();
        Elements TagsElements = TagsElement.select("a.tags__tag");
        for (Element tagElement : TagsElements) {
            Tag=Tag+tagElement.ownText()+", ";
        }
        if (Tag.length() > 0) {
            Tag = Tag.substring(0, Tag.length() - 2);
        }

        Element story__footer=element.select("div.story__footer").first();
        Element story__comments=story__footer.select("a.story__comments-link").first();

        coment = story__comments.child(1).ownText();
        coment = "---"+coment+"---";
        String strPr=story__footer.select("span.story__rating-count").first().ownText();
        if(strPr.equals(""))
            Points=0;
        else
            Points=Integer.parseInt(strPr);

        Elements button_tool=story__footer.select("button.tool");
        for (Element bl : button_tool) {
            if(bl.attr("data-role").equals("rating-up")){
                if(bl.attr("class").contains("tool_active")){
                    plusPost=1;
                }
            }
            if(bl.attr("data-role").equals("rating-down")){
                if(bl.attr("class").contains("tool_active")){
                    plusPost=-1;
                }
            }
            if(bl.attr("data-role").equals("save")){
                if(bl.attr("class").contains("tool_active")){
                    savePost=true;
                }
            }
        }

        Element story__content = element.child(2);
        if(story__content.children().size()>1)
        {
            Dlin=true;
        }
        Elements story_block = story__content.select("div.story-block");
        for (Element bl : story_block) {
            PostItem.Blok blok=new PostItem.Blok(this);
            if(bl.className().contains("story-block story-block_type_image")){
                Elements player = bl.select("div.player");
                if (player.size() > 0) {
                    int width = Integer.parseInt(player.attr("data-width"));
                    blok.height = (int) (Integer.parseInt(player.attr("data-height")) * (widthDisplay / width));
                    String s = player.first().attr("data-source");
                    blok.ssilk = s;
                    blok.soderjimoe = s.replace(".gif", ".jpg");
                    blok.tip = Blok.TipBloka.gif;
                } else {
                    Element size = bl.select("svg.story-image__stretch").first();
                    String l = size.attr("viewBox");
                    String[] sl = l.split(" ");
                    int width = Integer.parseInt(sl[2]);
                    blok.height = (int) (Integer.parseInt(sl[3]) * (widthDisplay / width));
                    Element img = bl.child(0).child(1).child(0).child(0);
                    blok.soderjimoe = img.attr("src");
                    blok.ssilk = img.attr("data-large-image");
                    if(blok.soderjimoe.equals("")){
                        blok.soderjimoe=img.attr("data-src");
                    }
                    if(blok.soderjimoe.equals("")){
                        blok.soderjimoe= blok.ssilk;
                    }
                    if(!blok.soderjimoe.equals(blok.ssilk)){
                        blok.soderjimoe= blok.ssilk;
                    }
                    blok.tip = Blok.TipBloka.image;
                }
            }
            if(bl.className().contains("story-block story-block_type_text")){
                String s = bl.html().substring(3);
                s = s.replace("\n", "");
                s = s.replace("<p>", "<br>");
                s = s.replace("</p>", "");
                s = s.replace("<br><br><br><br>", "<br><br>");
                s = s.replace("<br><br><br>", "<br><br>");
                blok.soderjimoe = s;
                blok.tip = Blok.TipBloka.text;
            }
            if(bl.className().contains("story-block story-block_type_video")){
                Element divElem = bl.child(0);
                blok.ssilk = divElem.attr("data-source");
                if (blok.ssilk.contains("pikabu.ru")) {
                    blok.ssilk = blok.ssilk + ".webm";
                }
                float dataratio = 1;
                if (divElem.hasAttr("data-ratio")) {
                    dataratio = Float.parseFloat(divElem.attr("data-ratio"));
                }
                blok.height = (int) (widthDisplay / dataratio);
                Element divElem2 = bl.select("div.player__preview").first();
                String s = divElem2.attr("style");
                s = s.replace("background-image: url(", "");
                s = s.replace(");", "");
                blok.soderjimoe = s;
                blok.tip = Blok.TipBloka.video;
            }
            switch(blok.tip) {
                case image:case gif:case video:
                    blok.setFL();
                    break;
                case text:
                    blok.setText();
                    break;
            }

            Blocks.add(blok);
        }
        return true;
    }
}
