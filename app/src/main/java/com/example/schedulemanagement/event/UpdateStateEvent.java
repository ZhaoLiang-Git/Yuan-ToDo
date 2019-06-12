package com.example.schedulemanagement.event;

import com.example.schedulemanagement.entity.Event;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/11
 *     desc   : 日程状态
 * </pre>
 */

public class UpdateStateEvent {
    Event.EventBean eventBean;
    boolean checked;
    public UpdateStateEvent(Event.EventBean eventBean, boolean checked){
        this.eventBean = eventBean;
        this.checked = checked;
    }

    public Event.EventBean getEventBean() {
        return eventBean;
    }

    public boolean isChecked() {
        return checked;
    }
}
