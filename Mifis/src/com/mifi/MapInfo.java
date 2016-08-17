package com.mifi;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MapInfo
{
	private UUID m_id;
	private String m_name;
	private String m_description;

	public MapInfo(String id)
	{
		m_id = UUID.fromString(id);
	}
	
	public MapInfo(UUID id)
	{
		m_id = id;
	}

	public void setMapId(UUID id)
	{
		m_id = id;
	}
	
	public void setMapId(String id)
	{
		m_id = UUID.fromString(id);
	}

	public UUID getMapId()
	{
		return m_id;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public String getDescription()
	{
		return m_description;
	}

	public void setDescription(String description)
	{
		m_description = description;
	}
	

	public static String toJson(MapInfo map)
	{
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static MapInfo fromJson(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, MapInfo.class);
	}
}
