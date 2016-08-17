package com.mifi;

import android.content.Context;


public class MapUploadHandler implements OnRequestCompleteListener
{

	private Context m_context;
	private OnUploadCompleteListener m_listener;
	
	public MapUploadHandler(Context context, OnUploadCompleteListener listener)
	{
		m_context = context;
		m_listener = listener;
	}

	@Override
	public void onRequestComplete(String result)
	{

		m_listener.onUploadComplete();
		
	}

	@Override
	public void onRequestError()
	{
		m_listener.onUploadError();
		
	}

}
