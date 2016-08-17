package com.mifi;

import com.mifi.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.Toast;

public class LocalMapsFragment extends ListFragment
	implements OnClickListener, OnUploadCompleteListener
{
	private MapProxyArrayAdapter m_adapter;
	private boolean m_expand;
	private int m_expandPos;
	private View m_uploadButton;
	
	@Override  
	public void onListItemClick(ListView l, View v, int position, long id) 
	{      
		if (m_expandPos != position)
			m_expand = true;
		else
			m_expand = !m_expand;
		m_expandPos = position;
		
		m_adapter.setExpand(m_expandPos, m_expand);
		m_adapter.notifyDataSetChanged();
	}  
	  
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  
			Bundle savedInstanceState) 
	{  
		setupAdapter();
		return super.onCreateView(inflater, container, savedInstanceState);  
	}  

    public void onClick(View v) {
    	Intent i;
    	MapInfo map;
        switch (v.getId()) 
        {
        case R.id.imageButton_use:
        	i = new Intent(getActivity(), PositioningActivity.class);
        	map = m_adapter.getItem(m_expandPos);
    		i.putExtra("current_map_id",""+map.getMapId());
            startActivity(i);
            break;
        case R.id.imageButton_edit:
        	i = new Intent(getActivity(), CreateMapActivity.class);
        	map = m_adapter.getItem(m_expandPos);
    		i.putExtra("current_map_id",""+map.getMapId());
            startActivity(i);
            break;
        case R.id.imageButton_delete:

    		m_expand = false;
    		m_adapter.setExpand(m_expandPos, m_expand);
    		
        	MapInfo m = m_adapter.getItem(m_expandPos);
        	MapStorage.deleteLocalMap(getActivity(), m);

        	setupAdapter();
    		
    		Log.d("Map", "Delete");
            break;

        case R.id.imageButton_upload:
        	
        	m_uploadButton = v;
			m_uploadButton.setEnabled(false);
			
        	map = m_adapter.getItem(m_expandPos);
        	MapStorage.uploadMap(getActivity(), map, this);
        	
        	break;
        }
    }
    
    private void setupAdapter()
    {
    	m_adapter = new MapProxyArrayAdapter(getActivity(), MapStorage.getLocalMaps(getActivity()));
    	m_adapter.setOnClickListener(this);
		setListAdapter(m_adapter);
    }

    @Override
    public void onPause()
    {
    	super.onPause();
    	m_expand = false;
		m_adapter.setExpand(m_expandPos, m_expand);
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
		m_adapter.notifyDataSetChanged();
    }

	@Override
	public void onUploadComplete()
	{
		 Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
		 if (m_uploadButton != null)
		 {
			 m_uploadButton.setEnabled(true);
			 m_uploadButton = null;
		 }
	}

	@Override
	public void onUploadError()
	{
		 Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_SHORT).show();
		 if (m_uploadButton != null)
		 {
			 m_uploadButton.setEnabled(true);
			 m_uploadButton = null;
		 }
	}
}
