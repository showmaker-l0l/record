package com.tang.shiyan3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tang.shiyan3.db.ApplicationState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PiechartActivity extends AppCompatActivity {

    private PieChart totaltimechart;
    private PieChart foreCountchart;
    private ArrayList<String> appname_t;
    private ArrayList<String> appname_f;
    private ArrayList<Float> percentages_t;
    private ArrayList<Float> percentages_f;
    private Float others_t = 0.0f;
    private Float others_f = 0.0f;
    private int totaltime = 0;
    private int foreCount = 0;
    private Float percentage_t;
    private Float percentage_f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        appname_t = new ArrayList<>();
        appname_f = new ArrayList<>();
        percentages_t = new ArrayList<>();
        percentages_f = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        List<ApplicationState> stateList = (List<ApplicationState>) bundle.getSerializable("data");

        for(ApplicationState state : stateList){
            totaltime += state.getTotalRuntime();
            foreCount += state.getForeCount();

        }

        for(ApplicationState state : stateList){
            percentage_t = (float)state.getTotalRuntime()/totaltime;
            percentage_f = (float)state.getForeCount()/foreCount;
            if(percentage_t > 0.03) {
                appname_t.add(state.getAppName());
                percentages_t.add(percentage_t);
            }else{
                others_t += percentage_t;
            }
            if(percentage_f > 0.03){
                appname_f.add(state.getAppName());
                percentages_f.add(percentage_f);
            }
            else{
                others_f += percentage_f;
            }
        }
        appname_t.add("其他软件");
        percentages_t.add(others_t);
        appname_f.add("其他软件");
        percentages_f.add(others_f);


        totaltimechart = findViewById(R.id.chart_1);
        foreCountchart = findViewById(R.id.chart_2);

        List<PieEntry> totaltimeList = getPieChartData(appname_t,percentages_t);
        showPieChart(totaltimechart,totaltimeList);
        List<PieEntry> foreCountlist = getPieChartData(appname_f,percentages_f);
        showPieChart(foreCountchart,foreCountlist);



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

    //获取饼图展示的数据
    private List<PieEntry> getPieChartData(ArrayList<String> appname,ArrayList<Float> percentages) {

        List<PieEntry> mPie = new ArrayList<>();

        //为饼图添加数据
        for(int i = 0;i< appname.size();i++){
            //第一个参数为float表示占比，第二个参数为string，每块表示的名字
            PieEntry pieEntry = new PieEntry(percentages.get(i), appname.get(i));
            mPie.add(pieEntry);
        }
        return mPie;
    }

    private void showPieChart(PieChart pieChart, List<PieEntry> pieList) {
        PieDataSet dataSet = new PieDataSet(pieList,"Label");

        // 设置颜色list，让不同的块显示不同颜色。
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);


        //设置描述
        Description description = new Description();
        //可以在这里添加描述展示的字
        //description.setText("");
        //是否显示描述
        description.setEnabled(false);
        pieChart.setDescription(description);

        //设置显示百分比
        pieChart.setUsePercentValues(true);

        //设置半透明圆环的半径, 0为透明
        pieChart.setHoleRadius(1f);
        pieChart.setTransparentCircleRadius(0f);

        //设置初始旋转角度
        pieChart.setRotationAngle(-15);

        //数据连接线距图形片内部边界的距离，为百分数
        //dataSet.setValueLinePart1OffsetPercentage(80.f);
        //dataSet.setValueLinePart1Length(0.3f);
        //dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        //设置连接线的颜色
        dataSet.setValueLineColor(Color.LTGRAY);


        // 设置饼块之间的间隔
        dataSet.setSliceSpace(1f);

        // 不显示图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // 和四周相隔一段距离
        pieChart.setExtraOffsets(0, 5, 0, 5);

        // 设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(true);

        // 设置piecahrt图表点击Item高亮是否可用
        pieChart.setHighlightPerTapEnabled(true);

        //设置pieChart是否只显示饼图上百分比不显示文字
        pieChart.setDrawEntryLabels(true);

        //是否绘制PieChart内部中心文本
        pieChart.setDrawCenterText(false);

        // 绘制内容value，设置字体颜色大小
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        // 更新piechart视图
        pieChart.postInvalidate();
    }


}