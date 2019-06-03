package com.example.schedulemanagement.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.entity.Article;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.widget.group.GroupRecyclerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/29
 *     desc   : 事件适配器
 * </pre>
 */

public class EventAdapter extends GroupRecyclerAdapter<String, Event.EventBean> {
    private static final String TAG = "EventAdapter";

    LinkedHashMap<String, List<Event.EventBean>> map = new LinkedHashMap<>();
    List<String> titles = new ArrayList<>();

    public EventAdapter(Context context, String title) {
        super(context);
        notifyChanged(context,title,getEvent("吃饭","睡觉"));
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new EventViewHolder(mInflater.inflate(R.layout.item_list_done, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Event.EventBean item, int position) {
        Log.d(TAG, "onBindViewHolder: "+item.getContent());
        EventViewHolder eventViewHolder = (EventViewHolder) holder;
        eventViewHolder.titleTv.setText(item.getContent());
    }

    public static Event.EventBean get(String content) {
        Event.EventBean event = new Event.EventBean();
        event.setContent(content);
        return event;
    }

    public static Event getEvent(String contentDone,String contentUndone) {
        Event event = new Event();
        List<Event.EventBean> doneBean = new ArrayList<>();
        List<Event.EventBean> unDoneBean = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            doneBean.add(get(contentDone));
        }

        for (int i = 0; i < 10; i++) {
            unDoneBean.add(get(contentUndone));
        }

        event.setDone(doneBean);
        event.setUndone(unDoneBean);
        return event;
    }

    public void notifyChanged(Context context, String title, Event event){
        map.clear();
        titles.clear();
        map.put(title, event.getDone());
        titles.add(title);
        Log.d(TAG, "notifyChanged: "+titles.get(0));
        if (event.getUndone().size() != 0) {
            String done = context.getString(R.string.done);
            map.put(done, event.getUndone());
            titles.add(done);
        }
        resetGroups(map, titles);
    }
}
