package com.mifi;

import java.io.FileOutputStream;
import java.util.UUID;

import com.mifi.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CreateMapActivity extends ActionBarActivity
	implements OnItemClickListener
{
	private MapArrayAdapter m_adapter;
	private Mapping m_map;
	
	private UUID m_mapId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.create_menu);
        
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			m_mapId = UUID.fromString(extras.getString("current_map_id"));
			try
			{
				Log.d("Map", "Loading++");
				m_map = MapStorage.loadLocalMap(this, m_mapId);
				Log.d("Map", "Loading--");
				EditText name = (EditText)findViewById(R.id.edittext_mapname);
				name.setText(new String(m_map.getInfo().getName()));
				EditText desc = (EditText)findViewById(R.id.edittext_mapdesc);
				desc.setText(new String(m_map.getInfo().getDescription()));
				Toast.makeText(this, "Load successful", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e)
			{
				Toast.makeText(this, "Couldn't load map", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		
		if (m_map == null)
		{
			m_mapId = UUID.randomUUID();
			m_map = new Mapping(m_mapId);
		}
		
		MapStorage.setCurrentMap(m_map);
		
        m_adapter = new MapArrayAdapter(this, m_map.getData());
		
		ListView listview = (ListView) findViewById(R.id.listview_create);
		listview.setAdapter(m_adapter);   
		listview.setOnItemClickListener(this);
		
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		m_adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.create_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	addItem();
	            return true;
	        case R.id.action_save:
	            saveMap();
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
	
	private void clearItems()
	{
		m_map.clearData();
		m_adapter.notifyDataSetChanged();
	}

	public void addItem() 
	{
		MapPoint mp = new MapPoint();
		mp.setName(new String("Name"));
		mp.setDescription(new String("Description"));
		m_map.addPoint(mp);
		m_adapter.notifyDataSetChanged();
	}
	
	public void saveMap() 
	{
		try
		{
			
			MapInfo info = m_map.getInfo();
			
			EditText name = (EditText)findViewById(R.id.edittext_mapname);
			info.setName(name.getText().toString());
			EditText desc = (EditText)findViewById(R.id.edittext_mapdesc);
			info.setDescription(desc.getText().toString());

			MapStorage.saveLocalMap(this.getApplicationContext(), m_map);
			
    		Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
    		Toast.makeText(this, "Couldn't save map", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

    public void onItemClick(AdapterView<?> parent, final View view,
            int position, long id) 
	{
		Intent i = new Intent(getApplicationContext(), DetailActivity.class);
	    i.putExtra("current_map_index", "" + position);
	    startActivity(i);
    }
	
	/* Called by delete button in the list */
	public void removeItem(View view)
	{
		m_map.removePoint(((ListView)findViewById(R.id.listview_create)).getPositionForView(view));
		m_adapter.notifyDataSetChanged();
	}
	
	
	public void toggleInfo(View view)
	{
		View edit = findViewById(R.id.edittext_mapdesc);
		if (edit.getVisibility() == View.GONE)
			edit.setVisibility(View.VISIBLE);
		else
			edit.setVisibility(View.GONE);
	}
	
}
