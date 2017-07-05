package com.uhf.r2000demo.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uhf.r2000demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Spinner_Adapter extends BaseAdapter
{
    private String[] arr;
    private Context mContext;

    public Spinner_Adapter(String[] arr, Context mContext)
    {
        super();
        this.arr = arr;
        this.mContext = mContext;
    }

    @Override
    public int getCount()
    {
        return arr.length;
    }

    @Override
    public Object getItem(int position)
    {
        return arr[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder vh;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinnerlayout, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        vh.tvSpinner.setText(arr[position]);

        return convertView;

    }

     static class ViewHolder
    {
        @BindView(R.id.tv_spinner)
        TextView tvSpinner;

        ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}
