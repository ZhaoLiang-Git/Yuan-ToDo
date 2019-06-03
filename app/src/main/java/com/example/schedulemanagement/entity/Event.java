package com.example.schedulemanagement.entity;

import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/27
 *     desc   : 日程显示
 * </pre>
 */

public class Event {

    /**
     * code : 0
     * data : {"done":[{"content":"去图书馆学习","id":2,"priority":"1","s_date":"2019-05-26","s_deadline":"11:30:00","s_starting":"08:30:00","status":"done","title":"学习","uid":1},{"content":"吃鸡扒饭","id":1,"priority":"3","s_date":"2019-05-26","s_deadline":"13:30:00","s_starting":"12:00:00","status":"done","title":"吃饭","uid":1}],"undone":[{"content":"测试优先级不填会怎么样","id":8,"priority":"3","s_date":"2019-05-26","s_starting":"02:02:02","status":"undone","title":"测试2","uid":1},{"content":"完善日程管理APP","id":4,"priority":"1","s_date":"2019-05-26","s_deadline":"23:59:59","s_starting":"07:00:00","status":"undone","title":"打码","uid":1},{"content":"回宿舍午睡","id":3,"priority":"2","s_date":"2019-05-26","s_deadline":"14:30:00","s_starting":"13:30:00","status":"undone","title":"午睡","uid":1},{"content":"测试截至时间能否为空","id":7,"priority":"3","s_date":"2019-05-26","s_starting":"18:00:00","status":"undone","title":"测试","uid":1},{"content":"完善日程管理APP","id":5,"priority":"1","s_date":"2019-05-26","s_deadline":"00:00:00","s_starting":"19:00:00","status":"undone","title":"打码2","uid":1},{"content":"期末复习","id":6,"priority":"1","s_date":"2019-05-26","s_starting":"19:00:00","status":"undone","title":"复习","uid":1}]}
     * msg : 查询行程成功
     */


    private List<EventBean> done;
    private List<EventBean> undone;

    public List<EventBean> getDone() {
        return done;
    }

    public void setDone(List<EventBean> done) {
        this.done = done;
    }

    public List<EventBean> getUndone() {
        return undone;
    }

    public void setUndone(List<EventBean> undone) {
        this.undone = undone;
    }

    public static class EventBean {
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
        private String s_deadline;
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

        public String getS_deadline() {
            return s_deadline;
        }

        public void setS_deadline(String s_deadline) {
            this.s_deadline = s_deadline;
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
}
