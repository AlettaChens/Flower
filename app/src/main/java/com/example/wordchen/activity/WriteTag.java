package com.example.wordchen.activity;

/**
 * Created by hskdfk on 2018/4/15.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wordchen.flower.R;

import java.io.IOException;
/**
 * 写入标签
 *
 * @warn：弹出dialog 允许写入
 * @author shenyang
 *
 */
@SuppressLint("NewApi")
public class WriteTag extends Activity {
    private IntentFilter[] mWriteTagFilters;
    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    String[][] mTechLists;
    Button writeBtn;
    boolean isWrite = false;
    EditText mContentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registercard_activity);
        writeBtn = (Button) findViewById(R.id.register_card_btn);
        writeBtn.setOnClickListener(new WriteOnClick());
        mContentEditText = (EditText) findViewById(R.id.register_card_id);
        // 获取nfc适配器，判断设备是否支持NFC功能
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.no_nfc),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, getResources().getString(R.string.open_nfc),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // 写入标签权限
        IntentFilter writeFilter = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { writeFilter };
        mTechLists = new String[][] {
                new String[] { MifareClassic.class.getName() },
                new String[] { NfcA.class.getName() } };// 允许扫描的标签类型

    }

    /**
     * 写入标签按钮点击事件监听
     *
     * @author shenyang
     *
     */
    class WriteOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            isWrite = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(WriteTag.this)
                    .setTitle("请将标签靠近！");
//			builder.setNegativeButton("确定",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//							mContentEditText.setText("");
//							isWrite = false;
//							WriteTag.this.finish();
//						}
//					});
//			builder.setPositiveButton("取消",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//							isWrite = false;
//						}
//					});
//			builder.create();
//			builder.show();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                mWriteTagFilters, mTechLists);
    }

    // 写入模式时，才执行写入操作
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (isWrite == true
                && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = getNoteAsNdef();
            if (ndefMessage != null) {
                writeTag(getNoteAsNdef(), tag);
            } else {
                showToast("请输入您要写入标签的内容");
            }
        }
    }

    // 根据文本生成一个NdefRecord
    private NdefMessage getNoteAsNdef() {
        String text = mContentEditText.getText().toString();
        if (text.equals("")) {
            return null;
        } else {
            byte[] textBytes = text.getBytes();
            // image/jpeg text/plain
            NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                    "image/jpeg".getBytes(), new byte[] {}, textBytes);
            return new NdefMessage(new NdefRecord[] { textRecord });
        }

    }

    // 写入tag
    boolean writeTag(NdefMessage message, Tag tag) {

        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    showToast("tag不允许写入");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    showToast("文件大小超出容量");
                    return false;
                }

                ndef.writeNdefMessage(message);
                showToast("写入数据成功.");
//                Intent intent = new Intent(WriteTag.this,
//                        ReadTag.class);
                //startActivity(intent);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        showToast("格式化tag并且写入message");
                        return true;
                    } catch (IOException e) {
                        showToast("格式化tag失败.");
                        return false;
                    }
                } else {
                    showToast("Tag不支持NDEF");
                    return false;
                }
            }
        } catch (Exception e) {
            showToast("写入数据失败");
        }

        return false;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

