package com.mifi;

import com.mifi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class TopLevelActivity extends Activity 
{
	/* TODO:
	 * Iki galo sutvarkyti MapInfo
	 */
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main_menu);
 
    }
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	public void button_browse(View view) 
	{
		Intent i = new Intent(TopLevelActivity.this, BrowseActivity.class);
        startActivity(i);
	}

	public void button_create(View view) 
	{
		Intent i = new Intent(getApplicationContext(), CreateMapActivity.class);
		startActivity(i);
	}
	
	public void button_detect(View view) 
	{
		Intent i = new Intent(getApplicationContext(), TopologyActivity.class);
		startActivity(i);
	}
}
