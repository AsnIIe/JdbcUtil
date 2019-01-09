package com.asniie.library.librarys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.asniie.utils.sqlite.InstanceProxy;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView view = (TextView)findViewById(R.id.tv);
        InstanceProxy proxy=new InstanceProxy();
        SQLiteAPI api = proxy.create(SQLiteAPI.class);
        Map<String, String> map = api.queryById(10);
        api.queryById(100);

        view.setText(map.toString());

        Person person = new Person();
        person.setAge(16);
        person.setId(1);
        person.setName("小玲");

        Student student=new Student();
        student.setId(100);

        api.insert(person,student);
    }
}
