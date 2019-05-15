package com.example.wordchen.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.FlowerSP;
import com.example.wordchen.uitls.GlideX;
import com.example.wordchen.widget.CircleImageView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;


public class WelcomeActivity extends BaseActivity {


	@BindView(R.id.iv_back)
	CircleImageView ivBack;
	@BindView(R.id.text_wait)
	TextView textWait;
	@BindView(R.id.ll_welcome)
	RelativeLayout llWelcome;
	private FlowerSP flowerSP;

	@Override
	protected int getContentViewId() {
		return R.layout.welcome_activity;
	}

	@Override
	public void onInit() {
		flowerSP = new FlowerSP(WelcomeActivity.this);
		if (flowerSP.getUserLoginStatue()) {
			if (!TextUtils.isEmpty(flowerSP.getAvatarURL())) {
				GlideX.getInstance().loadImage(WelcomeActivity.this, flowerSP.getAvatarURL(), ivBack);
			} else {
				ivBack.setImageResource(R.drawable.defaults);
			}
			textWait.setText("欢迎您回来");
		} else {
			ivBack.setImageResource(R.drawable.defaults);
			textWait.setText("终于等到你");
		}
	}

	@Override
	public void onBindData() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				if (flowerSP.getUserLoginStatue()) {
					Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				finish();
			}
		};
		timer.schedule(task, 3000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return false;
	}
}
