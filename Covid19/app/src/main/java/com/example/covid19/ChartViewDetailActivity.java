package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid19.model.Country;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.SneakyThrows;

public class ChartViewDetailActivity extends AppCompatActivity {

    Button khoi, nhiem, dieuTri, tuVong;
    BarChart barChart;
    TextView khuyenCao, hoTro, tinTuc, canBiet;
    ArrayList<Country> countryDayOfWeek = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chart_detail);
        khoi = findViewById(R.id.btnKhoi);
        dieuTri = findViewById(R.id.btnDieuTri);
        nhiem = findViewById(R.id.btnNhiem);
        tuVong = findViewById(R.id.btnTuVong);
        barChart = findViewById(R.id.barChart);
        khuyenCao = findViewById(R.id.khuyenCao);
        hoTro = findViewById(R.id.hoTro);
        tinTuc = findViewById(R.id.tinTuc);
        canBiet = findViewById(R.id.canBiet);
        Intent intent = getIntent();
        String nameOfCountry = intent.getStringExtra("nameCountry");

        nhiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> totalCaseWeek = countryDayOfWeek.stream()
                                                .map(t -> t.getCases())
                                                .collect(Collectors.toList());
                drawBarChart(totalCaseWeek, "ca nhiễm");
            }
        });

        khoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> totalRecoveredWeek = countryDayOfWeek.stream()
                                                .map(t -> t.getRecovered())
                                                .collect(Collectors.toList());
                drawBarChart(totalRecoveredWeek, "đã khỏi");
            }
        });

        dieuTri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> totalActiveWeek = countryDayOfWeek.stream()
                                                    .map(t -> t.getCritical())
                                                    .collect(Collectors.toList());
                drawBarChart(totalActiveWeek, "đang điều trị");
            }
        });

        tuVong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> totalDeathsWeek = countryDayOfWeek.stream()
                                                    .map(t -> t.getDeaths())
                                                    .collect(Collectors.toList());
                drawBarChart(totalDeathsWeek, "tử vong");
            }
        });

        khuyenCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://ncov.moh.gov.vn/web/guest/khuyen-cao"));
                startActivity(intent);
            }
        });

        hoTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://ncov.moh.gov.vn/web/guest/-hotro-nd"));
                startActivity(intent);
            }
        });

        tinTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://ncov.moh.gov.vn/web/guest/tin-tuc"));
                startActivity(intent);
            }
        });

        canBiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://ncov.moh.gov.vn/web/guest/-ieu-can-biet"));
                startActivity(intent);
            }
        });

        getDataFromApiCovid(nameOfCountry);
    }

    public void getDataFromApiCovid(String nameOfCountry) {
        RequestQueue requestQueue = Volley.newRequestQueue(ChartViewDetailActivity.this);
        String urlApi = "https://api.quarantine.country/api/v1/spots/week?region=" + nameOfCountry;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlApi,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SneakyThrows
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("data");
                            ArrayList<String> timeOfWeeks = (ArrayList<String>) genDateOfWeek("yyyy-MM-dd");
                            ArrayList<Country> countryDayOfWeekCpy = new ArrayList<>();
                            // Đổ data từ api vào List<Country>
                            for (String item : timeOfWeeks) {
                                Country country = new Country();
                                JSONObject jsonObjectDay = (JSONObject) jsonObject.get(item);
                                country.setCases(Integer.parseInt(jsonObjectDay.getString("total_cases")));
                                country.setDeaths(Integer.parseInt(jsonObjectDay.getString("deaths")));
                                country.setRecovered(Integer.parseInt(jsonObjectDay.getString("recovered")));
                                country.setCritical(Integer.parseInt(jsonObjectDay.getString("critical")));
                                countryDayOfWeekCpy.add(country);
                            }
                            Collections.reverse(countryDayOfWeekCpy);
                            countryDayOfWeek = countryDayOfWeekCpy;
                            // Vẽ biểu đồ
                            List<Integer> totalCaseWeek = countryDayOfWeekCpy.stream()
                                    .map(t -> t.getCases()).collect(Collectors.toList());
                            drawBarChart(totalCaseWeek, "ca nhiễm");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChartViewDetailActivity.this, "có lỗi xảy ra", Toast.LENGTH_LONG);
                    }
                });
        requestQueue.add(stringRequest);
    }

    public void drawBarChart(List<Integer> totalCaseOfWeek, String typeChart) {
        // Gen day of week
        ArrayList<String> timeOfWeeks = (ArrayList<String>) genDateOfWeekCpy("dd/MM");
        Collections.reverse(timeOfWeeks);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(timeOfWeeks.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeOfWeeks));

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int i = 1;
        for (Integer total : totalCaseOfWeek) {
            barEntries.add(new BarEntry(i, (int) total));
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "entry");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(11f);



        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText(typeChart);
        barChart.animateY(2000);
    }

    public List genDateOfWeek(String format) {
        ArrayList<String> timeOfWeeks = new ArrayList<>();
        // Lay ngay hien tai
        Date date = new Date();
//        long currentDate = date.getTime();
        long currentDate = date.getTime() - (24 * 3600 * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        for (int i = 0; i < 7 ; i++) {
            String timeOfWeek = dateFormat.format(currentDate);
            timeOfWeeks.add(timeOfWeek);
            currentDate = currentDate - (24 * 3600 * 1000);
        }
        return timeOfWeeks;
    }

    public List genDateOfWeekCpy(String format) {
        ArrayList<String> timeOfWeeks = new ArrayList<>();
        // Lay ngay hien tai
        Date date = new Date();
//        long currentDate = date.getTime();
        long currentDate = date.getTime() - (24 * 3600 * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        for (int i = 0; i < 8 ; i++) {
            String timeOfWeek = dateFormat.format(currentDate);
            timeOfWeeks.add(timeOfWeek);
            currentDate = currentDate - (24 * 3600 * 1000);
        }
        return timeOfWeeks;
    }
}
