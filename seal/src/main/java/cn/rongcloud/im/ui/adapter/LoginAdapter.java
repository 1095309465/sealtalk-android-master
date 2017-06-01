package cn.rongcloud.im.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.rongcloud.im.R;

/**
 * Created by DELL on 2017/5/5.
 */

public class LoginAdapter extends android.widget.BaseAdapter {
    private List<String> mList;
    private Context mContext;

    public LoginAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.login_list_time, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv1.setText((position + 1)+"");
        holder.tv2.setText(mList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;

        public ViewHolder(View view) {
            this.tv1 = (TextView) view.findViewById(R.id.tv_1);
            this.tv2 = (TextView) view.findViewById(R.id.tv_2);
        }
    }
}
