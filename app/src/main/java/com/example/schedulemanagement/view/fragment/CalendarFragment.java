package com.example.schedulemanagement.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.EventAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.EventResponseCallback;
import com.example.schedulemanagement.db.TaskDao;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.event.UpdateStateEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateFormatter;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.view.activity.AddActivity;
import com.example.schedulemanagement.view.activity.MainActivity;
import com.example.schedulemanagement.widget.group.GroupItemDecoration;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * <pre>
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
    @BindString(R.string.dialog_delete_text)
    String deleteText;
    @BindString(R.string.dialog_delete_title)
    String deleteTitle;

    private EventAdapter mAdapter;
    private Event mEvent = new Event();
    private String mTitle = "今天";
    private String mDateFormat;
    private TaskDao taskDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        taskDao = new TaskDao();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar();
        addListener();
        onClick();
        initRecyclerView();
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


    //在子线程更新日程
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdateEvent(UpdateStateEvent event) {
        if(taskDao.update(event.getEventBean())!=0){
            showDayEvent();
        }else {
            showFail("更新失败");
        }
    }



//    //http的更新方法
//    private void updateByHttp(UpdateStateEvent event) {
//        String status = event.isChecked()?"done":"undone";
//        OkHttpUtils.post()
//                .url(Constants.BASE_URL_MAIN+"updateTaskAndTag")
//                .addParams(Constants.Params_STATUS,status)
//                .addParams(Constants.Params_ID,event.getEventBean().getId()+"")
//                .addParams(Constants.Params_TITLE,event.getEventBean().getTitle())
//                .addParams(Constants.Params_CONTENT,event.getEventBean().getContent())
//                .addParams(Constants.Params_START,event.getEventBean().getS_starting())
//                .addParams(Constants.Params_DATE,event.getEventBean().getS_date())
//                .addParams(Constants.Params_PRIORITY,event.getEventBean().getPriority())
//                .build()
//                .execute(new BaseResponseCallback<Event>() {
//                    @Override
//                    public void onResponse(BaseResponse response, int id) {
//                        if(response.getCode() == Constants.CODE_SUCCESS){
//                            showDayEvent();
//                        }else {
//                            CommonUtils.showToast("修改日程失败");
//                        }
//                    }
//                });
//    }


    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        currentDayTv.setText(String.valueOf(calendarView.getCurDay()));
        monthDayTv.setText(calendarView.getCurMonth() + "月" + calendarView.getCurDay() + "日");
        yearTv.setText(calendarView.getCurYear() + "");
        mDateFormat = DateUtils.dateFormat(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay());
        mTitle = "今天";
    }

    //添加监听日历相关事件
    private void addListener(){
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                System.out.println("月份改变事件！年：" + year+"月："+month);
//                List<Calendar> li= calendarView.getCurrentMonthCalendars();
//                Map<String, Calendar> eventValue = new HashMap<String, Calendar>();
//                eventValue.put("假",li.get(5));
//                calendarView.addSchemeDate(eventValue);
//                Calendar xx = calendarView.getSelectedCalendar();
//                //xx.addScheme();
//                xx.addScheme(0,"假期");
//                calendarView.addSchemeDate(xx);

//                for (int i = 0; i < li.size(); i++) {
//                    System.out.println("输出获取到的 日" + li.get(i).getDay());
//                    System.out.println("输出获取到的 月" + li.get(i).getMonth());
//                }
                //在日历中显示日程标记先判断切换到那年那月，然后获取该月的所有月份日期，然后在数据库中也获取该月的所有数据然后比对匹配的话给有日程的日期批量添加标记
//                List<Calendar> li= calendarView.getCurrentMonthCalendars();
//                Map<String, Calendar> eventValue = new HashMap<String, Calendar>();
//                for (int i = 0; i < li.size(); i++) {
//                    eventValue.put("假",li.get(i));
//                }

                // calendarView.setSchemeDate()
            }
        });
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
                ((MainActivity) getActivity()).setDateText(
                        monthDay,
                        DateUtils.dateFormat(calendar.getYear(), calendar.getMonth(), calendar.getDay()),
                        DateFormatter.getTimeMillNew(calendar.getYear(), calendar.getMonth(), calendar.getDay()));
                //查询日程
                mTitle = monthDay;
                mDateFormat = DateUtils.dateFormat(calendar.getYear(), calendar.getMonth(), calendar.getDay());
                showDayEvent();
            }
        });
        //点击今天
        todayIv.setOnClickListener(view -> {
            calendarView.scrollToCurrent();
            mAdapter.notifyDataSetChanged();
            mDateFormat = DateUtils.dateFormat(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay());
            mTitle = "今天";
            showDayEvent();
        });

        //打开年份选择月份界面
        monthDayTv.setOnClickListener(view -> {
            calendarView.showYearSelectLayout(calendarView.getCurYear());
        });
    }

    private void initRecyclerView() {
        mAdapter = new EventAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new GroupItemDecoration<String, Task>());
        recyclerView.setAdapter(mAdapter);
        //子项跳转活动
        mAdapter.setOnClickListener((position,taskId) -> {
            AddActivity.startActivityByUpdate(getActivity(),taskId);
        });
        showDayEvent();
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
     */
    private void showDayEventByHttp() {
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN + "show")
                .addParams("s_date", mDateFormat)
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
                            showSuccess(mTitle, response.getData());
                        } else {
                            showFail(response.getMsg());
                        }
                    }
                });
    }

    //获取数据库任务表
    private void showDayEvent(){
        new Thread(()->{
            Event event = taskDao.queryTaskByDate(mDateFormat);
            if(event != null){
                showSuccess(mTitle,event);
            }
        }).start();
    }

    /**
     * 查询日程成功
     *
     * @param title 标题
     * @param event 事件
     */
    private void showSuccess(String title, Event event) {
        getActivity().runOnUiThread(() -> {
            mAdapter.notifyChanged(getActivity(), title, event);
            recyclerView.notifyDataSetChanged();
        });
    }

    /**
     * 查询日程失败
     */
    private void showFail(String msg) {
        CommonUtils.showToast(getActivity(), msg);
    }

}
