package com.dima.pikabu.src;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.pikabu.R;

import java.util.ArrayList;

/**
 * Created by Dima on 03.11.2015.
 */
public class PostAdapter extends RecyclerView.Adapter {
    private LayoutInflater lInflater;
    private ArrayList<PostItem> objects;

    class PostsViewHolder extends ViewHolder {
        PostItem pi=null;
        View.OnLayoutChangeListener list=null;
        LinearLayout blockLayout;
        TextView storia;
        TextView NazvanieText;
        TextView TimeText;
        TextView AvtorText;
        TextView ComentText;
        TextView tagText;
        TextView imDlin;
        ProgressBar progressBar;
        TextView reiteng;
        TextView addFaforit;
        TextView delFaforit;
        TextView plusPost;
        TextView minPost;
        View root;
        PostsViewHolder(View itemView) {
            super(itemView);
            root=itemView;
            blockLayout=itemView.findViewById(R.id.blockLayout);
            imDlin=itemView.findViewById(R.id.imageViewDlin);
            storia=itemView.findViewById(R.id.storia);
            NazvanieText=itemView.findViewById(R.id.NazvanieText);
            TimeText=itemView.findViewById(R.id.TimeText);
            AvtorText=itemView.findViewById(R.id.AvtorText);
            ComentText=itemView.findViewById(R.id.ComentText);
            tagText=itemView.findViewById(R.id.tagText);
            progressBar=itemView.findViewById(R.id.progressBarDownImag);
            reiteng=itemView.findViewById(R.id.textReiteng);
            addFaforit=itemView.findViewById(R.id.imageView);
            delFaforit=itemView.findViewById(R.id.imageViewDel);
            plusPost=itemView.findViewById(R.id.plusPost);
            minPost=itemView.findViewById(R.id.minPost);
        }
    }

    public PostAdapter(Context context, ArrayList<PostItem> products) {
        objects = products;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // используем созданные, но не используемые view
        View view = lInflater.inflate(R.layout.itempost, parent, false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        getProduct(position).setView((PostsViewHolder)holder);
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        return true;
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    // товар по позиции
    PostItem getProduct(int position) {
        return (objects.get(position));
    }


}
