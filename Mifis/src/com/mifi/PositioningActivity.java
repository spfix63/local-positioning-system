package com.mifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.math3.filter.DefaultMeasurementModel;
import org.apache.commons.math3.filter.DefaultProcessModel;
import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import com.mifi.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PositioningActivity extends ActionBarActivity
	implements OnItemClickListener
{
	
	private boolean m_positioningEnabled = true;
	
	private PositioningArrayAdapter m_adapter;
	private Mapping m_map;
	private ArrayList<Boolean> m_current;
	
	private UUID m_mapId;

	private WifiManager m_wifi;
	private BroadcastReceiver m_receiver;
	
	private Filter m_filter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.positioning_menu);

        m_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			m_mapId = UUID.fromString(extras.getString("current_map_id"));
			try
			{
				m_map = MapStorage.loadLocalMap(this, m_mapId);
				Toast.makeText(this, "Load successful", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e)
			{
				Toast.makeText(this, "Couldn't load map", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		
		m_receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                	
                	WifiManager mWm = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    //if (mWm.isWifiEnabled())
                    {
                    	PositioningActivity.this.updateResults(mWm.getScanResults());
                		Log.d("Wi-fi", "Got results");
                    }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        };
		
        m_filter = new MovingAverageFilter();
		
		MapStorage.setCurrentMap(m_map);

        m_current = new ArrayList<Boolean>(Collections.nCopies(m_map.getPointCount(), false));
        m_adapter = new PositioningArrayAdapter(this, m_map.getData(), m_current);
		ListView listview = (ListView) findViewById(R.id.listview_create);
		listview.setAdapter(m_adapter);   
		listview.setOnItemClickListener(this);
		
		
		
    }

	protected void updateResults(List<ScanResult> scanResults)
	{
		float current_min = Float.MAX_VALUE;
		int index = -1;
		


		TextView tv1 = (TextView) findViewById(R.id.pos_text1);
		TextView tv2 = (TextView) findViewById(R.id.pos_text2);
		TextView tv3 = (TextView) findViewById(R.id.pos_text3);
		String txt1 = new String("R: ");
		String txt2 = new String("F: ");
		String txt3 = new String("D: ");

		for (ScanResult sr : scanResults)
		{
			txt1 += sr.level + "  ";
		}
		m_filter.update(scanResults);
		for (ScanResult sr : scanResults)
		{
			txt2 += sr.level + "  ";
		}
		
		
		for (int i = 0; i < m_map.getPointCount(); i++)
		{
			float distance = 0;
			boolean valid = true; 
			
			m_current.set(i, false);
			
			if (m_map.getPoint(i).hasData())
			{	
				int match = 0;
				int total = m_map.getPoint(i).getData().size();
				
				for (ScanResult sr : scanResults)
				{
					WifiData wd = m_map.getPoint(i).getWifiData(sr.BSSID);
					
					if (wd != null) 
					{
						match++;
						if (sr.level > -45)
							sr.level = -45;
						if (sr.level < -90)
							sr.level = -90;
						
						/* tikimes sr.level -45 .. -90. coeff atitinkamai
						 * coeff bus 2.0 .. 1.0 
						 * tada stiprus signalai tures didesni svori
						 * */
						float coeff = 1 + (sr.level + 90.0f) / 45.0f;
								
						//Duombaze turi duomenys apie si Wi-Fi signala tikriname taske
						if (sr.level >= wd.getMin() && sr.level <= wd.getMax())
						{
							//Signalo stipris patenka i numatytus rezius
							distance += 0;
						}
						else if (sr.level > wd.getMax())
						{
							//Signalo stipris nepatenka i numatytus rezius, pridedam euklido atstuma
							distance += coeff * Math.pow(sr.level - wd.getMax(), 2);
						}
						else if (sr.level < wd.getMin())
						{
							//Signalo stipris nepatenka i numatytus rezius, pridedam euklido atstuma
							distance += coeff * Math.pow(sr.level - wd.getMin(), 2);
						}
					}
					else
					{
						//Duombaze neturi duomenu apie si Wi-Fi signala tikriname taske
						//Reikia patikrinti ar duombazeje yra bendrai zinomas sis Wi-Fi signalas, ar jis naujas
						
					}
				}
				
				if ((float)match / total < 0.5)
				{
					valid = false;
					distance = 9998;
				}
			}
			else
			{
				valid = false;
				distance = 9999;
			}
			txt3 += distance + "  ";
			
			if (valid && distance < current_min)
			{
				current_min = distance;
				index = i;
			}
				
			
			
		}
		if (index != -1)
		{
			m_current.set(index, true);
			
			if (m_map.getPoint(index).hasData())
			{
				Taskas t = new Taskas();
				t.id = index;
				
				UploadTask task = (UploadTask) new UploadTask(null);
				task.execute("http://"+MapStorage.HOSTNAME+":"+MapStorage.PORT+"/KursinisServlets/Positioning",
        			"action", "insert_point", "point_json", Taskas.toJson(t), "map", "dwadwa");
			}
		}
		

		tv1.setText(txt1);
		tv2.setText(txt2);
		tv3.setText(txt3);
		
		
		
		m_adapter.notifyDataSetChanged();
		m_wifi.startScan();
	}

	@Override
	protected void onResume()
	{
		if (m_positioningEnabled)
		{
			registerReceiver(m_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			m_wifi.startScan();
		}
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		if (m_positioningEnabled)
			unregisterReceiver(m_receiver);
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.positioning_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_pause:
	        	if(m_positioningEnabled)
	        	{
	    			unregisterReceiver(m_receiver);
	                item.setIcon(R.drawable.ic_action_play);
	                item.setTitle(R.string.action_resume);
	                m_positioningEnabled = false;
	            }
	        	else
	        	{
	    			registerReceiver(m_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	                item.setIcon(R.drawable.ic_action_pause);
	                item.setTitle(R.string.action_pause);
	                m_positioningEnabled = true;
	            }
	            return true;
	        case R.id.action_back:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    public void onItemClick(AdapterView<?> parent, final View view,
            int position, long id) 
	{
		
    }
	
}
