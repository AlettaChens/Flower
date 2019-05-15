package com.example.wordchen.uitls;

import android.content.Context;

import com.example.wordchen.base.BaseSPUtil;


public class FlowerSP extends BaseSPUtil {
	public FlowerSP(Context context) {
		super(context, "flower_sp");
	}

	public void putUserId(int userId) {
		putInt("userId", userId);
	}

	public int getUserId() {
		return getInt("userId", 0);
	}

	public void putNickName(String nickname) {
		putString("nickname", nickname);
	}

	public String getNickName() {
		return getString("nickname", null);
	}

	public void putAvatarURL(String url) {
		putString("url", url);
	}

	public String getAvatarURL() {
		return getString("url", null);
	}

	public void putuserIsLogin(boolean isLogining) {
		putBoolean("isLogin", isLogining);
	}

	public Boolean getUserLoginStatue() {
		return getBoolean("isLogin", false);
	}

	public void putPas(String pas) {
		putString("pas", pas);
	}

	public String getPas() {
		return getString("pas", null);
	}

}
