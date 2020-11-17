package com.tang.shiyan3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.tang.shiyan3.db.AppEvent;
import com.tang.shiyan3.db.AppInfo;
import com.tang.shiyan3.db.ApplicationState;
import com.tang.shiyan3.util.GetData;
import com.tang.shiyan3.util.Utility;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class AppInfoActivity extends AppCompatActivity {

    private ImageView icon;
    private TextView name;
    private TextView packageName;
    private TextView dir;
    private TextView size;
    private TextView permission;
    private TextView isSystemApp;
    private TextView usageState;
    private String appName;
    private LineChart chart;
    private static final String TAG = "AppInfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        //从RecyclerView的点击事件中获取AppName传过来
        Intent intent = getIntent();
        appName = intent.getStringExtra("name");
        String data = intent.getStringExtra("data");

        chart=findViewById(R.id.chart1);
        icon = findViewById(R.id.info_icon);
        name = findViewById(R.id.info_name);
        packageName = findViewById(R.id.info_package_name);
        dir = findViewById(R.id.info_dir);
        size = findViewById(R.id.info_size);
        isSystemApp = findViewById(R.id.info_is_system);
        permission = findViewById(R.id.info_permission);
        usageState = findViewById(R.id.info_usage_states);
        String date = Utility.longToDate(System.currentTimeMillis());
        //获取当天的appevents
        List<AppEvent> appEvents = LitePal.
                where("appName = ? and dataType = ? and eventDate = ?", appName, GetData.DAILY_DATA, date.split(" ")[0]).
                find(AppEvent.class);
        String eventLog = "";
        for (AppEvent event : appEvents){
            eventLog += "时间："+ "\t" + event.getEventTime() + "\n"
                    + event.getActivityName() + "\n"
                    + "事件类型："+ "\t" + event.getEventType() + "\n\n";
        }
        usageState.setText(data + "\n\n" + "应用活动记录：\n\n" + eventLog);

        //在数据库中查找name = appName的数据，并显示出来
        List<AppInfo> apps = LitePal.where("name = ?",appName).limit(1).find(AppInfo.class);
        AppInfo app = apps.get(0);
        byte[] bytes = app.getIcon();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        icon.setImageBitmap(bitmap);
        name.setText(app.getName());
        packageName.setText(app.getPackageName());
        dir.setText(app.getDir());
        size.setText(app.getSize() + "MB");
        if (app.isSystemApp()) {
            isSystemApp.setText("是系统应用");
        } else {
            isSystemApp.setText("不是系统应用");
        }
        if (app.getPermission() == null || "".equals(app.getPermission())){
            permission.setText("暂无应用权限");
        } else {
            permission.setText(app.getPermission());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void init() throws ParseException {
        Description description = new Description();//描述信息
        description.setText("");
        description.setEnabled(true);//是否可用
        chart.setDescription(description);//不然会显示默认的 Description。
        chart.setTouchEnabled(true); // 设置是否可以触摸
        chart.setDragEnabled(true);// 是否可以拖拽
        chart.setScaleEnabled(true);// 是否可以缩放
        chart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放
        chart.setDoubleTapToZoomEnabled(true);//是否允许双击进行缩放
        chart.setScaleXEnabled(false);//是否允许以X轴缩放
        chart.setDrawGridBackground(true);// 是否显示表格颜色
        chart.setGridBackgroundColor(Color.WHITE);// 表格的的颜色

        //chart.animateY(1000, Easing.Linear);//设置动画
        chart.setExtraBottomOffset(5f);//防止底部数据显示不完整，设置底部偏移量
        //x轴配置
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);//是否可用
        xAxis.setDrawLabels(true);//是否显示数值
        xAxis.setDrawAxisLine(true);//是否显示坐标线
        xAxis.setAxisLineColor(Color.BLACK);//设置坐标轴线的颜色
        xAxis.setAxisLineWidth(1.2f);//设置坐标轴线的宽度
        xAxis.setDrawGridLines(true);//是否显示竖直风格线
        xAxis.setTextColor(Color.BLACK);//X轴文字颜色
        xAxis.setTextSize(15f);//X轴文字大小
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴文字显示位置
        xAxis.setSpaceMin(1f);//左空白区大小
        xAxis.setSpaceMax(1f);//右空白区大小

        //左y轴配置
        YAxis lyAxis = chart.getAxisLeft();
        lyAxis.setEnabled(true);//是否可用
        lyAxis.setDrawLabels(true);//是否显示数值
        lyAxis.setDrawAxisLine(true);//是否显示坐标线
        lyAxis.setDrawGridLines(true);//是否显示水平网格线
        lyAxis.setDrawZeroLine(false);////是否绘制零线
        lyAxis.setZeroLineColor(Color.BLACK);
        lyAxis.setZeroLineWidth(0.8f);
        lyAxis.enableGridDashedLine(10f, 10f, 0f);//网格虚线
        lyAxis.setGridColor(Color.BLACK);//网格线颜色
        lyAxis.setGridLineWidth(0.8f);//网格线宽度
        lyAxis.setAxisLineColor(Color.BLACK);//坐标线颜色
        lyAxis.setTextColor(Color.BLACK);//左侧文字颜色
        lyAxis.setTextSize(15f);//左侧文字大小
        //右y轴配置
        YAxis ryAxis = chart.getAxisRight();
        ryAxis.setEnabled(false);//是否可用
        //标签配置
        Legend legend = chart.getLegend();
        legend.setEnabled(true);//是否可用//x轴和y轴的数据
        legend.setFormSize(20f);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setForm(Legend.LegendForm.LINE);

        List<Entry> entries = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(android.icu.util.Calendar.DAY_OF_YEAR, -7);
        long startTime = calendar.getTimeInMillis();//开始时间
        long endTime = System.currentTimeMillis();
        List<String> days = Utility.getDaysBetweenTwoDays(Utility.longToDate(startTime).split(" ")[0],
                Utility.longToDate(endTime).split(" ")[0]);

        for (String day : days) {
            ApplicationState state = LitePal.
                    where("appname = ? and recordDate = ? and dataType = ? ", appName, day, GetData.DAILY_DATA).
                    findFirst(ApplicationState.class);
            int count = 0;
            if (state != null){
                count = state.getTotalRuntime();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = sdf.parse(day);
            int d = date.getDate();
            Log.d(TAG, "init:date "+ date);
            Log.d(TAG, "init:day "+ day);
            Log.d(TAG, "init:d "+ d);
            entries.add(new Entry(d, count));
        }
        LineDataSet dataSet = new LineDataSet(entries, "应用一周使用时长统计"); // 添加数据
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // 刷新
    }
}