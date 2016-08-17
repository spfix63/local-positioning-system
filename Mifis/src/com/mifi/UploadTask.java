package com.mifi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

class UploadTask extends AsyncTask<String, String, String>
{

	private OnRequestCompleteListener m_listener;
	
	public UploadTask(OnRequestCompleteListener l)
	{
		super();
		m_listener = l;
	}
	
    @Override
    protected String doInBackground(String... uri) {
    	// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri[0]/*"http://www.yoursite.com/script.php"*/);
        HttpResponse response = null;
        String ret = null;
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair(uri[1], uri[2]));
            nameValuePairs.add(new BasicNameValuePair(uri[3], uri[4]));
            nameValuePairs.add(new BasicNameValuePair(uri[5], uri[6]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            if (response != null)
            	ret = response.toString();
            
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return ret;
    }

    @Override
    protected void onPostExecute(String result) {
    	Log.d("Upload", ""+result);
        super.onPostExecute(result);
        if (result == null) {
        	if (m_listener != null)
        		m_listener.onRequestError();
        }
    	else {
    		if (m_listener != null)
    			m_listener.onRequestComplete(result);
    	}
    }
}
