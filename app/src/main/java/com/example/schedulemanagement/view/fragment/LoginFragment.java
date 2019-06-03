package com.example.schedulemanagement.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.view.activity.LoginActivity;
import com.example.schedulemanagement.view.activity.MainActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/02
 *     desc   : 登录碎片
 * </pre>
 */

public class LoginFragment extends Fragment {


    @BindView(R.id.usernameEdit)
    EditText usernameEdit;
    @BindView(R.id.passwordEdit)
    EditText passwordEdit;
    @BindView(R.id.loginBtn)
    RippleView loginBtn;
    @BindView(R.id.registerBtn)
    TextView registerBtn;

    @BindString(R.string.username_empty)
    String usernameEmpty;
    @BindString(R.string.psw_empty)
    String pswEmpty;
    private static final String TAG = "LoginFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginBtn.setOnRippleCompleteListener(rippleView ->
//            login(usernameEdit.getText().toString(), passwordEdit.getText().toString()));
                startActivity(new Intent(getActivity(), MainActivity.class)));
        registerBtn.setOnClickListener(view -> ((LoginActivity) getActivity()).toRegisterFragment());

    }

    /**
     * 登录操作
     *
     * @param username 用户名
     * @param password 密码
     */
    private void login(String username, String password) {
        if(TextUtils.isEmpty(username)){
            CommonUtils.showToast(getActivity(),usernameEmpty);
        }else if(TextUtils.isEmpty(password)){
            CommonUtils.showToast(getActivity(),pswEmpty);
        }else {
            OkHttpUtils.post()
                    .url(Constants.BASE_URL+"login")
                    .addParams("uname", username) //用户名
                    .addParams("pwd", password) //密码
                    .build()
                    .execute(new BaseResponseCallback<LoginAndRegister>() { //回调
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonUtils.showToast(getActivity(),"网络错误："+e.toString());
                            e.printStackTrace();
                            loginFail(e.toString());
                        }

                        @Override
                        public void onResponse(BaseResponse<LoginAndRegister> response, int id) {
                            if (response.getCode() == Constants.CODE_SUCCESS) {
                                loginSuccess();
                            } else {
                                loginFail(response.getMsg());
                            }

                        }
                    });
        }

    }

    /**
     * 登录成功
     */
    private void loginSuccess() {
        getActivity().runOnUiThread(() -> {
            getActivity().finish();
            toMainActivity();
            CommonUtils.showToast(getActivity(),getString(R.string.login_success));
        });
    }

    /**
     * 登录失败
     */
    private void loginFail(String message) {
        CommonUtils.showToast(getActivity(),message);
    }

    /**
     * 跳转到主活动
     */
    private void toMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
