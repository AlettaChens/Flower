package com.example.wordchen.uitls;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 ");
		Date curDate = new Date(System.currentTimeMillis()); //获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

}
