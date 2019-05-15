package com.example.wordchen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.FlowerEntity;
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


public class UpdateFlowerActivity extends BaseActivity {

	@BindView(R.id.update_flowerimg_select1)
	CircleImageView updateFlowerimgSelect1;
	@BindView(R.id.update_edit_flowername1)
	EditText updateEditFlowername1;
	@BindView(R.id.update_edit_flowerinfo1)
	EditText updateEditFlowerinfo1;
	@BindView(R.id.btn_updateflower)
	Button btnUpdateflower;
	private File outPutFile;
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	LQRPhotoSelectUtils mLqrPhotoSelectUtils;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;
	private FlowerEntity flowerEntity;

	@Override
	protected int getContentViewId() {
		return R.layout.updateflower_activity;
	}

	@Override
	public void onInit() {
		flowerEntity = (FlowerEntity) getIntent().getSerializableExtra("item");
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
		mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(UpdateFlowerActivity.this, new LQRPhotoSelectUtils.PhotoSelectListener() {
			@Override
			public void onFinish(final File outputFile, Uri outputUri) {
				outPutFile = outputFile;
				Glide.with(UpdateFlowerActivity.this).load(outputUri).into(updateFlowerimgSelect1);
			}
		}, true);
	}

	@Override
	public void onBindData() {

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
						PermissionGen.needPermission(UpdateFlowerActivity.this, LQRPhotoSelectUtils.REQ_SELECT_PHOTO, new String[]{Manifest.permission
								.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
						break;
					case TAKE_PICTURE:
						PermissionGen.with(UpdateFlowerActivity.this).addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO).permissions(Manifest.permission
								.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).request();
						break;
				}
			}
		});
		builder.create().show();
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

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("权限申请");
		builder.setMessage("在设置-应用-权限 中开启相机、存储权限，才能正常使用拍照或图片选择功能");
		builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + UpdateFlowerActivity.this.getPackageName()));
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


	private void doUpdateFlower() {
		if (outPutFile == null) {
			MessageUtils.showLongToast(UpdateFlowerActivity.this, "未选择图片");
			return;
		}
		RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), outPutFile);
		MultipartBody.Part body = MultipartBody.Part.createFormData("file", outPutFile.getName(), requestBody);
		WebAPIManager.getInstance(UpdateFlowerActivity.this).updateInfo(updateEditFlowername1.getText().toString(), updateEditFlowerinfo1.getText().toString()
				, body, flowerEntity.getFlowerId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(lifecycleProvider.<WebResponse>bindToLifecycle())
				.subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					MessageUtils.showLongToast(UpdateFlowerActivity.this, "更新成功");
				} else {
					MessageUtils.showLongToast(UpdateFlowerActivity.this, "更新失败");
				}
			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(UpdateFlowerActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
	}

	@OnClick({R.id.update_flowerimg_select1, R.id.btn_updateflower})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.update_flowerimg_select1:
				showChoosePicDialog();
				break;
			case R.id.btn_updateflower:
				doUpdateFlower();
				break;
		}
	}
}
