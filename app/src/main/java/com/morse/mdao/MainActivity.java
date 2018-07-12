package com.morse.mdao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.morse.mdao.bean.User;
import com.morse.mdao.db.BaseDao;
import com.morse.mdao.db.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createTable(View view) {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(User.class);
        User user = new User(1, "morse", "123456");
        long id = baseDao.insert(user);
        Log.d("morse", id + "");
    }
}
