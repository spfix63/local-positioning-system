package com.mifi;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MapMetadataDownloadHandler implements OnRequestCompleteListener
{

	private Context m_context;
	private OnDownloadCompleteListener m_listener;
	
	public MapMetadataDownloadHandler(Context context,
			OnDownloadCompleteListener listener)
	{
		m_context = context;
		m_listener = listener;
	}

	@Override
	public void onRequestComplete(String result)
	{
		if (result != null)
		{
			SharedPreferences prefs = m_context.getSharedPreferences("internet_list", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.clear();
			
			MapMetadata mm = MapMetadata.fromJson(result);
			for (MapMetadatum mmm : mm.getData())
			{
				try
				{
					MapInfo info = new MapInfo(UUID.fromString(mmm.m_id));
					info.setName(mmm.m_name);
					info.setDescription(mmm.m_description);
					editor.putString(mmm.m_id, MapInfo.toJson(info));
				}
				catch (Exception e)
				{
					Log.d("Mifi", "Bad map UUID");
					e.printStackTrace();
				}
			}
			editor.commit();
			
			m_listener.onDownloadComplete();
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
