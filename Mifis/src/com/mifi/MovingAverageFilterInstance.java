package com.mifi;

public class MovingAverageFilterInstance extends GenericFilterInstance
{
	double[] m_data;
	int m_index;
	int m_size;
	boolean m_full;
	
	public MovingAverageFilterInstance(int n)
	{
		m_data = new double[n];
		m_index = 0;
		m_size = n;
		m_full = false;
	}
	
	@Override
	public double update(double data)
	{
		double result = 0;
		if (m_index < m_size)
		{
			m_data[m_index] = data;
			m_index = (m_index + 1) % m_size;
			
			if (m_index == 0)
				m_full = true;

			int size = m_full ? m_size : m_index;
			for (int i = 0; i < size; i++)
			{
				result += m_data[i];
			}
			result = result / size;	
		}
		return result;
	}
}
