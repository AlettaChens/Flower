package com.example.wordchen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.LQRPhotoSelectUtils;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.example.wordchen.widget.CircleImageView;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.wordchen.uitls.NfcUtils.readNfcTag;


public class AddActivity extends BaseActivity {
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	@BindView(R.id.flowerimg_select)
	CircleImageView flowerimgSelect;
	@BindView(R.id.edit_flowernum)
	EditText editFlowernum;
	@BindView(R.id.edit_flowername)
	EditText editFlowername;
	@BindView(R.id.edit_flowerinfo1)
	EditText editFlowerinfo1;
	@BindView(R.id.btn_add)
	Button btnAdd;
	private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
	private File outPutFile;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;
	protected NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_flower_add;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
		mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(AddActivity.this, new LQRPhotoSelectUtils.PhotoSelectListener() {
			@Override
			public void onFinish(final File outputFile, Uri outputUri) {
				outPutFile = outputFile;
				Glide.with(AddActivity.this).load(outputUri).into(flowerimgSelect);
			}
		}, true);
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		editFlowernum.setText(readNfcTag(intent));
	}


	protected void showChoosePicDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("设置头像");
		String[] items = {"选择本地照片", "拍照"};
		builder.setNegativeButton("取消", null);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case CHOOSE_PICTURE:
						PermissionGen.needPermission(AddActivity.this, LQRPhotoSelectUtils.REQ_SELECT_PHOTO, new String[]{Manifest.permission
								.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
						break;
					case TAKE_PICTURE:
						PermissionGen.with(AddActivity.this).addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO).permissions(Manifest.permission
								.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).request();
						break;
				}
			}
		});
		builder.create().show();
	}


	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("权限申请");
		builder.setMessage("在设置-应用-权限中开启相机、存储权限，才能正常使用拍照或图片选择功能");
		builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + AddActivity.this.getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}


	@PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
	private void takePhoto() {
		mLqrPhotoSelectUtils.takePhoto();
	}


	@PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
	private void selectPhoto() {
		mLqrPhotoSelectUtils.selectPhoto();
	}

	@PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
	private void showTip1() {
		showDialog();
	}

	@PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
	private void showTip2() {
		showDialog();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
	}

	@OnClick({R.id.flowerimg_select, R.id.btn_add})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.flowerimg_select:
				showChoosePicDialog();
				break;
			case R.id.btn_add:
				doAddFlower();
				break;
		}
	}

	private void doAddFlower() {
		if (outPutFile == null) {
			MessageUtils.showLongToast(AddActivity.this, "未选择图片");
			return;
		}
		RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), outPutFile);
		MultipartBody.Part body = MultipartBody.Part.createFormData("file", outPutFile.getName(), requestBody);
		WebAPIManager.getInstance(AddActivity.this).publish(editFlowername.getText().toString(), editFlowerinfo1.getText().toString(), body, editFlowernum
				.getText().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(lifecycleProvider
				.<WebResponse>bindToLifecycle()).subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					MessageUtils.showLongToast(AddActivity.this, "入库成功");
				} else {
					MessageUtils.showLongToast(AddActivity.this, "入库失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(AddActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
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



