package com.mifi;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import android.net.wifi.ScanResult;
import android.util.Log;

public class Filter
{
	private HashMap<String, GenericFilterInstance> m_filterMap;
	
	public Filter()
	{
		m_filterMap = new HashMap<String, GenericFilterInstance>();
	}
	
	public void flush()
	{
		m_filterMap.clear();
	}
	
	public void update(List<ScanResult> data)
	{
		HashMap<String, GenericFilterInstance> newMap = new HashMap<String, GenericFilterInstance>();
		GenericFilterInstance filter;
		
		for (ScanResult sr : data)
		{
			filter = getFilter(sr);
			newMap.put(sr.BSSID, filter);
			
			sr.level = (int) filter.update(sr.level);
		}
		
		m_filterMap = newMap;
	}

	private GenericFilterInstance getFilter(ScanResult sr)
	{
		GenericFilterInstance filter = null;
		if (m_filterMap.containsKey(sr.BSSID))
			filter = m_filterMap.get(sr.BSSID);
		else
		{
			filter = makeFilter();		
		}
		return filter;
	}

	protected GenericFilterInstance makeFilter()
	{
		return new KalmanFilterInstance();
	}
}
