package com.example.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.covid19.model.Country;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailActivity extends AppCompatActivity {

    ListView countriesListView;
    ArrayList<Country> countryList;
    TextView caNhiem, caDieuTri, caKhoi, caTuVong, countryName, khuyenCao, hoTro, tinTuc, canBiet;
    Button btnCloseDialog, btnSearchCountries;
    ImageView imgFlag;
    EditText srCountryText;
    int changeVlue = 0;
    ArrayList<Country> countryFilterByNameOrCode = null;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        countriesListView = findViewById(R.id.detailCountry);
        srCountryText = findViewById(R.id.srCountryText);
        btnSearchCountries = findViewById(R.id.btnSearchCountry);
        khuyenCao = findViewById(R.id.khuyenCao);
        hoTro = findViewById(R.id.hoTro);
        tinTuc = findViewById(R.id.tinTuc);
        canBiet = findViewById(R.id.canBiet);
        // Get data to Intent
        Intent intent = getIntent();
        countryList = (ArrayList<Country>) intent.getSerializableExtra("countries");

        // Gen data to List View
        genDataToListView(countryList);

        countriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                Country country = null;
//                // Co su dung search
//                if (changeVlue == 1) {
//                    country = countryFilterByNameOrCode.get(position);
//                } else {
//                    country = countryList.get(position);
//                }
//                Dialog dialog = new Dialog(DetailActivity.this);
//                dialog.setContentView(R.layout.dialog_detail_with_country);
//                dialog.setCanceledOnTouchOutside(false);
//                Window window = dialog.getWindow();
//                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                caNhiem = dialog.findViewById(R.id.caNhiem);
//                caDieuTri = dialog.findViewById(R.id.dieuTri);
//                caKhoi = dialog.findViewById(R.id.khoi);
//                caTuVong = dialog.findViewById(R.id.tuVong);
//                countryName = dialog.findViewById(R.id.countryName);
//                btnCloseDialog = dialog.findViewById(R.id.btnClose);
//                imgFlag = dialog.findViewById(R.id.imgFlag);
//
//                //Gen data
//                caNhiem.setText(String.valueOf(country.getCases()));
//                caKhoi.setText(String.valueOf(country.getRecovered()));
//                caDieuTri.setText(String.valueOf(country.getActive()));
//                caTuVong.setText(String.valueOf(country.getDeaths()));
//                countryName.setText(country.getCountryName() + " ("+country.getCountryCode()+")");
//                Picasso.with(DetailActivity.this).load(country.getCountryFlag()).into(imgFlag);
//                dialog.show();
//                btnCloseDialog.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        genDataToListView(countryList);
//                        srCountryText.setText("");
//                        countryFilterByNameOrCode.clear();
//                    }
//                });
                String nameOfCountry = "";
                if (changeVlue == 1) {
                    nameOfCountry = countryFilterByNameOrCode.get(position).getCountryName();
                } else {
                    nameOfCountry = countryList.get(position).getCountryName();
                }

                Intent intent = new Intent(DetailActivity.this, ChartViewDetailActivity.class);
                intent.putExtra("nameCountry", nameOfCountry);
                startActivity(intent);
            }
        });

        srCountryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() == "") {
                    changeVlue = 0;
                    genDataToListView(countryList);
                    return;
                }
                String namAndCode = s.toString().toLowerCase();
                countryFilterByNameOrCode = (ArrayList<Country>) countryList.stream()
                        .filter(t -> t.getCountryName().toLowerCase().contains(namAndCode)
                                || t.getCountryCode().toLowerCase().contains(s))
                        .collect(Collectors.toList());
                genDataToListView(countryFilterByNameOrCode);
                changeVlue = 1;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearchCountries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (srCountryText.getText().toString().isEmpty()) {
                    creatAlertDialog("Bạn phải nhập country name hoặc country code.");
                    return;
                }
                String nameAndCode = srCountryText.getText().toString().toLowerCase();
                // Tìm kiếm CountryName hoặc Code trong ListView
                Country countrySearch = (Country) countryList.stream()
                        .filter(t -> t.getCountryName().toLowerCase().equals(nameAndCode) || t.getCountryCode().toLowerCase().equals(nameAndCode));
                countryFilterByNameOrCode.add(countrySearch);
                if (countrySearch != null) {
                    genDataToListView(countryFilterByNameOrCode);
                } else {
                    creatAlertDialog("Country name hoặc country code bạn nhập không đúng.");
                }
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
    }

    public void creatAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("Thông báo!!!");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                srCountryText.setText("");
                genDataToListView(countryList);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void genDataToListView(List countries) {
        List<String> nameAndCode = (List<String>) countries.stream()
                .map(t -> ((Country) t).getCountryName() + " ("+((Country) t).getCountryCode()+")")
                .collect(Collectors.toList());
        ArrayAdapter adapter = new ArrayAdapter(DetailActivity.this, android.R.layout.simple_list_item_1, nameAndCode);
        countriesListView.setAdapter(adapter);
    }
}
