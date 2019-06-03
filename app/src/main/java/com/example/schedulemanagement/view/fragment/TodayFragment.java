package com.example.schedulemanagement.view.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schedulemanagement.R;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/28
 *     desc   : 今日日程模块
 * </pre>
 */

public class TodayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        return view;
    }

    public static TodayFragment newInstance(){
        return new TodayFragment();
    }
}
