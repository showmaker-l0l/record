package com.tang.shiyan3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.tang.shiyan3.db.ApplicationState;
import com.tang.shiyan3.db.User;
import com.tang.shiyan3.util.AppAdapter;
import com.tang.shiyan3.util.GetData;
import com.tang.shiyan3.util.Utility;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private List<ApplicationState> stateList = new ArrayList<>();
    private TextView brand;
    private TextView model;
    private TextView number;
    private TextView statTime;
    private TextView endTime;
    private EditText searchName;
    private Calendar calendar;
    private TextView lastStatTime;
    private TextView lastEndTime;
    private TextView lastName;
    private DrawerLayout mDrawerLayout;
    private CircleImageView head;
    private AppAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headview = navView.getHeaderView(0);
        //主界面的控件
        head = findViewById(R.id.main_headImg);
        brand = findViewById(R.id.phone_brand);
        model = findViewById(R.id.phone_model);
        number = findViewById(R.id.phone_number);
        //DrawerLayout的控件
        statTime = headview.findViewById(R.id.start_time);
        endTime = headview.findViewById(R.id.end_time);
        searchName = headview.findViewById(R.id.search_name);
        lastStatTime = headview.findViewById(R.id.last_start_time);
        lastEndTime = headview.findViewById(R.id.last_end_time);
        lastName = headview.findViewById(R.id.last_search_name);

        Button start_time = (Button) headview.findViewById(R.id.start_button);
        Button end_time = (Button) headview.findViewById(R.id.end_button);
        Button get_usage_state = (Button) headview.findViewById(R.id.get_usage_state);
        Button last_search = (Button) headview.findViewById(R.id.last_search);
        Button clear_button = (Button) headview.findViewById(R.id.clear_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.home);
        }

        //获得登录的账号，把用户头像显示出来
        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        List<User> users = LitePal.where("account = ?", account).find(User.class);
        if (users.size() != 0){
            byte[] headImg = users.get(0).getHeadImg();
            Bitmap bitmap = BitmapFactory.decodeByteArray(headImg, 0, headImg.length);
            head.setImageBitmap(bitmap);
        }
        calendar = Calendar.getInstance();

        //初始化用户状态列表
        initStatesList();

        RecyclerView recyclerView = findViewById(R.id.daily_usage_states);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AppAdapter(stateList);
        recyclerView.setAdapter(adapter);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLists();
            }
        });

        //申请权限，并显示手机型号、手机品牌和手机号码
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        brand.setText(Build.BRAND);
        model.setText(Build.MODEL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        number.setText(manager.getLine1Number());


            //设置开始时间
            start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    calendar.set(year, month, dayOfMonth);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                                    String date = sdf.format(calendar.getTime());
                                    statTime.setText(date);
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog.show();

                }
            });
            //设置结束时间
            end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog dialog1 = new DatePickerDialog(MainActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    calendar.set(year, month, dayOfMonth);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                                    String date = sdf.format(calendar.getTime());
                                    endTime.setText(date);
                                }
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    dialog1.show();
                }
            });
            //搜索
            get_usage_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, StatesActivity.class);
                    //获取选择时间，传入StatesActivity
                    String start_time = statTime.getText().toString();
                    String end_time = endTime.getText().toString();
                    String name = searchName.getText().toString();

                    intent.putExtra("start_time", start_time);
                    intent.putExtra("end_time", end_time);
                    intent.putExtra("name", name);

                    lastStatTime.setText(start_time);
                    lastEndTime.setText(end_time);
                    lastName.setText(name);


                    statTime.setText("");
                    endTime.setText("");
                    searchName.setText("");
                    startActivity(intent);
                }
            });
        //上一次搜索结果
        last_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, StatesActivity.class);
                i.putExtra("isLastSearch", true);
                i.putExtra("start_time", "");
                i.putExtra("end_time", "");
                i.putExtra("name", "");
                startActivity(i);
            }
        });
            //清除搜索条件
            clear_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statTime.setText("");
                    endTime.setText("");
                    searchName.setText("");
                }
            });
      
        }

    private void refreshLists() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initStatesList();
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    private void initStatesList(){
        stateList.clear();
        //将当日信息显示出来
        String date = Utility.longToDate(System.currentTimeMillis()).split(" ")[0];
        //必须对同一个statelist对象进行操作，否则adapter获取不到数据
        List<ApplicationState> states = LitePal.
                where("dataType = ? and recordDate = ?", GetData.DAILY_DATA, date).
                order("totalRuntime desc").
                find(ApplicationState.class);
        for (ApplicationState state : states){
            stateList.add(state);
        }
    }

    @Override
    protected void onRestart() {
        initStatesList();
        adapter.notifyDataSetChanged();
        super.onRestart();
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.show_piechart:
                Intent intent = new Intent(MainActivity.this, PiechartActivity.class);
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


}

