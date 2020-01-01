package com.example.schedulemanagement.event;

import com.example.schedulemanagement.entity.Type;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2020/01/01
 *     desc   : 删除分类,标签等信息
 * </pre>
 */

public class TypeDeleteEvent {
    private Type type;
    boolean isCategory;

    public TypeDeleteEvent(Type type, boolean isCategory) {
        this.type = type;
        this.isCategory = isCategory;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public boolean isCategory() {
        return isCategory;
    }

    public void setCategory(boolean category) {
        isCategory = category;
    }
}
