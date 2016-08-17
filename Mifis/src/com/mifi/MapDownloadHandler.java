package com.mifi;

import java.io.FileOutputStream;

import android.content.Context;
import android.content.SharedPreferences;

public class MapDownloadHandler implements OnRequestCompleteListener
{
	private MapInfo m_map;
	private Context m_context;
	private OnDownloadCompleteListener m_listener;
	
	public MapDownloadHandler(Context context, MapInfo item,
			OnDownloadCompleteListener l)
	{
		m_map = item;
		m_context = context;
		m_listener = l;
	}

	@Override
	public void onRequestComplete(String result)
	{
		if (result != null)
		{
			try
			{
				String jsonRepresentation = result;
				String uuid = m_map.getMapId().toString();
				
				FileOutputStream fos = m_context.openFileOutput(uuid, Context.MODE_PRIVATE);
				fos.write(jsonRepresentation.getBytes());
				fos.close();
				
				SharedPreferences prefs = m_context.getSharedPreferences("download_list", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(uuid, MapInfo.toJson(m_map));
				editor.commit();

				m_listener.onDownloadComplete();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				m_listener.onDownloadError();
			}
			
		}
		else
		{
			m_listener.onDownloadError();
		}
	}

	@Override
	public void onRequestError()
	{
		m_listener.onDownloadError();
	}

}
