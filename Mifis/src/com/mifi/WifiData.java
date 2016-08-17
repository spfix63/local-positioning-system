package com.mifi;

import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiData
{
	private int m_min;
	private int m_max;
	private int m_last;
	private String m_name;
	private String m_mac;
	
	private boolean m_hasData;
	
	public WifiData(String name, String mac)
	{
		m_name = name;
		m_mac = mac;
	}
	
	public void parseData(ScanResult sr)
	{
		m_last = sr.level;
		if (!m_hasData)
		{
			m_max = sr.level;
			m_min = sr.level;
			m_hasData = true;
		}
		else 
		{
			if (m_max < sr.level)
				m_max = sr.level;
			if (m_min > sr.level)
				m_min = sr.level;
		}
	}

	public int getMax()
	{
		return m_max;
	}
	
	public int getMin()
	{
		return m_min;
	}
	
	public int getLast()
	{
		return m_last;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String getMac()
	{
		return m_mac;
	}
	
	public boolean hasData()
	{
		return m_hasData;
	}
	
	public void clearData()
	{
		m_hasData = false;
	}
}
