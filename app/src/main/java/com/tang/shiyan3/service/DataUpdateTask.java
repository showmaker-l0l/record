package com.tang.shiyan3.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.tang.shiyan3.util.GetData;

public class DataUpdateTask extends AsyncTask<Void, Void, Boolean> {


    private Context mContext;
    DataUpdateTask(Context context){
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected Boolean doInBackground(Void... voids) {
        GetData.getAppInfo(mContext);

        GetData.getUsageEvents(mContext,GetData.GET_ROUTINE);
        GetData.getUsageEvents(mContext,GetData.GET_DAILY);

        GetData.getUsagestates(mContext,GetData.GET_ROUTINE);
        GetData.getUsagestates(mContext,GetData.GET_DAILY);

//        GetData.getUsageEvents(mContext,GetData.GET_STATES_WEEKLY);
//        GetData.getUsagestates(mContext,GetData.GET_STATES_WEEKLY);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Toast.makeText(mContext,"DataUpdateTask finish！",Toast.LENGTH_SHORT).show();
    }
}
