package com.mifi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MapMetadatum
{

	public String m_name;
	public String m_description;
	public String m_id;

	public static String toJson(MapMetadatum map)
	{
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static MapMetadatum fromJson(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, MapMetadatum.class);
	}
}
