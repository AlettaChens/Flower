package com.example.wordchen.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.UserInfoBean;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.FlowerSP;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends BaseActivity {
	@BindView(R.id.nick_name_ed_login)
	EditText nickNameEdLogin;
	@BindView(R.id.pwd_ed_login)
	EditText pwdEdLogin;
	@BindView(R.id.login_btn)
	Button loginBtn;
	@BindView(R.id.go_to_register)
	TextView goToRegister;
	@BindView(R.id.activity_login)
	LinearLayout activityLogin;

	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;
	private FlowerSP flowerSP;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_login;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
		flowerSP = new FlowerSP(LoginActivity.this);
	}

	@Override
	public void onBindData() {

	}


	@OnClick({R.id.login_btn, R.id.go_to_register})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.login_btn:
				String nickName = nickNameEdLogin.getText().toString().trim();
				String password = pwdEdLogin.getText().toString().trim();
				if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(password)) {
					login(nickName, password);
				} else {
					MessageUtils.showLongToast(this, "请按根据提示输入有效信息");
				}

				break;
			case R.id.go_to_register:
				startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
				break;
		}
	}

	private void login(String nickName, String password) {
		WebAPIManager.getInstance(LoginActivity.this).login(nickName, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose
				(lifecycleProvider.<WebResponse<UserInfoBean>>bindToLifecycle()).subscribe(new Observer<WebResponse<UserInfoBean>>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse<UserInfoBean> userInfoBeanWebResponse) {
				if (userInfoBeanWebResponse.getCode().equals("200")) {
					flowerSP.putNickName(userInfoBeanWebResponse.getData().getNickName());
					flowerSP.putAvatarURL(userInfoBeanWebResponse.getData().getAvatarUrl());
					flowerSP.putuserIsLogin(true);
					flowerSP.putUserId(userInfoBeanWebResponse.getData().getId());
					flowerSP.putPas(userInfoBeanWebResponse.getData().getPwd());
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();
				} else {
					MessageUtils.showLongToast(LoginActivity.this, "登录失败");
				}
			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(LoginActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
	}
}