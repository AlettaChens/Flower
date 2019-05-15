package com.example.wordchen;

import com.uuzuche.lib_zxing.ZApplication;

import org.xutils.x;

public class App extends ZApplication {  //二维码扫描功能由于界面的尺寸不兼容，所以需要继承自基类的 com.uuzuche.lib_zxing.ZApplication;
	//该类里做了一些宽高像素的获取，如果有自定义的application，继承他的ZApplication就好。
	@Override
	public void onCreate() {
		super.onCreate();
		x.Ext.init(this);
	}
}
