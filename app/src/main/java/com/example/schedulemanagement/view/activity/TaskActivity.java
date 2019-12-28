package com.example.schedulemanagement.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.EventAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.db.TaskDao;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Task;
import com.example.schedulemanagement.event.AddEvent;
import com.example.schedulemanagement.event.DeleteEvent;
import com.example.schedulemanagement.event.TypeEvent;
import com.example.schedulemanagement.event.UpdateStateEvent;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.widget.ConfirmDialog;
import com.example.schedulemanagement.widget.group.GroupItemDecoration;
import com.example.schedulemanagement.widget.group.GroupRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskActivity extends AppCompatActivity {

    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.recyclerView)
    GroupRecyclerView recyclerView;

    @BindString(R.string.dialog_delete_text)
    String deleteText;
    @BindString(R.string.dialog_delete_title)
    String deleteTitle;
    @BindView(R.id.titleTv)
    TextView titleTv;
    private EventAdapter mAdapter;
    private TaskDao taskDao;
    private int mId;
    private String cName;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        taskDao = new TaskDao();
        initData();
        initRecyclerView();
    }

    private void initData() {
        mId = getIntent().getIntExtra(Constants.KEY_ID, 0);
        cName = getIntent().getStringExtra(Constants.KEY_NAME);
        titleTv.setText(cName);
        mType = getIntent().getIntExtra(Constants.KEY_TYPE,0);
    }

    //添加日程成功重新显示日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddEvent(AddEvent event) {
        showTask();
    }

    //删除日程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteEvent(DeleteEvent event) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setOnClickListener(() -> {
            delete(event.getId());
        });
        dialog.setText(deleteText).setTitle(deleteTitle).show();
    }

    //更新日程
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdateEvent(UpdateStateEvent event) {
        if (taskDao.update(event.getEventBean()) != 0) {
            showTask();
        } else {
            showFail("修改失败");
        }
    }

    //更新标签，分类
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTypeEvent(TypeEvent event) {
        showTask();
    }

    private void initRecyclerView() {
        mAdapter = new EventAdapter(this, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new GroupItemDecoration<String, Task>());
        recyclerView.setAdapter(mAdapter);
        //item点击效果
        mAdapter.setOnClickListener((position, taskId) -> {
            AddActivity.startActivityByUpdate(this, taskId);
        });
        recyclerView.notifyDataSetChanged();
        showTask();
    }

    //获取数据库所有任务表
    private void showTask() {
        new Thread(() -> {
            Event event;
            if(mType == Constants.TYPE_CATEGORY){
                //分类
                event = taskDao.queryTaskByCategoryId(mId);
            }else {
                //标签
                event = taskDao.queryTaskByTagId(mId);
            }
            showSuccess(event);
        }).start();
    }

    /**
     * 删除日程
     */
    private void delete(int id) {
        new Thread(() -> {
            if (taskDao.delete(id) != -1) {
                showDeleteSuccess();
            }
        }).start();

    }

    /**
     * 查询日程成功
     *
     * @param event 事件
     */
    private void showSuccess(Event event) {
        runOnUiThread(() -> {
            mAdapter.notifyChanged(this, "待完成", event);
            recyclerView.notifyDataSetChanged();
        });
    }

    /**
     * 查询日程失败
     */
    private void showFail(String msg) {
        runOnUiThread(() -> {
            CommonUtils.showToast(msg);
        });
    }

    /**
     * 删除日程成功
     */
    private void showDeleteSuccess() {
        runOnUiThread(() -> {
            EventBus.getDefault().post(new AddEvent());
            CommonUtils.showToast("删除日程成功");
        });

    }

    public static void toTaskActivity(Activity activity, int id, String name,int type) {
        Intent intent = new Intent(activity, TaskActivity.class);
        intent.putExtra(Constants.KEY_ID, id);
        intent.putExtra(Constants.KEY_NAME, name);
        intent.putExtra(Constants.KEY_TYPE,type);
        activity.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
