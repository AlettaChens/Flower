package com.example.wordchen.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.wordchen.adapter.FuncListAdapter;
import com.example.wordchen.base.BaseActivity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.FlowerSP;
import com.example.wordchen.uitls.GlideX;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

	@BindView(R.id.rv_function_list)
	RecyclerView rvFunctionList;
	@BindView(R.id.iv_person)
	ImageView ivPerson;
	private FuncListAdapter funcListAdapter;
	private List<String> funlist;
	private FlowerSP flowerSP;


	@Override
	protected int getContentViewId() {
		return R.layout.activity_main;
	}

	@Override
	public void onInit() {
		if (funlist == null) {
			funlist = new ArrayList<>();
		}
		funlist.clear();
		funlist.add("卡片注册");
		funlist.add("花卉入库");
		funlist.add("NFC读卡");
		funlist.add("图码读取");
		funlist.add("列表展示");
		funlist.add("图表统计");
		flowerSP = new FlowerSP(MainActivity.this);

	}


	@Override
	protected void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(flowerSP.getAvatarURL())) {
			GlideX.getInstance().loadImage(MainActivity.this, flowerSP.getAvatarURL(), ivPerson);
		}
	}

	@Override
	public void onBindData() {
		funcListAdapter = new FuncListAdapter(MainActivity.this, funlist, new FuncListAdapter.onClickHandler() {
			@Override
			public void onClick(String name) {
				switch (name) {
					case "NFC读卡": {
						startActivity(new Intent(MainActivity.this, NFCReaderActivity.class));
						break;
					}
					case "图码读取": {
						startActivity(new Intent(MainActivity.this, ErweimaActivity.class));
						break;
					}
					case "卡片注册": {
						startActivity(new Intent(MainActivity.this, CardRegister.class));
						break;
					}
					case "花卉入库": {
						startActivity(new Intent(MainActivity.this, AddActivity.class));
						break;
					}
					case "列表展示": {
						startActivity(new Intent(MainActivity.this, DisplayActivity.class));
						break;
					}
					case "图表统计": {
						startActivity(new Intent(MainActivity.this, CountActivity.class));
						break;
					}
				}

			}
		});
		GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
		rvFunctionList.setLayoutManager(gridLayoutManager);
		rvFunctionList.setAdapter(funcListAdapter);
	}


	@OnClick(R.id.iv_person)
	public void onClick() {
		startActivity(new Intent(MainActivity.this, ManagerActivity.class));
	}
}

