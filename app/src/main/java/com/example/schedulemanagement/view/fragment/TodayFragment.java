package com.example.schedulemanagement.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.EventAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.EventResponseCallback;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.event.GroupTitlesEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.view.activity.AddActivity;
import com.example.schedulemanagement.widget.group.GroupItemDecoration;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private static final String TAG = "TodayFragment";
    private EventAdapter mAdapter;
    private Event mEvent = new Event();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //添加日程成功重新显示日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddEvent(GroupTitlesEvent event) {
        recyclerView.notifyDataSetChanged();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }
    private void initRecyclerView(){
        mAdapter = new EventAdapter(getActivity(),recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new GroupItemDecoration<String, Event.EventBean>());
        recyclerView.setAdapter(mAdapter);
        //item点击效果
        mAdapter.setOnClickListener(position -> {
            AddActivity.startActivityByToday(getActivity(),Constants.UPDATE);
        });


        recyclerView.notifyDataSetChanged();
//        showDayEvent();
        showEvent();

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
     * 测试
     */
    private void showEvent() {
        mEvent = mAdapter.getEvent();
        mAdapter.notifyChanged(getActivity(), "待完成", mEvent);
        recyclerView.notifyDataSetChanged();
    }

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }
}
