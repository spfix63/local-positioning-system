package com.mifi;

import java.util.ArrayList;

import com.mifi.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiDataArrayAdapter extends ArrayAdapter<WifiData> {
	private final Context context;
	private final ArrayList<WifiData> values;

	public WifiDataArrayAdapter(Context context, ArrayList<WifiData> list) {
		super(context, R.layout.wifidata_list_item, list);
		this.context = context;
		this.values = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.wifidata_list_item, parent, false);

		TextView nameView = (TextView) rowView.findViewById(R.id.wifiname);
		nameView.setText((position + 1) + ". " + values.get(position).getName());
		TextView macView = (TextView) rowView.findViewById(R.id.macaddr);
		macView.setText("" + values.get(position).getMac());
		TextView maxView = (TextView) rowView.findViewById(R.id.maxval);
		maxView.setText("" + values.get(position).getMax());
		TextView minView = (TextView) rowView.findViewById(R.id.minval);
		minView.setText("" + values.get(position).getMin());
		TextView lastView = (TextView) rowView.findViewById(R.id.lastval);
		lastView.setText("" + values.get(position).getLast());
		
		return rowView;
	}
} 