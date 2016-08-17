package com.mifi;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class TopologyActivity extends ActionBarActivity
{
	
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.topology_menu);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.topology_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		TopologyView tv = (TopologyView)findViewById(R.id.topology_view);
	    switch (item.getItemId()) {
	    	case R.id.action_scan:
	    		tv.scan();
	    		return true;
	    	case R.id.action_logtofile:
	    		tv.log("/sdcard/tri_res.txt");
	    		return true;
	    	case R.id.action_reset:
	    		tv.reset();
	    		return true;
	    	case R.id.action_mode1:
	    		tv.reset();
	    		tv.setKnowPositions(true);
	    		return true;
	    	case R.id.action_mode2:
	    		tv.reset();
	    		tv.setKnowPositions(false);
	    		return true;
	        case R.id.action_back:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
