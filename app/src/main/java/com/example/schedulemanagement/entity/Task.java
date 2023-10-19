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

    //日程id，必须要有，主键
    private long id;
    //地点
    private String location;
    //备注
    private String remark;
    //日程开始时间，已经格式化了
    //private String startTime;
    //日程结束时间，已经格式化了
    private String endTime;
    //日程重复模式，0是单个重复（永不、每天、每周...），1是自定义重复（周日、周一...）
    private int repeatMode;
    //日程重复id，repeatMode=0时：0永不，1每天，2每周，3每月，4每年；repeatMode=1时：0周日，1周一，2周二，3周三，4周四，5周五，6周六
    private String repeatId;
    //日程提醒id 0:无，1:日程发生时，2:5分钟前，3:15分钟前，4:30分钟前，5:1小时前，6：2小时前，7:1天前，8:2天前
    private int remindId;
    //是否全天模式 0:false;1:true
    private int allDay;
    //开始的日期，年月日，已经格式化了
   // private String date;
    //日程提醒时间，long类型
    private long alertTime;
    //日程结束时间，long类型
    private long endTimeMill;
    //用于日程列表排序，但默认用alertTime排序，该字段暂不存入数据库
    private long sortTimeMill;
    //跳转联系人详情页uri，该字段暂不存入数据库
    private String lookUpUri;
    /**
     * 是否是同一日期下的后续日程
     */
    private boolean isBehind;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getRemindId() {
        return remindId;
    }

    public void setRemindId(int remindId) {
        this.remindId = remindId;
    }

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public long getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }

    public long getEndTimeMill() {
        return endTimeMill;
    }

    public void setEndTimeMill(long endTimeMill) {
        this.endTimeMill = endTimeMill;
    }

    public boolean isBehind() {
        return isBehind;
    }

    public void setBehind(boolean behind) {
        isBehind = behind;
    }

    public long getSortTimeMill() {
        return sortTimeMill;
    }

    public void setSortTimeMill(long sortTimeMill) {
        this.sortTimeMill = sortTimeMill;
    }

    public String getLookUpUri() {
        return lookUpUri;
    }

    public void setLookUpUri(String lookUpUri) {
        this.lookUpUri = lookUpUri;
    }
}
