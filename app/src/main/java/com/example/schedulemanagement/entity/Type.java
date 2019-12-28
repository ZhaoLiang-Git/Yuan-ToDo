package com.example.schedulemanagement.entity;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/27
 *     desc   : 类别，标签等
 * </pre>
 */

public class Type {
    //名字
    private String name;
    //数量
    private int num;
    //id
    private int id;

    public Type(){

    }

    public Type(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
