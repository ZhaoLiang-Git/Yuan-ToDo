package com.example.schedulemanagement.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Schedule;
import com.example.schedulemanagement.event.DeleteEvent;
import com.example.schedulemanagement.event.UpdateStateEvent;
import com.example.schedulemanagement.widget.group.GroupRecyclerAdapter;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    private Context mContext;
    private OnClickListener listener;



    public EventAdapter(Context context) {
        super(context);
        mContext =context;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.stateCheckBox)
        CheckBox stateCheckBox;
        View mView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);

        }

    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new EventViewHolder(mInflater.inflate(R.layout.item_list_done, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Event.EventBean item, int position) {
        EventViewHolder eventViewHolder = (EventViewHolder) holder;
        eventViewHolder.titleTv.setText(item.getTitle());
        if(item.getStatus().equals("done")){
            eventViewHolder.stateCheckBox.setChecked(true);
            eventViewHolder.titleTv.setTextColor(mContext.getResources().getColor(R.color.gray));
        }else {
            eventViewHolder.stateCheckBox.setChecked(false);
            eventViewHolder.titleTv.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        eventViewHolder.stateCheckBox.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();
            EventBus.getDefault().post(new UpdateStateEvent(item,checked));
        });
        //点击效果
        eventViewHolder.mView.setOnClickListener(view -> {
            Schedule schedule = new Schedule();
            schedule.setId(item.getId());
            schedule.setTitle(item.getTitle());
            schedule.setContent(item.getContent());
            schedule.setPriority(item.getPriority());
            schedule.setS_date(item.getS_date());
            schedule.setStatus(item.getStatus());
            schedule.setS_starting(item.getS_starting());
            listener.onClick(position,schedule);
        });
        eventViewHolder.mView.setOnLongClickListener(view -> {
            EventBus.getDefault().post(new DeleteEvent(item.getId()));
            return true;
        });
    }
/*
测试方法
 */
//    public static Event.EventBean get(String title) {
//        Event.EventBean event = new Event.EventBean();
//        event.setTitle(title);
//        event.setStatus("undone");
//        return event;
//    }
//    public static Event.EventBean getDone(String title) {
//        Event.EventBean event = new Event.EventBean();
//        event.setTitle(title);
//        event.setStatus("done");
//        return event;
//    }
//
//    public static Event getEvent() {
//        Event event = new Event();
//        List<Event.EventBean> doneBean = new ArrayList<>();
//        List<Event.EventBean> unDoneBean = new ArrayList<>();
//        unDoneBean.add(get("洗澡"));
//        unDoneBean.add(get("睡觉"));
//        unDoneBean.add(get("软工考试"));
//        unDoneBean.add(get("复习"));
//        doneBean.add(getDone("吃饭"));
//        doneBean.add(getDone("上课"));
//        doneBean.add(getDone("对接"));
//        doneBean.add(getDone("打豆豆"));
//        doneBean.add(getDone("听歌"));
//        doneBean.add(getDone("唱歌"));
//        doneBean.add(getDone("嗨歌"));
//        doneBean.add(getDone("周杰伦"));
//        doneBean.add(getDone("暗号"));
//
//        event.setDone(doneBean);
//        event.setUndone(unDoneBean);
//        return event;
//    }
//    public static Event getEvent1() {
//        Event event = new Event();
//        List<Event.EventBean> doneBean = new ArrayList<>();
//        List<Event.EventBean> unDoneBean = new ArrayList<>();
//        unDoneBean.add(get("洗澡"));
//        unDoneBean.add(get("睡觉"));
//        unDoneBean.add(get("复习"));
//        doneBean.add(getDone("吃饭"));
//        doneBean.add(getDone("上课"));
//        doneBean.add(getDone("对接"));
//        doneBean.add(getDone("打豆豆"));
//        doneBean.add(getDone("听歌"));
//        doneBean.add(getDone("唱歌"));
//        doneBean.add(getDone("嗨歌"));
//        doneBean.add(getDone("周杰伦"));
//        doneBean.add(getDone("暗号"));
//        doneBean.add(getDone("软工考试"));
//
//        event.setDone(doneBean);
//        event.setUndone(unDoneBean);
//        return event;
//    }

    public void notifyChanged(Context context, String title, Event event) {
        map.clear();
        titles.clear();
        map.put(title, event.getUndone());
        titles.add(title);
        if (event.getDone().size() != 0) {
            String done = context.getString(R.string.done);
            map.put(done, event.getDone());
            titles.add(done);
        }
        resetGroups(map, titles);
    }

    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }

    public interface OnClickListener{
        void onClick(int position, Schedule schedule);
    }
}
