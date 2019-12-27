package com.example.schedulemanagement.entity;

import java.io.Serializable;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/24
 *     desc   : 任务实体类
 * </pre>
 */

public class Task implements Serializable {
    //任务号
    private int taskId;
    //分类号
    private int cId;
    //地点号
    private int pId;
    //时间类别号
    private int timeId;
    //用户id
    private int userId;
    //标题
    private String title;
    //内容
    private String content;
    //日期
    private String date;
    //开始时间
    private String startTime;
    //优先级
    private int priority;
    //任务状态
    private boolean state;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
