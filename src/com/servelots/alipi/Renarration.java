package com.servelots.alipi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class Renarration extends TabActivity implements LocationListener

{
	String url, result1, result;
	ListView lv;
	String language, state;
	String[] data, eType, xPath;
	FrameLayout tabcontent;
	String[] bLanguages;
	ArrayList<String> authors;
	ArrayList<Hashtable<String, String>> list;
	Hashtable<String, String> tableLang;
	webView web;
	SharedPreferences preferences;
	// MyExpandableListAdapter adapter;
	String[] languages, blogs;
	TabHost tHost; // The activity TabHost
	TabHost.TabSpec spec;// Resusable TabSpec for each tab
	Intent intent; // Reusable Intent for each tab
	Resources res;
	AlertDialog.Builder builder;
	ListView listView;
	ImageButton home;
	LocationManager lm;
	List<Address> addresses;
	double lat, lng;
	Bundle extras;
	int tabNo;
	AlertDialog alert1;
	Boolean flag;
	ListAdapter locationAdapter;
	static final HashMap<String,String> langMap = new HashMap<String,String>(){{ put("Andaman and Nicobar Islands", "Hindi"); put("Andhra Pradesh", "Telugu"); put("Arunachal Pradesh","Bengali");
	put("Assam","Assamese");put("Bihar","Hindi");put("Chandigarh","Hindi");put("Chhattisgarh","Hindi");put("Dadra and Nagar Haveli","Gujarati");put("Daman and Diu","Gujarati");put("Delhi","Hindi");
	put("Goa","Marathi");put("Gujarat","Gujarati");put("Haryana","Hindi");put("Himachal Pradesh","Hindi");put("Jammu and Kashmir","Kashmiri");put("Jharkhand","Hindi");put("Karnataka","Kannada");
	put("Kerala","Malayalam");put("Lakshadweep","Malayalam");put("Madhya Pradesh","Hindi");put("Maharashtra","Marathi");put("Manipur","Manipuri");put("Meghalaya","Bengali");put("Mizoram","Bengali");
	put("Nagaland","Bengali");put("Orissa","Oriya");put("Pondicherry","Tamil");put("Punjab","Punjabi");put("Rajasthan","Hindi");put("Sikkim","Limbu");put("Tamil Nadu","Tamil");
	put("Tripura","Kokborok");put("Uttaranchal","Hindi");put("Uttar Pradesh","Hindi");put("West Bengal","Bengali");}};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renlist);
		tabcontent = (FrameLayout)findViewById(android.R.id.tabcontent);
		listView = (ListView) findViewById(R.id.list);
		home = (ImageButton) findViewById(R.id.home);
		Intent myIntent = getIntent();
		url = myIntent.getStringExtra("url");
		extras = myIntent.getExtras();
		languages = extras.getStringArray("languages");
		blogs = extras.getStringArray("blogs");
		state = myIntent.getStringExtra("state");
		Log.d("state","state"+state);
		tHost = getTabHost();
		builder = new AlertDialog.Builder(Renarration.this);
		
		loadPage(languages);
		home.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Renarration.this, webView.class);
				setResult(RESULT_CANCELED, intent);
				// LocationManager.removeUpdates();
				lm = null;
				finish();
			}
		});
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		private String[] groups = { "With Audio", "Without Audio" };
		// private String[] audList,textList;
		private String[] langList; // {
		/*
		 * { "Arnold", "Barry", "Chuck", "David" }, // { "Ace", "Bandit",
		 * "Cha-Cha", "Deuce" }, // { "Fluffy", "Snuggles" }, // { "Goldy",
		 * "Bubbles" } // };
		 */
		private Context context;

		public MyExpandableListAdapter(Context context, String[] langList1) {
			// TODO Auto-generated constructor stub
			this.context = context;
			langList = new String[langList1.length];
			for (int i = 0; i < langList1.length; i++) {
				langList[i] = langList1[i];
			}
			// for(int i=0;i<textList1.length;i++)
			// {
			// this.textList[i] =textList1[i];
			// }
			// Log.d("hello", "hh    "+context);
			//
		}

		public Object getChild(int groupPosition, int childPosition) {
			return langList[childPosition];
			// if(groupPosition==0)
			// {
			// return audList[childPosition];
			// }
			// else
			// return textList[childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			// Log.d("Hello",""+children.length);
			// if(groupPosition==0)
			// {
			// return audList.length;
			// }
			// else
			// return textList.length;
			return langList.length;
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
			// Toast.makeText(getApplicationContext(),": Child " + childPosition
			// + " clicked in group " + groupPosition,
			// Toast.LENGTH_SHORT).show();
			return true;
		}

		public boolean onContextItemSelected(MenuItem item) {
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
					.getMenuInfo();

			String title = ((TextView) info.targetView).getText().toString();
			int type = ExpandableListView
					.getPackedPositionType(info.packedPosition);
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				int groupPos = ExpandableListView
						.getPackedPositionGroup(info.packedPosition);
				int childPos = ExpandableListView
						.getPackedPositionChild(info.packedPosition);
				// Toast.makeText(getApplicationContext(), title + ": Child " +
				// childPos + " clicked in group " + groupPos,
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplicationContext(), "Child"+childPos,
				// Toast.LENGTH_SHORT); return true;
			} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				int groupPos = ExpandableListView
						.getPackedPositionGroup(info.packedPosition);
				// Toast.makeText(getApplicationContext(), title + ": Group " +
				// groupPos + " clicked", Toast.LENGTH_SHORT).show();
				return true;
			}
			return false;

		}

	}

	public void loadPage(String[] languages1) {
		// TODO Auto-generated method stub
		// Log.d("hello", "here"+languages1[1]+"hh"+this);

		// MyExpandableListAdapter adapter = new
		// MyExpandableListAdapter(this,languages1);
		// listView.setAdapter(adapter);
		// listView.setOnChildClickListener(new OnChildClickListener() {
		//
		// public boolean onChildClick(ExpandableListView parent, View v,
		// int groupPosition, int childPosition, long id) {
		// // TODO Auto-generated method stub
		// Log.d("tab", "hello");
		// Toast.makeText(getApplicationContext(),": Child " +
		// languages[childPosition] + " clicked in group " + groupPosition,
		// Toast.LENGTH_SHORT).show();
		// try {
		// postURL(url, languages[childPosition]);
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// // Intent intent = new Intent(Renarration.this,webView.class);
		// // intent.putExtra("data",data);
		// // intent.putExtra("xPath",xPath);
		// // intent.putExtra("elementtype"UR[0],eType);
		// // setResult(RESULT_OK, intent);
		// // finish();
		// return true;
		// }
		// });
		// listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
		// R.layout.renlist,R.id.textview2, languages1));
		
		
		//////////////////////////////////////// Set the Adapers for with the relevent arrays
		final ListAdapter adapter1 = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				languages1);
		final ListAdapter blogsAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				blogs);
		
		String[] stateLang = new String[2];
		if(langMap.containsKey(state))	
		{
			stateLang[0]=langMap.get(state);	
		}
		else
		{
			stateLang[0]="No Renarrations Available for this location";
		}
		locationAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				stateLang);
		
		listView.setAdapter(adapter1);
		
		
		//Onclick of the ListView Item
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AjaxCalls ajaxCalls = new AjaxCalls();
				try {
					switch (tabNo) {
					case 0:
						postURL(url, languages[position]);
						break;

					case 1:
						bLanguages = ajaxCalls.getBlogLanguages(url,
								blogs[position]);
						builder = new AlertDialog.Builder(Renarration.this);
						builder.setTitle("Choose Narration");
						// final String[] aad =
						// {"http://team.servelots.com/my/pradeep/minimumwage.ogg"};
						builder.setSingleChoiceItems(languages, -1,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {
										// Toast.makeText(getApplicationContext(),
										// languages[item],
										// Toast.LENGTH_SHORT).show();
										alert1.dismiss();
										language = bLanguages[item];
										parseResult(url, blogs[position],
												language);
										// new
										// AsyncFinish(this).execute(languages[item]);
										// mediaPlayer(aud[item]);
									}
								});
						alert1 = builder.create();
						alert1.show();
						break;
						
					case 2: 
						postURL(url, languages[position]);
						break;	
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
		//////////Add tabs to the Tab Host
		
		tHost.addTab(tHost.newTabSpec("lang").setIndicator("LANGUAGES")
				.setContent(R.id.list));

		Intent tabIntent;

		tHost.addTab(tHost.newTabSpec("blog").setIndicator("BLOGS")
				.setContent(R.id.list));
		
		tHost.addTab(tHost.newTabSpec("location").setIndicator("LOCATION")
				.setContent(R.id.list));
		//Add fav Tab

		tHost.setCurrentTab(1);
		tHost.setCurrentTab(0);

		tHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId.equals("lang")) {
					tabNo = 0;
					listView.setAdapter(adapter1);
				}
				if (tabId.equals("blog")) {
					tabNo = 1;
					listView.setAdapter(blogsAdapter);
				}
				if(tabId.equals("location"))
				{
					if(langMap.containsKey(state))	
					{
						
					tabNo=2;
					listView.setAdapter(locationAdapter);
					}
					else
					{
						builder.setMessage(
								"No Renarrations available for this location")
								.setCancelable(false)
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												dialog.cancel();
												tHost.setCurrentTab(0);
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}
					
				}
			}
		});

	}

	public void postURL(String url, String lang)
			throws ClientProtocolException, IOException {
		HttpClient httpClient1 = new DefaultHttpClient();
		HttpPost httpPost1 = new HttpPost("http://y.a11y.in/replace");
		lang = lang.trim();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("lang", lang));
		nameValuePairs.add(new BasicNameValuePair("url", url));
		httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response1 = httpClient1.execute(httpPost1);
		result1 = HttpHelper.request(response1);
		parseResult(result1);
	}

	public void parseResult(String url, String blog, String language) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://y.a11y.in/filter");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			url = webView.url;
			url = url.trim();
			blog = blog.trim();
			language = language.trim();
			nameValuePairs.add(new BasicNameValuePair("url", url));
			nameValuePairs.add(new BasicNameValuePair("blog", blog));
			nameValuePairs.add(new BasicNameValuePair("lang", language));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			result1 = HttpHelper.request(response);
			list = new ArrayList<Hashtable<String, String>>();
			Hashtable<String, String> table;
			String[] renarrations = result1.split("###");
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

			xPath = new String[list.size()];
			data = new String[list.size()];
			eType = new String[list.size()];
			for (int ii = 0; ii < list.size(); ii++) {
				tableLang = list.get(ii);
				data[ii] = tableLang.get("data");
				eType[ii] = tableLang.get("elementtype");
				if (eType[ii].equals("audio/ogg"))
					;
				xPath[ii] = tableLang.get("xpath");
			}
			Intent intent = new Intent(Renarration.this, webView.class);
			intent.putExtra("data", data);
			intent.putExtra("xPath", xPath);
			intent.putExtra("elementtype", eType);
			setResult(RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	public void parseResult(String response) {
		list = new ArrayList<Hashtable<String, String>>();
		Hashtable<String, String> table;
		String[] renarrations = result1.split("###");
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
		web = new webView();
		xPath = new String[list.size()];
		data = new String[list.size()];
		eType = new String[list.size()];
		// authors = new ArrayList<String>();
		for (int ii = 0; ii < list.size(); ii++) {
			tableLang = list.get(ii);
			// // authors.add(tableLang.get("xpath"));
			data[ii] = tableLang.get("data");
			eType[ii] = tableLang.get("elementtype");
			if (eType[ii].equals("audio/ogg"))
				;
			xPath[ii] = tableLang.get("xpath");
		}

		// //// The old finish way. Onclick
		//
		// Intent intent = new Intent(Renarration.this,webView.class);
		// intent.putExtra("data",data);
		// intent.putExtra("xPath",xPath);
		// intent.putExtra("elementtype",eType);
		// setResult(RESULT_OK, intent);
		// finish();
		new AsyncFinish().execute("Finish");
	}

	public class AsyncFinish extends AsyncTask<String, Integer, Long> {
		@Override
		protected Long doInBackground(String... params) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Renarration.this, webView.class);
			intent.putExtra("data", data);
			intent.putExtra("xPath", xPath);
			intent.putExtra("elementtype", eType);
			setResult(RESULT_OK, intent);
			finish();
			return (long) 1;
		}

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = ProgressDialog.show(Renarration.this, "LOADING",
					"PLEASE WAIT.... LOADING THE RE-NARRATIONS");
			dialog.show();
		}

		protected void onPostExecute(Long result) {

			dialog.dismiss();
		}
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lat = location.getLatitude();
		lng = location.getLongitude();

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
