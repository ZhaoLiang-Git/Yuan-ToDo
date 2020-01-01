package com.example.schedulemanagement.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.entity.Type;
import com.example.schedulemanagement.event.TypeDeleteEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/12/27
 *     desc   :
 * </pre>
 */

public class TypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Type> typeList;
    private int type;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public TypeAdapter(List<Type> typeList,int type){
        this.typeList = typeList;
        this.type = type;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView titleTv;
        TextView numTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            titleTv = itemView.findViewById(R.id.titleTv);
            numTv = itemView.findViewById(R.id.numTv);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_type, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.titleTv.setText(typeList.get(i).getName());
        holder.numTv.setText(typeList.get(i).getNum()+"");
        //点击效果
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(typeList.get(i)));
        //长按删除
        holder.itemView.setOnLongClickListener(view -> {
            EventBus.getDefault().post(new TypeDeleteEvent(typeList.get(i), type == Constants.TYPE_CATEGORY));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public interface onItemClickListener{
        void onClick(Type type);
    }
}
