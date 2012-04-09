package com.servelots.alipi;

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

import android.util.Log;


public class AjaxCalls 
{
	String result;
	String[] languages,language,blogs;
	
	
	public String[] getLanguages(String url)
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://y.a11y.in/menu");
		try
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			
			//url = UR[0];
			//Log.d("a11y","is    "+UR[0]+ "url    :"+url);
			nameValuePairs.add(new BasicNameValuePair("url",url));
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpClient.execute(httpPost);
	        result= HttpHelper.request(response);
	        Log.d("result ","result is    "+ result);
	        if(result.contains("empty") && result.length()==6)
	        {
	        	languages = new String[0];
	        	//cancel(true);
	        	Log.d("equals", "len        Inside"+result);
	        }
	        else
	        {
	        	
	        	languages = result.split(",");
	        	int i=0;
		
	        	while(i<languages.length)
	        	{
	        	Log.d("equals", "else");
	        		if(languages[i].contains("\""))
	        		{
	        			languages[i]=languages[i].replace("\"", "");  		
	        		}	
	        		if(languages[i].contains("["));
	        		{
	        			languages[i]=languages[i].replace("[", "");  		
	        		}
	        		if(languages[i].contains("]"))
	        		{
	        			languages[i]=languages[i].replace("]", "");  		
	        		}
	        		i++;
	        	} 
			//audList = new String[languages.length];
			//textList= new String[languages.length];      
	        }	
		}
	        catch (Exception e) 
	        {
	        // TODO Auto-generated catch block
	        }
		return languages;
		
	}
	
	public String[] getBlogs(String url) 
	{
		
		// TODO Auto-generated method stub
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://y.a11y.in/blog");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			//Log.d("url", "url    :" + url);
			nameValuePairs.add(new BasicNameValuePair("url", url));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			result = HttpHelper.request(response);

			blogs = result.split(",");
			int i = 0;
			while (i < blogs.length) {
				//Log.d("Lang", "Lnn" + isCancelled());
				if (blogs[i].contains("\"")) {
					blogs[i] = blogs[i].replace("\"", "");
				}
				if (blogs[i].contains("["))
					;
				{
					blogs[i] = blogs[i].replace("[", "");
				}
				if (blogs[i].contains("]")) {
					blogs[i] = blogs[i].replace("]", "");
				}
				i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return blogs;
	}
	
	public String[] getBlogLanguages(String url, String blog)
			throws ClientProtocolException, IOException {
		HttpClient httpClient1 = new DefaultHttpClient();
		HttpPost httpPost1 = new HttpPost("http://y.a11y.in/menu");
		blog = blog.trim();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("options", blog));
		nameValuePairs.add(new BasicNameValuePair("url", url));
		// nameValuePairs.add(new BasicNameValuePair("lang", lang));
		httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response1 = httpClient1.execute(httpPost1);
		result = HttpHelper.request(response1);
		//Log.d("result", "ll   " + result);
		//parseResult(result);
		languages = result.split(",");
		int i = 0;

		while (i < languages.length) {
			if (languages[i].contains("\"")) {
				languages[i] = languages[i].replace("\"", "");
			}
			if (languages[i].contains("["))
				;
			{
				languages[i] = languages[i].replace("[", "");
			}
			if (languages[i].contains("]")) {
				languages[i] = languages[i].replace("]", "");
			}
			i++;
		}
		return languages;
	}
	

}

	
