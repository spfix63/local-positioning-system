package com.mifi;

import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MapMetadata
{
	private Vector<MapMetadatum> m_data;
	
	public MapMetadata()
	{
		m_data = new Vector<MapMetadatum>();
	}
	
	public void add(MapMetadatum mm)
	{
		m_data.add(mm);
	}
	
	public Vector<MapMetadatum> getData()
	{
		return m_data;
	}
	
	public static String toJson(MapMetadata map)
	{
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static MapMetadata fromJson(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, MapMetadata.class);
	}
}
