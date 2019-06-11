package com.example.schedulemanagement.event;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/11
 *     desc   : 日程状态
 * </pre>
 */

public class UpdateStateEvent {
    int id;
    boolean checked;
    public UpdateStateEvent(int id, boolean checked){
        this.id = id;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public boolean isChecked() {
        return checked;
    }
}
