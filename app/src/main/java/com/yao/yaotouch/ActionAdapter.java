package com.yao.yaotouch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yao on 2016/9/21 0021.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> implements View.OnClickListener {


    private List<Action> list;
    private ActionOnClickListener listener;

    public ActionAdapter(List<Action> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Action bean = list.get(position);
        holder.tvAction.setText(bean.getName());
        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        Action action = list.get(position);
        if (listener != null)
            listener.onClick(action);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAction;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(ActionAdapter.this);
            tvAction = (TextView) itemView.findViewById(R.id.tv_action);
        }
    }

    public void setListener(ActionOnClickListener listener) {
        this.listener = listener;
    }

    public interface ActionOnClickListener {
        void onClick(Action action);
    }
}
