package com.mifi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import mdsj.MDSJ;
import mdsj.StressMinimization;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TopologyView extends View
{
	private Context m_context;
	private WifiManager m_wifi;
	private BroadcastReceiver m_receiver;
	private String m_debugStr;
	
	private Point m_currentPos;
	private Vector<Point> m_posVector;
	private boolean m_newPos;
	
	private CanvasGrid m_grid;
	private Bitmap m_wifiIcon;
	
	private boolean m_knowPositions;
	private boolean m_useMds;
	private int m_scanCount;
	
	private Point m_wifiPos;

	private HashMap<String, Vector<Circle>> m_dataCache;
	private boolean m_doscan = false;
	
	public TopologyView(Context context)
	{
		super(context);
		init(context);
	}

	public TopologyView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public TopologyView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context);
	}


	private void init(Context context)
	{
		m_context = context;
		m_dataCache = new HashMap<String, Vector<Circle>>();
		m_debugStr = new String();
		
		m_newPos = false;
		m_posVector = new Vector<Point>();
		m_currentPos = new Point();
		m_wifiPos = new Point();
		
		m_knowPositions = true;
		m_useMds = true;
		m_scanCount = 0;
		
        m_wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		m_receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                	//m_context.unregisterReceiver(m_receiver);
            		
                	WifiManager mWm = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    //if (mWm.isWifiEnabled())
                    {
                    	TopologyView.this.updateResults(mWm.getScanResults());
                		Log.d("Wi-fi", "Got results");
                    }
                    m_wifi.startScan();
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        };
	}
	
	
	public void scan()
	{
		m_context.registerReceiver(m_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		m_wifi.startScan();
		m_doscan  = true;
	}
	
	public void reset()
	{
		m_dataCache.clear();
		m_newPos = false;
		m_posVector.clear();
		
		m_scanCount = 0;
		
		postInvalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			m_currentPos.x = (int) event.getX();
			m_currentPos.y = (int) event.getY();
			
			//m_grid.adjustToIntersection(m_currentPos);
			m_grid.adjustToMiddle(m_currentPos);
			
			Log.d("Topo", "x="+m_currentPos.x+" y="+m_currentPos.y);
			m_newPos = true;
			
			postInvalidate();
		}
		return true;
	}
	
	
	protected void updateResults(List<ScanResult> scanResults)
	{
		
		m_debugStr = new String("R: ");

		
		for (ScanResult sr : scanResults)
		{

			if (sr.SSID.equals("TEO-822149"))
			//if (sr.level > -70)
				m_debugStr += sr.SSID + ": " + sr.level + "  " + sr.frequency + " ";
		}
		if (!m_doscan)
		{
			postInvalidate();
			return;
		}
		m_doscan = false;
		if (m_newPos)
		{
			m_scanCount++;
			m_posVector.add(new Point(m_currentPos.x, m_currentPos.y));
		}
		
		for (ScanResult sr : scanResults)
		{

			if (sr.SSID.equals("TEO-822149"))
			//if (sr.level > -70)
			{
				if (!m_newPos)
				{
					
					if (!m_dataCache.containsKey(sr.BSSID))
					{
						m_dataCache.put(sr.BSSID, new Vector<Circle>());
					}
					Vector<Circle> v = m_dataCache.get(sr.BSSID);
					while ((v.size() + 1) < m_posVector.size())
					{
						v.add(new Circle(0, 0, 100));
					}
					v.lastElement().x = m_currentPos.x;
					v.lastElement().y = m_currentPos.y;
					v.lastElement().r = calculateDistance(sr.level, sr.frequency);
					Log.d("Yo", "level="+sr.level+" freq="+sr.frequency+" d="+calculateDistance(sr.level, sr.frequency));
					
				}
				else
				{
					
					if (!m_dataCache.containsKey(sr.BSSID))
					{
						m_dataCache.put(sr.BSSID, new Vector<Circle>());
					}
					Vector<Circle> v = m_dataCache.get(sr.BSSID);
					while ((v.size() + 1) < m_posVector.size())
					{
						v.add(new Circle(0, 0, 100));
					}
					v.add(new Circle(m_currentPos.x, m_currentPos.y, calculateDistance(sr.level, sr.frequency)));
					Log.d("Yo", "level="+sr.level+" freq="+sr.frequency+" d="+calculateDistance(sr.level, sr.frequency));
					
				}
			}
		}

		m_newPos = false;
		postInvalidate();
	}

	double[] pr = new double[] {
			-45, -47.66, -50.66, -54.66,
			-56.83, -57.66, -58.83, -59.66,
			-60.5, -60.83, -61.66, -61.83,
			-62.16, -62.66, -63.33, -64, 
			-64.83, -65.66, -66.5
			};
	public double calculateDistance(double signalLevelInDb, double freqInMHz) 
	{
		/*int i = 0;
		for (i = 0; i < pr.length; i++)
		{
			if (signalLevelInDb > pr[i])
				break;
		}
		return i*2;*/
		
	    double exp = (27.55 - (20 * Math.log10(freqInMHz)) - signalLevelInDb) / 20.0;
	    return Math.pow(10.0, exp);
	    
		
		

	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.drawText(m_debugStr, 5, 100, new Paint());
		
		drawGrid(canvas);
		
		if (m_knowPositions)
		{
			drawKnownPositions(canvas);
		}
		else if (m_useMds)
		{
			Paint pp = new Paint();
			pp.setAntiAlias(true);
			pp.setColor(Color.RED);
			
			int pointCount = m_scanCount + m_dataCache.keySet().size();
			if (pointCount > 0)
			{
				double[][] mdsinput = new double[pointCount][pointCount];
				double[][] weight = new double[pointCount][pointCount];
				
				for (int i = 0; i < m_scanCount; i++)
				{
					for (int ii = 0; ii < m_scanCount; ii++)
					{
						weight[i][ii] = 0;	
					}
				}
				
				for (int i = m_scanCount; i < pointCount; i++)
				{
					for (int ii = 0; ii < m_scanCount; ii++)
					{
						weight[i][ii] = 4;	
						weight[ii][i] = 4;	
					}
				}
				
				for (int i = 0; i < pointCount; i++)
				{
					weight[i][i] = 4;
				}
				
	
				int ind = 0;
				for (Vector<Circle> v : m_dataCache.values())
				{
					for (int i = 0; i < v.size(); i++)
					{
						mdsinput[m_scanCount + ind][i] = v.get(i).r;
						mdsinput[i][m_scanCount + ind] = v.get(i).r;
					}
					ind++;
				}
				
				for (int i = 0; i < m_scanCount; i++)
				{
					for (int ii = 0; ii < m_scanCount; ii++)
					{
						mdsinput[i][ii] = 20;	
					}
				}
				
				for (int i = m_scanCount; i < pointCount; i++)
				{
					for (int ii = m_scanCount; ii < pointCount; ii++)
					{
						mdsinput[i][ii] = 20;	
					}
				}
				
				
				for (int i = 0; i < pointCount; i++)
				{
					mdsinput[i][i] = 0;
				}
				
				String str = new String();
				for (int i = 0; i < pointCount; i++)
				{
					for (int ii = 0; ii < pointCount; ii++)
					{
						str += " " + mdsinput[i][ii];	
					}
					Log.d("Stuff", ""+str);
					str = new String();
				}
	
				Log.d("Stuff", ""+mdsinput.length);

				
				
				double[][] stress = MDSJ.classicalScaling(mdsinput, 2);
				double[][] points = new double[stress.length][stress[0].length];
				for (int i = 0; i < stress.length; i++)
					for (int j = 0; j < stress[0].length; j++)
						points[i][j] = stress[i][j];
				StressMinimization strm = new StressMinimization(mdsinput, stress, weight);
				strm.iterate(0, 0, 3);
				
				for (int i = 0; i < points[0].length; i++)
				{
					if (Double.isNaN(points[0][i]))
						points[0][i] = 0;
					if (Double.isNaN(points[1][i]))
						points[1][i] = 0;
				}
				double dispx = points[0][0], dispy = points[1][0];
				for (int i = 0; i < points[0].length; i++)
				{
					if (points[0][i] < dispx)
						dispx = points[0][i];
					if (points[1][i] < dispy)
						dispy = points[1][i];
					Log.d("Stuff", "x=" + points[0][i] + " y=" + points[1][i]);
				}
				Log.d("Stuff", "dx=" + dispx + " dy=" + dispy);
				for (int i = 0; i < points[0].length; i++)
				{
					points[0][i] = (points[0][i] - dispx) * m_grid.getCellWidth() + m_grid.getLeft();
					points[1][i] = (points[1][i] - dispy) * m_grid.getCellHeight() + m_grid.getTop();
					pp.setColor(Color.GREEN);
					canvas.drawCircle((float)points[0][i], (float)points[1][i], 5, pp);
					pp.setColor(Color.BLACK);
					canvas.drawText("" + (i+1), (float)points[0][i], (float)points[1][i] + 15, pp);
				}
				
				
				for (int i = 0; i < stress[0].length; i++)
				{
					if (Double.isNaN(stress[0][i]))
						stress[0][i] = 0;
					if (Double.isNaN(stress[1][i]))
						stress[1][i] = 0;
				}

				dispx = stress[0][0];
				dispy = stress[1][0];
				for (int i = 0; i < stress[0].length; i++)
				{
					if (stress[0][i] < dispx)
						dispx = stress[0][i];
					if (stress[1][i] < dispy)
						dispy = stress[1][i];
					Log.d("Stuff", "x=" + stress[0][i] + " y=" + stress[1][i]);
				}
				Log.d("Stuff", "dx=" + dispx + " dy=" + dispy);
				for (int i = 0; i < stress[0].length; i++)
				{
					stress[0][i] = (stress[0][i] - dispx) * m_grid.getCellWidth() + m_grid.getLeft();
					stress[1][i] = (stress[1][i] - dispy) * m_grid.getCellHeight() + m_grid.getTop();
					pp.setColor(Color.MAGENTA);
					canvas.drawRect((float)stress[0][i], (float)stress[1][i], (float)stress[0][i] + 5, (float)stress[1][i] + 5, pp);
					pp.setColor(Color.BLACK);
					canvas.drawText("" + (i+1), (float)stress[0][i] + 5, (float)stress[1][i] + 5, pp);
				}
			}
		}
		else
		{
			
		}
	}
	
	private void drawKnownPositions(Canvas canvas)
	{
		for (Point p : m_posVector)
		{
			canvas.drawCircle(p.x, p.y, 5, new Paint());
		}
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setAntiAlias(true);

		Paint pp = new Paint();
		pp.setAntiAlias(true);
		pp.setColor(Color.RED);
		
		Vector<PointF> intersectionPoints = new Vector<PointF>();
		
		int ppm = m_grid.getCellWidth(); //pixels per meter
		
		for (Vector<Circle> v : m_dataCache.values())
		{
			Vector<Circle> v0 = new Vector<Circle>();
			for (int i = 0; i < v.size(); i++)
			{
				if (v.get(i).r != 0)
					v0.add(v.get(i));
			}
			
			for (int i = 0; i < v0.size(); i++)
			{
				canvas.drawCircle((float)v0.get(i).x, (float)v0.get(i).y, (float)v0.get(i).r * ppm, p);
			}
			
			pp.setColor(((pp.getColor() & ~Color.BLACK) / 24) | 0x40000000);

			for (int i = 0; i < v0.size() - 1; i++)
			{
				for (int j = i + 1; j < v0.size(); j++)
				{
					int cc = circleIntersect(v0.get(i).x, v0.get(i).y, v0.get(i).r * ppm, 
							v0.get(j).x, v0.get(j).y, v0.get(j).r * ppm);
					if (cc == 1)
					{
						canvas.drawCircle(xi1, yi1, 7, pp);
						canvas.drawCircle(xi2, yi2, 7, pp);
						intersectionPoints.add(new PointF(xi1, yi1));
						intersectionPoints.add(new PointF(xi2, yi2));
					}
				}
			}
		}
		
		float ax = 0, ay = 0;
		for (PointF pf : intersectionPoints)
		{
			ax += pf.x;
			ay += pf.y;
		}
		ax /= intersectionPoints.size();
		ay /= intersectionPoints.size();
		float minx = ax, miny = ay;
		/*float d = 0, mind = Float.MAX_VALUE, minx = 0, miny = 0;
		for (PointF pf : intersectionPoints)
		{
			d = (pf.x - ax)*(pf.x - ax) + (pf.y - ay)*(pf.y - ay);
			if (d < mind)
			{
				mind = d;
				minx = pf.x; 
				miny = pf.y;
			}
		}*/
		
		m_wifiPos.x = (int) minx;
		m_wifiPos.y = (int) miny;
		m_wifiPos = m_grid.getPosition(m_wifiPos);
		
		canvas.drawBitmap(m_wifiIcon, minx - m_wifiIcon.getWidth()/2, miny - m_wifiIcon.getHeight()/2, new Paint() );
		
		if (m_newPos)
		{
			canvas.drawCircle(m_currentPos.x, m_currentPos.y, 5, new Paint());
		}
	}

	private void drawGrid(Canvas c)
	{
		if (m_grid == null)
		{
			int w = c.getWidth();
			int h = c.getHeight();
			
			int rc = 20; //rect count
			int gridw = (int) (w * 0.8f); //grid width
			int rw = gridw / rc; //rect width
			int x = (int) (w * 0.1);
			int y = (int) (h / 2 - gridw / 2);
		
			m_wifiIcon = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.wifi);
			m_wifiIcon = Bitmap.createScaledBitmap(m_wifiIcon, gridw/rc, gridw/rc, true);
					
			m_grid = new CanvasGrid(rc, rc, x, y, gridw, gridw);
		}

		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.LTGRAY);
		
		m_grid.draw(c, p);
	}

	//http://justbasic.wikispaces.com/Check+for+collision+of+two+circles,+get+intersection+points
	private float xi1, yi1, xi2, yi2;
	private int circleIntersect(double x0, double y0, double r0, double x1, double y1, double r1)
	{
	    /* This function checks for the intersection of two circles.
	    If one circle is wholly contained within the other a -1 is returned
	    If there is no intersection of the two circles a 0 is returned
	    If the circles intersect a 1 is returned and
	    the coordinates are placed in xi1, yi1, xi2, yi2*/
	 
	    // dx and dy are the vertical And horizontal distances between
	    // the circle centers.
		double dx = x1 - x0;
		double dy = y1 - y0;
	 
	    // Determine the straight-Line distance between the centers.
	    double d = Math.sqrt((dy*dy) + (dx*dx));
	 
	 
	    //Check for solvability.
	    if (d > (r0 + r1))
	    {
	        // no solution. circles do Not intersect
	        return 0;
	    }
	 
	    if (d < Math.abs(r0 - r1)) 
	    {
	    	// no solution. one circle is contained in the other
	        return -1;
	    }
	 
	    // 'point 2' is the point where the Line through the circle
	    // intersection points crosses the Line between the circle
	    // centers.
	 
	    // Determine the distance from point 0 To point 2.
	    double a = ((r0*r0) - (r1*r1) + (d*d)) / (2.0 * d);
	 
	    // Determine the coordinates of point 2.
	    double x2 = x0 + (dx * a/d);
	    double y2 = y0 + (dy * a/d);
	 
	    // Determine the distance from point 2 To either of the
	    // intersection points.
	    double h = Math.sqrt((r0*r0) - (a*a));
	 
	    // Now determine the offsets of the intersection points from
	    // point 2.
	    double rx = (0-dy) * (h/d);
	    double ry = dx * (h/d);
	 
	    // Determine the absolute intersection points.
	    xi1 = (float)(x2 + rx);
	    xi2 = (float)(x2 - rx);
	    yi1 = (float)(y2 + ry);
	    yi2 = (float)(y2 - ry);
	 
	    return 1;	
	}

	public void setKnowPositions(boolean b)
	{
		m_knowPositions = b;
	}
	
	public void setUseMds(boolean b)
	{
		m_useMds = b;
	}

	public void log(String fn)
	{
		try 
		{
			FileWriter fstream = new FileWriter(fn, true);
			BufferedWriter out = new BufferedWriter(fstream);
			Calendar cal = Calendar.getInstance();
	    	cal.getTime();
	    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			out.write("" + sdf.format(cal.getTime()) + "\r\n" );
			
			for (Entry<String, Vector<Circle>> v : m_dataCache.entrySet())
			{
				out.write(v.getKey() + ": ");
				for (int i = 0; i < v.getValue().size(); i++)
				{
					out.write(v.getValue().get(i).r + "\t" );
				}
				out.write("\r\n" );
			}
			out.write("Positions: \r\n" );
			
			for (Point p : m_posVector)
			{
				p = m_grid.getPosition(p);
				out.write(p.x + "\t" + p.y + "\r\n");
			}

			out.write("Wifi position: \r\n" );
			out.write(m_wifiPos.x + "\t" + m_wifiPos.y + "\r\n");
			
			out.close();
		} 
		catch (Exception e)
		{	
			  Log.e("TopologyView", "Error: " + e.getMessage());
		}
	}
}
