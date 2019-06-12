package com.example.schedulemanagement.entity;



import java.io.Serializable;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/12
 *     desc   : 用于传递日程对象
 * </pre>
 */

public class Schedule implements Serializable {
    /**
     * content : 去图书馆学习
     * id : 2
     * priority : 1
     * s_date : 2019-05-26
     * s_deadline : 11:30:00
     * s_starting : 08:30:00
     * status : done
     * title : 学习
     * uid : 1
     */

    private String content;
    private int id;
    private String priority;
    private String s_date;
    private String s_starting;
    private String status;
    private String title;
    private int uid;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getS_date() {
        return s_date;
    }

    public void setS_date(String s_date) {
        this.s_date = s_date;
    }


    public String getS_starting() {
        return s_starting;
    }

    public void setS_starting(String s_starting) {
        this.s_starting = s_starting;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
