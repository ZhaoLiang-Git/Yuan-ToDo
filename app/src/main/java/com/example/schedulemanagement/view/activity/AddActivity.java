package com.example.schedulemanagement.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.callback.EventResponseCallback;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.DateUtils;
import com.github.glomadrian.grav.figures.Grav;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

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
    private String mStatus = "undone";
    private int mNowHour;
    private int mNowMinute;
    private int mPriority = 3;
    private int[] priorityPic ={R.drawable.ic_priority_high,R.drawable.ic_priority_medium,
            R.drawable.ic_priority_low};

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
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
                if(mType == Constants.ADD){
                    add();
                }else {
                    update();
                }
                break;
        }
        return true;
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
    private void onClick() {
        //返回
        toolbar.setNavigationOnClickListener(view -> finish());
        //日期
        eventCl.setOnClickListener(view -> timePickerDialog.show(getSupportFragmentManager(),"timePickerDialog"));
        //状态选择
        statusCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked){
                mStatus = "done";
                dateTv.setTextColor(grayChecked);
            }else {
                mStatus = "undone";
                dateTv.setTextColor(blueUnChecked);
            }
        });
        //优先级选择
        priorityIv.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog
                    .Builder(this)
                    .setSingleChoiceItems(
                            new String[]{"高", "中", "低"},
                            mPriority-1,
                            (dialog1, which)
                            -> {
                                priorityIv.setImageDrawable(getDrawable(priorityPic[which]));
                                mPriority = which+1;
                            }).create();
            dialog.show();
        });
    }



    /**
     * 初始化数据
     */
    private void initData(){
        mType = getIntent().getIntExtra(Constants.KEY_ADD_TYPE,Constants.ADD);
        if(mType == Constants.UPDATE){
            show();
        }else {
            mDateText = getIntent().getStringExtra(Constants.KEY_ADD_DATE);
            mDateFormat = getIntent().getStringExtra(Constants.KEY_ADD_DATE_FORMAT);
            Calendar calendar = Calendar.getInstance();
            mNowHour = calendar.get(Calendar.HOUR_OF_DAY);
            mNowMinute = calendar.get(Calendar.MINUTE);
            mStartTime = DateUtils.formatTime(mNowHour) +":" +DateUtils.formatTime(mNowMinute) +":00";
            dateTv.setText(mDateText+"，"+DateUtils.formatTime(mNowHour)+"："+DateUtils.formatTime(mNowMinute));
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        dateTv.setText(mDateText+"，"+ DateUtils.formatTime(hourOfDay)+"："+DateUtils.formatTime(minute));
        mStartTime = DateUtils.formatTime(hourOfDay) +":" +DateUtils.formatTime(minute) +":00";
    }

    /**
     * 提交添加日程的请求
     */
    private void add(){

        mTitle =titleTv.getText().toString().trim();
        mContent = descriptionEdit.getText().toString().trim();
        if(mTitle.equals("")){
            CommonUtils.showToast("事件不能为空");
        }
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN+"add")
                .addParams(Constants.Params_TITLE,mTitle)
                .addParams(Constants.Params_CONTENT,mContent)
                .addParams(Constants.Params_STATUS,mStatus)
                .addParams(Constants.Params_START,mStartTime)
                .addParams(Constants.Params_DATE,mDateFormat)
                .addParams(Constants.Params_PRIORITY,String.valueOf(mPriority))
                .build()
                .execute(new BaseResponseCallback<Event>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if(response.getCode()==0){
                            showAddSuccess();
                        } else {
                            CommonUtils.showToast("添加日程失败，请重新添加");
                        }
                    }
                });
    }

    //更新修改
    public void update(){
        mTitle =titleTv.getText().toString().trim();
        mContent = descriptionEdit.getText().toString().trim();
        if(mTitle.equals("")){
            CommonUtils.showToast("事件不能为空");
        }
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN+"update")
                .addParams(Constants.Params_TITLE,mTitle)
                .addParams(Constants.Params_CONTENT,mContent)
                .addParams(Constants.Params_STATUS,mStatus)
                .addParams(Constants.Params_START,mStartTime)
                .addParams(Constants.Params_DATE,mDateFormat)
                .addParams(Constants.Params_PRIORITY,String.valueOf(mPriority))
                .build()
                .execute(new BaseResponseCallback<Event>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if(response.getCode()==0){
                            showAddSuccess();
                        } else {
                            CommonUtils.showToast("修改日程失败，请重新修改");
                        }
                    }
                });
    }
    public void show(){
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN + "show")
                .addParams("s_date", DateUtils.getTodayDate())
                .build()
                .execute(new EventResponseCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonUtils.showToast("网络错误：" + e.toString());
                        Log.d(TAG, "onError: " + e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(BaseResponse<Event> response, int id) {
                        if (response.getCode() == Constants.CODE_SUCCESS) {
                            showSuccess(response.getData());
                        } else {
                            CommonUtils.showToast("显示日程失败，请重新操作");
                        }
                    }
                });
    }

    private void showAddSuccess(){
        runOnUiThread(() -> {
            EventBus.getDefault().post(new AddEvent()); //发送成功添加日程消息，告诉主活动显示改变
            finish();
            if(mType ==Constants.ADD){
                CommonUtils.showToast("成功添加日程");
            } else {
                CommonUtils.showToast("成功修改日程");
            }

        });
    }

    /**
     * 显示日程
     * @param event
     */
    private void showSuccess(Event event){

    }
    /**
     * 其他活动开启这个活动调用
     * @param activity 需跳转到这个活动的活动
     * @param date 日期
     */
    public static void startActivity(Activity activity,String date,String formatDate) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(Constants.KEY_ADD_DATE,date);
        intent.putExtra(Constants.KEY_ADD_DATE_FORMAT,formatDate);
        activity.startActivity(intent);
    }

    public static void startActivityByToday(Activity activity,int type) {
        Intent intent = new Intent(activity, AddActivity.class);
        intent.putExtra(Constants.KEY_ADD_TYPE,type);
        activity.startActivity(intent);
    }
}
