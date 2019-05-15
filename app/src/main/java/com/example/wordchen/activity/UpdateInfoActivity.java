package com.example.wordchen.activity;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wordchen.base.BaseActivity;
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


public class UpdateInfoActivity extends BaseActivity {
	@BindView(R.id.update_nickname_textview)
	TextView updateNicknameTextview;
	@BindView(R.id.nicks)
	ImageView nicks;
	@BindView(R.id.update_name_layout)
	RelativeLayout updateNameLayout;
	@BindView(R.id.update_pwd_textview)
	TextView updatePwdTextview;
	@BindView(R.id.pas)
	ImageView pas;
	@BindView(R.id.update_pwd_layout)
	RelativeLayout updatePwdLayout;
	@BindView(R.id.updateinfo_btn)
	Button updateinfoBtn;
	private FlowerSP flowerSP;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.updateuserinfo_activity;
	}

	@Override
	public void onInit() {
		flowerSP = new FlowerSP(UpdateInfoActivity.this);
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
	}

	@Override
	public void onBindData() {
		updateNicknameTextview.setText(flowerSP.getNickName());
		updatePwdTextview.setText(flowerSP.getPas());
	}


	public void updateInfoDialog(String titleString, final OnEditCompleteListener onEditCompleteListener) {
		final Dialog dialog = new Dialog(this, R.style.dialog);
		dialog.setContentView(R.layout.update_user_info_dialog_layout);
		TextView mTitleTv = dialog.findViewById(R.id.c_title_tv);
		final EditText mContent = dialog.findViewById(R.id.content_ed);
		Button mNegativeButton = dialog.findViewById(R.id.negative_btn);
		Button mPositiveButton = dialog.findViewById(R.id.positive_btn);
		mTitleTv.setText(titleString);
		mNegativeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		mPositiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditCompleteListener.complete(mContent.getText().toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}


	@OnClick({R.id.update_name_layout, R.id.update_pwd_layout, R.id.updateinfo_btn})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.update_name_layout:
				updateInfoDialog("昵称", new OnEditCompleteListener() {
					@Override
					public void complete(String content) {
						updateNicknameTextview.setText(content);
					}
				});
				break;
			case R.id.update_pwd_layout:
				updateInfoDialog("密码", new OnEditCompleteListener() {
					@Override
					public void complete(String content) {
						updatePwdTextview.setText(content);
					}
				});
				break;
			case R.id.updateinfo_btn:
				doUpdate();
				break;
		}
	}

	private void doUpdate() {

		WebAPIManager.getInstance(UpdateInfoActivity.this).UserInfoUpdate(flowerSP.getUserId(), updateNicknameTextview.getText().toString(), updatePwdTextview
				.getText().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(lifecycleProvider
				.<WebResponse>bindToLifecycle()).subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					MessageUtils.showLongToast(UpdateInfoActivity.this, "更新成功");
					flowerSP.putPas(updatePwdTextview.getText().toString());
					flowerSP.putNickName(updateNicknameTextview.getText().toString());
				} else {
					MessageUtils.showLongToast(UpdateInfoActivity.this, "更新失败");
				}
			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(UpdateInfoActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
	}

	public interface OnEditCompleteListener {
		void complete(String content);
	}

}
