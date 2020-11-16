package com.tang.shiyan3.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.tang.shiyan3.util.GetData;

public class DataInitTask extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;

    public DataInitTask(Context context){
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected Boolean doInBackground(Void... voids) {
        GetData.initEventsAndStates(mContext);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Toast.makeText(mContext,"DataInitTask finish！",Toast.LENGTH_SHORT).show();
    }
}
