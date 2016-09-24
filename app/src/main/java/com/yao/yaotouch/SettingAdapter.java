package com.yao.yaotouch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.yao.yaotouch.ConfigurationUtil.sponsorActions;

/**
 * Created by Yao on 2016/9/21 0021.
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> implements View.OnClickListener, ActionAdapter.ActionOnClickListener {

    List<SettingBean> list;

    public SettingAdapter(List<SettingBean> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettingBean bean = list.get(position);
        holder.tvKey.setText(bean.getKey().getName());
        holder.itemView.setTag(position);
        if (bean.getValue() != null)
            holder.tvValue.setText(bean.getValue().getName());
        else
            holder.tvValue.setText("无");
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        SettingBean settingBean = list.get(position);
        new ActionDialog(view.getContext(), settingBean, sponsorActions).setListener(this).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKey, tvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvKey = (TextView) itemView.findViewById(R.id.tv_key);
            tvValue = (TextView) itemView.findViewById(R.id.tv_value);
            itemView.setOnClickListener(SettingAdapter.this);
        }
    }

    @Override
    public void onClick(Action action) {
        ConfigurationUtil.saveConfiguration();
        notifyDataSetChanged();
    }
}
