package com.example.wordchen.uitls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

public class NfcUtils {


	public static boolean NfcCheck(Activity activity) {
		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		if (mNfcAdapter == null) {
			Toast.makeText(activity, "设备不支持NFC功能!", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			if (!mNfcAdapter.isEnabled()) {
				IsToSet(activity);
			}
		}
		return true;
	}

	private static void IsToSet(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("是否跳转到设置页面打开NFC功能");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToSet(activity);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private static void goToSet(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			activity.startActivity(intent);
			return;
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			activity.startActivity(intent);
			return;
		}
	}


	public static NdefRecord createTextRecord(String text) {
		byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = Charset.forName("UTF-8");
		//将文本转换为UTF-8格式
		byte[] textBytes = text.getBytes(utfEncoding);
		//设置状态字节编码最高位数为0
		int utfBit = 0;
		//定义状态字节
		char status = (char) (utfBit + langBytes.length);
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		//设置第一个状态字节，先将状态码转换成字节
		data[0] = (byte) status;
		//设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		//设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
		//到textBytes.length的位置
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		//通过字节传入NdefRecord对象
		//NdefRecord.RTD_TEXT：传入类型 读写
		NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		return ndefRecord;
	}


	public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
		try {
			Ndef ndef = Ndef.get(tag);
			ndef.connect();
			ndef.writeNdefMessage(ndefMessage);
			return true;
		} catch (Exception e) {
		}
		return false;
	}


	public static String readNfcTag(Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage msgs[] = null;
			int contentSize = 0;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
					contentSize += msgs[i].toByteArray().length;
				}
			}
			try {
				if (msgs != null) {
					NdefRecord record = msgs[0].getRecords()[0];
					String textRecord = parseTextRecord(record);
					return textRecord;

				}
			} catch (Exception e) {
			}
		}
		return null;
	}


	public static String parseTextRecord(NdefRecord ndefRecord) {
		//判断TNF
		if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
			return null;
		}
		//判断可变的长度的类型
		if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
			return null;
		}
		try {
			//获得字节数组，然后进行分析
			byte[] payload = ndefRecord.getPayload();
			//下面开始NDEF文本数据第一个字节，状态字节
			//判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
			//其他位都是0，所以进行"位与"运算后就会保留最高位
			String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
			//3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
			int languageCodeLength = payload[0] & 0x3f;
			//下面开始NDEF文本数据第二个字节，语言编码
			//获得语言编码
			String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			//下面开始NDEF文本数据后面的字节，解析出文本
			String textRecord = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
			return textRecord;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}


}
