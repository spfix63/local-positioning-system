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

public class MapArrayAdapter extends ArrayAdapter<MapPoint> {
	private final Context context;
	private final ArrayList<MapPoint> values;

	public MapArrayAdapter(Context context, ArrayList<MapPoint> m_list) {
		super(context, R.layout.create_map_list_item, m_list);
		this.context = context;
		this.values = m_list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.create_map_list_item, parent, false);

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
		
		ImageButton btn = (ImageButton) rowView.findViewById(R.id.imageButton_delete);
        btn.setFocusable(false);
		
		return rowView;
	}
} 