package com.example.wordchen.activity;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.FlowerEntity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ErweimaActivity extends BaseActivity {
	int REQUEST_CODE = 1;
	@BindView(R.id.flowernumdispaly_er)
	EditText flowernumdispalyEr;
	@BindView(R.id.flowernamedispaly_er)
	EditText flowernamedispalyEr;
	@BindView(R.id.flowerintroducedispaly_er)
	EditText flowerintroducedispalyEr;
	@BindView(R.id.erweimaButton)
	Button erweimaButton;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_erweima;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
		getCameraPermission();
	}

	@Override
	public void onBindData() {

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (null != data) {
				Bundle bundle = data.getExtras();
				if (bundle == null) {
					return;
				}
				if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
					String result = bundle.getString(CodeUtils.RESULT_STRING);
					doGetInfo(result);
				} else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
					Toast.makeText(ErweimaActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void doGetInfo(String result) {
		WebAPIManager.getInstance(ErweimaActivity.this).getCollectionById(result).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.compose(lifecycleProvider.<WebResponse<FlowerEntity>>bindToLifecycle()).subscribe(new Observer<WebResponse<FlowerEntity>>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse<FlowerEntity> webResponse) {
				if (webResponse.getCode().equals("200")) {
					flowernumdispalyEr.setText(webResponse.getData().getFlowerId());
					flowernamedispalyEr.setText(webResponse.getData().getFlowerName());
					flowerintroducedispalyEr.setText(webResponse.getData().getFlowerInfo());
				} else {
					MessageUtils.showLongToast(ErweimaActivity.this, "扫描失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(ErweimaActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});

	}

	public void getCameraPermission() {
		if (Build.VERSION.SDK_INT > 22) {
			if (ContextCompat.checkSelfPermission(ErweimaActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				//先判断有没有权限 ，没有就在这里进行权限的申请
				ActivityCompat.requestPermissions(ErweimaActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
			} else {
			}
		} else {
		}
	}


	@OnClick(R.id.erweimaButton)
	public void onClick() {
		Intent intent = new Intent(ErweimaActivity.this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
	}
}
