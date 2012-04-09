package com.servelots.alipi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.xml.sax.SAXException;

import com.servelots.alipi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class webView extends Activity implements OnClickListener, LocationListener {
	WebView myWebView;
	String result, result1, favLink;
	public static String url;
	ArrayList<String> audio, autoTextArray;
	TagNode info_node;
	HtmlCleaner cleaner;
	Button go, stop, play, ren;
	ImageButton fav;
	AutoCompleteTextView eUrl;
	URL newurl;
	InputMethodManager imm;
	ProgressBar loadingProgressBar, loadingTitle;
	Stack<String> history;
	MediaPlayer mp, mp1;
	ArrayAdapter<String> adapter;
	String[] aud, languages, language, blogs;
	AlertDialog.Builder builder, builder1;
	AlertDialog alert1;
	RelativeLayout rl;
	SharedPreferences urlBackup, favs;
	Boolean favorite;
	LocationManager lm;
	double lat, lng;
	Boolean networkState;
	List<Address> addresses;
	String state;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		networkState = HaveNetworkConnection();
		setContentView(R.layout.main);

		myWebView = (WebView) findViewById(R.id.webview);
		loadingProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		eUrl = (AutoCompleteTextView) findViewById(R.id.editurl);
		go = (Button) findViewById(R.id.buttongo);
		ren = (Button) findViewById(R.id.ren);
		play = (Button) findViewById(R.id.buttonaudio);
		fav = (ImageButton) findViewById(R.id.fav);
		fav.setVisibility(View.GONE);
		urlBackup = getSharedPreferences("prefFile", 0);
		favs = getSharedPreferences("favfile", 0);
		history = new Stack<String>();
		autoTextArray = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		builder = new AlertDialog.Builder(webView.this);
		builder1 = new AlertDialog.Builder(webView.this);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(this, "android");
		url = eUrl.getText().toString();
		eUrl.setAdapter(adapter);
		adapter.setNotifyOnChange(true);
		play.setVisibility(View.GONE);
		Map<String, ?> urlStored = urlBackup.getAll();
		Set<String> urlSet = urlStored.keySet();
		Iterator<String> itertor = urlSet.iterator();
		go.setOnClickListener(this);
		ren.setOnClickListener(this);
		play.setOnClickListener(this);
		fav.setOnClickListener(this);
		ren.setBackgroundResource(R.drawable.rennarate);
		ren.setEnabled(false);

		while (itertor.hasNext()) {
			adapter.add(itertor.next());
		}

		if (networkState == false) {
			builder.setMessage(
					"No data conectivity. Please turn on Internet Connectivity")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS));
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
			myWebView.loadUrl(eUrl.getText().toString());
		}
		myWebView.setWebChromeClient(new WebChromeClient() {
			// this will be called on page loading progress
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				loadingProgressBar.setProgress(newProgress);
				if (newProgress == 100) {
					loadingProgressBar.setVisibility(View.GONE);
				} else {
					loadingProgressBar.setVisibility(View.VISIBLE);
				}
			}
		});
		// Makes Progress bar Visible
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, final String url) {
				final AjaxCalls ajaxCalls = new AjaxCalls();
				Thread t = new Thread() {
					public void run() {
						networkState = HaveNetworkConnection();
						if (networkState == true) {
							languages = ajaxCalls.getLanguages(url);
							blogs = ajaxCalls.getBlogs(url);
							messageHandler.sendMessage(Message
									.obtain(messageHandler));
						}
					}
				};
				t.start();
			}

			Handler messageHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						if (languages.length > 0) {
							ren.setEnabled(true);
							ren.setBackgroundResource(R.drawable.buttonren);
						}
					} catch (NullPointerException e) {
						Toast.makeText(
								getApplicationContext(),
								"There must have been some problem with the network.Please Try Later",
								Toast.LENGTH_LONG).show();
					}
				}

			};
		});
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      Log.d("called","called");
		      
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
		myWebView.loadUrl("http://team.servelots.com/my/pradeep/flyer/content.html");
		eUrl.setText("http://team.servelots.com/my/pradeep/flyer/content.html");
		history.push(eUrl.getText().toString());
		adapter.add(eUrl.getText().toString());
		eUrl.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// code for detecting change in scroll goes here
		// if satisfied with the level of scroll
		eUrl.setVisibility(View.INVISIBLE);
	}

	private boolean HaveNetworkConnection() {
		boolean HaveConnectedWifi = false;
		boolean HaveConnectedMobile = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					HaveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					HaveConnectedMobile = true;
		}
		return HaveConnectedWifi || HaveConnectedMobile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent iData) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, iData);
		if (resultCode == RESULT_OK) {
			String[] d = iData.getStringArrayExtra("data");
			String[] x = iData.getStringArrayExtra("xPath");
			String[] et = iData.getStringArrayExtra("elementtype");

			try {

				reNarrate(x, et, d);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

		}
	}

	public void mediaPlayer(String path) {
		alert1.dismiss();
		mp1 = new MediaPlayer();

		try {
			mp1.setDataSource(path);
		}

		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		try {
			mp1.prepare();
		}

		catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		mp1.start();
		play.setEnabled(false);
		mp1.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				play.setEnabled(true);
			}
		});

	}

	public void reNarrate(String[] xp, String[] et, String[] dt)
			throws XPatherException, ParserConfigurationException,
			SAXException, IOException, XPatherException {
		newurl = new URL(eUrl.getText().toString());
		cleaner = new HtmlCleaner();
		audio = new ArrayList<String>();
		aud = new String[xp.length];
		String innerContent = new String();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		TagNode node = cleaner.clean(newurl);
		int i = 0;
		// USE THE CLEANER TO "CLEAN" THE HTML AND RETURN IT AS A TAGNODE OBJECT
		while (i < xp.length) {
			// ONCE THE HTML IS CLEANED, THEN RUN XPATH EXPRESSIONS ON THE NODE,
			// WHICH WILL THEN RETURN AN ARRAY OF TAGNODE OBJECTS (THESE ARE
			// RETURNED AS OBJECTS BUT GET CASTED BELOW)
			xp[i] = xp[i].toLowerCase();

			if (xp[i].startsWith("/html")) {
				xp[i] = xp[i].replace("/html", "/");
			}
			Object[] nodes = node.evaluateXPath(xp[i]);
			// MAKE SURE THAT XPATH WAS CORRECT AND THAT AN ACTUAL NODE(S) WAS
			// RETURNED
			if (nodes.length > 0) {
				// CASTED TO A TAGNODE
				try {
					info_node = (TagNode) nodes[0];
				} catch (ArrayIndexOutOfBoundsException arr) {

				}

				if (et[i].equals("image")) {
					info_node.setAttribute("src", dt[i].split(",")[1]);
					String width = dt[i].split(",")[0].split("x")[0];
					String height = dt[i].split(",")[0].split("x")[1];
					info_node.setAttribute("width", width);
					info_node.setAttribute("height", height);
				}

				if (et[i].equals("audio/ogg")) {
					play.setVisibility(View.VISIBLE);
					audio.add(dt[i].trim());
					dt[i] = "";
					Toast.makeText(getApplicationContext(),
							"Audio Narrations available", Toast.LENGTH_LONG)
							.show();
				}
				cleaner.setInnerHtml(info_node, dt[i]);
				innerContent = cleaner.getInnerHtml(node);
				myWebView
						.loadDataWithBaseURL(eUrl.getText().toString(),
								innerContent, "text/html", "utf-8",
								"http://google.com");
			}
			i++;
		}
	}

	@Override
	public void onBackPressed() {
		if (history.empty() == false) {
			history.pop();
			// myWebView.loadUrl(url);
			// eUrl.setText(url);
			if (myWebView.canGoBack()) {
				myWebView.goBack();
				eUrl.setText(myWebView.getOriginalUrl());
			}
			play.setVisibility(View.GONE);
			// mp1.pause();
		} else {
			builder1.setMessage("Are you Sure You want Quit the Application?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder1.create();
			alert.show();
		}
		return;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ren:
			Intent intent = new Intent(webView.this, Renarration.class);
			// intent.putExtra("url", url);
			Bundle extras = new Bundle();
			extras.putStringArray("languages", languages);
			extras.putStringArray("blogs", blogs);

			intent.putExtras(extras);
			intent.putExtra("url", url);
			intent.putExtra("State", state);
			startActivityForResult(intent, 1);
			break;

		case R.id.buttonaudio:
			if (audio.size() > 0) {
				aud = new String[audio.size()];
				builder = new AlertDialog.Builder(webView.this);
				builder.setTitle("Choose Narration");
				// final String[] aad =
				// {"http://team.servelots.com/my/pradeep/minimumwage.ogg"};
				for (int len = 0; len < audio.size(); len++) {
					aud[len] = audio.get(len);
				}
				builder.setSingleChoiceItems(aud, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								Toast.makeText(getApplicationContext(),
										aud[item], Toast.LENGTH_SHORT).show();
								mediaPlayer(aud[item]);
							}
						});
				alert1 = builder.create();
				alert1.show();
			}
			break;

		case R.id.buttongo:
			if (eUrl.getText().toString().length() == 0) {
				Toast.makeText(getApplicationContext(), "Please Enter URL",
						Toast.LENGTH_LONG).show();
			} else {
				url = eUrl.getText().toString();
				if (url.contains("http://")) {
					ren.setEnabled(false);
					ren.setBackgroundResource(R.drawable.rennarate);
					myWebView.loadUrl(url);
				} else {
					url = "http://" + url;
					myWebView.loadUrl(url);
					eUrl.setText(url);
				}
				int position = adapter.getPosition(url);
				if (position == -1) {
					adapter.add(url);
					urlBackup = getSharedPreferences("prefFile", 0);
					favs = getSharedPreferences("favfile", 0);
					SharedPreferences.Editor editor = urlBackup.edit();
					if (urlBackup.contains(url)) {
					} else {
						editor.putString(url, url);
						// Commit the edits!
						editor.commit();
					}
				}
				if (favs.contains(url)) {
					fav.setBackgroundResource(R.drawable.selectfav);
				} else {
					fav.setBackgroundResource(R.drawable.fav);
				}
				play.setVisibility(View.GONE);
				history.push(url);
				imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(go.getWindowToken(), 0);
			}
			break;

		case R.id.fav:
			fav.setBackgroundResource(R.drawable.selectfav);
			if (favs.contains(url)) {
				// Do not add if the url is already present
			} else {
				// add if the url is not present
				favs = getSharedPreferences("favfile", 0);
				SharedPreferences.Editor favEdit = favs.edit();
				favEdit.putString("favorite", url);
				favEdit.commit();
			}
		}
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lat = location.getLatitude();
		lng = location.getLongitude();
		Geocoder gcd = new Geocoder(getApplicationContext(),
				Locale.getDefault());
		Log.d("state","state called"+lat+ lng);
		try {
			addresses = gcd.getFromLocation(lat, lng, 1);
			if (addresses.size() > 0) {
				System.out.println(addresses.get(0).getLocality());
				state = addresses.get(0).getAdminArea();
				
				

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Unable to get Location",
					Toast.LENGTH_LONG).show();
		} catch (NullPointerException e) {
		}
		// Toast.makeText(getApplicationContext(),"lat"+lat+"long"+lng,Toast.LENGTH_SHORT);
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