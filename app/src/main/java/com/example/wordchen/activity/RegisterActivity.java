package com.example.wordchen.activity;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;
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


public class RegisterActivity extends BaseActivity {
	@BindView(R.id.nick_name_ed_reg)
	EditText nickNameEdReg;
	@BindView(R.id.pwd_ed_reg)
	EditText pwdEdReg;
	@BindView(R.id.register_btn)
	Button registerBtn;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_register;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
	}

	@Override
	public void onBindData() {

	}


	public void doRegister() {
		String nickName = nickNameEdReg.getText().toString().trim();
		String password = pwdEdReg.getText().toString().trim();
		if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(password)) {
			register(nickName, password);
		} else {
			MessageUtils.showLongToast(this, "请按根据提示输入有效信息");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}


	public void register(String nickName, String pwd) {
		WebAPIManager.getInstance(RegisterActivity.this).register(nickName, pwd).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.compose(lifecycleProvider.<WebResponse>bindToLifecycle()).subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					MessageUtils.showLongToast(RegisterActivity.this, "注册成功");
				} else {
					MessageUtils.showLongToast(RegisterActivity.this, "注册失败");
				}
			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(RegisterActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
	}

	@OnClick(R.id.register_btn)
	public void onClick() {
		doRegister();
	}
}