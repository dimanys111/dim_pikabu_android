package com.dima.pikabu.src;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dima.pikabu.R;

import java.io.File;
import java.util.List;

import static com.dima.pikabu.MainActivity.activity;
import static com.dima.pikabu.MainActivity.fpl;

public class MySpinnerAdapter extends ArrayAdapter<String> {

    // CUSTOM SPINNER ADAPTER
    private Context mContext;
    private List<String> obj;
    public MySpinnerAdapter(Context context, int textViewResourceId,
                            List<String> objects) {
        super(context, textViewResourceId, objects);
        obj=objects;
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater =
                ( LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customspinneritem, null);
            holder = new ViewHolder();
            holder.txt01 = (TextView) convertView.findViewById(R.id.TextView01);
            holder.but = (ImageButton) convertView.findViewById(R.id.imageDelArh);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt01.setText(obj.get(position));

        holder.txt01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.spinnerArx.setSelection(position);
                activity.spinnerArx.onDetachedFromWindow();
            }
        });

        holder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File fileArhiv = new File(activity.fileSohr, obj.get(position));
                obj.remove(position);
                fileArhiv.delete();
                fpl.adapterArh.notifyDataSetChanged();
                activity.spinnerArx.onDetachedFromWindow();
                if(obj.isEmpty())
                {
                    fpl.postsClear();
                }
            }
        });

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater =
                ( LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customspinneritem1, null);
            holder = new ViewHolder();
            holder.txt01 = (TextView) convertView.findViewById(R.id.TextView01);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt01.setText(obj.get(position));

        return convertView;

    }

    class ViewHolder {
        TextView txt01;
        ImageButton but;
    }



} // end custom adapter