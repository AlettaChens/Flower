package com.example.wordchen.activity;

import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.EditText;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.FlowerEntity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.wordchen.uitls.NfcUtils.readNfcTag;


public class NFCReaderActivity extends BaseActivity {
	@BindView(R.id.flowernumdispaly_nfc)
	EditText flowernumdispalyNfc;
	@BindView(R.id.flowernamedispaly_nfc)
	EditText flowernamedispalyNfc;
	@BindView(R.id.flowerintroducedispaly_nfc)
	EditText flowerintroducedispalyNfc;


	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;
	protected NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_nfcread;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
	}

	@Override
	public void onBindData() {

	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		flowernumdispalyNfc.setText(readNfcTag(intent));
		doGetInfo(readNfcTag(intent));
	}


	private void doGetInfo(String result) {
		WebAPIManager.getInstance(NFCReaderActivity.this).getCollectionById(result).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.compose(lifecycleProvider.<WebResponse<FlowerEntity>>bindToLifecycle()).subscribe(new Observer<WebResponse<FlowerEntity>>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse<FlowerEntity> webResponse) {
				if (webResponse.getCode().equals("200")) {
					//flowernumdispalyNfc.setText(webResponse.getData().getFlowerid());
					flowernamedispalyNfc.setText(webResponse.getData().getFlowerName());
					flowerintroducedispalyNfc.setText(webResponse.getData().getFlowerInfo());
				} else {
					MessageUtils.showLongToast(NFCReaderActivity.this, "扫描失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(NFCReaderActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});

	}


	@Override
	protected void onStart() {
		super.onStart();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
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
}


