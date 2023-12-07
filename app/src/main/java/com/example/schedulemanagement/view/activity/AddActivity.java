package com.example.schedulemanagement.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eebbk.common.utils.datatimewheel.util.DatechooseConfig;
import com.eebbk.common.utils.datatimewheel.util.ModeConst;
import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.ConstData;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.app.ScheduleConst;
import com.example.schedulemanagement.db.TaskDao;
import com.example.schedulemanagement.db.TypeDao;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.event.TypeEvent;
import com.example.schedulemanagement.utils.CalendarEvent;
import com.example.schedulemanagement.utils.CalendarProviderManager;
import com.example.schedulemanagement.utils.CalendarUtils;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateFormatter;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.utils.MaxLengthWatcher;
import com.example.schedulemanagement.utils.lunar.LunarCalendar;
import com.example.schedulemanagement.view.activity.customreminder.ReminderActivity;
import com.example.schedulemanagement.view.activity.customrepeat.RepeatActivity;
import com.example.schedulemanagement.view.activity.lunarCalendarReminder.LunarCalendarReminderActivity;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import com.eebbk.common.utils.datatimewheel.DateChooseWheelViewDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.statusCheckBox)
    CheckBox statusCheckBox;
    @BindView(R.id.dateTv)
    TextView dateTv;
    @BindView(R.id.priorityIv)
    ImageView priorityIv;
    @BindView(R.id.eventCl)
    ConstraintLayout eventCl;
    @BindView(R.id.titleTv)
    EditText titleTv;
    @BindView(R.id.descriptionEdit)
    EditText descriptionEdit;
    @BindString(R.string.dialog_ok)
    String okText;
    @BindString(R.string.dialog_cancel)
    String cancelText;
    @BindColor(R.color.gray)
    int grayChecked;
    @BindColor(R.color.colorAccent)
    int blueUnChecked;

    private Context mContext;
  //  private EditText mEtTitle;
    private EditText mEtLocation;
    private EditText mEtRemark;
    private TextView mTvStart;
    private TextView mTvEnd;
    private TextView mTvRepeat;
    private TextView mTvReminder;
    private Switch mSwitchView;
    private RelativeLayout mAllDaySwitchLayout;
    private Switch mNonliSwitchView;
    private RelativeLayout mNonliSwitchLayout;
    private RelativeLayout rl_start;
    private RelativeLayout rl_end;
    private RelativeLayout rl_repeat;
    private RelativeLayout rl_reminder;

    private static final String TAG = "AddActivity";

    private TimePickerDialog timePickerDialog;
    private String mDateText;
    private String mDateFormat;
    private String mTitle;
    private String mContent;
    private String mStartTime;
    private boolean mState;
    private int mNowHour;
    private int mNowMinute;
    private int mPriority = 0;
    private int[] priorityPic ={R.drawable.ic_priority_low,R.drawable.ic_priority_medium,
            R.drawable.ic_priority_high};
    //任务号
    private int mTaskId;
    //分类号
    private int mCategoryId;
    //标签号
    private List<Integer> mTagIdList = new ArrayList<>();

    //更新时，初始的标签号
    private List<Integer> mOrignTagIdList = new ArrayList<>();

    //分类位置
    private int mCategoryIndex = -1;
    //标签位置集合
    private List<Integer> mTagIndexList = null;
    private TaskDao taskDao;
    private TypeDao typeDao;

    //记录时间选择器的日期
    private Calendar mTimePickCalendar;
    //日程保存的日期时间，查找所有日程的关键字，保存格式为yyyy年MM月dd日
    private String mStartDate;
    //0全天关闭，1全天打开
    private int isAllDay = 0;
    //0农历关闭，1农历打开
    private int isNonli = 0;
    private CharSequence timeFormat = "yyyy年M月d日  HH:mm";
    private CharSequence allDayTimeFormat = "yyyy年M月d日";
    //当前编辑的是否结束日期
    private boolean isEndDate = false;
    private String str = "开始";
    private long mAlertTime;
    private long mEndTimeMill;

    //全天打开的时间格式
    private long mAllDayStartTime;
    private long mAllDayEndTime;
    private ArrayList<Integer> repeatList = new ArrayList<>();
    //判断是否自定义
    private boolean isCustomRepeat = false;
    private String mRepeatId = "0";
    private int mReminderId = 3;
    private int mRepeatMode = 0;
    private AlertDialog mDatePickerAlertDialog;
    private DateChooseWheelViewDialog mDateChooseWheelViewDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        taskDao = new TaskDao();
        typeDao = new TypeDao();
        initView();
        initData();
        initTimeDialog();
        onClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                if(mTaskId == Constants.ADD){
                    add();
                }else {
                    update();
                }
                break;
//            case R.id.menu_category:
//                selectCategory();
//                break;
//            case R.id.menu_tag:
//                selectTag();
//                break;
        }
        return true;
    }

    //选择分类
    private void selectCategory(){
        new Thread(()->{
            //如果当前没有分类，则新建一个默认分类
            if(typeDao.isEmptyOfCategory()) typeDao.insert("默认",true);

            //如果是一开始设置分类就得在数据库中获取分类的位置
            if(mCategoryIndex == -1 && mCategoryId!=0) mCategoryIndex = typeDao.queryCategoryIndex(mCategoryId);
            //查询分类列表
            HashMap<String,Integer> map = typeDao.queryCategory();
            showCategoryDialog(map);
        }).start();
    }

    //选择标签
    private void selectTag(){
        new Thread(()-> {
            //如果是一开始设置分类就得在数据库中获取分类的位置
            if(mTagIndexList == null) {
                mOrignTagIdList = typeDao.queryTagByTaskId(mTaskId);
                if(mOrignTagIdList == null) {
                    mTagIndexList = new ArrayList<>();
                }else {
                    mTagIndexList = typeDao.queryTagIndexList(mOrignTagIdList);
                }
            }
            //查询分类列表
            HashMap<String,Integer> map = typeDao.queryTag();
            showTagDialog(map);
        }).start();
    }

    //显示分类弹窗
    private void showCategoryDialog(HashMap<String,Integer> map){
        runOnUiThread(()->
            new MaterialDialog.Builder(this)
                    .title("分类")
                    .titleGravity(GravityEnum.CENTER) //标题居中
                    .items(map.keySet())
                    .itemsCallbackSingleChoice(mCategoryIndex, (dialog, itemView, which, text) -> {
                        mCategoryIndex = which;
                        mCategoryId = map.get(text);
                        CommonUtils.showToast("成功选择"+text);
                        return true;
                    })
                    .neutralText("新建")
                    .positiveText("确定")
                    .negativeText("取消")
                    .negativeColor(getResources().getColor(R.color.gray))
                    .onNeutral((dialog, which) -> showAddDialog(true))
                    .autoDismiss(true)
                    .show()
        );
    }
    //显示标签弹窗
    private void showTagDialog(HashMap<String,Integer> map){
        runOnUiThread(()->
                new MaterialDialog.Builder(this)
                        .title("选择标签")
                        .titleGravity(GravityEnum.CENTER) //标题居中
                        .items(map.keySet())
                        .itemsCallbackMultiChoice(mTagIndexList.toArray(new Integer[mTagIndexList.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                mTagIndexList.clear();
                                mTagIdList.clear();
                                //添加到标签位置集合
                                mTagIndexList.addAll(Arrays.asList(which));

                                //添加到标签号集合
                                for(CharSequence c:text){
                                    mTagIdList.add(map.get(c));
                                }
                                return false;
                            }
                        })
                        .neutralText("新建")
                        .positiveText("确定")
                        .negativeText("取消")
                        .negativeColor(getResources().getColor(R.color.gray))
                        .onNeutral((dialog, which) -> showAddDialog(false))
                        .autoDismiss(true)
                        .show()
        );
    }


    //显示新建分类弹窗
    private void showAddDialog(boolean isCategory){
        new MaterialDialog.Builder(this)
                .title(isCategory?"新建分类":"新建标签")
                .titleGravity(GravityEnum.CENTER)
                .input("输入名称", "", (dialog, input) ->
                        new Thread(()->{
                    if(typeDao.insert(input.toString(),isCategory)==-1){
                        showToast(isCategory?"建立失败，分类名已存在":"建立失败，标签名已存在");
                    }else {
                        EventBus.getDefault().post(new TypeEvent());//告诉分类，标签值改变
                        showToast("建立成功");
                    }
                }).start())
                .positiveText("确定")
                .neutralText("取消")
                .show();
    }



    /**
     * 初始化UI
     */
    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtLocation = (EditText) findViewById(R.id.et_location);
        mEtRemark = (EditText) findViewById(R.id.et_remark);

      //  mEtTitle.addTextChangedListener(new MaxLengthWatcher(AddActivity.this, ScheduleConst.MAX_SCHEDULE_TITLE_INPUT_LENGTH));
        mEtLocation.addTextChangedListener(new MaxLengthWatcher(AddActivity.this, ScheduleConst.MAX_SCHEDULE_TITLE_INPUT_LENGTH));
        mEtRemark.addTextChangedListener(new MaxLengthWatcher(AddActivity.this, ScheduleConst.MAX_SCHEDULE_REMARK_INPUT_LENGTH));

        mTvStart = (TextView) findViewById(R.id.tv_start);
        mTvEnd = (TextView) findViewById(R.id.tv_end);
        mTvRepeat = (TextView) findViewById(R.id.tv_repeat);
        mTvReminder = (TextView) findViewById(R.id.tv_reminder);
        mSwitchView = (Switch) findViewById(R.id.tv_switch);
        mAllDaySwitchLayout = (RelativeLayout) findViewById(R.id.rl_all_day_switch);
        mNonliSwitchView = (Switch) findViewById(R.id.tv_nonli_switch);
        mNonliSwitchLayout = (RelativeLayout) findViewById(R.id.rl_nonli_switch);
        rl_start = (RelativeLayout) findViewById(R.id.rl_start);
        rl_end = (RelativeLayout) findViewById(R.id.rl_end);
        rl_repeat = (RelativeLayout) findViewById(R.id.rl_repeat);
        rl_reminder = (RelativeLayout) findViewById(R.id.rl_reminder);
        mSwitchView.setClickable(false);
        mNonliSwitchView.setClickable(false);
    }

    /**
     * 初始化时间选择dialog
     */
    private void initTimeDialog() {
        timePickerDialog = TimePickerDialog.newInstance(
                AddActivity.this,
                mNowHour,
                mNowMinute,
                true);
        timePickerDialog.setCancelText(cancelText);
        timePickerDialog.setOkText(okText);
    }

    /**
     * 点击事件
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void onClick() {
        //返回
        toolbar.setNavigationOnClickListener(view -> finish());
        //日期选择器
        //eventCl.setOnClickListener(view -> timePickerDialog.show(getFragmentManager(),"timePickerDialog"));
        //状态选择
        statusCheckBox.setOnClickListener(view -> {
            mState = ((CheckBox)view).isChecked();
//            if(mState){
//                dateTv.setTextColor(grayChecked);
//            }else {
//                dateTv.setTextColor(blueUnChecked);
//            }
        });
        //优先级选择
        priorityIv.setOnClickListener(view -> {
            android.support.v7.app.AlertDialog dialog = new  android.support.v7.app.AlertDialog
                    .Builder(this)
                    .setSingleChoiceItems(
                            new String[]{"低", "中", "高"},
                            mPriority,
                            (dialog1, which)
                            -> {
                                priorityIv.setImageDrawable(getDrawable(priorityPic[which]));
                                mPriority = which;
                                dialog1.cancel();
                            }).create();
            dialog.show();
        });

        mAllDaySwitchLayout.setOnClickListener(view -> {
            //是否打开全天
            setAllDayOpen(!mSwitchView.isChecked());
            mSwitchView.setChecked(!mSwitchView.isChecked());
        });

        mNonliSwitchLayout.setOnClickListener(view -> {
            //是否打开农历
            mRepeatMode = 0;
            mRepeatId  = "0";
            mTvRepeat.setText("永不");
            setNonliOpen(!mNonliSwitchView.isChecked());
            mNonliSwitchView.setChecked(!mNonliSwitchView.isChecked());
        });
        rl_start.setOnClickListener(view -> {
            if (isDatePickerDialogShowing()) {
                return;
            }
            str = "开始";
            isEndDate = false;
            showDatePickerDialog(isAllDay());
           // InputManagerUtil.closeSoftInput(AddScheduleActivity.this);
        });

        rl_end.setOnClickListener(view -> {
            if (isDatePickerDialogShowing()) {
                return;
            }
            str = "结束";
            isEndDate = true;
            showDatePickerDialog(isAllDay());
           // InputManagerUtil.closeSoftInput(AddScheduleActivity.this);
        });
        rl_repeat.setOnClickListener(view->{
            if (1 == isNonli) {
                scheduleLunarCalendarRemindEdit();
            } else {
                scheduleRepeat();
            }
        });

        rl_reminder.setOnClickListener(view->{
            scheduleRemindEdit();
        });
    }

    /**
     * 初始化数据
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initData(){
        mTaskId = getIntent().getIntExtra(Constants.KEY_ID,Constants.ADD);
        if(mTaskId == Constants.ADD){//添加数据
            //分类位置为默认
            mCategoryIndex = 0;
            //标签位置默认为空
            mTagIndexList = new ArrayList<>();

            mDateText = getIntent().getStringExtra(Constants.KEY_ADD_DATE);
            mDateFormat = getIntent().getStringExtra(Constants.KEY_ADD_DATE_FORMAT);
            long selectTime = getIntent().getLongExtra(Constants.KEY_SELECT_DATA, System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            mNowHour = calendar.get(Calendar.HOUR_OF_DAY);
            mNowMinute = calendar.get(Calendar.MINUTE);
            //mStartTime = DateUtils.formatTime(mNowHour) +":" +DateUtils.formatTime(mNowMinute) ;
            //dateTv.setText(mDateText+"，"+DateUtils.formatTime(mNowHour)+"："+DateUtils.formatTime(mNowMinute));

//            //获取选中日期的时间
//            Intent it = getIntent();
//            long selectTime = it.getLongExtra(ConstData.INTENT_SCHEDULE_SELECT_DATE_KEY, System.currentTimeMillis());
            System.out.println("获取选中日期的时间"+ selectTime);
            Calendar selectCalendar = Calendar.getInstance();
            selectCalendar.setTimeInMillis(selectTime);
            int year = selectCalendar.get(Calendar.YEAR);
            int month = selectCalendar.get(Calendar.MONTH);
            int day = selectCalendar.get(Calendar.DAY_OF_MONTH);
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
            //,hour ,minute
            mTimePickCalendar = Calendar.getInstance();
            mTimePickCalendar.set(year, month, day );
            mStartDate = (String) DateFormat.format("yyyy年MM月dd日", mTimePickCalendar);
            mAlertTime = mTimePickCalendar.getTimeInMillis();
            mTvStart.setText(DateFormat.format(timeFormat, mTimePickCalendar));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(year, month, day);
            endCalendar.add(Calendar.HOUR_OF_DAY, +1);
            mEndTimeMill = endCalendar.getTimeInMillis();
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));

        }else {//修改数据
            show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        //时间选择器 暂时弃用
        //dateTv.setText(mDateText+"，"+ DateUtils.formatTime(hourOfDay)+"："+DateUtils.formatTime(minute));
        //mStartTime = DateUtils.formatTime(hourOfDay) +":" +DateUtils.formatTime(minute);
    }

    /**
     * 提交添加日程的请求
     */
    private void add(){
        mTitle =titleTv.getText().toString().trim();
        mContent = descriptionEdit.getText().toString().trim();
        if(mTitle.equals("")){
            CommonUtils.showToast("事件不能为空");
        }else {
            new Thread(()->{
                Task task = new Task();
                task.setTaskId(randomNum());
                task.setcId(mCategoryId);
                task.setTitle(mTitle);
                task.setContent(mContent);
                task.setState(mState);
                //task.setStartTime(mStartTime);
                task.setDate(mDateFormat);
                task.setPriority(mPriority);

                String str = "";
                if (repeatList.size() < 1) {
                    repeatList.add(0);
                }

                for (int i = 0; i < repeatList.size(); i++) {
                    if (1 == repeatList.size()) {
                        str += repeatList.get(i);
                    } else {
                        str += repeatList.get(i) + ",";
                    }
                }

                //这里需要区分是否全天状态，保存的开始、结束日期会不同
                if (1 == isAllDay){
                    //全天状态
                    task.setAlertTime(mAllDayStartTime);
                    task.setEndTimeMill(mAllDayEndTime);
                } else {
                    task.setAlertTime(mAlertTime);
                    task.setEndTimeMill(mEndTimeMill);
                }
                if (1==isNonli){
                    task.setRepeatId(mRepeatId);
                }
                else {
                    task.setRepeatId(str);
                }
                task.setRepeatMode(mRepeatMode);
                task.setRemindId(mReminderId);
                task.setLocation(mEtLocation.getText().toString());
                task.setAllDay(isAllDay);
                task.setNonLi(isNonli);
               // task.setTitle(mEtTitle.getText().toString());
                task.setRemark(mEtRemark.getText().toString());
                task.setStartTime(mTvStart.getText().toString());
                task.setEndTime(mTvEnd.getText().toString());

                //开始的时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mAlertTime);
                mStartDate = (String) DateFormat.format("yyyy年MM月dd日", calendar);
                /////////////////////task.setDate(mStartDate);//暂时注释
                //task.setId(String.valueOf(System.currentTimeMillis()));
                // Long.parseLong(String.valueOf((int)Math.random()*999+1));
                //设置系统系统日历ID
                task.setId(task.getTaskId());
                taskDao.insert(task,mTagIdList);

                //给日历软件增加一个日程
                addCalender(task);
                //弹出日程操作提示
                showAddSuccess();

            }).start();
        }
    }




    //更新修改
    public void update(){
        mTitle =titleTv.getText().toString().trim();
        mContent = descriptionEdit.getText().toString().trim();
        if(mTitle.equals("")){
            CommonUtils.showToast("事件不能为空");
        }else {
            new Thread(()->{
                Task task = new Task();
                task.setTaskId(mTaskId);
                task.setcId(mCategoryId);
                task.setTitle(mTitle);
                task.setContent(mContent);
                task.setState(mState);
                //task.setStartTime(mStartTime);
                task.setDate(mDateFormat);
                task.setPriority(mPriority);
                String str = "";
                if (repeatList.size() < 1) {
                    repeatList.add(0);
                }

                for (int i = 0; i < repeatList.size(); i++) {
                    if (1 == repeatList.size()) {
                        str += repeatList.get(i);
                    } else {
                        str += repeatList.get(i) + ",";
                    }
                }

                //这里需要区分是否全天状态，保存的开始、结束日期会不同
                if (1 == isAllDay){
                    //全天状态
                    task.setAlertTime(mAllDayStartTime);
                    task.setEndTimeMill(mAllDayEndTime);
                } else {
                    task.setAlertTime(mAlertTime);
                    task.setEndTimeMill(mEndTimeMill);
                }
                if (1==isNonli){
                    task.setRepeatId(mRepeatId);
                }
                else {
                    task.setRepeatId(str);
                }
                task.setRepeatMode(mRepeatMode);
                task.setRemindId(mReminderId);
                task.setLocation(mEtLocation.getText().toString());
                task.setAllDay(isAllDay);
                task.setNonLi(isNonli);
                //task.setTitle(mEtTitle.getText().toString());
                task.setRemark(mEtRemark.getText().toString());
                task.setStartTime(mTvStart.getText().toString());
                task.setEndTime(mTvEnd.getText().toString());

                //开始的时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mAlertTime);
                mStartDate = (String) DateFormat.format("yyyy年MM月dd日", calendar);
                /////////////////////task.setDate(mStartDate);//暂时注释
                //task.setId(String.valueOf(System.currentTimeMillis()));//更新的时候不更新系统日程的事件ID
                task.setId(task.getTaskId());
                taskDao.updateTaskAndTag(task,mOrignTagIdList,mTagIdList);
                updataCalender(task);
                showAddSuccess();

            }).start();
        }

    }

    private void addCalender(Task task){
        CalendarEvent calendarEvent = new CalendarEvent(
                task.getId(),
                task.getTitle(),
                task.getContent(),
                task.getLocation(),
                task.getAlertTime(),
                task.getEndTimeMill(),
                CalendarUtils.remindidTimeConvert(task.getRemindId()),
                CalendarUtils.repeatRrule(task.getAlertTime(), task.getEndTimeMill(),task.getAllDay(),task.getRepeatMode(),task.getRepeatId(),task.getNonLi())
        );

//                // 公历转农历：
//                Calendar today = Calendar.getInstance();
//                LunarCalendar lunar = LunarCalendar.solar2Lunar(today);
//                System.out.println(today.getTime() + " <====> " + lunar.getFullLunarName());
//
//                // 农历转公历：
//                LunarCalendar lunar1 = new LunarCalendar();
//                Calendar today1 = LunarCalendar.lunar2Solar(lunar.getLunarYear(), lunar.getLunarMonth(), lunar.getDayOfLunarMonth(),
//                        lunar.isLeapMonth());
//                System.out.println(lunar1.getFullLunarName() + " <====> " + today1.getTime());

        // 添加系统日历事件
        int addResult = CalendarProviderManager.addCalendarEvent(AddActivity.this, calendarEvent);
        if (addResult == 0) {
            System.out.println("插入成功");
        } else if (addResult == -1) {
            System.out.println("插入失败");
        } else if (addResult == -2) {
            System.out.println("没有权限");
        }
    }

    // 更新系统日历事件
    private void updataCalender(Task task){
//       boolean isx = CalendarProviderManager.isEventAlreadyExist(AddActivity.this,task.getAlertTime(),task.getEndTimeMill(),task.getTitle());
//       if (isx){
//           System.out.println("在系统日历找到该事件了");
//       }
//       else {
//           System.out.println("在系统日历没找到该事件");
//       }

        CalendarEvent calendarEvent = new CalendarEvent(
                task.getId(),
                task.getTitle(),
                task.getContent(),
                task.getLocation(),
                task.getAlertTime(),
                task.getEndTimeMill(),
                CalendarUtils.remindidTimeConvert(task.getRemindId()),
                CalendarUtils.repeatRrule(task.getAlertTime(), task.getEndTimeMill(),task.getAllDay(),task.getRepeatMode(),task.getRepeatId(),task.getNonLi())
        );

        // 更新系统事件
        int addResult = CalendarProviderManager.updateCalendarEvent(AddActivity.this,task.getId(), calendarEvent);
        if (addResult == 0) {
            System.out.println("更新成功");
        } else if (addResult == -1) {
            System.out.println("更新失败");
        } else if (addResult == -2) {
            System.out.println("没有权限");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    public void show(){
        new Thread(()->{
            Task task = taskDao.queryTaskById(mTaskId);
            if(task == null){
                showToast("当前任务不存在");
            }else {
                showSuccess(task);
            }
        }).start();
    }

    private void showAddSuccess(){
        runOnUiThread(() -> {
            EventBus.getDefault().post(new AddEvent()); //发送成功添加日程消息，告诉主活动显示改变
            EventBus.getDefault().post(new TypeEvent());//告诉分类，标签值改变
            finish();
            if(mTaskId ==Constants.ADD){
                CommonUtils.showToast("成功添加日程");
            } else {
                CommonUtils.showToast("成功修改日程");
            }

        });
    }

    /**
     * 其他活动开启这个活动调用
     * @param activity 需跳转到这个活动的活动
     * @param date 日期
     */
    public static void startActivityByAdd(Activity activity, String date, String formatDate,long dateLong) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(Constants.KEY_ADD_DATE,date);
        intent.putExtra(Constants.KEY_ADD_DATE_FORMAT,formatDate);
        intent.putExtra(Constants.KEY_SELECT_DATA,dateLong);
        activity.startActivity(intent);
    }


    /**
     * 根据taskId来决定是修改还是添加任务
     * @param activity 活动
     * @param taskId 任务号为-1表示是添加任务，否则则是修改任务
     *
     **/
        public static void startActivityByUpdate(Activity activity,int taskId) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(Constants.KEY_ID,taskId);
        activity.startActivity(intent);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSuccess(Task task) {
        runOnUiThread(()->{
            mTaskId = task.getTaskId();
            mCategoryId = task.getcId();
            mStartTime = task.getStartTime();
            mDateFormat =task.getDate();
            mState = task.isState();
            mDateText = DateUtils.dateFormat(task.getDate());
            //dateTv.setText(DateUtils.dateFormat(task.getDate())+"，"+DateUtils.timeFormat(task.getStartTime()));
            statusCheckBox.setChecked(mState);
//            if(mState){
//                dateTv.setTextColor(grayChecked);
//            }else {
//                dateTv.setTextColor(blueUnChecked);
//            }
            priorityIv.setImageDrawable(getDrawable(priorityPic[task.getPriority()]));
            titleTv.setText(task.getTitle());
            descriptionEdit.setText(task.getContent());
            ////////////////////////////////////

//            String mScheduleTitle = task.getTitle() + "";
            String mScheduleLocatoin = task.getLocation() + "";
            isAllDay = task.getAllDay();
            isNonli = task.getNonLi();
            String mStartTime = task.getStartTime() + "";
            String mEndTime = task.getEndTime() + "";
            mReminderId = task.getRemindId();
            String mRemark = task.getRemark() + "";
            mRepeatId = task.getRepeatId();
            mRepeatMode = task.getRepeatMode();
            mAlertTime = task.getAlertTime();
            mEndTimeMill = task.getEndTimeMill();
            mAllDayStartTime = mAlertTime;
            mAllDayEndTime = mEndTimeMill;
            mStartDate = task.getDate();

//            mEtTitle.setText(mScheduleTitle);
//            //设置光标位置在末尾
//            if (!TextUtils.isEmpty(mEtTitle.getText())){
//                mEtTitle.setSelection(mEtTitle.length());
//                mEtTitle.requestFocus();
//            }
            mEtLocation.setText(mScheduleLocatoin);

            isCustomRepeat = 1 == mRepeatMode;

            if (1 == isAllDay) {
                mSwitchView.setChecked(true);
            } else {
                mSwitchView.setChecked(false);
            }
            if (1 == isNonli) {
                mNonliSwitchView.setChecked(true);
            } else {
                mNonliSwitchView.setChecked(false);
            }
            mTvStart.setText(getStartTimeStr(task));
            mTvEnd.setText(getEndTimeStr(task));

            if (1 == isNonli) {
                int repeatId = 0;
                try {
                    repeatId = Integer.parseInt(task.getRepeatId());
                }

                catch (NumberFormatException e) {
                    repeatId = 0;
                }
                mTvRepeat.setText(ConstData.NONLIREPEATSTR[repeatId] + "");

            } else {
                mTvRepeat.setText(getRepeatStr() + "");
            }
            mTvReminder.setText(ConstData.REMINDERSTR[task.getRemindId()] + "");
            mEtRemark.setText(mRemark);
        });
    }

    private void showToast(String msg) {
        runOnUiThread(()->{
            CommonUtils.showToast(msg);
        });
    }

    //返回5位随机数
    private int randomNum(){
        Random random = new Random();
        int num = -1 ;

        for (int i = 0;i<1;i++){
            num = (int)(random.nextDouble()*(100000 - 10000) + 10000);
            //if(!( num+"").contains("4")) break ;

        }
        Log.i("Gyyx", "输出随机数"+num+"");
        return num;
    }
//////////////////////////////////////////////////////////////////////新增的逻辑///////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，回传的是RESULT_OK
            case ConstData.repeatCode:
                Bundle b = data.getExtras(); //data为B中回传的Intent
                // mRepeatId = b.getInt("repeat");//str即为回传的值
                repeatList = b.getIntegerArrayList("repeat");
                isCustomRepeat = b.getBoolean(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT);
                if (null == repeatList){
                    return;
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < repeatList.size(); i++) {
                    if (isCustomRepeat) {
                        //自定义
                        mRepeatMode = 1;
                        builder.append(ConstData.CUSTOMREPEATSTR[repeatList.get(i)]);
                        if (i != repeatList.size() - 1) {
                            builder.append(",");
                        }
                        mRepeatId = repeatList.get(i) +",";
                    } else {
                        mRepeatMode = 0;
                        builder.append(ConstData.REPEATSTR[repeatList.get(i)]);
                        mRepeatId = repeatList.get(i) + "";
                    }
                }
                if (isCustomRepeat && repeatList.size()==7){
                    builder.append(ConstData.REPEATSTR[1]);
                }
                mTvRepeat.setText(builder.toString());
                break;
            case ConstData.reminderCode:
                Bundle bReminder = data.getExtras(); //data为B中回传的Intent
                mReminderId = bReminder.getInt("reminder");//str即为回传的值
                mTvReminder.setText(ConstData.REMINDERSTR[mReminderId]);
                break;
            case ConstData.lunarCalendarreminderCode:
                mRepeatMode = 0;
                Bundle cReminder = data.getExtras(); //data为B中回传的Intent
                mRepeatId  = cReminder.getInt("repeat")+"";//str即为回传的值
                mTvRepeat.setText(ConstData.NONLIREPEATSTR[cReminder.getInt("repeat")]);
                break;
        }
    }

    private String getStartTimeStr(Task task){
        String startTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getAlertTime());
        if (1 == isAllDay&&0 == isNonli){
            //打开全天关闭农历
            startTime = DateFormat.format(allDayTimeFormat, calendar) + "  " + DateFormatter.getWeekDay(calendar);
        }
        else if (1 == isAllDay&&1 == isNonli) {
            //打开全天打开农历
            startTime = DateFormatter.getLunarFormatDay(calendar,isAllDay);
        }
        else if (0 == isAllDay&&1 == isNonli) {
            //关闭全天打开农历
            startTime = DateFormatter.getLunarFormatDay(calendar,isAllDay);
        }
        else {
            startTime = DateFormat.format(timeFormat, calendar) + "";
        }
        return startTime;
    }

    private String getEndTimeStr(Task task){
        String endTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getEndTimeMill());
        if (1 == isAllDay&&0 == isNonli){
            //打开全天关闭农历
            endTime = DateFormat.format(allDayTimeFormat, calendar) + "  " + DateFormatter.getWeekDay(calendar);
        }
        else if (1 == isAllDay&&1 == isNonli) {
            //打开全天打开农历
            endTime = DateFormatter.getLunarFormatDay(calendar,isAllDay);
        }
        else if (0 == isAllDay&&1 == isNonli) {
            //关闭全天打开农历
            endTime = DateFormatter.getLunarFormatDay(calendar,isAllDay);
        }
        else {
            endTime = DateFormat.format(timeFormat, calendar) + "";
        }
        return endTime;
    }

    private String getRepeatStr(){
        String[] str = mRepeatId.split(",");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            int temp = Integer.parseInt(str[i]);
            repeatList.add(temp);
            if (isCustomRepeat){
                //自定义
                if (repeatList.size() == 7){
                    builder.append(ConstData.REPEATSTR[1]) ;
                }else {
                    builder.append(ConstData.CUSTOMREPEATSTR[temp]);
                    if (i != str.length - 1) {
                        builder.append(",");
                    }
                }
            } else {
                builder.append(ConstData.REPEATSTR[temp]);
            }
        }
        return builder.toString();
    }

    /**
     * 获取全天状态时的开始日期
     * <p> 这里整天时间，提醒时间设置为上午9点，比如开始日期是：2016年10月5日 9:00 </p>
     * */
    private Calendar getAllDayStartTime() {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(mAlertTime);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        return startCalendar;
    }

    /**
     * 获取全天状态时的结束日期
     * <p> 时分秒设置为0，这里只需要整天时间，比如结束日期是：2016年10月3日 23:59 </p>
     * */
    private Calendar getAllDayEndTime() {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(mEndTimeMill);
        endCalendar.set(
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH),
                ScheduleConst.MAX_HOUR,
                ScheduleConst.MAX_MINUTE,
                ScheduleConst.MAX_SECOND);
        return endCalendar;
    }

    /**
     * 初始化时间选择器的时间
     * */
    private void initPickerTime() {
        mTimePickCalendar = Calendar.getInstance();
        if (isEndDate) {
            mTimePickCalendar.setTimeInMillis(mEndTimeMill);
        } else {
            mTimePickCalendar.setTimeInMillis(mAlertTime);
        }
        DateChooseWheelViewDialog.setmDate(mTimePickCalendar);
    }

    /**
     * 获取时间选择器日期
     * <p> 不能超出最大或者最小日期</p>
     * @param date
     * */
    private Calendar getTimePickCalendar(Date date) {
        Calendar minCalendar = CalendarUtils.getMinCalendar();
        Calendar maxCalendar = CalendarUtils.getMaxCalendar();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.compareTo(maxCalendar) == 1) {
            calendar =  maxCalendar;
        }
        if (calendar.compareTo(minCalendar) == -1) {
            calendar =  minCalendar;
        }
        return calendar;
    }

    /**
     * 设置时间选择器标题的时间
     * */
    private void setDatePickerTitle(Calendar calendar) {
        if (1 == isAllDay&&0 == isNonli){
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormat.format("yyyy/M/d", calendar));
        }
        else if (1 == isAllDay&&1 == isNonli) {
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormatter.getLunarFormatDay(calendar,isAllDay));
        }
        else if (0 == isAllDay&&1 == isNonli) {
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormatter.getLunarFormatDay(calendar,isAllDay));
        }
        else {
            mDatePickerAlertDialog.setTitle(str + "   " + DateFormat.format("yyyy/M/d  HH:mm", calendar));
        }
    }

    private void saveTimePick() {
        if (!isEndDate){
            //开始日期
            saveStartTime();
        } else {
            //结束日期
            saveEndTime();
        }
    }

    /**
     * 保存选择的开始日期
     * <p> 结束日期是否小于开始日期，是否选择全天状态，需要针对不同情况设定最终的日期 </p>
     * */
    private void saveStartTime() {
        Calendar startCalendar= Calendar.getInstance();
        startCalendar.set(mTimePickCalendar.get(Calendar.YEAR),
                mTimePickCalendar.get(Calendar.MONTH),
                mTimePickCalendar.get(Calendar.DAY_OF_MONTH),
                mTimePickCalendar.get(Calendar.HOUR_OF_DAY),
                mTimePickCalendar.get(Calendar.MINUTE),
                0);
        mAlertTime = startCalendar.getTimeInMillis();

        if (mAlertTime > mEndTimeMill) {
            //当结束时间小于开始时间
            mEndTimeMill = mAlertTime;
        }

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(mEndTimeMill);
        if (1 == isAllDay){
            //全天状态
            startCalendar = getAllDayStartTime();
            mAllDayStartTime = startCalendar.getTimeInMillis();

            endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();

            mTvStart.setText(DateFormat.format(allDayTimeFormat, startCalendar) + "  " + DateFormatter.getWeekDay(startCalendar));
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, endCalendar) + "  " + DateFormatter.getWeekDay(endCalendar));
        } else {
            mTvStart.setText(DateFormat.format(timeFormat, startCalendar));
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    /**
     * 保存选择的结束日期
     * <p> 结束日期是否小于开始日期，是否选择全天状态，需要针对不同情况设定最终的日期 </p>
     * */
    private void saveEndTime() {
        Calendar endCalendar= Calendar.getInstance();
        endCalendar.set(mTimePickCalendar.get(Calendar.YEAR),
                mTimePickCalendar.get(Calendar.MONTH),
                mTimePickCalendar.get(Calendar.DAY_OF_MONTH),
                mTimePickCalendar.get(Calendar.HOUR_OF_DAY),
                mTimePickCalendar.get(Calendar.MINUTE),
                0);
        mEndTimeMill = endCalendar.getTimeInMillis();
        if (mAlertTime > mEndTimeMill) {
            //当结束时间小于开始时间
            mEndTimeMill = mAlertTime;
        }

        if (1 == isAllDay){
            //全天状态
            endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, endCalendar) + "  " + DateFormatter.getWeekDay(endCalendar));
        } else {
            endCalendar.setTimeInMillis(mEndTimeMill);
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    /**
     * 是否打开全天
     * @param isChecked switch开关状态
     * */
    private void setAllDayOpen(boolean isChecked) {
        if (isChecked) {
            isAllDay = 1;
            //开始日期
            Calendar startCalendar = getAllDayStartTime();
            mAllDayStartTime = startCalendar.getTimeInMillis();

            //结束日期
            Calendar endCalendar = getAllDayEndTime();
            mAllDayEndTime = endCalendar.getTimeInMillis();
            setTimeState();
        } else {
            isAllDay = 0;
            setTimeState();
        }
    }

    /**
     * 是否打开农历
     * @param isChecked switch开关状态
     * */
    private void setNonliOpen(boolean isChecked) {
        if (isChecked) {
            isNonli = 1;
            setTimeState();
        } else {
            isNonli = 0;
            setTimeState();
        }
    }
    /**
     * 设置农历和全天打开关闭时的时间显示
     * */
    private void setTimeState(){
        if (1 == isAllDay&&0 == isNonli){
            //打开全天关闭农历
            mTvStart.setText(DateFormat.format(allDayTimeFormat, mAllDayStartTime) + "  " + DateFormatter.getWeekDay(mAllDayStartTime));
            mTvEnd.setText(DateFormat.format(allDayTimeFormat, mAllDayEndTime) + "  " + DateFormatter.getWeekDay(mAllDayEndTime));
        }
        else if (1 == isAllDay&&1 == isNonli) {
            //打开全天打开农历
            mTvStart.setText(DateFormatter.getLunarFormatDay(mAllDayStartTime,isAllDay));
            mTvEnd.setText(DateFormatter.getLunarFormatDay(mAllDayStartTime,isAllDay));
        }
        else if (0 == isAllDay&&1 == isNonli) {
            //关闭全天打开农历
            mTvStart.setText(DateFormatter.getLunarFormatDay(mAlertTime,isAllDay));
            mTvEnd.setText(DateFormatter.getLunarFormatDay(mEndTimeMill,isAllDay));
        }
        else {
            //关闭全天关闭农历
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(mAlertTime);
            mTvStart.setText(DateFormat.format(timeFormat, startCalendar));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(mEndTimeMill);
            mTvEnd.setText(DateFormat.format(timeFormat, endCalendar));
        }
    }

    /**
     * 获取时间选择弹框开关状态
     * */
    private boolean isDatePickerDialogShowing() {
        return null != mDatePickerAlertDialog && mDatePickerAlertDialog.isShowing();
    }

    private boolean isAllDay(){
        return 1 == isAllDay;
    }

    private void scheduleRemindEdit() {
        Intent inReminder = new Intent();
        inReminder.putExtra(ConstData.INTENT_SCEDULE_REMIND_EDIT_KEY, mReminderId);
        inReminder.setClass(this, ReminderActivity.class);
        startActivityForResult(inReminder, ConstData.reminderCode);
    }

    private void scheduleLunarCalendarRemindEdit() {
        Intent inReminder = new Intent();
        int repeatId = 0;
        try {
            repeatId = Integer.parseInt(mRepeatId);
        }

        catch (NumberFormatException e) {
            repeatId = 0;
        }
        inReminder.putExtra(ConstData.INTENT_SCEDULE_LUNAR_CALENDAR_REMIND_EDIT_KEY, repeatId);
        inReminder.setClass(this, LunarCalendarReminderActivity.class);
        startActivityForResult(inReminder, ConstData.lunarCalendarreminderCode);
    }
    private void scheduleRepeat() {
        Intent rlRepeat = new Intent();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ConstData.INTENT_SCHEDULE_REPEAT_EDIT_KEY, repeatList);
//        bundle.putString(ConstData.INTENT_SCHEDULE_REPEAT_EDIT_KEY, mRepeatId);
        bundle.putBoolean(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT, isCustomRepeat);
        rlRepeat.putExtras(bundle);
        rlRepeat.setClass(this, RepeatActivity.class);
        startActivityForResult(rlRepeat, ConstData.repeatCode);
    }

    private void showDatePickerDialog(boolean isAllDay) {
        int mode;
        initPickerTime();
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveTimePick();
                mDateChooseWheelViewDialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mDateChooseWheelViewDialog.dismiss();
            }
        });
        mDatePickerAlertDialog = builder.create();
        mDatePickerAlertDialog.setTitle("");
        DatechooseConfig mDateChooseConfig = new DatechooseConfig();

        //是否打开了全天
        if (isAllDay) {
            mode = ModeConst.YEAR_MONTH_DAY;
        } else {
            mode = ModeConst.DAYMONTH_HOUR_MINUTE;
        }

        mDateChooseConfig.setmMonthLeadYearAdd(true);
        mDateChooseWheelViewDialog = new DateChooseWheelViewDialog(AddActivity.this, new DateChooseWheelViewDialog.DateChooseInterface(){
            @Override
            public void getDateTime(Date date, View view){
                Calendar calendar = getTimePickCalendar(date);
                setDatePickerTitle(calendar);
                mTimePickCalendar = calendar;
            }

            @Override
            public void getSetContenView(View view){

            }
        }, mode, mDatePickerAlertDialog, builder, mDateChooseConfig);

        mDateChooseWheelViewDialog.showDateChooseDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDatePickerAlertDialog) {
            mDatePickerAlertDialog.cancel();
            mDatePickerAlertDialog = null;
        }
        if (null != mDateChooseWheelViewDialog) {
            mDateChooseWheelViewDialog.cancel();
            mDateChooseWheelViewDialog = null;
        }
    }
}
