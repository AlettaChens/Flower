package com.example.wordchen.web;

import android.os.Environment;

public class Constant {
	public static final String PATH;
	public static final String BASEURL = "http://192.168.50.98:8080";
	public static final String WEBDIR = "/log_web";

	static {
		PATH = Environment.getExternalStorageDirectory().getPath() + "/recipe/phone";
	}
}
