package com.mifi;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class KalmanFilterInstance extends GenericFilterInstance
{
	private KalmanFilter m_filter;
	
	public KalmanFilterInstance()
	{
		
	}
	
	
	@Override
	public double update(double data)
	{
		double newval;
		if (m_filter == null)
		{
			/*
		 	A - state transition matrix
			B - control input matrix
			H - measurement matrix
			Q - process noise covariance matrix
			R - measurement noise covariance matrix
			P - error covariance matrix 
			*/
			RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
			RealMatrix B = new Array2DRowRealMatrix(new double[]{ 1d });
			RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
			RealVector x = new ArrayRealVector(new double[] { data });
			RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0 });
			RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 10d });
			RealMatrix R = new Array2DRowRealMatrix(new double[] { 10d });

			ProcessModel pm = new DefaultProcessModel(A, B, Q, x, P0);
			MeasurementModel mm = new DefaultMeasurementModel(H, R);
			
			m_filter = new KalmanFilter(pm, mm);  
			
			newval = data;
		}
		else
		{
			RealVector z = new ArrayRealVector(1);
			m_filter.predict();
		    z.setEntry(0, data);
		    m_filter.correct(z);
		    
		    newval = m_filter.getStateEstimation()[0];
		}
		
		return newval;
	}
}
