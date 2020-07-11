package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covid19.model.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;

import lombok.SneakyThrows;


public class MainActivity extends AppCompatActivity {
    ImageView mapView;
    TextView btnChiTietDichBenh, caNhiemVn, caDieuTriVn, caKhoiVn, caChetVn,
            caNhiemTg, caDieuTriTg, caKhoiTg, caChetTg, khuyenCao, hoTro, tinTuc;
    ArrayList countryInfoCovids = new ArrayList<Country>();

    @SneakyThrows
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        caNhiemVn = findViewById(R.id.caNhiemVn);
        caNhiemTg = findViewById(R.id.nhiemTg);
        caDieuTriVn = findViewById(R.id.dieuTriVn);
        caDieuTriTg = findViewById(R.id.dangNhiemTg);
        caKhoiVn = findViewById(R.id.khoiVn);
        caKhoiTg = findViewById(R.id.khoiTg);
        caChetVn = findViewById(R.id.tuVongVn);
        caChetTg = findViewById(R.id.tuVongTg);
        khuyenCao = findViewById(R.id.khuyenCao);
        hoTro = findViewById(R.id.hoTro);
        tinTuc = findViewById(R.id.tinTuc);
        countryInfoCovids = new ArrayList<Country>();
        btnChiTietDichBenh = findViewById(R.id.chiTietDich);
        btnChiTietDichBenh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("countries", countryInfoCovids);
                startActivity(intent);
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

        getDataApiCovid();
    }

    public void getDataApiCovid() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String urlApi = "https://corona.lmao.ninja/v2/countries";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlApi,
                new Response.Listener<String>() {
                    @SneakyThrows
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArrayCountry = new JSONArray(response);
                            for (int i = 0; i < jsonArrayCountry.length(); i++) {
                                JSONObject countryJson = jsonArrayCountry.getJSONObject(i);
                                Country countryModel = new Country();
                                long updateday = Long.parseLong(countryJson.getString("updated"));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String dateFormat = simpleDateFormat.format(updateday);
                                countryModel.setUpdateDay(dateFormat);
                                countryModel.setRecovered(Integer.parseInt(countryJson.getString("recovered")));
                                countryModel.setDeaths(Integer.parseInt(countryJson.getString("deaths")));
                                countryModel.setCritical(Integer.parseInt(countryJson.getString("critical")));
                                countryModel.setCases(Integer.parseInt(countryJson.getString("cases")));
                                countryModel.setActive(Integer.parseInt(countryJson.getString("active")));
                                countryModel.setContinent(countryJson.getString("continent"));
                                countryModel.setCountryName(countryJson.getString("country"));

                                JSONObject countryCodeJson = countryJson.getJSONObject("countryInfo");
                                countryModel.setCountryCode(countryCodeJson.getString("iso2"));
                                countryModel.setCountryFlag(countryCodeJson.getString("flag"));
                                countryInfoCovids.add(countryModel);
                            }
                            // Gen data to table main screen
                            genDataToTable();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage();
                        Intent intent = new Intent(MainActivity.this, ErrorActivity.class);
                        intent.putExtra("errorMessage", errorMessage);
                        startActivity(intent);
                    }
                });
        requestQueue.add(stringRequest);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void genDataToTable() {
        // get data of VietNam
        Optional<Country> countryObject = countryInfoCovids.stream().filter((p -> ((Country) p).getCountryName().equals("Vietnam"))).findFirst();
        if (countryObject.isPresent()) {
            caNhiemVn.setText(String.format("%,d", countryObject.get().getCases()));
            caDieuTriVn.setText(String.format("%,d", countryObject.get().getActive()));
            caKhoiVn.setText(String.format("%,d", countryObject.get().getRecovered()));
            caChetVn.setText(String.format("%,d", countryObject.get().getDeaths()));
            // get data of the world
            int totalCaseTg = countryInfoCovids.stream().filter(p -> !((Country) p).getCountryName().equals("Vietnam"))
                    .mapToInt(t -> ((Country) t).getCases()).sum();
            int totalActiveTg = countryInfoCovids.stream().filter(p -> !((Country) p).getCountryName().equals("Vietnam"))
                    .mapToInt(t -> ((Country) t).getActive()).sum();
            int totalRecoverdTg = countryInfoCovids.stream().filter(p -> !((Country) p).getCountryName().equals("Vietnam"))
                    .mapToInt(t -> ((Country) t).getRecovered()).sum();
            int totalDeathsTg = countryInfoCovids.stream().filter(p -> !((Country) p).getCountryName().equals("Vietnam"))
                    .mapToInt(t -> ((Country) t).getDeaths()).sum();
            caChetTg.setText(String.format("%,d", totalDeathsTg));
            caDieuTriTg.setText(String.format("%,d", totalActiveTg));
            caKhoiTg.setText(String.format("%,d", totalRecoverdTg));
            caNhiemTg.setText(String.format("%,d", totalCaseTg));
        }
    }
}
