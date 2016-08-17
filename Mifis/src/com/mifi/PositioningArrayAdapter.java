package com.mifi;

import java.util.ArrayList;
import java.util.Collections;

import com.mifi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PositioningArrayAdapter extends ArrayAdapter<MapPoint> {
	private final Context context;
	private final ArrayList<MapPoint> values;
	private final ArrayList<Boolean> current;

	public PositioningArrayAdapter(Context context, ArrayList<MapPoint> list, ArrayList<Boolean> current) {
		super(context, R.layout.positioning_list_item, list);
		this.context = context;
		this.values = list;
		this.current = current;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (values.get(position).hasData())
		{
			if (current.get(position))
				rowView = inflater.inflate(R.layout.positioning_list_item_current, parent, false);
			else	
				rowView = inflater.inflate(R.layout.positioning_list_item, parent, false);
		
		}
		else
			rowView = inflater.inflate(R.layout.positioning_list_item_disabled, parent, false);
		
		TextView nameView = (TextView) rowView.findViewById(R.id.firstLine);
		nameView.setText((position + 1) + ". " + values.get(position).getName());
		
		TextView descView = (TextView) rowView.findViewById(R.id.secondLine);
		descView.setText(values.get(position).getDescription());
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		if (values.get(position).hasData()) {
			imageView.setImageResource(R.drawable.ic_action_accept);
		} else {
			imageView.setImageResource(R.drawable.ic_action_edit);
		}
		
		return rowView;
	}
} 