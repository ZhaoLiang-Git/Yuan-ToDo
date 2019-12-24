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


    private List<Task> done;
    private List<Task> undone;

    public List<Task> getDone() {
        return done;
    }

    public void setDone(List<Task> done) {
        this.done = done;
    }

    public List<Task> getUndone() {
        return undone;
    }

    public void setUndone(List<Task> undone) {
        this.undone = undone;
    }
}
