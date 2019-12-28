package com.example.schedulemanagement.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.adapter.TypeAdapter;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.db.TypeDao;
import com.example.schedulemanagement.entity.Event;
import com.example.schedulemanagement.entity.Type;
import com.example.schedulemanagement.event.TypeEvent;
import com.example.schedulemanagement.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TypeActivity extends AppCompatActivity {

    private static final String TAG = "TypeActivity";


    private RecyclerView recyclerView;
    private List<Type> typeList;
    private TypeAdapter typeAdapter;
    private LinearLayoutManager manager;
    private TextView titleTv;
    private TypeDao typeDao;

    private int mType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        typeDao = new TypeDao();
        EventBus.getDefault().register(this);
        initData();
        initView();
        initRecycler();
        show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onTypeEvent(TypeEvent typeEvent){
        typeList.clear();
        typeList.addAll(typeDao.query(mType));
        showSuccess();
    }

    private void initData(){
        mType = getIntent().getIntExtra(Constants.KEY_TYPE,0);
    }

    private void initView(){
        titleTv = findViewById(R.id.typeTv);
        recyclerView = findViewById(R.id.typeRecycler);
        typeList = new ArrayList<>();
    }

    private void initRecycler(){
        manager = new LinearLayoutManager(this);
        typeAdapter = new TypeAdapter(typeList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(typeAdapter);
        typeAdapter.setOnItemClickListener(type -> TaskActivity.toTaskActivity(this,type.getId(),type.getName(),mType));
    }

    private void show(){
        if(mType == Constants.TYPE_CATEGORY){
            titleTv.setText(getString(R.string.person_category));
        }else {
            titleTv.setText(getString(R.string.person_tag));
        }
        new Thread(()->{
            typeList.clear();
            typeList.addAll(typeDao.query(mType));
            showSuccess();
        }).start();
    }
    //显示成功
    private void showSuccess(){
        runOnUiThread(()->{
            typeAdapter.notifyDataSetChanged();
        });
    }


    public static void toTypeActivity(Activity activity,int type){
        Intent intent = new Intent(activity,TypeActivity.class);
        intent.putExtra(Constants.KEY_TYPE,type);
        activity.startActivity(intent);
    }
}
