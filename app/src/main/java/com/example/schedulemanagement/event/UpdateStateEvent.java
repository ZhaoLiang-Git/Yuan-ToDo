package com.example.schedulemanagement.event;

import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/11
 *     desc   : 日程状态
 * </pre>
 */

public class UpdateStateEvent {
    Task eventBean;
    public UpdateStateEvent(Task eventBean){
        this.eventBean = eventBean;
    }

    public Task getEventBean() {
        return eventBean;
    }
}
