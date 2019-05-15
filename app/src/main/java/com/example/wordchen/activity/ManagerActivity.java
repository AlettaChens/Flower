package com.example.wordchen.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.DataCleanManager;
import com.example.wordchen.uitls.FlowerSP;
import com.example.wordchen.uitls.GlideX;
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


public class ManagerActivity extends BaseActivity {
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	@BindView(R.id.manager_img)
	CircleImageView managerImg;
	@BindView(R.id.manager_nickname)
	TextView managerNickname;
	@BindView(R.id.update_function1)
	ImageView updateFunction1;
	@BindView(R.id.updateinfo_function1)
	RelativeLayout updateinfoFunction1;
	@BindView(R.id.cache_function)
	ImageView cacheFunction;
	@BindView(R.id.cache_text1)
	TextView cacheText1;
	@BindView(R.id.cache_arr)
	ImageView cacheArr;
	@BindView(R.id.clear_function1)
	RelativeLayout clearFunction1;
	@BindView(R.id.about_function)
	ImageView aboutFunction;
	@BindView(R.id.about_function1)
	RelativeLayout aboutFunction1;
	@BindView(R.id.recomend_function)
	ImageView recomendFunction;
	@BindView(R.id.introduce_function)
	RelativeLayout introduceFunction;
	@BindView(R.id.logout)
	Button logout;
	private FlowerSP flowerSP;
	LQRPhotoSelectUtils mLqrPhotoSelectUtils;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_manager;
	}

	@Override
	public void onInit() {
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
		flowerSP = new FlowerSP(ManagerActivity.this);
		try {
			String data = DataCleanManager.getTotalCacheSize(this);
			cacheText1.setText(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBindData() {
		mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
			@Override
			public void onFinish(File outputFile, Uri outputUri) {
				Glide.with(ManagerActivity.this).load(outputFile).into(managerImg);
				doAvatar(outputFile);
			}
		}, true);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(flowerSP.getAvatarURL())) {
			GlideX.getInstance().loadImage(ManagerActivity.this, flowerSP.getAvatarURL(), managerImg);
		}
		managerNickname.setText(flowerSP.getNickName());
	}

	private void doAvatar(File outputFile) {
		if (outputFile == null) {
			MessageUtils.showLongToast(ManagerActivity.this, "未选择图片");
			return;
		}
		RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), outputFile);
		MultipartBody.Part body = MultipartBody.Part.createFormData("file", outputFile.getName(), requestBody);
		WebAPIManager.getInstance(ManagerActivity.this).uploadUserAvatar(flowerSP.getUserId(), body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
				.mainThread()).compose(lifecycleProvider.<WebResponse>bindToLifecycle()).subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					MessageUtils.showLongToast(ManagerActivity.this, "上传成功");
					flowerSP.putAvatarURL(webResponse.getData().toString());
				} else {
					MessageUtils.showLongToast(ManagerActivity.this, "上传失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(ManagerActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});
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
						PermissionGen.needPermission(ManagerActivity.this, LQRPhotoSelectUtils.REQ_SELECT_PHOTO, new String[]{Manifest.permission
								.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
						break;
					case TAKE_PICTURE:
						PermissionGen.with(ManagerActivity.this).addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO).permissions(Manifest.permission
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
				intent.setData(Uri.parse("package:" + ManagerActivity.this.getPackageName()));
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

	@OnClick({R.id.manager_img, R.id.updateinfo_function1, R.id.clear_function1, R.id.about_function1, R.id.introduce_function})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.manager_img:
				showChoosePicDialog();
				break;
			case R.id.updateinfo_function1:
				Intent it_update = new Intent(ManagerActivity.this, UpdateInfoActivity.class);
				startActivity(it_update);
				break;
			case R.id.clear_function1:
				AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						DataCleanManager.clearAllCache(ManagerActivity.this);
						cacheText1.setText("0.00k");
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
				builder.setTitle("缓存清理确认");
				builder.setMessage("确认要清除缓存吗？");
				builder.show();
				break;
			case R.id.about_function1:
				startActivity(new Intent(ManagerActivity.this, AboutActivity.class));
				break;
			case R.id.introduce_function:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "好友推荐");
				intent.putExtra(Intent.EXTRA_TEXT, "嗨，我正在使用花卉管理系统，可以语音介绍花卉的哦！");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "推荐给好友"));
				break;
		}
	}

}


