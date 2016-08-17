package com.mifi;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.mifi.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class DetailActivity extends ActionBarActivity 
	implements OnItemClickListener
{
	private MapPoint m_mp;
	private WifiDataArrayAdapter m_adapter;
	
	private boolean m_collectingData;
	private boolean m_updateList;
	

	private WifiManager m_wifi;
	private BroadcastReceiver m_receiver;
	private ProgressDialog m_progress;
	private Filter m_filter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.detail_menu);
       
        m_filter = new Filter();
        m_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int index = Integer.parseInt(extras.getString("current_map_index"));
            m_mp = MapStorage.getCurrentMap().getPoint(index);
            
            
            
            m_adapter = new WifiDataArrayAdapter(this, m_mp.getData());
            
    		EditText name = (EditText) findViewById(R.id.edittext_name);
    		name.addTextChangedListener(new TextWatcher(){
    	        public void afterTextChanged(Editable s) {
    	            boolean valid = true;
    	            if (valid)
    	            {
    	            	m_mp.setName(s.toString());
    	            }
    	        }
    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
    	    }); 
    		
    		EditText desc = (EditText) findViewById(R.id.edittext_desc);
    		desc.addTextChangedListener(new TextWatcher(){
    	        public void afterTextChanged(Editable s) {
    	            boolean valid = true;
    	            if (valid)
    	            {
    	            	m_mp.setDescription(s.toString());
    	            }
    	        }
    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
    	    }); 
    		
    		name.setText(m_mp.getName());
    		desc.setText(m_mp.getDescription());
    		

    		ListView listview = (ListView) findViewById(R.id.listview_detail);
    		listview.setAdapter(m_adapter);   
    		listview.setOnItemClickListener(this);
		}
		m_receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                	
                	WifiManager mWm = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                	List<ScanResult> data = null;
                	if (mWm.isWifiEnabled())
                	{
                		data = mWm.getScanResults();
                		m_filter.update(data);
                	}
                	
                	if (m_collectingData)
                	{
                		m_updateList = true;
                		m_mp.parseData(data);
	                	mWm.startScan();
	                	//Toast.makeText(DetailActivity.this, "Got Wi-Fi results", Toast.LENGTH_SHORT).show();	
                	}
                	else
                	{
                		if (m_updateList)
                		{
                			m_updateList = false;
                			if (data != null)
                			{	
		                    	m_mp.parseData(data);
		                    	
		                    	m_adapter.clear();
		                		for (WifiData wd : m_mp.getData()) {
		                			m_adapter.add(wd);
		                		}
		                		m_adapter.notifyDataSetChanged();
                			}
    	                	supportInvalidateOptionsMenu();
                		}
                	}

                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        };
		registerReceiver(m_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(m_receiver);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detail_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
        menu.findItem(R.id.action_gather).setEnabled(!m_collectingData);
	    return super.onPrepareOptionsMenu(menu);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_gather:
	        	gatherData(item);
	            return true;
	        case R.id.action_clear:
	            clearItems();
	            return true;
	        case R.id.action_back:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void gatherData(MenuItem item)
	{
		item.setEnabled(false);

		m_progress = ProgressDialog.show(this, "Scanning...",
				  "Gathering Wi-Fi data.", true);

		new Thread(new Runnable() {
			@Override
			public void run()
			{
				m_filter.flush();
				m_collectingData = true;
		        m_wifi.startScan();
				try
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				m_collectingData = false;
				m_progress.dismiss();
			}
		}).start();
	}
	
	
	public void clearItems()
	{
		m_mp.clearData();
		m_adapter.clear();
		m_adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view,
            int position, long id) 
	{
		// TODO Auto-generated method stub
		
	}

}
