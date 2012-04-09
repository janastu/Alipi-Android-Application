package com.servelots.alipi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.servelots.alipi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class BlogTab extends Activity {
	ListView listView;
	String url, result1, result, blog, language;
	ArrayList<Hashtable<String, String>> list;
	Hashtable<String, String> tableLang;
	String[] data, eType, xPath, languages;
	ArrayList<String> authors;
	Hashtable<String, String> tableBlog;
	webView web;
	String[] blogs;
	TabHost tHost; // The activity TabHost
	TabHost.TabSpec spec;// Resusable TabSpec for each tab
	Intent intent; // Reusable Intent for each tab
	Resources res;
	AlertDialog alert1;
	AlertDialog.Builder builder;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// listView = (ExpandableListView) findViewById(R.id.list);
		setContentView(R.layout.explist);
		Intent myIntent = getIntent();
		url = myIntent.getStringExtra("url");
		new AsyncLoad(this).execute(url);
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		private String[] groups = { "With Audio", "Without Audio" };

		private String[] blogList; // {
		/*
		 * { "Arnold", "Barry", "Chuck", "David" }, // { "Ace", "Bandit",
		 * "Cha-Cha", "Deuce" }, // { "Fluffy", "Snuggles" }, // { "Goldy",
		 * "Bubbles" } // };
		 */
		private Context context;

		public MyExpandableListAdapter(Context context, String[] blogList1) {
			// TODO Auto-generated constructor stub
			this.context = context;
			Log.d("langList", "h   " + blogList1.length + " uu" + blogList);
			blogList = new String[blogList1.length];
			Log.d("langList", "lang    " + blogList);
			for (int i = 0; i < blogList1.length; i++) {
				blogList[i] = blogList1[i];
			}
		}

		public Object getChild(int groupPosition, int childPosition) {
			return blogList[childPosition];

		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return blogList.length;
		}

		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.child, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.chld1);
			tv.setText(getChild(groupPosition, childPosition).toString());
			Log.d("tab", "hello chld");
			return convertView;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String group = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.group, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.grp1);
			tv.setText(group);
			Log.d("tab", "hello grp");
			return convertView;

		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Log.d("tab", "hello");
			Toast.makeText(
					getApplicationContext(),
					": Child " + childPosition + " clicked in group "
							+ groupPosition, Toast.LENGTH_SHORT).show();
			return true;
		}
	}

	public class AsyncLoad extends AsyncTask<String, Integer, String> {

		public AsyncLoad(BlogTab blogTab) {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String doInBackground(String... UR) {
			// TODO Auto-generated method stub

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://y.a11y.in/blog");

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				Log.d("url", "url    :" + UR[0]);
				nameValuePairs.add(new BasicNameValuePair("url", UR[0]));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpClient.execute(httpPost);
				result = HttpHelper.request(response);

				blogs = result.split(",");
				int i = 0;
				while (i < blogs.length && isCancelled() == false) {
					Log.d("Lang", "Lnn" + isCancelled());
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
			Log.d("background", "l   " + blogs.length);
			Log.d("background",
					"len" + result.length() + "data" + result.toString());
			return result;
		}

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(BlogTab.this, "LOADING",
					"FETCHING RE-NARRATIONS");
			dialog.show();
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.d("post", "posted" + blogs.length);
			loadPage(blogs);
		}
	}

	public class AsyncFinish extends AsyncTask<String, Integer, String> {
		Intent intent1;
		public AsyncFinish(OnClickListener onClickListener) {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String doInBackground(String... lang) {
			// TODO Auto-generated method stub

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://y.a11y.in/filter");

			try 
			{
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				// Log.d("url", "blog    :"+blog+"url  "
				// +url+"lang    "+language);
				url = webView.url;
				Log.d("url", "blog    " + blog + "    url  " + url
						+ "     lang    " + language);
				url = url.trim();
				blog = blog.trim();
				language = language.trim();
				nameValuePairs.add(new BasicNameValuePair("url", url));
				nameValuePairs.add(new BasicNameValuePair("blog", blog));
				nameValuePairs.add(new BasicNameValuePair("lang", language));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpClient.execute(httpPost);
				result1 = HttpHelper.request(response);
				Log.d("return", "response    " + result1);

				list = new ArrayList<Hashtable<String, String>>();
				Hashtable<String, String> table;
				String[] renarrations = result1.split("###");
			    Log.d("return", "response    "+renarrations.length);
				int i, j;
				for (i = 1; i < renarrations.length; i++) {
					table = new Hashtable<String, String>();
					String[] parsed = renarrations[i].split("&");
					for (j = 1; j < parsed.length; j++) {
						String[] pairs = parsed[j].split("::");
						for (int k = 1; k < pairs.length; k++) {
							String key = pairs[0];
							String value = pairs[1];
							table.put(key, value);
						}
					}
					list.add(table);
				}
				
				//web = new webView();
				xPath = new String[list.size()];
				data = new String[list.size()];
				eType = new String[list.size()];
				Log.d("size", "list   " + list.size());
				for (int ii = 0; ii < list.size(); ii++) {
					tableLang = list.get(ii);
					// // Log.d("GGGGG", ""+tableLang.containsKey("xpath"));
					data[ii] = tableLang.get("data");
					Log.d("Type", "ty  " + data[ii]);
					eType[ii] = tableLang.get("elementtype");
					if (eType[ii].equals("audio/ogg"))
						;
					Log.d("Type", "ty  " + eType[ii]);
					xPath[ii] = tableLang.get("xpath");
				}

				intent1 = new Intent(BlogTab.this, webView.class);
				intent1.putExtra("data", "data");
				intent1.putExtra("xPath", "xPath");
				intent1.putExtra("elementtype", "eType");
				// blogs = result.split(",");
				// int i=0;
				// while(i<blogs.length && isCancelled()==false)
				// {
				// Log.d("Lang", "Lnn"+isCancelled());
				// if(blogs[i].contains("\""))
				// {
				// blogs[i]=blogs[i].replace("\"", "");
				// }
				// if(blogs[i].contains("["));
				// {
				// blogs[i]=blogs[i].replace("[", "");
				// }
				// if(blogs[i].contains("]"))
				// {
				// blogs[i]=blogs[i].replace("]", "");
				// }
				// i++;
				// }
			}
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
			}
			
			return result;
		}

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(BlogTab.this, "LOADING",
					"FETCHING RE-NARRATIONS");
			dialog.show();
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			startActivity(intent1);
//			try {
//				web.reNarrate(xPath, eType, data);
//			} catch (XPatherException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ParserConfigurationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (SAXException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//setResult(BlogTab.RESULT_OK, intent1);
			//finish();
			// finish();
		}
	}

	public void loadPage(final String[] blogs2) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), " " + blogs2[0],
				Toast.LENGTH_LONG).show();
		listView = (ListView) findViewById(R.id.explist);
		// ListAdapter adapter = new ListAdapter(this,blogs2);
		// listView.setAdapter(adapter);
		final String[] countries = { "India", "Brazil", "Germany", "Spain",
				"Italy", "france", "Engand", "Ukraine" };
		Log.d("String", "countries   " + countries + "BLogs   " + blogs2[0]);
		ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, blogs2);
		// listView.setCacheColorHint(color.white);
		// listView.setBackgroundColor(Color.BLACK);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("click", "clcike");
				try {

					Toast.makeText(getApplicationContext(), "Hello "
							+ blogs2[position], Toast.LENGTH_LONG);
					blog = blogs2[position];
					postURL(url, blogs2[position]);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		// listView.setOnChildClickListener(new OnChildClickListener() {
		//
		// public boolean onChildClick(ExpandableListView parent, View v,
		// int groupPosition, int childPosition, long id) {
		// // TODO Auto-generated method stub
		// Log.d("tab", "hello"+blogs[childPosition]);
		// // Toast.makeText(getApplicationContext(),": Child " +
		// blogs[childPosition] + " clicked in group " + groupPosition,
		// Toast.LENGTH_SHORT).show();
		// try {
		// blog = blogs[childPosition];
		// postURL(url, blog);
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Intent intent = new Intent(Renarration.this,webView.class);
		// intent.putExtra("data",data);
		// intent.putExtra("xPath",xPath);
		// intent.putExtra("elementtype",eType);
		// setResult(RESULT_OK, intent);
		// finish();
		// return true;
		// }
		// });
	}

	public void postURL(String url, String blog)
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
		result1 = HttpHelper.request(response1);
		Log.d("result", "ll   " + result1);
		parseResult(result1);
	}

	public void parseResult(String response) {
		languages = response.split(",");
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

		builder = new AlertDialog.Builder(BlogTab.this);
		builder.setTitle("Choose Narration");
		// final String[] aad =
		// {"http://team.servelots.com/my/pradeep/minimumwage.ogg"};
		Log.d("Lang", "langg   " + languages[0]);
		builder.setSingleChoiceItems(languages, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						Toast.makeText(getApplicationContext(),
								languages[item], Toast.LENGTH_SHORT).show();
						alert1.dismiss();
						language = languages[item];
						new AsyncFinish(this).execute(languages[item]);
						// mediaPlayer(aud[item]);
					}
				});
		alert1 = builder.create();
		alert1.show();

		// list = new ArrayList<Hashtable<String,String>>();
		// Hashtable<String, String> table;
		// String[] renarrations = result1.split("###");
		// int i,j;
		// for(i=1;i<renarrations.length;i++)
		// {
		// table = new Hashtable<String, String>();
		// String[] parsed = renarrations[i].split("&");
		// for(j=1;j<parsed.length;j++)
		// {
		// String[] pairs = parsed[j].split("::");
		// for(int k=1;k<pairs.length;k++)
		// {
		// String key = pairs[0];
		// String value = pairs[1];
		// table.put(key, value);
		// }
		// }
		// list.add(table);
		// }
		// web = new webView();
		// xPath=new String[list.size()];
		// data = new String[list.size()];
		// eType = new String[list.size()];
		// // authors = new ArrayList<String>();
		// for(int ii=0;ii<list.size();ii++)
		// {
		// tableBlog=list.get(ii);
		// //// Log.d("GGGGG", ""+tableLang.containsKey("xpath"));
		// //// authors.add(tableLang.get("xpath"));
		// data[ii]=tableBlog.get("data");
		// eType[ii]=tableBlog.get("elementtype");
		// if(eType[ii].equals("audio/ogg"));
		// // Log.d("Type", "ty  "+eType[ii]);
		// xPath[ii]=tableBlog.get("xpath");
		// }
		// return true;

		// //// The old finish way. Onclick
		// try
		// {
		//
		// // new AsyncFinish().execute("Finish");
		// // ProgressDialog dialog = ProgressDialog.show(this, "",
		// // "Loading. Please wait...", true);
		// // dialog.show();
		// //new AsyncFinish().execute("Finish");
		// Intent intent = new Intent(BlogTab.this,webView.class);
		// intent.putExtra("data",data);
		// intent.putExtra("xPath",xPath);
		// intent.putExtra("elementtype",eType);
		// setResult(RESULT_OK, intent);
		// finish();
		//
		// //web.reNarrate(tableLang.get("xpath"), tableLang.get("elementtype"),
		// tableLang.get("data"));
		// }
		// catch (Exception e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// setListAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1 , authors));
		// lv = getListView();
		// lv.setTextFilterEnabled(true);
		// flag= true;

	}

}
