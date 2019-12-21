package com.example.schedulemanagement.view.fragment;

import android.app.Instrumentation;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.db.UserDao;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.example.schedulemanagement.utils.CommonUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/03
 *     desc   : 注册界面
 * </pre>
 */

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";

    @BindView(R.id.usernameEdit)
    EditText usernameEdit;
    @BindView(R.id.passwordEdit)
    EditText passwordEdit;
    @BindView(R.id.passwordRepeatEdit)
    EditText passwordRepeatEdit;
    @BindView(R.id.registerBtn)
    RippleView registerBtn;
    @BindView(R.id.loginBtn)
    TextView loginBtn;

    @BindString(R.string.register_psw_repeat_error)
    String repeatError;
    @BindString(R.string.username_empty)
    String usernameEmpty;
    @BindString(R.string.psw_empty)
    String pswEmpty;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this,view);
        userDao = new UserDao();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册
        registerBtn.setOnRippleCompleteListener(rippleView ->
                register(usernameEdit.getText().toString(),
                        passwordEdit.getText().toString(),
                        passwordRepeatEdit.getText().toString()));
        loginBtn.setOnClickListener(view -> toLoginFragment());

    }


    /**
     * 模拟返回键
     */
    public void toLoginFragment() {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 注册操作
     *
     * @param username       用户名
     * @param password       密码
     * @param repeatPassword 确认密码
     */
    private void register(String username, String password, String repeatPassword) {
        if(TextUtils.isEmpty(username)){
            CommonUtils.showToast(getActivity(),usernameEmpty);
        }else if(password.trim().length()< 6){
            CommonUtils.showToast("密码不能少于6位");
        } else if (!password.equals(repeatPassword)) {
            CommonUtils.showToast(getActivity(),repeatError);
        }else {
            //executeByOkHttp(username, password);
            new Thread(()->{
                String msg = userDao.register(username,password);
                if(msg.equals("success")){
                    registerSuccess("注册成功");
                }else {
                    registerFail(msg);
                }
            }).start();
        }
    }


    //API方式
    private void executeByOkHttp(String username, String password) {
        OkHttpUtils.post()
                .url(Constants.BASE_URL+"register")
                .addParams("registerName",username)
                .addParams("registerPwd",password)
                .build()
                .execute(new BaseResponseCallback<LoginAndRegister>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        CommonUtils.showToast(getActivity(),"网络错误："+e.toString());
                        Log.d(TAG, "onError: "+e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(BaseResponse<LoginAndRegister> response, int id) {
                        if(response.getCode()==Constants.CODE_SUCCESS){
                            registerSuccess(response.getMsg());
                        }else {
                            registerFail(response.getMsg());
                        }
                    }
                });
    }


    /**
     * 注册成功
     */
    private void registerSuccess(String msg) {
        getActivity().runOnUiThread(() -> {
            CommonUtils.showToast(getActivity(),msg);
            toLoginFragment();
        });

    }

    /**
     * 注册失败
     */
    private void registerFail(String msg) {
        getActivity().runOnUiThread(()->{
            Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 创建实例
     *
     * @return 实例
     */
    public static Fragment newInstance() {
        return new RegisterFragment();
    }

}
