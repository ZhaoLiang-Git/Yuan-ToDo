package com.example.schedulemanagement.event;

import com.example.schedulemanagement.entity.Task;

/**
 * <pre>
 *     desc   :
 * </pre>
 */

public class DeleteEvent {
    int id;
    Task task;
    public DeleteEvent(Task task){
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
