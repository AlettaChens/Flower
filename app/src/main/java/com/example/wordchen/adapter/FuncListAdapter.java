package com.example.wordchen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wordchen.flower.R;

import java.util.List;

public class FuncListAdapter extends RecyclerView.Adapter<FuncListAdapter.ListFuncHolder> {
	private Context context;
	private List<String> funcName;
	private onClickHandler onClickHandler;

	public FuncListAdapter(Context context, List<String> funcName, FuncListAdapter.onClickHandler onClickHandler) {
		this.context = context;
		this.funcName = funcName;
		this.onClickHandler = onClickHandler;
	}

	public ListFuncHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ListFuncHolder(LayoutInflater.from(context).inflate(R.layout.item_adapter_func_, parent, false));
	}

	@Override
	public void onBindViewHolder(ListFuncHolder holder, int position) {
		holder.update(funcName.get(position), position);
	}

	@Override
	public int getItemCount() {
		return funcName.size();
	}

	public class ListFuncHolder extends RecyclerView.ViewHolder {

		private int position;
		private TextView tv_func;
		private ImageView iv_func;

		public ListFuncHolder(View itemView) {
			super(itemView);
			tv_func = itemView.findViewById(R.id.tv_func);
			iv_func = itemView.findViewById(R.id.iv_func);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (onClickHandler != null) {
						onClickHandler.onClick(funcName.get(position));
					}
				}
			});
		}

		public void update(String s, int position) {
			this.position = position;
			tv_func.setText(s);
			switch (s) {
				case "NFC读卡": {
					iv_func.setBackgroundResource(R.drawable.nfc);
					break;
				}
				case "图码读取": {
					iv_func.setBackgroundResource(R.drawable.ma);
					break;
				}
				case "卡片注册": {
					iv_func.setBackgroundResource(R.drawable.card);
					break;
				}
				case "花卉入库": {
					iv_func.setBackgroundResource(R.drawable.insert);
					break;
				}
				case "列表展示": {
					iv_func.setBackgroundResource(R.drawable.list);
					break;
				}
				case "图表统计": {
					iv_func.setBackgroundResource(R.drawable.count);
					break;
				}
			}

		}
	}

	public interface onClickHandler {
		void onClick(String name);
	}
}
