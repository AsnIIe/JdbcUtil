package com.asniie.library.librarys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.asniie.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestPermission();

        TextView view = findViewById(R.id.tv);

        SQLiteAPI api = AndroidSQLite.create(SQLiteAPI.class);

        api.createTable();

        String names[] = {"小玲", "小辉", "小红", "小马", "大明"};
        List<Person> persons = new ArrayList<>(15);
        Random random = new Random();

        int n = random.nextInt(20) + 1;

        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setAge(random.nextInt(12) + 15);
            person.setId(random.nextInt(1000));
            person.setName(names[random.nextInt(4)]);

            persons.add(person);
        }


        Teacher teacher = initTeacher();

        Student student = new Student();
        student.setId(100);
        student.setAge(30);

        int count = api.insertStudents(persons, teacher.getStudents());

        view.setText(String.format("插入数据：%d条,\n通过Teacher查询Student：\n%s", count, api.queryStudentByTeacher(teacher, 5)));

        LogUtil.debug(api.queryById(100));

        Person person = new Person();
        person.setAge(18);
        person.setId(1);
        person.setName("小明");

        LogUtil.debug(api.insert(person, student));
    }

    private Teacher initTeacher() {
        Teacher teacher = new Teacher();
        List<Student> students = new ArrayList<>();
        Map<String, Book> books = new HashMap<>();

        String keys[] = new String[]{"热爱", "喜欢", "看过"};

        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setId(12358 + i);
            student.setName("小玲");
            student.setAge(25 + i);

            students.add(student);

            Book book = new Book();
            book.setName("《三国演义》");
            book.setPrice(35.5);

            books.put(keys[i % 3], book);
        }

        teacher.setStudents(students);
        teacher.setBooks(books);

        return teacher;
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
