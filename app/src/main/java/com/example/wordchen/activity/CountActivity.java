package com.example.wordchen.activity;


import android.arch.lifecycle.Lifecycle;

import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.bean.CountDataBean;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.MessageUtils;
import com.example.wordchen.web.WebAPIManager;
import com.example.wordchen.web.WebResponse;
import com.example.wordchen.widget.ChartView;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CountActivity extends BaseActivity {
	@BindView(R.id.chartview)
	ChartView chartview;
	private List<String> xValue;
	private List<Integer> yValue;
	private Map<String, Integer> value;
	private LifecycleProvider<Lifecycle.Event> lifecycleProvider;

	@Override
	protected int getContentViewId() {
		return R.layout.activity_piepicture;
	}

	@Override
	public void onInit() {
		if (xValue == null) {
			xValue = new ArrayList<>();
		}
		if (yValue == null) {
			yValue = new ArrayList<>();
		}

		if (value == null) {
			value = new HashMap<>();
		}
		lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
	}

	@Override
	public void onBindData() {
		setPieChartData();
	}

	private void setPieChartData() {
		WebAPIManager.getInstance(CountActivity.this).getInfoCount().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose
				(lifecycleProvider.<WebResponse<List<CountDataBean>>>bindToLifecycle()).subscribe(new Observer<WebResponse<List<CountDataBean>>>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(WebResponse<List<CountDataBean>> webResponse) {
				if (webResponse.getCode().equals("200")) {
					for (int i = 0; i < webResponse.getData().size(); i++) {
						xValue.add(webResponse.getData().get(i).getFlowername());
						yValue.add(i * 10);
						value.put(webResponse.getData().get(i).getFlowername(), webResponse.getData().get(i).getFlowercount());
					}
					chartview.setValue(value, xValue, yValue);
				} else {
					MessageUtils.showLongToast(CountActivity.this, "入库失败");
				}

			}

			@Override
			public void onError(Throwable e) {
				MessageUtils.showLongToast(CountActivity.this, e.toString());
			}

			@Override
			public void onComplete() {

			}
		});

	}
}
