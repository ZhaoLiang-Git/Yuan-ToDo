package com.example.schedulemanagement.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Schedule;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.event.DeleteEvent;
import com.example.schedulemanagement.event.UpdateStateEvent;
import com.example.schedulemanagement.widget.group.GroupRecyclerAdapter;

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

public class EventAdapter extends GroupRecyclerAdapter<String, Task> {
    private static final String TAG = "EventAdapter";

    private LinkedHashMap<String, List<Task>> map = new LinkedHashMap<>();
    private List<String> titles = new ArrayList<>();
    private Context mContext;
    private OnClickListener listener;
    private boolean isAll = false;



    public EventAdapter(Context context) {
        super(context);
        mContext = context;
    }
    public EventAdapter(Context context,boolean isAll) {
        this(context);
        this.isAll = isAll;
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.titleTv)
        TextView titleTv;
        @BindView(R.id.stateCheckBox)
        CheckBox stateCheckBox;
        @BindView(R.id.dateTv)
        TextView dateTv;
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
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Task item, int position) {
        EventViewHolder eventViewHolder = (EventViewHolder) holder;
        eventViewHolder.titleTv.setText(item.getTitle());
        if(isAll){
            eventViewHolder.dateTv.setText(item.getDate());
        }else {
            eventViewHolder.dateTv.setText(item.getStartTime());
        }
        if (item.isState()) {
            eventViewHolder.stateCheckBox.setChecked(true);
            eventViewHolder.titleTv.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else {
            eventViewHolder.stateCheckBox.setChecked(false);
            eventViewHolder.titleTv.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        eventViewHolder.stateCheckBox.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();
            item.setState(checked);
            EventBus.getDefault().post(new UpdateStateEvent(item));
        });
        //点击效果
        eventViewHolder.mView.setOnClickListener(view -> {
            listener.onClick(position, item.getTaskId());
        });
        eventViewHolder.mView.setOnLongClickListener(view -> {
            EventBus.getDefault().post(new DeleteEvent(item.getTaskId()));
            return true;
        });
    }

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

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(int position, int taskId);
    }
}
