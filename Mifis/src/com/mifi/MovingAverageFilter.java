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

public class MovingAverageFilter extends Filter
{
	protected GenericFilterInstance makeFilter()
	{
		return new MovingAverageFilterInstance(4);
	}
}
