package com.mifi;

import java.util.ArrayList;

import com.mifi.R;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InternetMapArrayAdapter extends ArrayAdapter<MapInfo> {
	private final Context context;
	private final ArrayList<MapInfo> values;

	private int m_expanded;
	private boolean m_doExpand;
	private OnClickListener m_onButtonClickListener;

	public InternetMapArrayAdapter(Context context, ArrayList<MapInfo> list) {
		super(context, R.layout.internetmaps_list_item_expanded, list);
		this.context = context;
		this.values = list;
	}
	
	public void setExpand(int position, boolean expand)
	{
		m_doExpand = expand;
		m_expanded = position;
	}
	
	public void setOnClickListener(OnClickListener l)
	{
		m_onButtonClickListener = l;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (m_doExpand && position == m_expanded)
		{
			rowView = inflater.inflate(R.layout.internetmaps_list_item_expanded, parent, false);
			ImageView expandView = (ImageView) rowView.findViewById(R.id.expand_icon);
			expandView.setImageResource(R.drawable.ic_action_collapse);
			TextView descView = (TextView) rowView.findViewById(R.id.item_desc);
			descView.setText(values.get(position).getDescription());
			

			ImageButton btn = (ImageButton) rowView.findViewById(R.id.imageButton_download);
	        btn.setFocusable(false);
	        btn.setOnClickListener(m_onButtonClickListener);
			
		}
		else
		{
			rowView = inflater.inflate(R.layout.internetmaps_list_item, parent, false);
			ImageView expandView = (ImageView) rowView.findViewById(R.id.expand_icon);
			expandView.setImageResource(R.drawable.ic_action_expand);
		}


		TextView nameView = (TextView) rowView.findViewById(R.id.mapid);
		String name = values.get(position).getName();
		if (name == null || name.trim().length() == 0)
			name = values.get(position).getMapId().toString();
		nameView.setText((position + 1) + ". " + name);
		
		return rowView;
	}
	
	
} 