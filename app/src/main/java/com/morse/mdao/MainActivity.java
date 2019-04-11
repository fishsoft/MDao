package com.morse.mdao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.morse.mdao.bean.User;
import com.morse.mdao.db.BaseDao;
import com.morse.mdao.db.BaseDaoFactory;
import com.morse.mdao.db.DBManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createTable(View view) {
        initDao();
        User user = new User();
        user.setName("morse");
        user.setPassword("123456");
        long id = DBManager.getManagerDao(User.class).insert(user);
        Log.d("morse", id + "");
    }

    public void update(View view) {
        initDao();
        User user = new User();
        user.setName("morse");
        user.setPassword("12345");
        User user1 = new User();
        user1.setPassword("123456");
        DBManager.getManagerDao(User.class).update(user, user1);
    }

    public void query(View view) {
        initDao();
        User user = new User();
        user.setName("morse");
        List<User> users = DBManager.getManagerDao(User.class).query(user);
        System.out.println("数据数目：" + users.size() + "\n数据：" + users.toString());
    }

    public void delete(View view) {
        initDao();
        User user = new User();
        user.setId(1);
        DBManager.getManagerDao(User.class).delete(user);
    }
}
