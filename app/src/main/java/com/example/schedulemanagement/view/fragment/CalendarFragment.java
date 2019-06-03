package com.example.schedulemanagement.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.ArticleAdapter;
import com.example.schedulemanagement.adapter.EventAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.entity.Article;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.view.activity.MainActivity;
import com.example.schedulemanagement.widget.group.GroupItemDecoration;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.DateFormat;
import java.util.List;

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
 *     desc   : 日历模块
 * </pre>
 */

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    @BindView(R.id.monthDayTv)
    TextView monthDayTv;
    @BindView(R.id.yearTv)
    TextView yearTv;
    @BindView(R.id.lunarTv)
    TextView lunarTv;
    @BindView(R.id.currentDayTv)
    TextView currentDayTv;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.recyclerView)
    GroupRecyclerView recyclerView;
    @BindView(R.id.calendarLayout)
    CalendarLayout calendarLayout;
    @BindView(R.id.todayIv)
    ImageView todayIv;

    private EventAdapter mAdapter;
    private Event mEvent = new Event();
    private String mTitle = "今天";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar();
        onClick();
        initRecyclerView();
    }



    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        currentDayTv.setText(String.valueOf(calendarView.getCurDay()));
        monthDayTv.setText(calendarView.getCurMonth() + "月" + calendarView.getCurDay() + "日");
        yearTv.setText(calendarView.getCurYear() + "");
    }

    private void onClick() {
        //监控日历点击事件
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                String monthDay = calendar.getMonth() + "月" + calendar.getDay() + "日";
                monthDayTv.setText(monthDay);
                yearTv.setText(calendar.getYear() + "");
                if (calendar.getDay() == calendarView.getCurDay()) {
                    monthDay = "今天";
                    lunarTv.setText("今天");
                } else {
                    lunarTv.setText(calendar.getLunar());
                }
                ((MainActivity)getActivity()).setDateText(
                        monthDay,
                        DateUtils.dateFormat(calendar.getYear(),calendar.getMonth(),calendar.getDay()));
                showEvent(monthDay);
                //查询日程
//                showDayEvent(monthDay,calendar.getYear(),calendar.getMonth(),calendar.getDay());
            }
        });
        //点击今天
        todayIv.setOnClickListener(view -> {
            calendarView.scrollToCurrent();
            mTitle = "5月20日";
            mAdapter.notifyDataSetChanged();
          showDayEvent("今天",calendarView.getCurYear(),calendarView.getCurMonth(),calendarView.getCurDay());
        });
    }

    private void initRecyclerView(){
        mAdapter = new EventAdapter(getActivity(),mTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new GroupItemDecoration<String, Event.EventBean>());
        recyclerView.setAdapter(mAdapter);
        recyclerView.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * @return 返回碎片实例
     */
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    /**
     * 网络获取日程
     * @param date 5月20日这种格式的日期
     * @param year 年
     * @param month 月
     * @param day 日
     */
    private void showDayEvent(String date,int year,int month,int day){
        OkHttpUtils.post()
                .url(Constants.BASE_URL+"show")
                .addParams("s_date", DateUtils.dateFormat(year,month,day))
//                .addParams("s_date","2019-05-26")
                .build()
                .execute(new BaseResponseCallback<Event>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonUtils.showToast(getActivity(),"网络错误："+e.toString());
                        Log.d(TAG, "onError: "+e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(BaseResponse<Event> response, int id) {
                        if(response.getCode() == Constants.CODE_SUCCESS){
                            showSuccess(date,response.getData());
                        }else {
                            showFail(response.getMsg());
                        }
                    }
                });
    }

    /**
     * 查询日程成功
     * @param title 标题
     * @param event 事件
     */
    private void showSuccess(String title,Event event){
        getActivity().runOnUiThread(()->{
            mTitle = title;
            mEvent = event;
            mAdapter.notifyDataSetChanged();
        });
    }

    /**
     * 查询日程失败
     */
    private void showFail(String msg){
        CommonUtils.showToast(getActivity(),msg);
    }

    /**
     * 测试
     */
    private void showEvent(String title){
        if(title.equals("今天")){
            mEvent = mAdapter.getEvent("唱跳","rap");
            Log.d(TAG, "showEvent: "+title);
        }
        mEvent = mAdapter.getEvent("打篮球","吃关东煮");
        mAdapter.notifyChanged(getActivity(),title,mEvent);
    }
}
