package com.mifi;

import com.mifi.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class InternetMapsFragment extends ListFragment
	implements OnClickListener, OnDownloadCompleteListener
{
	private InternetMapArrayAdapter m_adapter;
	private boolean m_expand;
	private int m_expandPos;

	private ImageButton m_downloadButton;
	private DownloadTask m_downloadTask;
	private boolean m_downloadInProgress;
	
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
		loadMaps();
		return super.onCreateView(inflater, container, savedInstanceState);  
	}  

    private void loadMaps()
	{
		MapStorage.downloadInternetMapMetadata(getActivity(), this);
	}

	public void onClick(View v) {
        switch (v.getId()) 
        {
        case R.id.imageButton_download:
    		m_downloadButton = (ImageButton)v;
        	if (m_downloadInProgress)
        	{
        	
        		m_downloadTask = null;
            	m_downloadInProgress = false;
        	}
        	else
        	{
        		m_downloadTask = MapStorage.downloadMap(getActivity(), m_adapter.getItem(m_expandPos), this);
            	m_downloadInProgress = true;
            	m_downloadButton.setImageResource(R.drawable.ic_action_remove);
        	}
        	
            break;
        }
    }
    
    private void setupAdapter()
    {
    	m_adapter = new InternetMapArrayAdapter(getActivity(), MapStorage.getInternetMaps(getActivity()));
    	m_adapter.setOnClickListener(this);
		setListAdapter(m_adapter);
    }

	@Override
	public void onDownloadComplete()
	{
		if (m_downloadInProgress)
		{
	    	m_downloadInProgress = false;
	    	if (m_downloadButton != null)
			{
				m_downloadButton.setImageResource(R.drawable.ic_action_download);
			}
	    	Toast.makeText(getActivity(), "Download successful", Toast.LENGTH_SHORT).show();
		}
		else
		{
			setupAdapter();	
		}
	}

	@Override
	public void onDownloadError()
	{
		m_downloadInProgress = false;
		if (m_downloadButton != null)
		{
			m_downloadButton.setImageResource(R.drawable.ic_action_download);
		}
	}

    @Override
    public void onPause()
    {
    	super.onPause();
    	if (m_adapter != null)
    	{
    		m_expand = false;
			m_adapter.setExpand(m_expandPos, m_expand);
    	}
    }
    
}
