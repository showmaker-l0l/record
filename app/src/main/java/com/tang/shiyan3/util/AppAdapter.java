package com.tang.shiyan3.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tang.shiyan3.AppInfoActivity;
import com.tang.shiyan3.R;
import com.tang.shiyan3.db.AppInfo;
import com.tang.shiyan3.db.ApplicationState;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<ApplicationState> mAppList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView appName;
        TextView firstTime;
        TextView lastTime;
        TextView totalTime;
        TextView location;
        TextView foreCount;
        TextView backCount;
        TextView actionCount;
        View stateView;

        public ViewHolder(View view){
            super(view);
            stateView = view;
            appName = view.findViewById(R.id.item_app_name);
            firstTime = view.findViewById(R.id.item__first_runtime);
            lastTime = view.findViewById(R.id.item_last_runtime);
            totalTime = view.findViewById(R.id.item_total_runtime);
            location = view.findViewById(R.id.item_location);
            foreCount = view.findViewById(R.id.item_foreground);
            backCount = view.findViewById(R.id.item_background);
            actionCount = view.findViewById(R.id.item_interaction);

        }
    }
    public AppAdapter(List<ApplicationState> applicationStateList){
        mAppList = applicationStateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.states_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.stateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ApplicationState state = mAppList.get(position);
                Intent intent = new Intent(mContext, AppInfoActivity.class);
                intent.putExtra("name",state.getAppName());
                String data = state.getAppName() + "已运行" + state.getTotalRuntime() + "分钟，\n 最近一次启动时间为 " + state.getLastRuntime();
                intent.putExtra("data",data);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationState state = mAppList.get(position);
        holder.appName.setText(state.getAppName());
        holder.firstTime.setText(state.getFirstRuntime());
        holder.lastTime.setText(state.getLastRuntime());
        holder.totalTime.setText(state.getTotalRuntime() + "min");
        holder.location.setText(state.getLocation());
        holder.foreCount.setText(state.getForeCount()+"");
        holder.backCount.setText(state.getBackCount()+"");
        holder.actionCount.setText(state.getInteractionCount()+"");
    }

    @Override
    public int getItemCount() {
        return mAppList.size();
    }
}
