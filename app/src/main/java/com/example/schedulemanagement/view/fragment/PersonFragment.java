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
 *     desc   : 个人模块
 * </pre>
 */

public class PersonFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        return view;
    }

    public static PersonFragment newInstance(){
        return new PersonFragment();
    }
}
