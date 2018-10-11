package com.lmachine.mlda.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmachine.mlda.R;
import com.lmachine.mlda.util.SPUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.LinkedList;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 显示传感器的View
 */

public class SensorView extends CardView {

    private Context mContext;
    private TextView sensorName;
    private CheckBox[] checkBoxes;
    private TextView sensorVendor;

    private ImageView infoImg;
    private String sensorDes = null;

    private LinearLayout chartView;
    private XYSeries[] lines;
    private XYMultipleSeriesDataset dataSet;
    private XYSeriesRenderer[] renderers;
    private XYMultipleSeriesRenderer multipleSeriesRenderer;
    private GraphicalView chart;

    private Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("DefaultLocale")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    chart.postInvalidate();
                    break;
                case 0:
                    Bundle b = msg.getData();
                    String[] strData = b.getStringArray("data");
                    checkBoxes[0].setText(strData[0]);
                    checkBoxes[1].setText(strData[1]);
                    checkBoxes[2].setText(strData[2]);
                    checkBoxes[3].setText(strData[3]);

                default:

            }
            return true;
        }
    });

    private int[] colors = new int[]{
            Color.parseColor("#26a69a"),
            Color.parseColor("#42a5f5"),
            Color.parseColor("#66bb6a"),
            Color.parseColor("#607d8b")
    };

    private final int dataDuration = 2000;

    private LinkedList<Float> dataListX, dataListY, dataListZ, dataListXYZ;
    private boolean showX = true, showY = true, showZ = true, showXYZ = true;

    public SensorView(Context context) {
        super(context);
        init(context);
    }

    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.sensor_view, this);
        sensorName = v.findViewById(R.id.tv_sensor_name);
        checkBoxes = new CheckBox[]{
                v.findViewById(R.id.checkbox_x),
                v.findViewById(R.id.checkbox_y),
                v.findViewById(R.id.checkbox_z),
                v.findViewById(R.id.checkbox_xyz)
        };
        infoImg = v.findViewById(R.id.tv_sensor_info);
        sensorVendor = v.findViewById(R.id.tv_sensor_vendor);
        chartView = v.findViewById(R.id.chart_view);

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setTextColor(colors[i]);
        }

        initChart();

        setSensorName("传感器");
        setSensorData(new float[]{0, 0, 0});

        infoImg.setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(sensorDes)) return;
            new AlertDialog.Builder(mContext)
                    .setMessage(sensorDes)
                    .setPositiveButton("知道了", null)
                    .create().show();
        });

        OnClickListener onClickListener = v12 -> {
            setShowX(checkBoxes[0].isChecked());
            setShowY(checkBoxes[1].isChecked());
            setShowZ(checkBoxes[2].isChecked());
            setShowXYZ(checkBoxes[3].isChecked());
        };

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnClickListener(onClickListener);
        }
    }

    private void initChart() {
        dataListX = new LinkedList<>();
        dataListY = new LinkedList<>();
        dataListZ = new LinkedList<>();
        dataListXYZ = new LinkedList<>();

        lines = new XYSeries[]{
                new XYSeries("X"),
                new XYSeries("Y"),
                new XYSeries("Z"),
                new XYSeries("XYZ")
        };

        renderers = new XYSeriesRenderer[]{
                new XYSeriesRenderer(),
                new XYSeriesRenderer(),
                new XYSeriesRenderer(),
                new XYSeriesRenderer()
        };

        dataSet = new XYMultipleSeriesDataset();

        multipleSeriesRenderer = new XYMultipleSeriesRenderer();

        for (int i = 0; i < renderers.length; i++) {
            initRenderer(renderers[i], colors[i]);
            multipleSeriesRenderer.addSeriesRenderer(renderers[i]);
        }

        initChartProp();

        String rateStr = SPUtil.load(getContext()).getString("data_rate", "40");
        int rate = Integer.parseInt(rateStr);
        setRate(rate);
        setYAxisRange(0, 20);

        for (XYSeries line : lines) {
            dataSet.addSeries(line);
        }

        chart = ChartFactory.getLineChartView(getContext(), dataSet, multipleSeriesRenderer);
        chartView.addView(chart, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initRenderer(XYSeriesRenderer renderer, int color) {
        renderer.setColor(color);
        renderer.setLineWidth(3);
    }

    private void initChartProp() {
        multipleSeriesRenderer.setPanEnabled(false);
        multipleSeriesRenderer.setBackgroundColor(Color.WHITE);
        multipleSeriesRenderer.setShowAxes(false);
        multipleSeriesRenderer.setShowLabels(false);
        multipleSeriesRenderer.setShowLegend(false);
        multipleSeriesRenderer.setMargins(new int[]{0, 0, 0, 0});
        multipleSeriesRenderer.setMarginsColor(Color.TRANSPARENT);
        multipleSeriesRenderer.setLabelsTextSize(0);
        multipleSeriesRenderer.setAxisTitleTextSize(0);
    }

    public void setRate(int rate) {
        setXAxisRange(0, dataDuration / rate);
    }

    public void setXAxisRange(double min, double max) {
        multipleSeriesRenderer.setXAxisMax(max);
        multipleSeriesRenderer.setXAxisMin(min);
    }

    public void setYAxisRange(double min, double max) {
        multipleSeriesRenderer.setYAxisMin(min);
        multipleSeriesRenderer.setYAxisMax(max);
    }

    public void setSensorName(String name) {
        sensorName.setText(name);
    }

    //不在主线程
    @SuppressLint("DefaultLocale")
    public void setSensorData(float[] data) {
        float sum = (float) Math.sqrt(Math.pow(data[0], 2) + Math.pow(data[1], 2) + Math.pow(data[2], 2));
        Message message1 = handler.obtainMessage(0);
        Bundle b = new Bundle();
        b.putStringArray("data", new String[]{
                String.format("x: %.2f", data[0]),
                String.format("y: %.2f", data[1]),
                String.format("z: %.2f", data[2]),
                String.format("s: %.2f", sum)
        });
        message1.setData(b);
        handler.sendMessage(message1);

        for (XYSeries line : lines) {
            line.clear();
        }
        if (showX) {
            dataListX.add(data[0]);
            updateLineData(dataListX, lines[0]);
        }
        if (showY) {
            dataListY.add(data[1]);
            updateLineData(dataListY, lines[1]);
        }
        if (showZ) {
            dataListZ.add(data[2]);
            updateLineData(dataListZ, lines[2]);
        }
        if (showXYZ) {
            dataListXYZ.add(sum);
            updateLineData(dataListXYZ, lines[3]);
        }
        Message message2 = handler.obtainMessage(1);
        handler.sendMessage(message2);
    }

    private void updateLineData(LinkedList<Float> dataList, XYSeries line) {
        if (dataList.size() > multipleSeriesRenderer.getXAxisMax()) {
            dataList.remove(0);
        }
        for (int i = 0; i < dataList.size(); i++) {
            line.add(i, dataList.get(i));
        }
    }

    public void setSensorDes(String sensorInfo) {
        this.sensorDes = sensorInfo;
    }

    public void setSensorVendor(String vendor) {
        sensorVendor.setText(vendor);
    }

    public void setShow(boolean showX, boolean showY, boolean showZ, boolean showXYZ) {
        setShowX(showX);
        setShowY(showY);
        setShowZ(showZ);
        setShowXYZ(showXYZ);
        checkBoxes[0].setChecked(showX);
        checkBoxes[1].setChecked(showY);
        checkBoxes[2].setChecked(showZ);
        checkBoxes[3].setChecked(showXYZ);
    }

    public boolean isShowX() {
        return showX;
    }

    public void setShowX(boolean showX) {
        this.showX = showX;

    }

    public boolean isShowY() {
        return showY;
    }

    public void setShowY(boolean showY) {
        this.showY = showY;
    }

    public boolean isShowZ() {
        return showZ;
    }

    public void setShowZ(boolean showZ) {
        this.showZ = showZ;
    }

    public boolean isShowXYZ() {
        return showXYZ;
    }

    public void setShowXYZ(boolean showXYZ) {
        this.showXYZ = showXYZ;
    }
}
