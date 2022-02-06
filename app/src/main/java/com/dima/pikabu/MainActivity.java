package com.dima.pikabu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dima.pikabu.src.MySpinner;
import com.dima.pikabu.src.PostItem;
import com.felipecsl.android.imaging.DiskLruImageCache;
import com.imagezoom.ImageViewTouch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.felipecsl.android.imaging.Utils.getDiskCacheDir;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String HTTP_M_PIKABU_RU = "https://m.pikabu.ru/";
    public static MainActivity activity=null;

    public static volatile boolean prerZag_=false;
    public static volatile boolean prerZag=false;
    public static volatile boolean prerZagAll=false;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String fileSohr;
    public static String fileMap;
    public static File fileFavorit=null;
    public static ArrayList<PostItem> postItemsFavorit = new ArrayList<>();
    public static volatile int siz=0;
    public static volatile boolean isWifi;
    public static DiskLruImageCache diskLruImageCache=null;
    public static ConnectivityManager manager;
    public static HashMap<String, Integer> map = new HashMap<>();
    public static String Login="";
    public static float widthDisplay = (float) 720.0;
    public static float heightDisplay = (float) 720.0;
    public static FragmentManager fragmentManager;

    public static Fragment_Pika_List fpl;

    public static SearchView searchView;
    public MySpinner spinnerArx;
    public Spinner spinnerGOR;
    public FloatingActionButton fab;
    private long back_pressed;
    public NavigationView navigationView;
    final List<MenuItem> items=new ArrayList<>();
    public Animator mCurrentAnimator;
    public int mShortAnimationDuration;
    public RelativeLayout rr;
    public TextView vhod_login;

    public static final int VER=47;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void zoomImageFromThumb(PostItem.Blok blok) {
        final Rect startBounds = new Rect();
        blok.draws.get(0).getGlobalVisibleRect(startBounds);
        String ssilk=blok.ssilk;
        int nach = ssilk.lastIndexOf("/") + 1;
        int con = ssilk.lastIndexOf(".");
        String key = ssilk.substring(nach, con);
        Bitmap bm = diskLruImageCache.getBitmap(key);
        if(bm==null){
            byte[] b = blok.DownloadFile(ssilk);
            bm = BitmapFactory.decodeByteArray(b, 0, b.length);
            if (bm != null) {
                diskLruImageCache.putData(key, b);
            }
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        final ImageViewTouch expandedImageView = new ImageViewTouch(activity);
        //expandedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        expandedImageView.setImageBitmap(bm);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        expandedImageView.setLayoutParams(params);
        rr.removeAllViews();
        rr.addView(expandedImageView);

        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        findViewById(R.id.cont).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        rr.setVisibility(View.VISIBLE);
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                        startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rr.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        rr.setVisibility(View.INVISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        activity = this;
        setDisp();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fab = findViewById(R.id.fab);
        spinnerGOR=findViewById(R.id.spinnerGOR);
        spinnerArx=findViewById(R.id.spinnerArhiv);
        searchView=findViewById(R.id.searchView);

        fpl=new Fragment_Pika_List();
        fragmentTransaction.add(R.id.rlf, fpl);
        fragmentTransaction.commit();

        File filemap = getDiskCacheDir(this, "map");
        fileMap=filemap.getAbsolutePath();
        boolean b=filemap.exists();
        if (!b) {
            filemap.mkdirs();
        }
        File fileSohNach = getDiskCacheDir(this, "data");
        fileSohr=fileSohNach.getAbsolutePath();
        b=fileSohNach.exists();
        if (!b) {
            fileSohNach.mkdirs();
        }

        try {
            File file = new File(fileMap, "map");
            ObjectInputStream outputStream = new ObjectInputStream(new FileInputStream(file));
            map=(HashMap<String,Integer>)outputStream.readObject();
            outputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        int Versia = settings.getInt("silentMode", 0);
//        PHPSESS = settings.getString("PHPSESS", "");
//        phpDug2 = settings.getString("phpDug2", "");
        Login = settings.getString("Login", "");

        manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        diskLruImageCache=new DiskLruImageCache(this);
        fileFavorit = new File(filemap, "favorit");
        loadFavorit();


        rr=findViewById(R.id.cont);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        Toolbar mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mtoolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        for(int i = 0; i< menu.size(); i++){
            items.add(menu.getItem(i));
        }

        View headerLayout = navigationView.getHeaderView(0);
        vhod_login=headerLayout.findViewById(R.id.vhod_login);
        if(!Login.equals(""))
        {
            vhod_login.setText(Login);
        }
    }

    private void setDisp() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthDisplay = size.x;
        heightDisplay = size.y;
    }

    public void Check(View v) {
        CheckBox cb = (CheckBox)v;
        fpl.setKlub(cb.isChecked());
    }

    public void Vhod_Click(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (rr.getVisibility()==View.VISIBLE) {
                rr.removeAllViews();
                rr.setVisibility(View.INVISIBLE);
            }
            else
            {
                int n=fragmentManager.getBackStackEntryCount();
                if(n>0)
                {
                    super.onBackPressed();
                }
                else {
                    if(!searchView.isIconified())
                    {
                        searchView.setIconified(true);
                        searchView.setIconified(true);
                    }
                    else {
                        if (back_pressed + 2000 > System.currentTimeMillis()) {
                            super.onBackPressed();
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода!",
                                    Toast.LENGTH_SHORT).show();
                            back_pressed = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int position=items.indexOf(item);

        fpl.setPos(position);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        String s=((TextView)v).getText().toString();
        String[] parts = s.split(", ");
        for (String t : parts) {
            menu.add(0, 1, 0, t);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String s=item.toString();

        fpl=new Fragment_Pika_List();
        Bundle args = new Bundle();
        args.putString("TAG", s);
        fpl.setArguments(args);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.rlf, fpl);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        return super.onContextItemSelected(item);
    }

    public void ContextMenu(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
        unregisterForContextMenu(v);
    }

    private void loadFavorit() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileFavorit));
            int n = (int) inputStream.readObject();
            ArrayList<PostItem> postItemsP = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                PostItem p = (PostItem) inputStream.readObject();
                if (p != null) {
                    postItemsP.add(p);
                    for (PostItem.Blok blok : p.Blocks) {
                        blok.setParent(p);
                    }
                }
            }
            postItemsFavorit.addAll(postItemsP);
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveFavorit()
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileFavorit));
            outputStream.writeObject(postItemsFavorit.size());
            for (PostItem f : postItemsFavorit)
                outputStream.writeObject(f);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setDisp();
    }

}
