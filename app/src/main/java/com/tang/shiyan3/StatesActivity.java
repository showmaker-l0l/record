package com.tang.shiyan3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tang.shiyan3.db.ApplicationState;
import com.tang.shiyan3.util.AppAdapter;
import com.tang.shiyan3.util.GetData;
import com.tang.shiyan3.util.Utility;

import org.litepal.LitePal;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StatesActivity extends AppCompatActivity {

    private List<ApplicationState> stateList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_states);

        //获取MainActivity传过来的Intent
        Intent intent = getIntent();
        boolean isLastSearch = intent.getBooleanExtra("isLastSearch",false);
        String name = intent.getStringExtra("name");
        String startTime = intent.getStringExtra("start_time");
        String endTime = intent.getStringExtra("end_time");

        if ("".equals(startTime) && "".equals(endTime)) {
            //在搜索条件缺省时，默认显示7天内数据
            initStateList(name);
            //initStateList(name, isLastSearch);
        }
        else if (startTime.length() != 0) {
            //开始时间不为空就直接查询数据库
            long start = Utility.stringDateToLong(startTime);
            long end = System.currentTimeMillis();
            //结束时间不为空则获取毫秒值
            if (endTime.length() != 0) {
                end = Utility.stringDateToLong(endTime);
            }
            //时间选择不合法
            if (end < start || start > System.currentTimeMillis() || end > System.currentTimeMillis()) {
                finish();
                Toast.makeText(this, "时间选择有误，请重新选择！", Toast.LENGTH_SHORT).show();
            }
            //根据输入时间查询所有数据
            try {
                List<String> days = Utility.getDaysBetweenTwoDays(startTime, Utility.longToDate(end).split(" ")[0]);
                if (name.length() != 0){
                    for (String day : days){
                        stateList.addAll(LitePal.
                                where("appName = ? and dataType = ? and recordDate = ?", name, GetData.DAILY_DATA,day).
                                order("totalRuntime desc").
                                find(ApplicationState.class));
                    }
                } else {
                    for (String day : days){
                        stateList.addAll(LitePal.
                                where("dataType = ? and recordDate = ?", GetData.DAILY_DATA, day).
                                order("totalRuntime desc").
                                find(ApplicationState.class));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            GetData.getUsagestatesBefore(StatesActivity.this, GetData.GET_STATES_INPUT, start, end);
//            if (name.length() != 0) {
//                stateList = LitePal.where("appName = ? and dataType = ?", name, GetData.SEARCH_DATA)
//                        .order("totalRuntime desc").
//                                find(ApplicationState.class);
//            } else {
//                stateList = LitePal.where("dataType = ?", GetData.SEARCH_DATA)
//                        .order("totalRuntime desc").
//                                find(ApplicationState.class);
//            }
        }
        else {
            finish();
            Toast.makeText(this, "时间选择有误，请重新选择！", Toast.LENGTH_SHORT).show();
        }

        if (stateList.size() == 0){
            Toast.makeText(this,"没有记录，请重新输入",Toast.LENGTH_SHORT).show();
            finish();
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view_states);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AppAdapter adapter = new AppAdapter(stateList);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.show_piechart:
                Intent intent = new Intent(StatesActivity.this, PiechartActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) stateList);
                intent.putExtra("Message",bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }


    //    private void initStateList(String name, boolean isLastSearch) {
    private void initStateList(String name) {
        //按照totalRuntime从大到小排列
//        if (isLastSearch) {
//            stateList = LitePal.
//                    where("dataType = ?", GetData.SEARCH_DATA).
//                    order("totalRuntime desc").
//                    find(ApplicationState.class);
//            return;
//        }
        String date =  Utility.longToDate(System.currentTimeMillis()).split(" ")[0];
        if (name.length() == 0) {
            stateList = LitePal.
                    where("dataType = ? and recordDate = ?", GetData.DAILY_DATA, date).
                    order("totalRuntime desc").
                    find(ApplicationState.class);
        } else {
            stateList = LitePal.
                    where("appName = ?", name).
                    order("totalRuntime desc").
                    find(ApplicationState.class);
        }
    }
}