package com.morse.mdao.bean;

import com.morse.mdao.annotation.DbField;
import com.morse.mdao.annotation.DbTabel;

/**
 * Created by morse on 2018/3/26.
 */
@DbTabel("tb_user")//如果不写tb_user，创建的表明为user
public class User {
    @DbField("_id")
    private Integer id;
    private String name;
    private String password;
}
