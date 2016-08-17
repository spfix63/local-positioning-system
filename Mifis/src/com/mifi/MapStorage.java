package com.mifi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MapStorage
{
	final static public String HOSTNAME = "www.mifis.tk";
	final static public String PORT = "20001";

	static private Mapping s_current;

	static public ArrayList<MapInfo> getLocalMaps(Context context)
	{
		ArrayList<MapInfo> maps = new ArrayList<MapInfo>();
		SharedPreferences prefs = context.getSharedPreferences("local_list", Context.MODE_PRIVATE);
		Map<String,?> keys = prefs.getAll();
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			maps.add(MapInfo.fromJson(entry.getValue().toString()));            
		}
		return maps;
	}
	
	static public ArrayList<MapInfo> getDownloadedMaps(Context context)
	{
		ArrayList<MapInfo> maps = new ArrayList<MapInfo>();
		SharedPreferences prefs = context.getSharedPreferences("download_list", Context.MODE_PRIVATE);
		Map<String,?> keys = prefs.getAll();
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			maps.add(MapInfo.fromJson(entry.getValue().toString()));            
		}
		return maps;
	}

	static public ArrayList<MapInfo> getInternetMaps(Context context)
	{
		ArrayList<MapInfo> maps = new ArrayList<MapInfo>();
		SharedPreferences prefs = context.getSharedPreferences("internet_list", Context.MODE_PRIVATE);
		Map<String,?> keys = prefs.getAll();
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			maps.add(MapInfo.fromJson(entry.getValue().toString()));            
		}
		return maps;
	}

	static public DownloadTask downloadInternetMapMetadata(Context context, OnDownloadCompleteListener listener)
	{
		DownloadTask task = new DownloadTask(new MapMetadataDownloadHandler(context, listener));
		task.execute("http://"+HOSTNAME+":"+PORT+"/KursinisServlets/Positioning?action=metadata");
		return task;
	}

	static public Mapping getCurrentMap()
	{
		return s_current;
	}
	
	static public void setCurrentMap(Mapping mp)
	{
		s_current = mp;
	}
	
	static public Mapping loadLocalMap(Context context, UUID mapid) throws Exception
	{

		Mapping ret = null;
		BufferedReader bufferedReader = null;
	    try 
	    {
	    	bufferedReader = new BufferedReader(
	    		new InputStreamReader(context.openFileInput(mapid.toString()))
	    		);
	    	
	    	StringBuilder sb = new StringBuilder();
	    	String line;
	    	while ((line = bufferedReader.readLine()) != null) {
	    		sb.append(line);
	    	}
	    
	    	bufferedReader.close();

	    	Log.d("Map", ""+mapid.toString()+" "+sb.toString());
	    	ret = Mapping.fromJson(sb.toString());
	    } 
	    finally 
	    {
	    	try 
	    	{
				if (bufferedReader != null)
					bufferedReader.close();
		    } 
	    	catch (IOException e) 
	    	{ 
		    	e.printStackTrace();
		    }    
	    }
		return ret;
	}
	

	public static void saveLocalMap(Context context, Mapping m_map) throws IOException
	{

    	
		MapInfo info = m_map.getInfo();
		String mapId = info.getMapId().toString();
		
		String jsonRepresentation = Mapping.toJson(m_map);

    	Log.d("Map_save", jsonRepresentation);
    	
		FileOutputStream fos = context.openFileOutput(mapId, Context.MODE_PRIVATE);
		fos.write(jsonRepresentation.getBytes());
		fos.close();
		
		SharedPreferences prefs = context.getSharedPreferences("local_list", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(mapId, MapInfo.toJson(info));
		editor.commit();
	}

	public static void deleteLocalMap(Context context, MapInfo m)
	{
		String filename = m.getMapId().toString();
		SharedPreferences prefs = context.getSharedPreferences("local_list", Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.remove(filename);
		e.commit();
		
		context.deleteFile(filename);		
	}
	
	public static void deleteDownloadedMap(Context context, MapInfo m)
	{
		String filename = m.getMapId().toString();
		SharedPreferences prefs = context.getSharedPreferences("download_list", Context.MODE_PRIVATE);
		SharedPreferences.Editor e = prefs.edit();
		e.remove(filename);
		e.commit();
		
		context.deleteFile(filename);		
	}
	

	public static UploadTask uploadMap(Context context, 
			MapInfo map, OnUploadCompleteListener listener)
	{

    	UploadTask task = null;
    	MapMetadatum mmm = new MapMetadatum();
    	mmm.m_id = map.getMapId().toString();
    	mmm.m_name = map.getName();
    	mmm.m_description = map.getDescription();
    	try
		{
			Mapping mapping = loadLocalMap(context, map.getMapId());
			if (mapping != null) 
			{
				task = (UploadTask) new UploadTask(new MapUploadHandler(context, listener));
				task.execute("http://"+HOSTNAME+":"+PORT+"/KursinisServlets/Positioning",
        			"action", "insert", "metadatum", MapMetadatum.toJson(mmm), "map", Mapping.toJson(mapping));
			}
		}
		catch (Exception e)
		{
			listener.onUploadError();
		}
		return task;
	}

	public static DownloadTask downloadMap(Context context,
			MapInfo map, OnDownloadCompleteListener l)
	{
		DownloadTask task = new DownloadTask(new MapDownloadHandler(context, map, l));
		task.execute("http://"+HOSTNAME+":"+PORT+"/KursinisServlets/Positioning?action=json&uuid="+map.getMapId());
		return task;
	}


}
