package com.example.wordchen.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.wordchen.adapter.DisplayListViewAdapter;
import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.FlowerEntity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.example.wordchen.widget.XListView;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DisplayActivity extends BaseActivity implements XListView.IXListViewListener {

	@BindView(R.id.flowerInfo_list)
	XListView flowerInfoList;
	private int pageNumber = 1;
	private String id;

	DisplayListViewAdapter adapter;
	ArrayList<FlowerEntity> flower_list;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_display;
	}

	@Override
	public void onInit() {
		if (flower_list == null) {
			flower_list = new ArrayList<>();
		}
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);

	}

	@Override
	public void onBindData() {
		adapter = new DisplayListViewAdapter(DisplayActivity.this, flower_list);
		flowerInfoList.setPullLoadEnable(true);
		flowerInfoList.setPullRefreshEnable(true);
		flowerInfoList.setXListViewListener(this);
		flowerInfoList.setAdapter(adapter);
		setListener();

	}

	@Override
	protected void onResume() {
		super.onResume();
		doRequest(pageNumber);
	}

	@Override
	public void onRefresh() {
		adapter.clear();
		pageNumber = 1;
		doRequest(pageNumber);
	}

	@Override
	public void onLoadMore() {
		pageNumber++;
		doRequest(pageNumber);
	}


	private void setListener() {
		flowerInfoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				FlowerEntity newsInfo = flower_list.get(position - 1);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("item", newsInfo);
				intent.setClass(DisplayActivity.this, UpdateFlowerActivity.class);
				intent.putExtras(bundle);
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
		flowerInfoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
				id = flower_list.get(pos - 1).getFlowerId();
				AlertDialog.Builder builder = new AlertDialog.Builder(DisplayActivity.this);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						doDelete(id);
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
				builder.setTitle("删除确认");
				builder.setMessage("确认删除这条数据吗？");
				builder.show();
				return true;
			}
		});
	}

	private void doDelete(String long_position) {
		WebAPIManager.getInstance(DisplayActivity.this).deleteInfoById(long_position).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.compose(lifecycleProvider.<WebResponse>bindToLifecycle()).subscribe(new Observer<WebResponse>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse webResponse) {
				if (webResponse.getCode().equals("200")) {
					adapter.clear();
					pageNumber = 1;
					doRequest(pageNumber);
					MessageUtils.showLongToast(DisplayActivity.this, "出库成功");
				} else {
					MessageUtils.showLongToast(DisplayActivity.this, "出库失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(DisplayActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});

	}

	private void doRequest(int pageNumber) {
		WebAPIManager.getInstance(DisplayActivity.this).getInfoByPage(pageNumber, Integer.MAX_VALUE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
				.mainThread()).compose(lifecycleProvider.<WebResponse<List<FlowerEntity>>>bindToLifecycle()).subscribe(new Observer<WebResponse<List<FlowerEntity>>>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse<List<FlowerEntity>> webResponse) {
				if (webResponse.getCode().equals("200")) {
					flower_list.addAll(webResponse.getData());
					adapter.addAll(webResponse.getData());
				} else {
					MessageUtils.showLongToast(DisplayActivity.this, "请求失败");
				}
			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(DisplayActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});

	}
}
