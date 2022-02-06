package com.dima.pikabu.src;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dima.pikabu.R;

import java.util.ArrayList;

public class ComentsAdapter extends BaseAdapter {
    LayoutInflater lInflater;
    ArrayList<CommentItem> objects;

    public class PostsViewHolder {
        TextView NazvanieText;
        TextView TimeText;
        TextView AvtorText;
        TextView reiteng;
        LinearLayout cvetLayout;
        View root;
        PostsViewHolder(View itemView) {
            root=itemView;
            cvetLayout=(LinearLayout) itemView.findViewById(R.id.linearSmCom);
            NazvanieText=(TextView) itemView.findViewById(R.id.NazvanieTextCom);
            TimeText=(TextView) itemView.findViewById(R.id.TimeTextCom);
            AvtorText=(TextView) itemView.findViewById(R.id.AvtorTextCom);
            reiteng=(TextView)itemView.findViewById(R.id.textReitengCom);
        }
    }

    public ComentsAdapter(Context context, ArrayList<CommentItem> products) {
        objects = products;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        if(objects.isEmpty())
            return null;
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        PostsViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.itemcoment, parent, false);
            holder = new PostsViewHolder(view);
            view.setTag(holder);
        }
        else
        {
            holder = (PostsViewHolder) view.getTag();
        }
        CommentItem ci=getProduct(position);
        if(ci!=null)
            ci.setView(holder);
        return view;
    }

    // товар по позиции
    CommentItem getProduct(int position) {
        return ((CommentItem) getItem(position));
    }
}
