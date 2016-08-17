package com.mifi;

import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Mapping 
{
	private MapInfo m_info;
	private ArrayList<MapPoint> m_list;
	
	public Mapping()
	{
		m_info = new MapInfo(UUID.randomUUID());
		m_list = new ArrayList<MapPoint>();
	}
	
	public Mapping(UUID m_mapId)
	{
		m_info = new MapInfo(m_mapId);
		m_list = new ArrayList<MapPoint>();
	}

	public static String toJson(Mapping map)
	{
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static Mapping fromJson(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, Mapping.class);
	}

	public MapInfo getInfo()
	{
		return m_info;
	}

	public void setInfo(MapInfo info)
	{
		m_info = info;
	}

	public ArrayList<MapPoint> getData()
	{
		return m_list;
	}

	public void addPoint(MapPoint mp)
	{
		m_list.add(mp);
	}
	
	public void clearData()
	{
		m_list.clear();
	}

	public void removePoint(int index)
	{
		m_list.remove(index);
	}

	public MapPoint getPoint(int index)
	{
		return m_list.get(index);
	}

	public int getPointCount()
	{
		return m_list.size();
	}
	
}
