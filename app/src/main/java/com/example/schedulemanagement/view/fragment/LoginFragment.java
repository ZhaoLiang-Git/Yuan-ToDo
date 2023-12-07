package com.example.schedulemanagement.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.LoginAndRegisterCallback;
import com.example.schedulemanagement.db.UserDao;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.example.schedulemanagement.utils.CommonUtils;
import com.example.schedulemanagement.utils.MySharedPreferences;
import com.example.schedulemanagement.view.activity.LoginActivity;
import com.example.schedulemanagement.view.activity.MainActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * <pre>
 *     desc   : 登录
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
    private UserDao userDao;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);
        userDao = new UserDao();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String username = (String) LoginActivity.sharedPreferencesUtil.getData(MySharedPreferences.Contants.USERNAME, "");
        String password = (String) LoginActivity.sharedPreferencesUtil.getData(MySharedPreferences.Contants.PASSWORD, "");

        if (!username.equals("")&&!password.equals("")) {
            usernameEdit.setText(username);
            passwordEdit.setText(password);
        }
        loginBtn.setOnRippleCompleteListener(rippleView ->
          login(usernameEdit.getText().toString(), passwordEdit.getText().toString()));
//                startActivityByAdd(new Intent(getActivity(), MainActivity.class)));
        registerBtn.setOnClickListener(view -> {
            ((LoginActivity) getActivity()).toRegisterFragment();
            usernameEdit.setText("");
            passwordEdit.setText("");
        });

    }

    /**
     * 登录操作
     *
     * @param username 用户名
     * @param password 密码
     */
    private void login(String username,String password){
        new Thread(()->{
            if(userDao.login(username,password)){
                loginSuccess(username);
            }else {
                loginFail("用户不存在或密码错误");
            }
        }).start();
    }


    //API登录
    private void loginByHttp(String username, String password) {
        if(TextUtils.isEmpty(username)){
            CommonUtils.showToast(getActivity(),usernameEmpty);
        }else if(password.trim().length()< 6){
            CommonUtils.showToast("密码不能少于6位");
        }else {
            OkHttpUtils.post()
                    .url(Constants.BASE_URL+"login")
                    .addParams("uname", username) //用户名
                    .addParams("pwd", password) //密码
                    .build()
                    .execute(new LoginAndRegisterCallback() { //回调
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            CommonUtils.showToast(getActivity(),"网络错误："+e.toString());
                            e.printStackTrace();
                            loginFail(e.toString());
                        }

                        @Override
                        public void onResponse(BaseResponse<LoginAndRegister> response, int id) {
                            if (response.getCode() == Constants.CODE_SUCCESS) {
                                loginSuccess(response.getData().getUname());
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
    private void loginSuccess(String username) {
        getActivity().runOnUiThread(() -> {
            CommonUtils.showToast(getActivity(),getString(R.string.login_success));
            getActivity().finish();
            MainActivity.startActivity(getActivity(),username);
        });
    }

    /**
     * 登录失败
     */
    private void loginFail(String message) {
        getActivity().runOnUiThread(()->CommonUtils.showToast(getActivity(),message));
    }

    /**
     * 跳转到主活动
     */
    private void toMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
