package com.example.schedulemanagement.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.db.TaskDao;
import com.example.schedulemanagement.db.TypeDao;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateUtils;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        taskDao = new TaskDao();
        typeDao = new TypeDao();
        initData();
        initView();
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
            case R.id.menu_category:
                selectCategory();
                break;
            case R.id.menu_tag:
                selectTag();
                break;
        }
        return true;
    }

    //选择分类
    private void selectCategory(){
        new Thread(()->{
            //如果当前没有分类，则新建一个默认分类
            if(typeDao.isEmptyOfCategory()) typeDao.insert("默认",true);

            //如果是一开始设置分类就得在数据库中获取分类的位置
            if(mCategoryIndex == -1) mCategoryIndex = typeDao.queryCategoryIndex(mCategoryId);
            //查询分类列表
            HashMap<String,Integer> map = typeDao.queryCategory();
            showCategoryDialog(map);
        }).start();
    }

    //选择标签
    private void selectTag(){
        new Thread(()->{
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
        //日期
        eventCl.setOnClickListener(view -> timePickerDialog.show(getFragmentManager(),"timePickerDialog"));
        //状态选择
        statusCheckBox.setOnClickListener(view -> {
            mState = ((CheckBox)view).isChecked();
            if(mState){
                dateTv.setTextColor(grayChecked);
            }else {
                dateTv.setTextColor(blueUnChecked);
            }
        });
        //优先级选择
        priorityIv.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog
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
            Calendar calendar = Calendar.getInstance();
            mNowHour = calendar.get(Calendar.HOUR_OF_DAY);
            mNowMinute = calendar.get(Calendar.MINUTE);
            mStartTime = DateUtils.formatTime(mNowHour) +":" +DateUtils.formatTime(mNowMinute) ;
            dateTv.setText(mDateText+"，"+DateUtils.formatTime(mNowHour)+"："+DateUtils.formatTime(mNowMinute));
        }else {//修改数据
            show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        dateTv.setText(mDateText+"，"+ DateUtils.formatTime(hourOfDay)+"："+DateUtils.formatTime(minute));
        mStartTime = DateUtils.formatTime(hourOfDay) +":" +DateUtils.formatTime(minute);
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
                task.setcId(mCategoryId);
                task.setTitle(mTitle);
                task.setContent(mContent);
                task.setState(mState);
                task.setStartTime(mStartTime);
                task.setDate(mDateFormat);
                task.setPriority(mPriority);
                taskDao.insert(task,mTagIdList);
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
                task.setStartTime(mStartTime);
                task.setDate(mDateFormat);
                task.setPriority(mPriority);
                taskDao.updateTaskAndTag(task,mOrignTagIdList,mTagIdList);
                showAddSuccess();

            }).start();
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
    public static void startActivityByAdd(Activity activity, String date, String formatDate) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(Constants.KEY_ADD_DATE,date);
        intent.putExtra(Constants.KEY_ADD_DATE_FORMAT,formatDate);
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
            dateTv.setText(DateUtils.dateFormat(task.getDate())+"，"+DateUtils.timeFormat(task.getStartTime()));
            statusCheckBox.setChecked(mState);
            if(mState){
                dateTv.setTextColor(grayChecked);
            }else {
                dateTv.setTextColor(blueUnChecked);
            }
            priorityIv.setImageDrawable(getDrawable(priorityPic[task.getPriority()]));
            titleTv.setText(task.getTitle());
            descriptionEdit.setText(task.getContent());
        });
    }

    private void showToast(String msg) {
        runOnUiThread(()->{
            CommonUtils.showToast(msg);
        });
    }
}
