package com.asniie.library.librarys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestPermission();

        TextView view = findViewById(R.id.tv);
        SQLiteAPI api = AndroidDb.create(SQLiteAPI.class);

        api.createTable();

        String names[] = {"小玲", "小辉", "小红", "小马", "大明"};
        List<Person> persons = new ArrayList<>(15);
        Random random = new Random();

        for (int i = 0; i < 15; i++) {
            Person person = new Person();
            person.setAge(random.nextInt(12) + 15);
            person.setId(1);
            person.setName(names[random.nextInt(4)]);

            persons.add(person);
        }

        api.insert2(persons);

        view.setText("" + api.queryByAge(22));

        /*Map<String, String> map = api.queryById(100);

        view.setText(map.toString());

        Person person = new Person();
        person.setAge(18);
        person.setId(1);
        person.setName("小玲");

        Student student = new Student();
        student.setId(100);

        api.insert(person, student);*/
    }

    private void requestPermission() {

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean shouldRequest = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!shouldRequest) {
                        // 提示用户去应用设置界面手动开启权限
                        requestPermission();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
