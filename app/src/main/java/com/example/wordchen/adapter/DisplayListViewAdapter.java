package com.example.wordchen.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wordchen.bean.FlowerEntity;
import com.example.wordchen.flower.R;
import com.example.wordchen.uitls.GlideX;

import java.util.ArrayList;
import java.util.Collection;

public class DisplayListViewAdapter extends BaseAdapter {
	private ArrayList<FlowerEntity> flower_list;
	private Context context;

	public DisplayListViewAdapter(Context context, ArrayList<FlowerEntity> apk_list) {
		this.flower_list = new ArrayList<>();
		if (apk_list != null) {
			this.flower_list.addAll(apk_list);
		}
		this.context = context;
	}

	public boolean isEmpty() {
		return flower_list.isEmpty();
	}

	public boolean add(FlowerEntity item) {
		boolean success = flower_list.add(item);
		notifyDataSetChanged();
		return success;
	}

	public boolean remove(FlowerEntity item) {
		boolean success = flower_list.remove(item);
		notifyDataSetChanged();
		return success;
	}

	public boolean addAll(@NonNull Collection<? extends FlowerEntity> c) {
		boolean success = flower_list.addAll(c);
		notifyDataSetChanged();
		return success;
	}

	public void clear() {
		flower_list.clear();
		notifyDataSetChanged();
	}

	public void add(int index, FlowerEntity element) {
		flower_list.add(index, element);
		notifyDataSetChanged();
	}

	public FlowerEntity remove(int index) {
		FlowerEntity item = flower_list.remove(index);
		notifyDataSetChanged();
		return item;
	}

	public int indexOf(FlowerEntity item) {
		return flower_list.indexOf(item);
	}


	@Override
	public int getCount() {
		return flower_list.size();
	}

	@Override
	public Object getItem(int position) {
		return flower_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FlowerEntity entity = flower_list.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_layout, null);
			holder.flowerid = convertView.findViewById(R.id.flowerinfo_id);
			holder.flowername = convertView.findViewById(R.id.flowerName);
			holder.flowerpic2 = convertView.findViewById(R.id.flower_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.flowerid.setText(entity.getFlowerId());
		holder.flowername.setText(entity.getFlowerName());
		GlideX.getInstance().loadImage(context, entity.getFlowerAvatarUrl(), holder.flowerpic2);
		return convertView;
	}

	class ViewHolder {
		TextView flowerid;
		TextView flowername;
		ImageView flowerpic2;
	}
}
