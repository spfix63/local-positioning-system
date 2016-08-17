package com.mifi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

class DownloadTask extends AsyncTask<String, String, String>
{

	private OnRequestCompleteListener m_listener;
	
	public DownloadTask(OnRequestCompleteListener l)
	{
		super();
		m_listener = l;
	}
	
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
    	Log.d("Download", ""+result);
        super.onPostExecute(result);
        if (result == null)
        	m_listener.onRequestError();
        else
        	m_listener.onRequestComplete(result);
    }
}
