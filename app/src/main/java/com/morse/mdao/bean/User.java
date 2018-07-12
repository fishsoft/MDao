package com.morse.mdao.bean;

import com.morse.mdao.annotation.DbField;
import com.morse.mdao.annotation.DbTable;

/**
 * Created by morse on 2018/3/26.
 */
@DbTable("tb_user")//如果不写tb_user，创建的表明为user
public class User {
    @DbField("_id")
    private Integer id;
    private String name;
    private String password;


    public User(int id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.password = pwd;
    }
}
