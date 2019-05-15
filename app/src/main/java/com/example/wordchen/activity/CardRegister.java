package com.example.wordchen.activity;


import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;

import butterknife.BindView;

import static com.example.wordchen.uitls.NfcUtils.createTextRecord;
import static com.example.wordchen.uitls.NfcUtils.writeTag;


public class CardRegister extends BaseActivity {

	@BindView(R.id.et_card)
	EditText etCard;
	protected NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;

	@Override
	protected int getContentViewId() {
		return R.layout.registercard_activity;
	}

	@Override
	public void onInit() {

	}

	@Override
	public void onBindData() {

	}

	@Override
	protected void onStart() {
		super.onStart();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent intent = new Intent(this, getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createTextRecord(etCard.getText().toString())});
		boolean result = writeTag(ndefMessage, detectedTag);
		if (result) {
			Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
		}
	}
}

