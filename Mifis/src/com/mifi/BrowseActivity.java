package com.mifi;

import com.mifi.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;



public class BrowseActivity extends ActionBarActivity 
{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        // Notice that setContentView() is not used, because we use the root
        // android.R.id.content as the container for each fragment
        
        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab = actionBar.newTab()
                           .setText(R.string.localmaps)
                           .setIcon(R.drawable.ic_action_computer)
                           .setTabListener(new TabListener<LocalMapsFragment>(
                                   this, "local", LocalMapsFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                       .setText(R.string.internetmaps)
                       .setIcon(R.drawable.ic_action_cloud)
                       .setTabListener(new TabListener<InternetMapsFragment>(
                               this, "internet", InternetMapsFragment.class));
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                       .setText(R.string.downloads)
                       .setIcon(R.drawable.ic_action_download)
                       .setTabListener(new TabListener<DownloadsFragment>(
                               this, "downloads", DownloadsFragment.class));
        actionBar.addTab(tab);
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public void button_done(View view)
	{
		this.finish();
	}
	

}
