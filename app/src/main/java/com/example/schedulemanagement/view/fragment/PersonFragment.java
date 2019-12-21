package com.example.schedulemanagement.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.App;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.view.activity.LoginActivity;
import com.example.schedulemanagement.widget.ConfirmDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/28
 *     desc   : 个人模块
 * </pre>
 */

public class PersonFragment extends Fragment {
    @BindView(R.id.personLogout)
    TextView personLogout;
    @BindString(R.string.dialog_text)
    String dialogText;
    @BindString(R.string.dialog_title)
    String dialogTitle;
    @BindView(R.id.personUsernameTv)
    TextView personUsernameTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if(bundle!=null){
            personUsernameTv.setText(bundle.getString(Constants.KEY_USERNAME));
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onClick();
    }

    public void onClick() {
        //退出登录点击效果
        personLogout.setOnClickListener(view -> showLogoutDialog());
    }


    /**
     * 显示退出的dialog
     */
    public void showLogoutDialog() {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setOnClickListener(this::logout);
        dialog.setText(dialogText).setTitle(dialogTitle).show();
    }

    /**
     * 请求退出登录的操作
     */
    private void logout() {
        OkHttpUtils.post()
                .url(Constants.BASE_URL_MAIN + "exit")
                .build()
                .execute(new BaseResponseCallback<LoginAndRegister>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response.getCode() == Constants.CODE_SUCCESS) {
                            toLoginActivity();
                        } else {
                            CommonUtils.showToast("退出登录失败");
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        e.printStackTrace();
                        CommonUtils.showToast("网络错误" + e.toString());
                    }
                });
    }

    /**
     * 登录成功，返回到登录界面
     */
    public void toLoginActivity() {
        App.getCookieStore().removeAll();//清空cookie
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }


    /**
     * 活动调用，并携带username的参数
     * @return
     */
    public static PersonFragment newInstance(String username) {
        PersonFragment personFragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_USERNAME,username);
        personFragment.setArguments(bundle);
        return personFragment;
    }
}
