package com.dima.pikabu;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dima.pikabu.src.DownloadGIFTask;
import com.dima.pikabu.src.DownloadImageTask;
import com.dima.pikabu.src.FindImageTask;
import com.dima.pikabu.src.HtmlGetPosts;
import com.dima.pikabu.src.InfiniteScrollListener;
import com.dima.pikabu.src.MySpinnerAdapter;
import com.dima.pikabu.src.PostAdapter;
import com.dima.pikabu.src.PostItem;
import com.dima.pikabu.src.ProwDownloadImageTask;
import com.felipecsl.android.imaging.DiskLruImageCache;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.os.AsyncTask.SERIAL_EXECUTOR;
import static com.dima.pikabu.MainActivity.HTTP_M_PIKABU_RU;
import static com.dima.pikabu.MainActivity.PREFS_NAME;
import static com.dima.pikabu.MainActivity.VER;
import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.diskLruImageCache;
import static com.dima.pikabu.MainActivity.fileMap;
import static com.dima.pikabu.MainActivity.fileSohr;
import static com.dima.pikabu.MainActivity.fpl;
import static com.dima.pikabu.MainActivity.fragmentManager;
import static com.dima.pikabu.MainActivity.isWifi;
import static com.dima.pikabu.MainActivity.manager;
import static com.dima.pikabu.MainActivity.map;
import static com.dima.pikabu.MainActivity.postItemsFavorit;
import static com.dima.pikabu.MainActivity.prerZag;
import static com.dima.pikabu.MainActivity.prerZagAll;
import static com.dima.pikabu.MainActivity.prerZag_;
import static com.dima.pikabu.MainActivity.saveFavorit;
import static com.dima.pikabu.MainActivity.searchView;


public class Fragment_Pika_List extends Fragment {

    private View V=null;
    public RelativeLayout ProgresLayout;
    public TextView TextProg_Arh;
    public PostAdapter postAdapter;

    private boolean pr=true;
    public MySpinnerAdapter adapterArh;
    public static ProgressBar myProgressBar;
    private int sch=0;
    private String TAG="";
    private boolean tag_bool=false;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private String GOR="hot";
    private InfiniteScrollListener uuu;
    private ArrayList<Integer> ProvPovtA=new ArrayList<>();

    private ArrayList<PostItem> postItems = new ArrayList<>();
    public static volatile boolean boolArhiv,boolFavorit=false;
    private boolean boolAvtor=false;
    private String Avtor="";
    public static LinearLayoutManager llm;
    private List<String> listArh =new ArrayList<>();
    private String[] dataGOR = {"Гор", "Луч", "Све"};
    private boolean bolPodg=false;
    private boolean bol_VnizVverh=true;
    private boolean bol_Klub=false;
    private int scroll=0;
    public static volatile boolean boolLoadArhiv=false;
    private File fileArhiv=null;
    private Menu mOptionsMenu=null;
    private ArrayAdapter<String> adapterGOR;
    private View.OnLongClickListener olcl;
    private View.OnClickListener ocl;

    public synchronized ArrayList<PostItem> getPostItems() {
        return postItems;
    }
    private synchronized void postItems_clear() {
        postItems.clear();
    }
    private synchronized void postItems_addAll(ArrayList<PostItem> postItemsP) {
        postItems.addAll(postItemsP);
    }
    public synchronized int postItems_size() {
        return postItems.size();
    }

    public Fragment_Pika_List() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);

        menu.setGroupVisible(R.id.groupDownload, boolArhiv);
        if (mOptionsMenu!=null) {
            mOptionsMenu=menu;
        }
        CheckBox cb = MenuItemCompat.getActionView(menu.findItem(R.id.menuSort1)).findViewById(R.id.check_klub);
        cb.setChecked(bol_Klub);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bol_Klub=isChecked;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHome:
                llm.scrollToPosition(0);
                return true;
            case R.id.itemDownload:
                if (boolArhiv) {
                    clearPikabu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            TAG = getArguments().getString("TAG");
            Avtor = getArguments().getString("Avtor");
            if(TAG!=null)
                tag_bool=true;
            if(Avtor!=null)
                boolAvtor=true;
        }
    }

    @Override
    public void onPause() {
        sohr();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(V==null) {
            V=inflater.inflate(R.layout.fragment_pika_list, container, false);
            ProgresLayout = V.findViewById(R.id.ProgresLayout);
            TextProg_Arh = V.findViewById(R.id.TextProg_Arh);

            activity.spinnerGOR.setOnItemSelectedListener(itemlistGOR);
            adapterGOR = new ArrayAdapter<>(getActivity(), R.layout.customspinneritem2, dataGOR);
            adapterGOR.setDropDownViewResource(R.layout.customspinneritem2);
            activity.spinnerGOR.setAdapter(adapterGOR);

            activity.spinnerArx.setOnItemSelectedListener(itemlist);
            adapterArh = new MySpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, listArh);
            adapterArh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            activity.spinnerArx.setAdapter(adapterArh);

            RecyclerView rv = V.findViewById(R.id.listView);
            uuu = new InfiniteScrollListener(5, postItems) {
                @Override
                public void loadMore(int page, int totalItemsCount) {
                    if (!boolArhiv)
                        podgruzka();
                }
            };

            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            rv.setItemAnimator(itemAnimator);

            llm = new LinearLayoutManager(getActivity());
            rv.setLayoutManager(llm);
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (dy > 0) {
                            if (!bol_VnizVverh) {
                                activity.fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vn));
                                bol_VnizVverh = true;
                            }
                        } else if (dy < 0) {
                            if (bol_VnizVverh) {
                                activity.fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vv));
                                bol_VnizVverh = false;
                            }
                        }
                    } else {
                        if (dy > 0) {
                            if (!bol_VnizVverh) {
                                activity.fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vn));
                                bol_VnizVverh = true;
                            }
                        } else if (dy < 0) {
                            if (bol_VnizVverh) {
                                activity.fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.vv));
                                bol_VnizVverh = false;
                            }
                        }
                    }
                    int f = llm.findFirstVisibleItemPosition();
                    int l = llm.findLastVisibleItemPosition() - f + 1;
                    if (boolArhiv)
                        scroll = f;
                    uuu.onScroll(f, l);
                }
            });
            postAdapter = new PostAdapter(getActivity(), postItems);
            rv.setAdapter(postAdapter);

            olcl=new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int i = llm.findFirstVisibleItemPosition();
                    if (bol_VnizVverh)
                        if ((i + 40) <= postItems_size() - 1)
                            llm.scrollToPositionWithOffset(i + 40, -1);
                        else
                            llm.scrollToPositionWithOffset(postItems_size() - 1, -1);
                    else {
                        if ((i - 40) >= 1)
                            llm.scrollToPositionWithOffset(i - 40, -1);
                        else
                            llm.scrollToPositionWithOffset(0, -1);
                    }
                    return true;
                }
            };

            ocl=new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = llm.findFirstVisibleItemPosition();
                    if (bol_VnizVverh)
                        llm.scrollToPositionWithOffset(i + 1, -1);
                    else {
                        View v = llm.getChildAt(0);
                        int offsetTop = v.getTop();
                        if (offsetTop < -50)
                            llm.scrollToPositionWithOffset(i, -1);
                        else {
                            if (i >= 1)
                                llm.scrollToPositionWithOffset(i - 1, -1);
                            else
                                llm.scrollToPositionWithOffset(0, -1);
                        }
                    }
                }
            };

            activity.fab.setOnClickListener(ocl);
            activity.fab.setOnLongClickListener(olcl);

            Edit_Button();

            mSwipeRefreshLayout = V.findViewById(R.id.refresh);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    if (direction == SwipyRefreshLayoutDirection.TOP) {
                        if (!boolArhiv)
                            clearPikabu();
                    } else
                        podgruzka();
                }
            });
            // делаем повеселее
            mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN);
            myProgressBar = V.findViewById(R.id.progressbar);
        }
        else
        {
            Edit_Button();
            activity.spinnerGOR.setOnItemSelectedListener(itemlistGOR);
            activity.spinnerArx.setOnItemSelectedListener(itemlist);
            activity.fab.setOnClickListener(ocl);
            activity.fab.setOnLongClickListener(olcl);
        }

        return V;
    }

    public void setKlub(boolean b)
    {
        bol_Klub=b;
    }

    public void setPos(int position)
    {
        if (position!=3) {
            boolArhiv = false;
            mSwipeRefreshLayout.setEnabled(true);
            activity.invalidateOptionsMenu();
            activity.spinnerArx.setVisibility(View.GONE);
        }

        if (position!=4) {
            boolFavorit = false;
            mSwipeRefreshLayout.setEnabled(true);
            activity.invalidateOptionsMenu();
        }

        if (position<3) {
            if (activity.spinnerGOR.getSelectedItemPosition()==position) {
                vibor(position);
            }
            activity.spinnerGOR.setSelection(position);
        }
        else {
            vibor(position);
        }
    }

    private class SohrArh extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            saveArhiv();
            return true;
        }
    }

    private class DeleteFile extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            deleteDirectory(new File(fileSohr));
            return true;
        }
    }

    public boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(true);
    }

    private class ParseSite extends AsyncTask<String, Void, ArrayList<PostItem>> {
        //Фоновая операция
        PostItem fromJSOUP(Element element) {
            PostItem item = new PostItem();
            if(item.fromJSOUP(element)) {
                if (boolArhiv)
                    FindImageTask.nach(item);
                return item;
            }
            else {
                return null;
            }
        }

        protected ArrayList<PostItem> doInBackground(String... arg)
        {
            ArrayList<PostItem> vremPostItems = new ArrayList<>();
            prerZag = true;
            Document document = HtmlGetPosts.getDocument(arg[0]);
            prerZag = false;
            if (document!=null) {
                Elements storiesContainer = document.select("div[class=story__main]");
                if (!storiesContainer.isEmpty()) {
                    for (int i = 0; i < storiesContainer.size(); i++) {
                        try {
                            PostItem p = fromJSOUP(storiesContainer.get(i));
                            if (p != null) {
                                if (!ProvPovtA.contains(p.id)) {
                                    vremPostItems.add(p);
                                    ProvPovtA.add(p.id);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return vremPostItems;
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(ArrayList<PostItem> vremPostItems)
        {
            if(!prerZagAll) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (!vremPostItems.isEmpty()) {
                    postItems_addAll(vremPostItems);
                    postAdapter.notifyDataSetChanged();
                    myHandle.sendEmptyMessage(100);
                    if (boolArhiv) {
                        if (sch < 5) {
                            podgruzka();
                        } else {
                            new SohrArh().execute();
                        }
                    }
                } else {
                    if (boolArhiv) {
                        new SohrArh().execute();
                    }
                    myHandle.sendEmptyMessage(0);
                    sch--;
                }
            }
            prerZag_=false;
        }
    }

    private static Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myProgressBar.setProgress(msg.what);
        }
    };

    public void sohr() {
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("silentMode", VER);
        editor.apply();
        saveFavorit();
        try {
            if(fileArhiv!=null)
                map.put(fileArhiv.getName(),scroll);
            File file = new File(fileMap, "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(map);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return sdf.format(cal.getTime());
    }

    public synchronized void saveArhiv()
    {
        try {
            String s="";
            if(bol_Klub)
            {
                s="_K";
            }
            if(tag_bool)
            {
                s=s+"_"+TAG;
            }
            fileArhiv = new File(fileSohr, now()+ s + "." + GOR);
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileArhiv));
            outputStream.writeObject(postItems_size());
            for (PostItem f : getPostItems())
                outputStream.writeObject(f);
            outputStream.flush();
            outputStream.close();

            pr = false;
            activity.spinnerArx.setVisibility(View.VISIBLE);
            listArh.add(fileArhiv.getName());

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapterArh.notifyDataSetChanged();
                    activity.spinnerArx.setSelection(listArh.size() - 1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Edit_Button() {
        if(tag_bool)
        {
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setQuery(TAG,false);
        }
        else
        {
            searchView.setQuery("",false);
            searchView.setIconified(true);
        }

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                TAG = "";
                tag_bool = false;
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(TAG,false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                fpl=new Fragment_Pika_List();
                Bundle args = new Bundle();
                args.putString("TAG", query);
                fpl.setArguments(args);

                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.rlf, fpl);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void clearPikabu() {
        postsClear();
        podgruzka();
    }

    final Handler handler = new Handler();
    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (DownloadImageTask.spisZagr.size()!=0 || DownloadGIFTask.spisZagr.size()!=0 || prerZag_)
            {
                handler.postDelayed(r, 100);
            }
            else {
                clearPost();
            }
        }
    };
    final Runnable r1 = new Runnable() {
        @Override
        public void run() {
            if (prerZagAll || prerZag_)
            {
                handler.postDelayed(r1, 100);
            }
            else {
                obn();
            }
        }
    };

    public void postsClear() {
        if(!prerZagAll) {
            prerZagAll = true;
            r.run();
        }
    }

    private void clearPost() {
        sch = 0;
        ProvPovtA.clear();
        postItems_clear();
        postAdapter.notifyDataSetChanged();
        prerZagAll = false;
        if (boolLoadArhiv)
            loadArhiv_();
    }

    public void loadArhiv(int position)
    {
        if(fileArhiv!=null)
            map.put(fileArhiv.getName(),scroll);
        if (listArh.size()>0) {
            fileArhiv = new File(fileSohr, listArh.get(position));
            boolLoadArhiv=true;
        }
        else
            fileArhiv = null;
        postsClear();
    }

    private void loadArhiv_() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileArhiv));
            int n = (int) inputStream.readObject();
            ArrayList<PostItem> postItemsP = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                PostItem p = (PostItem) inputStream.readObject();
                if (p != null) {
                    postItemsP.add(p);
                    ProwDownloadImageTask.nach(p);
                }
            }
            postItems_addAll(postItemsP);
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        postAdapter.notifyDataSetChanged();
        Integer i=map.get(fileArhiv.getName());
        if(i!=null) {
            if (i < postItems_size())
                llm.scrollToPosition(i);
            else
                llm.scrollToPosition(0);
        }
        else
            llm.scrollToPosition(0);
        boolLoadArhiv=false;
    }

    private void podgruzka()
    {
        if(!bolPodg) {
            bolPodg=true;
            r1.run();
        }
    }

    private void obn()
    {
        bolPodg = false;
        NetworkInfo ni=manager.getActiveNetworkInfo();
        if(ni!=null) {
            isWifi = ni.getType() == ConnectivityManager.TYPE_WIFI;
            mSwipeRefreshLayout.setRefreshing(true);
            prerZag_ = true;
            sch++;
            if (boolAvtor) {
                new ParseSite().executeOnExecutor(SERIAL_EXECUTOR, Avtor + "?page=" + String.valueOf(sch));
            } else {
                String s = "";
                if (tag_bool) {
                    String[] sl = TAG.split(" ");
                    for (String s1 : sl) {
                        try {
                            s = s + URLEncoder.encode(s1, "UTF-8") + " ";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    s = s.substring(0, s.length() - 1);
                }
                if (bol_Klub) {
                    if (tag_bool) {
                        new ParseSite().executeOnExecutor(SERIAL_EXECUTOR, HTTP_M_PIKABU_RU + "search.php?n=32&t=" + s + "&page=" + String.valueOf(sch));
                    } else
                        new ParseSite().executeOnExecutor(SERIAL_EXECUTOR, HTTP_M_PIKABU_RU + "search.php?n=32&page=" + String.valueOf(sch));
                } else {
                    if (tag_bool) {
                        new ParseSite().executeOnExecutor(SERIAL_EXECUTOR, HTTP_M_PIKABU_RU + "tag/" + s + "/" + GOR + "?page=" + String.valueOf(sch));
                    } else
                        new ParseSite().executeOnExecutor(SERIAL_EXECUTOR, HTTP_M_PIKABU_RU + GOR + "?page=" + String.valueOf(sch));
                }
            }
        }
        else
        {
            Toast toast = Toast.makeText(getActivity(),
                    "Инета то нет!!!", Toast.LENGTH_SHORT);
            toast.show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class MyFileFilter implements FileFilter {
        public boolean accept(File pathname)
        {
            // проверям что это файл и что он заканчивается на .txt
            return pathname.isFile();
        }
    }

    private void arxSet() {
        boolArhiv = true;
        mSwipeRefreshLayout.setEnabled(false);
        activity.invalidateOptionsMenu();
        activity.spinnerArx.setVisibility(View.VISIBLE);
        listArh.clear();
        adapterArh.notifyDataSetChanged();
        File f = new File(fileSohr);
        MyFileFilter filter = new MyFileFilter();
        File[] lis = f.listFiles(filter);

        for (File t : lis) {
            listArh.add(t.getName());
        }

        if (listArh.size()==0)
        {
            loadArhiv(0);
        }
        else {
            adapterArh.notifyDataSetChanged();
            if (activity.spinnerArx.getSelectedItemPosition()== listArh.size() - 1)
            {
                loadArhiv(listArh.size() - 1);
            }
            activity.spinnerArx.setSelection(listArh.size() - 1);
        }
    }

    private void vizFavorit() {
        boolFavorit = true;
        mSwipeRefreshLayout.setEnabled(false);
        postItems.clear();
        postItems.addAll(postItemsFavorit);
        postAdapter.notifyDataSetChanged();
    }

    public void delFavorit(PostItem p) {
        postItemsFavorit.remove(p);
        postItems.clear();
        postItems.addAll(postItemsFavorit);
        postAdapter.notifyDataSetChanged();
    }

    private void vibor(int id) {
        boolean b=false;
        switch(id) {
            case 0:
                GOR = "hot";
                break;
            case 1:
                GOR = "best";
                break;
            case 2:
                GOR = "new";
                break;
            case 3:
                arxSet();
                break;
            case 4: {
                b=true;
                vizFavorit();
                break;
            }
            case 5: {
                b = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Важное сообщение!")
                        .setMessage("КЭШ будет очищен!")
                        .setIcon(R.drawable.x1)
                        .setCancelable(false)
                        .setPositiveButton("ОЧИСТИТЬ",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        new DeleteFile().execute();
                                        activity.navigationView.getMenu().getItem(activity.spinnerGOR.getSelectedItemPosition()).setChecked(true);
                                        diskLruImageCache.clear();
                                        diskLruImageCache=new DiskLruImageCache(getActivity());
                                        adapterArh.clear();
                                        if (!boolArhiv)
                                            clearPikabu();
                                        else
                                            postsClear();
                                    }
                                })
                        .setNegativeButton("ОТМЕНА",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            }
        }
        if (!boolArhiv && !b)
            clearPikabu();
    }

    private AdapterView.OnItemSelectedListener itemlistGOR=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            vibor(position);
            if (!boolArhiv) {
                activity.navigationView.getMenu().getItem(position).setChecked(true);
            }
            else
            {
                arxSet();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private AdapterView.OnItemSelectedListener itemlist=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (pr) {
                loadArhiv(position);
            }
            else
                pr=true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

}
