package com.coin.karaoke.misook.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdViewListener;
import com.admixer.CustomPopup;
import com.admixer.CustomPopupListener;
import com.admixer.InterstitialAd;
import com.admixer.InterstitialAdListener;
import com.admixer.PopupInterstitialAdOption;
import com.coin.karaoke.misook.R;
import com.coin.karaoke.misook.adapter.MainAdapter;
import com.coin.karaoke.misook.data.Favorite_DBopenHelper;
import com.coin.karaoke.misook.data.Main_Data;
import com.coin.karaoke.misook.data.Pause_DBOpenHelper;
import com.coin.karaoke.misook.mediaplayer.ContinueMediaPlayer;
import com.coin.karaoke.misook.util.Crypto;
import com.coin.karaoke.misook.util.PreferenceUtil;
import com.coin.karaoke.misook.util.Utils;
import com.coin.karaoke.misook.videoplayer.CustomVideoPlayer;
import com.coin.karaoke.misook.youtubeplayer.CustomYoutubePlayer;
import com.google.android.gms.ads.AdView;
import com.mogua.localization.KoreanTextMatch;
import com.mogua.localization.KoreanTextMatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import kr.co.inno.autocash.service.AutoServiceActivity;
public class MainActivity extends Activity implements OnItemClickListener, OnClickListener, OnScrollListener, AdViewListener, CustomPopupListener, InterstitialAdListener{
	public static Context context;
	public ConnectivityManager connectivityManger;
	public NetworkInfo mobile;
	public NetworkInfo wifi;
	public static MainAdapter main_adapter;
	public static ListView listview_main;
	public static int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public static LinearLayout layout_nodata;
	public static RelativeLayout ad_layout;
	public static boolean loadingMore = true;
	public static boolean exeFlag;
	public Handler handler = new Handler();
	public static int start_index;
	public static int itemsPerPage = 50;
	public static int current_position = 0;
	public boolean flag;
	public Main_ParseAsync main_parseAsync = null;
	public ProgressDialog progressDialog = null;
	public static Favorite_DBopenHelper favorite_mydb;
	public static Pause_DBOpenHelper pause_mydb;
	public static NotificationManager notificationManager;
	public static Notification notification;
	public static int noti_state = 1;
	public static TextView txt_main_title;
	public static int TotalRow;;
	public static ArrayList<Main_Data> list;
	public static LinearLayout layout_progress;
	public static Button bt_category, bt_intent_favorite, bt_plus_coin;
	public static LinearLayout action_layout;
	public static Button bt_all_select, bt_play_video, bt_play_media;
	public Cursor cursor;
	public static AlertDialog alertDialog;
	public static int category_which = 0;
	public static SharedPreferences settings,pref;
	public Editor edit;
	public boolean retry_alert = false;
	public String num;
	public static EditText edit_searcher;
	public static ImageButton bt_home, bt_search_result; 
	public static String searchKeyword = "";
	private AdView adView;
	public static com.admixer.InterstitialAd interstialAd;
	KoreanTextMatch match1, match2, match3, match4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_activity);
	context = this;
	
	txt_main_title = (TextView)findViewById(R.id.txt_main_title);
	txt_main_title.setText(context.getString(R.string.app_name));
	
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "ronrhzuy");
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/8025216969");
	AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/9501950166");
	
//	  Custom Popup 시작
	CustomPopup.setCustomPopupListener(this);
	CustomPopup.startCustomPopup(this, "ronrhzuy");
	
	addBannerView();

	num = "902";
	
	settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
	edit = settings.edit();
	edit.putInt("category_which", 1);
	edit.commit();
	
	start_index = 1;
	layout_nodata = (LinearLayout)findViewById(R.id.layout_nodata);
	layout_progress = (LinearLayout)findViewById(R.id.layout_progress);
	action_layout = (LinearLayout)findViewById(R.id.action_layout);
	listview_main = (ListView)findViewById(R.id.listview_main);
	bt_category = (Button)findViewById(R.id.bt_category);
	bt_category.setText(context.getString(R.string.txt_main_activity21));
	bt_category.setTextColor(Color.BLACK);
	bt_all_select = (Button)findViewById(R.id.bt_all_select);
	bt_play_video = (Button)findViewById(R.id.bt_play_video);
	bt_play_media = (Button)findViewById(R.id.bt_play_media);
	edit_searcher = (EditText)findViewById(R.id.edit_searcher);
	bt_home = (ImageButton)findViewById(R.id.bt_home);
	bt_search_result = (ImageButton)findViewById(R.id.bt_search_result);
	bt_intent_favorite = (Button)findViewById(R.id.bt_intent_favorite);
	bt_plus_coin = (Button)findViewById(R.id.bt_plus_coin);
	bt_home.setOnClickListener(this);
	bt_search_result.setOnClickListener(this);
	bt_all_select.setOnClickListener(this);
	bt_play_video.setOnClickListener(this);
	bt_play_media.setOnClickListener(this);
	bt_category.setOnClickListener(this);
	bt_intent_favorite.setOnClickListener(this);
	bt_plus_coin.setOnClickListener(this);
	pause_mydb = new Pause_DBOpenHelper(this);
	favorite_mydb = new Favorite_DBopenHelper(this);
	retry_alert = true;
	
	if (SDK_INT >= Build.VERSION_CODES.M){ 
		checkPermission();
	}else {
		list = new ArrayList<Main_Data>();
		list.clear();
		seacher_start();
		displaylist();	
		exit_handler();
		auto_service();
	}
	}
	
	public void Return_AlertShow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
            	PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
                finish();
            	dialog.dismiss();
            }
        });
        AlertDialog myAlertDialog = builder.create();
        myAlertDialog.show();
    }
	
	private void checkPermission() {
		if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE)) {
                // Explain to the user why we need to write the permission.
            	Return_AlertShow(context.getString(R.string.permission_cancel));
            }
            requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        } else {
        	list = new ArrayList<Main_Data>();
    		list.clear();
        	seacher_start();
    		displaylist();	
    		exit_handler();
    		auto_service();
        }
	}
	
	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                try{
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    	list = new ArrayList<Main_Data>();
                		list.clear();
                    	seacher_start();
                		displaylist();	
                		exit_handler();
                		auto_service();
                    } else {
                    	Return_AlertShow(context.getString(R.string.permission_cancel));
                    }
                    break;
                }catch (ArrayIndexOutOfBoundsException e){
                }catch (Exception e){
                }
        	}
    	}
	
	private void auto_service() {
        Intent intent = new Intent(context, AutoServiceActivity.class);
        context.stopService(intent);
        context.startService(intent);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		retry_alert = false;
		
		// Custom Popup 종료
		CustomPopup.stopCustomPopup();
		
		edit_searcher.setText("");
    	
		settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
		edit = settings.edit();
		edit.putInt("category_which", 1);
		edit.commit();
		
		current_position = 0;
    	start_index = 1;
		loadingMore = true;
		exeFlag = false;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		layout_progress.setVisibility(View.GONE);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(main_adapter != null){
			main_adapter.notifyDataSetChanged();	
		}
		
		txt_main_title = (TextView)findViewById(R.id.txt_main_title);
		txt_main_title.setText(context.getString(R.string.app_name));
		Log.i("dsu", "onRestart");
	}
	
	public void exit_handler(){
    	handler = new Handler(){
    		@Override
    		public void handleMessage(Message msg) {
    			if(msg.what == 0){
    				flag = false;
    			}
    		}
    	};
    }
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("ronrhzuy");
    	adInfo.setTestMode(false);
        com.admixer.AdView adView = new com.admixer.AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
    }
	
	public void addInterstitialView_popup() {
    	if(interstialAd != null)
			return;
		AdInfo adInfo = new AdInfo("ronrhzuy");
		adInfo.setInterstitialTimeout(0); // 초단위로 전면 광고 타임아웃 설정 (기본값 : 0, 0 이면 서버 지정 시간(20)으로 처리됨)
		adInfo.setUseRTBGPSInfo(false);
		adInfo.setMaxRetryCountInSlot(-1); // 리로드 시간 내에 전체 AdNetwork 반복 최대 횟수(-1 : 무한, 0 : 반복 없음, n : n번 반복)
		adInfo.setBackgroundAlpha(true); // 고수익 전면광고 노출 시 광고 외 영역 반투명처리 여부 (true: 반투명, false: 처리안함)

//		 이 주석을 제거하시면 고수익 전면광고가 팝업형으로 노출됩니다.
		// 팝업형 전면광고 세부설정을 원하시면 아래 PopupInterstitialAdOption 설정하세요
		PopupInterstitialAdOption adConfig = new PopupInterstitialAdOption();
		// 팝업형 전면광고 노출 상태에서 뒤로가기 버튼 방지 (true : 비활성화, false : 활성화)
		adConfig.setDisableBackKey(true);
		// 왼쪽버튼. 디폴트로 제공되며, 광고를 닫는 기능이 적용되는 버튼 (버튼문구, 버튼색상)
		adConfig.setButtonLeft(context.getString(R.string.txt_finish_no), "#234234");
		// 오른쪽 버튼을 사용하고자 하면 반드시 설정하세요. 앱을 종료하는 기능을 적용하는 버튼. 미설정 시 위 광고종료 버튼만 노출
		adConfig.setButtonRight(context.getString(R.string.txt_finish_yes), "#234234");
		// 버튼영역 색상지정
		adConfig.setButtonFrameColor(null);
		// 팝업형 전면광고 추가옵션 (com.admixer.AdInfo$InterstitialAdType.Basic : 일반전면, com.admixer.AdInfo$InterstitialAdType.Popup : 버튼이 있는 팝업형 전면)
		adInfo.setInterstitialAdType(AdInfo.InterstitialAdType.Popup, adConfig);
		
		interstialAd = new InterstitialAd(this);
		interstialAd.setAdInfo(adInfo, this);
		interstialAd.setInterstitialAdListener(this);
		interstialAd.startInterstitial();
    }

	
	public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("ronrhzuy");
//        	adInfo.setTestMode(false);
        	interstialAd = new com.admixer.InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }
	
	
	public void seacher_start(){
		edit_searcher.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					searchKeyword = s.toString().toLowerCase();
					Log.e("dsu", "검색어 : " + searchKeyword);
				} catch (Exception e) {
				}
			}
		});
	}
	
	public void displaylist(){
		main_parseAsync = new Main_ParseAsync();
		main_parseAsync.execute();
		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //허니콤 버전에서만 실행 가능한 API 사용}
			listview_main.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		listview_main.setOnItemClickListener(this);
		listview_main.setItemsCanFocus(false);
		listview_main.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview_main.setOnScrollListener(this);
	}
	
	public class Main_ParseAsync extends AsyncTask<String, Integer, String>{
		String Response;
		Main_Data main_data;
		ArrayList<Main_Data> menuItems = new ArrayList<Main_Data>();
		String i;
		int _id;
		String id;
		String title;
		String portal;
		String thumbnail_hq;
		String sprit_title[];
		public Main_ParseAsync(){
		}
			@Override
			protected String doInBackground(String... params) {
				String sTag;
				try{
				   String data = Crypto.decrypt(Utils.data, context.getString(R.string.txt_str8));
		           String str = data+i+".php?view="+num;
		           HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(str).openConnection();
		           HttpURLConnection.setFollowRedirects(false);
		           localHttpURLConnection.setConnectTimeout(15000);
		           localHttpURLConnection.setReadTimeout(15000); 
		           localHttpURLConnection.setRequestMethod("GET");
		           localHttpURLConnection.connect();
		           InputStream inputStream = new URL(str).openStream(); //open Stream을 사용하여 InputStream을 생성합니다.
		           XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		           XmlPullParser xpp = factory.newPullParser();
		           xpp.setInput(inputStream, "EUC-KR"); //euc-kr로 언어를 설정합니다. utf-8로 하니깐 깨지더군요
		           int eventType = xpp.getEventType();
		           while (eventType != XmlPullParser.END_DOCUMENT) {
			        	if (eventType == XmlPullParser.START_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.END_DOCUMENT) {
			        	}else if (eventType == XmlPullParser.START_TAG){
			        		sTag = xpp.getName();
			        		if(sTag.equals("Content")){
			        			main_data = new Main_Data();
			        			_id = Integer.parseInt(xpp.getAttributeValue(null, "id") + "");
			            	}else if(sTag.equals("videoid")){
			        			Response = xpp.nextText()+"";
			            	}else if(sTag.equals("subject")){
			            		title = xpp.nextText()+"";
			            		sprit_title = title.split("-");
			            	}else if(sTag.equals("portal")){
			            		portal = xpp.nextText()+"";
			            	}else if(sTag.equals("thumb")){
			            		thumbnail_hq = xpp.nextText()+"";
			            	}
			        	} else if (eventType == XmlPullParser.END_TAG){
			            	sTag = xpp.getName();
			            	if(sTag.equals("Content")){
			            		main_data._id = _id;
			            		main_data.id = Response;
			            		main_data.title = title;
			            		main_data.portal = portal;
			            		main_data.category = context.getString(R.string.app_name);
			            		main_data.thumbnail_hq = thumbnail_hq;
			            		if(searchKeyword != null && "".equals(searchKeyword.trim()) == false){
			            			KoreanTextMatcher matcher1 = new KoreanTextMatcher(searchKeyword.toLowerCase());
			            			KoreanTextMatcher matcher2 = new KoreanTextMatcher(searchKeyword.toUpperCase());
			            			match1 = matcher1.match(main_data.title.toLowerCase());
			            			match2 = matcher1.match(main_data.title.toUpperCase());
			            			match3 = matcher2.match(main_data.title.toLowerCase());
			            			match4 = matcher2.match(main_data.title.toUpperCase());
			            			if (match1.success()) {
			            				list.add(main_data);
			            			}else if (match2.success()) {
			            				list.add(main_data);
			            			}else if (match3.success()) {
			            				list.add(main_data);
			            			}else if (match4.success()) {
			            				list.add(main_data);
			            			}
			            		}else{
			            			list.add(main_data);
			            		}
			            	}
			            } else if (eventType == XmlPullParser.TEXT) {
			            }
			            eventType = xpp.next();
			        }
		         }
				 catch (SocketTimeoutException localSocketTimeoutException)
		         {
		         }
		         catch (ClientProtocolException localClientProtocolException)
		         {
		         }
		         catch (IOException localIOException)
		         {
		         }
		         catch (Resources.NotFoundException localNotFoundException)
		         {
		         }
		         catch (NullPointerException NullPointerException)
		         {
		         }
				 catch (JSONException e) 
				 {
				 } 
				 catch (Exception e) 
				 {
				 }
				 return Response;
			}
			
			@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            i="4";
	            layout_progress.setVisibility(View.VISIBLE);
	        }
			@Override
			protected void onPostExecute(String Response) {
				super.onPostExecute(Response);
				layout_progress.setVisibility(View.GONE);
				try{
					if(Response != null){
						for(int i=0;; i++){
							if(i >= list.size()){
//							while (i > list.size()-1){
								main_adapter = new MainAdapter(context, menuItems, listview_main);
								listview_main.setAdapter(main_adapter);
								listview_main.setFocusable(true);
								listview_main.setSelected(true);
								listview_main.setSelection(current_position);
								if(listview_main.getCount() == 0){
									layout_nodata.setVisibility(View.VISIBLE);
								}else{
									layout_nodata.setVisibility(View.GONE);
								}
								action_layout.setVisibility(View.GONE);
								return;
							}
							menuItems.add(list.get(i));
						}
					}else{
						layout_nodata.setVisibility(View.VISIBLE);
						Retry_AlertShow(context.getString(R.string.sub6_txt8));
					}
				}catch(NullPointerException e){
				}
			}
			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
			}
		}
	
	@Override
	public void onClick(View view) {
		if(view == bt_category){
			final String channel_title[] = {
					context.getString(R.string.txt_channel_title0),
					context.getString(R.string.txt_channel_title1),
					context.getString(R.string.txt_channel_title2),
					context.getString(R.string.txt_channel_title3),
					context.getString(R.string.txt_channel_title4),
					context.getString(R.string.txt_channel_title5),
					context.getString(R.string.txt_channel_title6),
					context.getString(R.string.txt_channel_title7),
					context.getString(R.string.txt_channel_title8),
					context.getString(R.string.txt_channel_title9),
					context.getString(R.string.txt_channel_title10)
			};
			settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
			edit = settings.edit();
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", 0);
			
			alertDialog = new AlertDialog.Builder(context)
			.setTitle(context.getString(R.string.txt_love_ment))
			.setSingleChoiceItems(channel_title, category_which, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "901";
						displaylist();
						bt_category.setText(channel_title[0]);
						edit.putInt("category_which", 0);
					}else if(which == 1){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "902";
						displaylist();
						bt_category.setText(channel_title[1]);
						edit.putInt("category_which", 1);
					}else if(which == 2){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "903";
						displaylist();
						bt_category.setText(channel_title[2]);
						edit.putInt("category_which", 2);
					}else if(which == 3){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "904";
						displaylist();
						bt_category.setText(channel_title[3]);
						edit.putInt("category_which", 3);
					}else if(which == 4){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "905";
						displaylist();
						bt_category.setText(channel_title[4]);
						edit.putInt("category_which", 4);
					}else if(which == 5){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "906";
						displaylist();
						bt_category.setText(channel_title[5]);
						edit.putInt("category_which", 5);
					}else if(which == 6){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "907";
						displaylist();
						bt_category.setText(channel_title[6]);
						edit.putInt("category_which", 6);
					}else if(which == 7){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "908";
						displaylist();
						bt_category.setText(channel_title[7]);
						edit.putInt("category_which", 7);
					}else if(which == 8){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "909";
						displaylist();
						bt_category.setText(channel_title[8]);
						edit.putInt("category_which", 8);
					}else if(which == 9){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "910";
						displaylist();
						bt_category.setText(channel_title[9]);
						edit.putInt("category_which", 9);
					}else if(which == 10){
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
						num = "911";
						displaylist();
						bt_category.setText(channel_title[10]);
						edit.putInt("category_which", 10);
					}
					edit.commit();
					dialog.dismiss();
				}
			}).show();
			
		}else if(view == bt_all_select){
			if(bt_all_select.isSelected()){
				bt_all_select.setSelected(false);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity4));
				for(int i=0; i < listview_main.getCount(); i++){
					listview_main.setItemChecked(i, false);
				}
				action_layout.setVisibility(View.GONE);
			}else{
				bt_all_select.setSelected(true);
				bt_all_select.setText(context.getString(R.string.txt_continue_activity7));
				for(int i=0; i < listview_main.getCount(); i++){
					listview_main.setItemChecked(i, true);
				}
			}
		}else if(view == bt_play_video){
			SparseBooleanArray sba = listview_main.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			if(sba.size() != 0){
				Main_Data tmp_main_data = (Main_Data)main_adapter.getItem(0);
				String tmp_portal = tmp_main_data.portal;
				if(tmp_portal.equals("youtube")){
					for(int i = 0; i < listview_main.getCount(); i++){
						if(sba.get(i)){
							Main_Data main_data = (Main_Data)main_adapter.getItem(i);
							String videoid = main_data.id;
							String subject = main_data.title;
							String portal = main_data.portal;
							array_videoid.add(videoid);
							array_subject.add(subject);
							array_portal.add(portal);
							sba = listview_main.getCheckedItemPositions();
						}
					}
					if(array_videoid.size() != 0){
						Intent intent = new Intent(context, CustomYoutubePlayer.class);
						intent.putExtra("array_videoid", array_videoid);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
				}else{
					for(int i = listview_main.getCount() -1; i>=0; i--){
						if(sba.get(i)){
							Main_Data main_data = (Main_Data)main_adapter.getItem(i);
							String videoid = main_data.id;
							String subject = main_data.title;
							String portal = main_data.portal;
							array_videoid.add(videoid);
							array_subject.add(subject);
							array_portal.add(portal);
							sba = listview_main.getCheckedItemPositions();
						}
					}
					if(array_videoid.size() != 0){
						Intent intent = new Intent(context, CustomVideoPlayer.class);
						intent.putExtra("array_videoid", array_videoid);
						intent.putExtra("array_subject", array_subject);
						intent.putExtra("array_portal", array_portal);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
				}
			}
		}else if(view == bt_play_media){
			SparseBooleanArray sba = listview_main.getCheckedItemPositions();
			ArrayList<String> array_videoid = new ArrayList<String>();
			ArrayList<String> array_subject = new ArrayList<String>();
			ArrayList<String> array_thumb = new ArrayList<String>();
			ArrayList<String> array_portal = new ArrayList<String>();
			ArrayList<String> array_artist = new ArrayList<String>();
			ArrayList<String> array_playtime = new ArrayList<String>();
			if(sba.size() != 0){
			for(int i = listview_main.getCount() -1; i>=0; i--){
				if(sba.get(i)){
					Main_Data main_data = (Main_Data)main_adapter.getItem(i);
					String videoid = main_data.id;
					String subject = main_data.title;
					String thumb = main_data.thumbnail_hq;
					String portal = main_data.portal;
					String artist = context.getString(R.string.app_name);
//					String sprit_playtime[] = subject.split("-");
					String playtime = "";
					array_videoid.add(videoid);
					array_subject.add(subject);
					array_thumb.add(thumb);
					array_portal.add(portal);
					array_artist.add(artist);
					array_playtime.add(playtime);
					sba = listview_main.getCheckedItemPositions();
				}
			}
			if(array_videoid.size() != 0){
				Intent intent = new Intent(context, ContinueMediaPlayer.class);
				intent.putExtra("array_videoid", array_videoid);
				intent.putExtra("array_subject", array_subject);
				intent.putExtra("array_thumb", array_thumb);
				intent.putExtra("array_artist", array_artist);
				intent.putExtra("array_playtime", array_playtime);
				intent.putExtra("array_portal", array_portal);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
		}else if(view == bt_search_result){
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", category_which);
			if(category_which != 0){
				alertDialog = new AlertDialog.Builder(this)
			    .setTitle(context.getString(R.string.txt_alert_search_ment1))
				.setIcon(R.drawable.bt_search_on)
				.setMessage(context.getString(R.string.txt_alert_search_ment2))
				.setPositiveButton(context.getString(R.string.txt_alert_search_button_yes), new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						current_position = 0;
						start_index = 1;
						loadingMore = true;
						exeFlag = false;
						
						list = new ArrayList<Main_Data>();
						list.clear();
//						edit_searcher.setText("");
						num = "901";
						displaylist();
						bt_category.setText(context.getString(R.string.txt_channel_title0));
						
						settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
						edit = settings.edit();
						pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
						category_which = pref.getInt("category_which", 0);
						edit.putInt("category_which", 0);
						edit.commit();
						
						InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
			    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
					}
				}).show();
			}
			else{
				String search_text = edit_searcher.getText().toString();
				if ((search_text != null) && (search_text.length() > 0)){
					InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
		    		inputMethodManager.hideSoftInputFromWindow(edit_searcher.getWindowToken(), 0);
					
		    		list = new ArrayList<Main_Data>();
					list.clear();
					displaylist();
				}else{
					Toast.makeText(context, context.getString(R.string.txt_search_empty), Toast.LENGTH_SHORT).show();
				}
			}
		}else if(view == bt_home){
			current_position = 0;
			start_index = 1;
			loadingMore = true;
			exeFlag = false;
			
			list = new ArrayList<Main_Data>();
			list.clear();
			edit_searcher.setText("");
			num = "902";
			displaylist();
			bt_category.setText(context.getString(R.string.txt_channel_title1));
			
			settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
			edit = settings.edit();
			pref = getSharedPreferences(context.getString(R.string.txt_main_activity36), Activity.MODE_PRIVATE);
			category_which = pref.getInt("category_which", 1);
			edit.putInt("category_which", 1);
			edit.commit();
		}else if(view == bt_intent_favorite){
			Intent intent = new Intent(this, FavoriteActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(view == bt_plus_coin){
			alertDialog = new AlertDialog.Builder(this)
		    .setTitle(context.getString(R.string.txt_plus_coin_ment1))
			.setIcon(R.drawable.bt_plus_coin_normal)
			.setMessage(context.getString(R.string.txt_plus_coin_ment2))
			.setPositiveButton(context.getString(R.string.txt_alert_button_yes), new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(context, context.getString(R.string.txt_coin_ment), Toast.LENGTH_LONG).show();
					addInterstitialView();
				}
			})
			.setNegativeButton(context.getString(R.string.txt_alert_button_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		int selectd_count = 0;
    	SparseBooleanArray sba = listview_main.getCheckedItemPositions();
		if(sba.size() != 0){
			for(int i = listview_main.getCount() -1; i>=0; i--){
				if(sba.get(i)){
					sba = listview_main.getCheckedItemPositions();
					selectd_count++;
				}
			}
		}
		if(selectd_count == 0){
			action_layout.setVisibility(View.GONE);
		}else{
			action_layout.setVisibility(View.VISIBLE);
		}
		if(main_adapter != null){
			main_adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(view == listview_main){
			if(totalItemCount != 0 && firstVisibleItem  > 1 ){
				listview_main.setFastScrollEnabled(true);
			}else{
				listview_main.setFastScrollEnabled(false);
			}
		}
	}
	
	public void Retry_AlertShow(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false);
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_main_activity14), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				 current_position = 0;
            	 loadingMore = true;
            	 exeFlag = false;
            	 main_parseAsync = new Main_ParseAsync();
            	 main_parseAsync.execute();
            	 dialog.dismiss();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_main_activity13), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
	}
	
	public static void setNotification_continue(Context context, ArrayList<String> array_music, ArrayList<String> array_videoid, ArrayList<String> array_playtime, ArrayList<String> array_imageurl, ArrayList<String> array_artist, int video_num) {
    	if (SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			try{
				notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		        Intent intent = new Intent(context, ContinueMediaPlayer.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        intent.putExtra("array_music", array_music);
				intent.putExtra("array_videoid", array_videoid);
				intent.putExtra("array_playtime", array_playtime);
				intent.putExtra("array_imageurl", array_imageurl);
				intent.putExtra("array_artist", array_artist);
				intent.putExtra("video_num", video_num);
				PendingIntent content = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		        Notification.Builder builder = new Notification.Builder(context)
		                .setContentIntent(content)
		                .setSmallIcon(R.drawable.icon128)
		                .setContentTitle(array_music.get(video_num))
//		                .setContentText("")
		                .setDefaults(Notification.FLAG_AUTO_CANCEL)
		                .setTicker(context.getString(R.string.app_name));
		        notification = builder.build();
		        notificationManager.notify(noti_state, notification);
			}catch(NullPointerException e){
			}
		}
    }
	
	public static void setNotification_Cancel(){
    	if(notificationManager != null) notificationManager.cancel(noti_state);
    }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 if(!flag){
				 Toast.makeText(context, context.getString(R.string.txt_main_activity6) , Toast.LENGTH_SHORT).show();
				 flag = true;
				 handler.sendEmptyMessageDelayed(0, 2000);
			 return false;
			 }else{
				 try{
					 if(ContinueMediaPlayer.mediaPlayer.isPlaying()){
						 ContinueMediaPlayer.mediaPlayer.stop();
					 }
					 MainActivity.setNotification_Cancel();
					 handler.postDelayed(new Runnable() {
						 @Override
						 public void run() {
							 PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
							 finish();
						 }
					 },0);
						 
				 }catch(Exception e){
				 }
			 }
            return false;	 
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	//** BannerAd 이벤트들 *************
	@Override
	public void onClickedAd(String arg0, com.admixer.AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1,
			com.admixer.AdView arg2) {
	}

	@Override
	public void onReceivedAd(String arg0, com.admixer.AdView arg1) {
	}
	//** CustomPopup 이벤트들 *************
	@Override
	public void onCloseCustomPopup(String arg0) {
	}

	@Override
	public void onHasNoCustomPopup() {
	}

	@Override
	public void onShowCustomPopup(String arg0) {
	}

	@Override
	public void onStartedCustomPopup() {
	}

	@Override
	public void onWillCloseCustomPopup(String arg0) {
	}

	@Override
	public void onWillShowCustomPopup(String arg0) {
	}

	@Override
	public void onInterstitialAdClosed(InterstitialAd arg0) {
		interstialAd = null;
		PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
		finish();
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			InterstitialAd arg2) {
		interstialAd = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.app_name));
		builder.setMessage(context.getString(R.string.txt_finish_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_finish_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_AD_VIEW, true);
				finish();
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_finish_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
             	 dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
		
	}

	@Override
	public void onInterstitialAdReceived(String arg0, InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, InterstitialAd arg1) {
	}
}
