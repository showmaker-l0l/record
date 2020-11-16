package com.tang.shiyan3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tang.shiyan3.db.User;
import com.tang.shiyan3.service.AutoCollectService;
import com.tang.shiyan3.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity  {

    private EditText loginAccount;
    private EditText loginPassword;
    private CircleImageView loginImage;
    private Button login;
    private CheckBox rememberPassword;
    private TextView register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginAccount = findViewById(R.id.login_account);
        loginPassword = findViewById(R.id.login_password);
        loginImage = findViewById(R.id.login_image);
        rememberPassword = findViewById(R.id.login_remeber_password);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login_button);

        //检查权限
        Utility.checkAppUsagePermission(LoginActivity.this);

        //开启服务，第一次收集信息

        Intent i = new Intent(LoginActivity.this, AutoCollectService.class);
        startService(i);

        //读取SharedPreferences，获取账号密码以及之前记住密码的状态
        SharedPreferences pref = getSharedPreferences("login_data",MODE_PRIVATE);
        String account = pref.getString("account","");
        boolean checked = pref.getBoolean("checked",false);
        loginAccount.setText(account);

        //若之前记住密码，则恢复密码
        if (checked){
            List<User> users = LitePal.where("account = ?",account).find(User.class);
            String password = users.get(0).getPassword();
            byte[] headImg = users.get(0).getHeadImg();
            Bitmap bitmap = BitmapFactory.decodeByteArray(headImg, 0, headImg.length);
            loginImage.setImageBitmap(bitmap);
            rememberPassword.setChecked(true);
            loginPassword.setText(password);
        }

        //注册跳转
        Intent intent = getIntent();
        String account1 = intent.getStringExtra("account");
        String imagePath = intent.getStringExtra("image");

        if (!(account1 == null || account1.equals(""))){
            loginAccount.setText(account1);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null){
                loginImage.setImageBitmap(bitmap);
            }
        }

        //登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //获取输入的账号密码
                String inputAccount = loginAccount.getText().toString();
                String inputPassword = loginPassword.getText().toString();
                List<User> users = LitePal.where("account = ?", inputAccount).find(User.class);

                //账号不能为空
                if (inputAccount == null || inputAccount.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入账号密码！", Toast.LENGTH_SHORT).show();
                } else {
                    if (users.size() == 0) {
                        Toast.makeText(LoginActivity.this, "该用户不存在！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (users.get(0).getAccount().equals(inputAccount) && users.get(0).getPassword().equals(inputPassword)) {
                            if (rememberPassword.isChecked()) {
                                //选择记住密码则将该状态写入SharedPreferences
                                SharedPreferences.Editor editor = getSharedPreferences("login_data", MODE_PRIVATE).edit();
                                editor.putString("account",inputAccount);
                                editor.putBoolean("checked", true);
                                editor.apply();
                            }
                            intent.putExtra("account",inputAccount);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "账号或密码错误,请重新输入！", Toast.LENGTH_SHORT).show();
                            loginPassword.setText("");
                        }
                    }
                }
            }}
        );

        //注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}