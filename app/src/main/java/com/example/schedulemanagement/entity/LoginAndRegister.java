package com.example.schedulemanagement.entity;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/02
 *     desc   : 注册实体类
 * </pre>
 */

public class LoginAndRegister {
    private String uid;
    private String uname;
    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public String getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
