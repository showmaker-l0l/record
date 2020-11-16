package com.tang.shiyan3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tang.shiyan3.db.User;
import com.tang.shiyan3.service.DataInitTask;
import com.tang.shiyan3.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView registImage;
    private EditText registAccount;
    private EditText registPassword;
    private EditText registPassword2;
    private Button regist;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO=2;
    private String imagePath = "";
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE};
    private List<String> mPermissionList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regist = findViewById(R.id.regist_button);
        registImage = findViewById(R.id.regist_image);
        registAccount = findViewById(R.id.regist_account);
        registPassword = findViewById(R.id.regist_password);
        registPassword2 = findViewById(R.id.regist_password2);

        checkPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        SharedPreferences pref = getSharedPreferences("init_data",MODE_PRIVATE);
        if (!pref.getBoolean("IsInit",false)){

            Toast.makeText(this,"Init data",Toast.LENGTH_SHORT).show();
            DataInitTask dataInitTask = new DataInitTask(this);
            dataInitTask.execute();

            SharedPreferences.Editor editor = getSharedPreferences("init_data", MODE_PRIVATE).edit();
            editor.putBoolean("IsInit", true);
            editor.apply();
        }

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = registAccount.getText().toString();
                String password = registPassword.getText().toString();
                String password2 = registPassword2.getText().toString();

                //账号密码不能为空
                if (account == null || account.equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入账号!" ,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password == null || password.equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入密码!" ,Toast.LENGTH_SHORT).show();
                    return;
                }
                //两次输入的密码要一致
                if (!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this,"两次输入的密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                    registPassword.setText("");
                    registPassword2.setText("");
                } else {
                    //查询账号是否存在
                    List<User> users = LitePal.where("account = ?",account).find(User.class);
                    if (users.size() != 0){
                        Toast.makeText(RegisterActivity.this, "用户名已存在！", Toast.LENGTH_LONG).show();
                        registAccount.setText("");
                    }else {
                        //把user写入数据库
                        User user = new User();
                        user.setAccount(account);
                        user.setPassword(password);
                        Drawable drawable = registImage.getDrawable();
                        byte[] bytes = Utility.drawableToByte(drawable);
                        user.setHeadImg(bytes);
                        user.save();
                        //把用户名传回loginActivity
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.putExtra("account",account);
                        intent.putExtra("image",imagePath);
                        startActivity(intent);
                    }
                }
            }
        });

        //选择头像
        registImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openAlbum();
            }
        });
    }
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

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void checkPermission() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(RegisterActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        //判断存储委授予权限的集合是否为空
        if (!mPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(RegisterActivity.this,permissions,1 );
        }
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            registImage.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "打开文件失败", Toast.LENGTH_SHORT).show();
        }
    }

}