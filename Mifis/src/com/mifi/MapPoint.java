package com.mifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MapPoint
{
	private String m_name;
	private String m_description;
	private boolean m_hasData;
	private HashMap<String, WifiData> m_vals;
	
	MapPoint()
	{
		m_vals = new HashMap<String, WifiData>();
	}

	public String getName()
	{
		return m_name;
	}
	
	public String getDescription()
	{
		return m_description;
	}
	
	public void setName(String val)
	{
		m_name = val;
	}
	
	public void setDescription(String val)
	{
		m_description = val;
	}
	
	public boolean hasData()
	{
		return m_hasData;
	}
	
	public void parseData(List<ScanResult> list)
	{
		m_hasData = true;
		Log.d("MyApp", "==================================");
		for (ScanResult res : list)
		{
			Log.d("MyApp", "MAC: " + res.BSSID + " Name: " + res.SSID + " Signal strength: " + res.level);
			if (!m_vals.containsKey(res.BSSID))
				m_vals.put(res.BSSID, new WifiData(res.SSID, res.BSSID));
			WifiData wd = m_vals.get(res.BSSID);
			wd.parseData(res);
		}
	}
	
	public ArrayList<WifiData> getData()
	{
		return new ArrayList<WifiData>(m_vals.values());	
	}

	public WifiData getWifiData(String mac)
	{
		return m_vals.get(mac);
	}
	
	public void clearData()
	{
		m_hasData = false;
		m_vals = new HashMap<String, WifiData>();
	}
}
