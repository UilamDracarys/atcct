package com.scbpfsdgis.atcct.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scbpfsdgis.atcct.R;

/**
 * Created by William on 3/20/2019.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] Title;
    private String[] SubTitle;
    private int[] mnuIcon;
    private int[] rightArrow;

    public CustomAdapter(Context context, String[] menuTitle, String[] menuSubTitle, int[] imageIds, int[] arrows) {
        mContext = context;
        Title = menuTitle;
        SubTitle = menuSubTitle;
        mnuIcon = imageIds;
        rightArrow = arrows;
    }

    public int getCount() {
        return Title.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.main_menu_item, parent, false);
        TextView title, subtitle;
        ImageView i1, ar;
        i1 = row.findViewById(R.id.icon);
        title = row.findViewById(R.id.menuTitle);
        subtitle = row.findViewById(R.id.menuSubTitle);
        ar = row.findViewById(R.id.arrow);
        title.setText(Title[position]);
        i1.setImageResource(mnuIcon[position]);
        ar.setImageResource(rightArrow[position]);
        subtitle.setText(SubTitle[position]);
        return (row);
    }
}

