package com.example.covid19;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


import com.example.covid19.model.Users;
import com.example.covid19.repository.UserRepository;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
public class LoginActivity extends AppCompatActivity {

    EditText userName, password;
    Button btnLogin;
    LoginButton loginButton;
    TextView signUp;
    Users users;
    CheckBox saveUser;
    UserRepository userRepository = new UserRepository(this);
    SharedPreferences sharedPreferences;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        // Ánh xạ các thành phần
        anhXa();
        sharedPreferences = getSharedPreferences("save_user", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        //Xử lý nhớ đăng nhập
        String username = sharedPreferences.getString("username", null);
        String pass = sharedPreferences.getString("password", null);
        boolean save_user = sharedPreferences.getBoolean("save_user", false);
        if (save_user) {
            userName.setText(username);
            password.setText(pass);
            saveUser.setChecked(save_user);
        }

        // Xử lý khi user click vào login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Users users = new Users();
                users.setEmail(userName.getText().toString());
                users.setPassword(LoginActivity.this.password.getText().toString());
                boolean isSuccess = userRepository.checkLogin(users);
                if (isSuccess) {
                    if (saveUser.isChecked()) {
                        edit.putString("username",userName.getText().toString());
                        edit.putString("password", LoginActivity.this.password.getText().toString());
                        edit.putBoolean("save_user", true);
                        edit.commit();
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    String mess = "Tài khoản hoặc mật khẩu không chính xác...";
                    alertDialog(mess);
                }
            }
        });

        //Xử lý khi user click vào đăng kí tài khoản
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.activity_register);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(1070, 1300);
                final TextView name = dialog.findViewById(R.id.name);
                final TextView password = dialog.findViewById(R.id.password);
                final TextView email = dialog.findViewById(R.id.email);
                final TextView addr = dialog.findViewById(R.id.address);
                Button huy = dialog.findViewById(R.id.btnHuy);
                Button signup = dialog.findViewById(R.id.btnSignup);
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Users users = new Users();
                        users.setFullName(name.getText().toString());
                        users.setEmail(email.getText().toString());
                        users.setPassword(password.getText().toString());
                        users.setAddress(addr.getText().toString());
                        if (users.getFullName().equals("") || users.getEmail().equals("") || users.getAddress().equals("")
                                            || users.getPassword().equals("")) {
                            String mess = "Bạn điền thiếu thông tin!!! vui lòng điền đầy đủ.";
                            alertDialog(mess);
                            return;
                        }
                        long stt = userRepository.registerUser(users);
                        if (stt != 0) {
                            String mess = "Đăng kí tài khoản thành công!!!";
                            alertDialog(mess);
                        }
                        dialog.dismiss();
                    }
                });

                huy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // Tich hợp facebook
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        checkLoginFacebook();

    }

    private void checkLoginFacebook() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Log.w("111111111111", "thanh cong" );
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.w("2222222222222", "fail" );
            }
        });
    }

    public void anhXa() {
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.tvSignup);
        saveUser = findViewById(R.id.saveUser);
        loginButton = findViewById(R.id.login_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void alertDialog(String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Thông báo!!!");
        builder.setMessage(mess);
        builder.setCancelable(false);
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
