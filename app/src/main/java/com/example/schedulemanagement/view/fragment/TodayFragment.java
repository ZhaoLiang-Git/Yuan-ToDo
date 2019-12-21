package com.example.schedulemanagement.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.EventAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.callback.EventResponseCallback;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.event.DeleteEvent;
import com.example.schedulemanagement.event.UpdateStateEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.view.activity.AddActivity;
import com.example.schedulemanagement.widget.ConfirmDialog;
import com.example.schedulemanagement.widget.group.GroupItemDecoration;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/28
 *     desc   : 今日日程模块
 * </pre>
 */

public class TodayFragment extends Fragment {
    @BindView(R.id.recyclerView)
    GroupRecyclerView recyclerView;
    @BindString(R.string.dialog_delete_text)
    String deleteText;
    @BindString(R.string.dialog_delete_title)
    String deleteTitle;

    private static final String TAG = "TodayFragment";
    private EventAdapter mAdapter;
    private Event mEvent = new Event();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    //添加日程成功重新显示日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddEvent(AddEvent event) {
        showDayEvent();
    }

    //删除日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteEvent(DeleteEvent event) {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setOnClickListener(() -> {
            delete(event.getId());
        });
        dialog.setText(deleteText).setTitle(deleteTitle).show();
    }

    //更新日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateStateEvent event) {
        String status = event.isChecked()?"done":"undone";
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN+"update")
                .addParams(Constants.Params_STATUS,status)
                .addParams(Constants.Params_ID,event.getEventBean().getId()+"")
                .addParams(Constants.Params_TITLE,event.getEventBean().getTitle())
                .addParams(Constants.Params_CONTENT,event.getEventBean().getContent())
                .addParams(Constants.Params_START,event.getEventBean().getS_starting())
                .addParams(Constants.Params_DATE,event.getEventBean().getS_date())
                .addParams(Constants.Params_PRIORITY,event.getEventBean().getPriority())
                .build()
                .execute(new BaseResponseCallback<Event>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if(response.getCode() == Constants.CODE_SUCCESS){
                            showDayEvent();
                        }else {
                            CommonUtils.showToast("修改日程失败");
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }
    private void initRecyclerView(){
        mAdapter = new EventAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new GroupItemDecoration<String, Event.EventBean>());
        recyclerView.setAdapter(mAdapter);
        //item点击效果
        mAdapter.setOnClickListener((position,schedule) -> {
            AddActivity.startActivityByUpdate(getActivity(),Constants.UPDATE,schedule);
        });
        recyclerView.notifyDataSetChanged();
        showDayEvent();
//        showEvent();+


    }

    /**
     * 网络获取日程
     */
    private void showDayEvent() {
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN + "show")
                .addParams("s_date", DateUtils.getTodayDate())
                .build()
                .execute(new EventResponseCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonUtils.showToast(getActivity(), "网络错误：" + e.toString());
                        Log.d(TAG, "onError: " + e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(BaseResponse<Event> response, int id) {
                        if (response.getCode() == Constants.CODE_SUCCESS) {
                            showSuccess(response.getData());
                        } else {
                            showFail(response.getMsg());
                        }
                    }
                });
    }
    /**
     * 删除日程
     */
    private void delete(int id){
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN+"delete")
                .addParams(Constants.Params_ID,id+"")
                .build()
                .execute(new BaseResponseCallback<Event>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if(response.getCode() == 0){
                            showDeleteSuccess();
                        }else {
                            CommonUtils.showToast(response.getMsg());
                        }
                    }
                });
    }
    /**
     * 查询日程成功
     *
     * @param event 事件
     */
    private void showSuccess(Event event) {
        getActivity().runOnUiThread(() -> {
            mAdapter.notifyChanged(getActivity(), "待完成", event);
            recyclerView.notifyDataSetChanged();
        });
    }

    /**
     * 查询日程失败
     */
    private void showFail(String msg) {
        CommonUtils.showToast(getActivity(), msg);
    }

    /**
     * 删除日程成功
     */
    private void showDeleteSuccess(){
        getActivity().runOnUiThread(()->{
            EventBus.getDefault().post(new AddEvent());
            CommonUtils.showToast("删除日程成功");
        });

    }

    /**
     * 测试
     */
//    private void showEvent() {
//        mEvent = mAdapter.getEvent();
//        mAdapter.notifyChanged(getActivity(), "待完成", mEvent);
//        recyclerView.notifyDataSetChanged();
//    }

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }
}
