package com.example.schedulemanagement.event;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/10
 *     desc   :
 * </pre>
 */

public class DeleteEvent {
    int id;
    public DeleteEvent(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
