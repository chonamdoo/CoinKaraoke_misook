package com.coin.karaoke.misook.mediaplayer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.AdView;
import com.admixer.AdViewListener;
import com.admixer.InterstitialAdListener;
import com.coin.karaoke.misook.R;
import com.coin.karaoke.misook.activity.MainActivity;
import com.coin.karaoke.misook.util.ImageLoader;
import com.coin.karaoke.misook.util.TimeUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ContinueMediaPlayer extends Activity implements OnClickListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,android.widget.SeekBar.OnSeekBarChangeListener, OnErrorListener, AdViewListener, InterstitialAdListener{
	public static LinearLayout layout_progress;
	public static Context context;
	public static SeekBar mediacontroller_progress;
	public static Handler navigator_handler = new Handler();
	public static TextView max_time, current_time, txt_description_title,txt_title, txt_pubDate ;
	public static ImageButton bt_duration_rew, bt_pause, bt_duration_ffwd, bt_rew, bt_ffwd;
	public int seekBackwardTime = 5000; // 5000 milliseconds
	public int seekForwardtime = 5000; // 5000 milliseconds
	public static LinearLayout layout_control;
	public int duration_check = 0;
	public static boolean CALL_STATE_OFFHOOK = false;
	public static boolean CALL_STATE_RINGING = false;
	public static MediaPlayer mediaPlayer = new MediaPlayer();
	public static ArrayList<String> array_videoid,array_music,array_artist,array_imageurl, array_portal, array_playtime;
	public static ImageView img_image, btn_media_continue;
	public int video_num = 0;
	public static LinearLayout control_panel_layout;
	public static RelativeLayout ad_layout;
//	public YoutubeAsync youtubeAsync = null;
	public Mobile_YoutubeAsync mobile_youtubeAsync = null;
	public YoutubeAsync2 youtubeAsync2 = null;
	public Handler handler = new Handler();
	public static Toast toast;
	public static com.admixer.InterstitialAd interstialAd;
	public PandoraAsync_High pandoraAsync_high = null;
	public DaumAsync daumAsync = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.continue_mediaplayer);
		context = this;
		
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "ronrhzuy");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/8025216969");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/9501950166");
		
		addBannerView();
		
		layout_progress = (LinearLayout)findViewById(R.id.layout_progress);
		layout_control = (LinearLayout)findViewById(R.id.layout_control);
		control_panel_layout = (LinearLayout)findViewById(R.id.control_panel_layout);
		mediacontroller_progress = (SeekBar)findViewById(R.id.mediacontroller_progress);
		max_time = (TextView)findViewById(R.id.max_time);
		current_time = (TextView)findViewById(R.id.current_time);
		txt_description_title = (TextView)findViewById(R.id.txt_description_title);
		txt_title = (TextView)findViewById(R.id.txt_title);
		txt_title.setSelected(true);
		txt_pubDate = (TextView)findViewById(R.id.txt_pubDate);
		img_image = (ImageView)findViewById(R.id.img_image);
		btn_media_continue = (ImageView)findViewById(R.id.btn_media_continue);
		btn_media_continue.setOnClickListener(this);
		bt_duration_rew = (ImageButton)findViewById(R.id.bt_duration_rew);
		bt_pause = (ImageButton)findViewById(R.id.bt_pause);
		bt_duration_ffwd = (ImageButton)findViewById(R.id.bt_duration_ffwd);
		bt_rew = (ImageButton)findViewById(R.id.bt_rew);
		bt_ffwd = (ImageButton)findViewById(R.id.bt_ffwd);
		bt_duration_rew.setOnClickListener(this);
		bt_pause.setOnClickListener(this);
		bt_duration_ffwd.setOnClickListener(this);
		bt_rew.setOnClickListener(this);
		bt_ffwd.setOnClickListener(this);
		
		TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonymanager.listen(new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: 
//					if ((duration_check > 0) && (mediaPlayer != null) && (!mediaPlayer.isPlaying())){
//						if(duration_check > 0){
//							mediaPlayer.seekTo(duration_check);						
//							mediaPlayer.start();
//						}
//					}
				case TelephonyManager.CALL_STATE_OFFHOOK:
					if ((mediaPlayer != null) && (mediaPlayer.isPlaying())){
						mediaPlayer.pause();
						duration_check = mediaPlayer.getCurrentPosition();
					}
				case TelephonyManager.CALL_STATE_RINGING:
					if ((mediaPlayer != null) && (mediaPlayer.isPlaying())){
						mediaPlayer.pause();
						duration_check = mediaPlayer.getCurrentPosition();
					}
				default: break;
				} 
			}
		}, PhoneStateListener.LISTEN_CALL_STATE); 
		
		array_videoid = getIntent().getStringArrayListExtra("array_videoid");
		array_music = getIntent().getStringArrayListExtra("array_subject");
		array_imageurl = getIntent().getStringArrayListExtra("array_thumb");
		array_artist = getIntent().getStringArrayListExtra("array_artist");
		array_playtime = getIntent().getStringArrayListExtra("array_playtime");
		array_portal = getIntent().getStringArrayListExtra("array_portal");
		
		video_num = array_videoid.size()-1;
		video_sequence_start(video_num);
//		toast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);
//		
//		Toast.makeText(this, context.getString(R.string.txt_custom_videoplayer4), Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		invokeFragmentManagerNoteStateNotSaved();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void invokeFragmentManagerNoteStateNotSaved() {
	    /**
	     * For post-Honeycomb devices
	     */
	    if (Build.VERSION.SDK_INT < 11) {
	        return;
	    }
	    try {
	        Class cls = getClass();
	        do {
	            cls = cls.getSuperclass();
	        } while (!"Activity".equals(cls.getSimpleName()));
	        Field fragmentMgrField = cls.getDeclaredField("mFragments");
	        fragmentMgrField.setAccessible(true);

	        Object fragmentMgr = fragmentMgrField.get(this);
	        cls = fragmentMgr.getClass();

	        Method noteStateNotSavedMethod = cls.getDeclaredMethod("noteStateNotSaved", new Class[] {});
	        noteStateNotSavedMethod.invoke(fragmentMgr, new Object[] {});
	        Log.d("dsu", "Successful call for noteStateNotSaved!!!");
	    } catch (Exception ex) {
	        Log.e("dsu", "Exception on worka FM.noteStateNotSaved", ex);
	    }
	}
	
	public void addBannerView() {
    	AdInfo adInfo = new AdInfo("ronrhzuy");
    	adInfo.setTestMode(false);
        AdView adView = new AdView(this);
        adView.setAdInfo(adInfo, this);
        adView.setAdViewListener(this);
        ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        if(ad_layout != null){
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ad_layout.addView(adView, params);	
        }
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
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		navigator_handler.removeCallbacks(UpdateTimetask);
//		if(youtubeAsync != null){
//			youtubeAsync.cancel(true);
//		}
		if(youtubeAsync2 != null){
			youtubeAsync2.cancel(true);
		}
		if(mobile_youtubeAsync != null){
			mobile_youtubeAsync.cancel(true);
		}
		
		if(pandoraAsync_high != null){
			pandoraAsync_high.cancel(true);
		}
		if(daumAsync != null){
			daumAsync.cancel(true);
		}
		
//		MainActivity.setNotification_Cancel();
		finish();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		try{
			updateProgressBar();
	    	if(!mediaPlayer.isPlaying()){
	    		if(duration_check > 0){
	    			mediaPlayer.seekTo(duration_check);
	    			mediaPlayer.start();
	    		}
	    		return;
	    	}
	    }catch (IllegalStateException localIllegalStateException){
	    }
	    catch (IllegalArgumentException localIllegalArgumentException){
	    }
	    catch (NullPointerException localNullPointerException){
	    }
	}
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		try{
			MainActivity.setNotification_continue(context, array_music, array_videoid, array_playtime, array_imageurl, array_artist, video_num );	
		}catch(Exception e){
		}
	}
	
    public static void updateProgressBar(){
		navigator_handler.postDelayed(UpdateTimetask, 100);
	}
    
    public static Runnable UpdateTimetask = new Runnable() {
		@Override
		public void run() {
			if(mediaPlayer != null){
				if(mediaPlayer.isPlaying()){
					layout_progress.setVisibility(View.GONE);
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				}else{
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_play));
				}
				long totalDuration = mediaPlayer.getDuration();
				long currentDuration = mediaPlayer.getCurrentPosition();
				// Displaying Total Duration time
				max_time.setText(""+TimeUtil.milliSecondsToTimer(totalDuration));
				// Displaying time completed playing
				current_time.setText(""+TimeUtil.milliSecondsToTimer(currentDuration));
				// Updating progress bar
				int progress = (int)(TimeUtil.getProgressPercentage(currentDuration, totalDuration));
				mediacontroller_progress.setProgress(progress);
				navigator_handler.postDelayed(this, 100);	
			}
		}
	};
	
	public static String getFiletype(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}
	
	public void video_sequence_start(int video_num){
		try{
			if(array_portal.get(video_num) != null){
				if(array_portal.get(video_num).equals("pandora")){
					pandoraAsync_high = new PandoraAsync_High(array_videoid.get(video_num));
					pandoraAsync_high.execute();
				}else if(array_portal.get(video_num).equals("daum")){
					String file_type = getFiletype(array_videoid.get(video_num));
					if(file_type.equalsIgnoreCase("swf")){
						PlayMedia(array_videoid.get(video_num).replace(".swf", ".mp4"));
					}else{
						daumAsync = new DaumAsync(array_videoid.get(video_num));
						daumAsync.execute();
					}
				}
				else{
					mobile_youtubeAsync = new Mobile_YoutubeAsync(array_videoid.get(video_num));
					mobile_youtubeAsync.execute();
				}
			}
		}catch(Exception e){
		}
	}
	
	public class YoutubeAsync2 extends AsyncTask<String, Integer, String>{
		String videoId;
//		String Response = "fail";
		String url;
		public YoutubeAsync2(String videoId){
			this.videoId = videoId;
		}
		@Override
		protected String doInBackground(String... params) {
			int m = 0;
			try 
			{
				String url_youtube_watch_mobile = "http://m.youtube.com/watch?v=" + videoId+ "&xl=xl_blazer&ajax=1&tsp=1&tspv=v2&xl=xl_blazer";
				URL localURL = new URL(url_youtube_watch_mobile);
				HttpURLConnection localHttpURLConnection1 = (HttpURLConnection)localURL.openConnection();
				HttpURLConnection.setFollowRedirects(false);
				localHttpURLConnection1.setConnectTimeout(15000);
				localHttpURLConnection1.setReadTimeout(15000);
				localHttpURLConnection1.setRequestMethod("GET");
				localHttpURLConnection1.connect();
				InputStream localInputStream1 = localHttpURLConnection1.getInputStream();
				InputStreamReader localInputStreamReader1 = new InputStreamReader(localInputStream1);
				BufferedReader localBufferedReader1 = new BufferedReader(localInputStreamReader1, 8192);
				StringBuilder localStringBuilder = new StringBuilder();
				HttpURLConnection localHttpURLConnection2;
				JSONArray localJSONArray;
				while (true){
					String str1 = localBufferedReader1.readLine();
					if (str1 == null){
						localBufferedReader1.close();
						localHttpURLConnection1.disconnect();
						String str2 = localStringBuilder.toString().replace("\\/", "/").replace("\\u0026", "&");
						String str3 = str2.substring(str2.indexOf("fmt_stream_map"));
						int i = str3.indexOf("duration");
						int j = str3.indexOf("fmt_stream_map");
						if ((i != -1) && (j != -1)){
							String str5 = str3.substring(j, i);
							localJSONArray = new JSONArray(str5.substring(str5.indexOf("["), str5.indexOf("]")) + "]");
							m = 0;
							if (m < localJSONArray.length());  
							JSONObject localJSONObject = localJSONArray.getJSONObject(m);
							String str6 = localJSONObject.getString("url");
							String str7 = localJSONObject.getString("quality");
							if ((str6 != null) && (str7.equals("medium"))){
								url = str6;
							}else if ((str6 != null) && (str7.equals("small")) && (url == null)){
								url = str6;			
							}else{
							if (url == null)
								continue;
							localHttpURLConnection2 = (HttpURLConnection)new URL(url).openConnection();
							int k = localHttpURLConnection2.getResponseCode();
							if ((k == 200) || (k == 302))
								continue;
							url = null;
							break;
							}
						}
					}else{
						localStringBuilder.append(str1);
						continue;
					}
					m++;
				} 
			} catch (MalformedURLException e) {
			
			} catch (IOException e) {
			
			} catch (IllegalArgumentException e){
			
			} catch (Resources.NotFoundException e){
			
			} catch (StringIndexOutOfBoundsException e){
			
			} catch (JSONException e){
				
			} catch (RuntimeException e){
				
			} 
			return url;
		}
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout_progress.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(String Response) {
			super.onPostExecute(Response);
			try{
				Log.i("dsu", "Response3 : " + Response);
				if(Response != null){
					PlayMedia(Response);
				}else{
					if(youtubeAsync2 != null){
						youtubeAsync2.cancel(true);
					}
					mobile_youtubeAsync = new Mobile_YoutubeAsync(array_videoid.get(video_num));
					mobile_youtubeAsync.execute();
				}	
			}catch(NullPointerException e){
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
	}
	public class Mobile_YoutubeAsync extends AsyncTask<String, Integer, String>{
	 	   String videoId;
	 	   String url;
	 	   public Mobile_YoutubeAsync(String videoId){
	 		   this.videoId = videoId;
	 	   }
				@Override
				protected String doInBackground(String... params) {
			  	   	try{
						String url_youtube_video_info = "http://www.youtube.com/get_video_info?video_id=" + videoId;
				        URL localURL = new URL(url_youtube_video_info);
				        HttpURLConnection localHttpURLConnection1 = (HttpURLConnection)localURL.openConnection();
				        HttpURLConnection.setFollowRedirects(false);
				        localHttpURLConnection1.setConnectTimeout(15000);
				        localHttpURLConnection1.setReadTimeout(15000);
				        localHttpURLConnection1.setRequestMethod("GET");
				        localHttpURLConnection1.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3 (.NET CLR 3.5.30729) (Prevx 3.0.5)");
				        localHttpURLConnection1.connect();
				        InputStream localInputStream1 = localHttpURLConnection1.getInputStream();
				        InputStreamReader localInputStreamReader1 = new InputStreamReader(localInputStream1);
				        BufferedReader localBufferedReader1 = new BufferedReader(localInputStreamReader1, 8192);
				        StringBuilder localStringBuilder = new StringBuilder();
				        HttpURLConnection localHttpURLConnection2;
				        while (true)
				        {
				        	String str2 = localBufferedReader1.readLine();
				        	if (str2 == null)
					        {
				        		localBufferedReader1.close();
				        		localHttpURLConnection1.disconnect();
				        		String str3 = localStringBuilder.toString();
				        		String str4 = str3.substring(str3.indexOf("url_encoded_fmt_stream_map"));
				        		String str5 = URLDecoder.decode(str4.substring(0, str4.indexOf("&")).trim(), "UTF-8");
				        		if (str5 == null)
				                    break;
				                  String str6 = URLDecoder.decode(str5, "UTF-8").replace("url_encoded_fmt_stream_map=", "").replace("sig=", "signature=");
				                  if (!str6.startsWith("url="))
				                    break;
				                  url = getUrlType(str6, "mp4");
				                  if (url == null)
				                    break;
				                  url = removeComma(url).replace("&&", "&");
				                  URL localURL2 = new URL(url);
				                  localHttpURLConnection2 = (HttpURLConnection)localURL2.openConnection();
				                  int i = localHttpURLConnection2.getResponseCode();
				                  if ((i == 200) || (i == 302))
				                  break;
				                  url = null;
				                  break;
					        }
				        	localStringBuilder.append(str2);
				        } 
			  	   	}catch (MalformedURLException e) {
						
					} catch (IOException e) {
					
					} catch (IllegalArgumentException e){
					
					} catch (Resources.NotFoundException e){
					
					} catch (StringIndexOutOfBoundsException e){
					
					} catch (RuntimeException e){
						
					} 
			  	   	return url;
				}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layout_progress.setVisibility(View.VISIBLE);
		}
		@Override
		protected void onPostExecute(String Response) {
			super.onPostExecute(Response);
			try{
				Log.i("dsu", "Response2 : " + Response);
				if(Response != null){
					PlayMedia(Response);
					
				}else{
					if(mobile_youtubeAsync != null){
						mobile_youtubeAsync.cancel(true);
					}
					youtubeAsync2 = new YoutubeAsync2(array_videoid.get(video_num));
					youtubeAsync2.execute();
				}		
			}catch(NullPointerException e){
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}									
	}
	
	public class PandoraAsync_High extends AsyncTask<String, Integer, String>{
	 	   String videoId;
	 	   String xml_resolution = null;
	 	   String xml_url = null;
	 	   String high_url = null;
	 	   String low_url = null;
	 	   InputStream inputStream = null;
	 	   String prgid = "";
	 	   String ch_userid = "";
	 	   String parserName = null;
				public PandoraAsync_High(String videoId){
					this.videoId = videoId;
				}
				@Override
				protected String doInBackground(String... params) {
			        String sTag;
			        try {
			        	String search_vid = videoId;
				        String[] fields_tmp = search_vid.split("&");
				        String[] kv_tmp;
				        for (int i_tmp = 0; i_tmp < fields_tmp.length; ++i_tmp) {
				        	kv_tmp = fields_tmp[i_tmp].split("=");
				        	if (2 == kv_tmp.length) {
				        		if (kv_tmp[0].equals("prgid")) {
				        			prgid = kv_tmp[1];
				        		} else if (kv_tmp[0].equals("ch_userid")) {
				        			ch_userid = kv_tmp[1];
				        		}
				        	}
				        }
			        	
			        	String urlstring = "http://mobile.pandora.tv/mlib/video.PlayUrl.php";
		                URL url = new URL(urlstring);
		                HttpURLConnection httpURLcon = (HttpURLConnection)url.openConnection();
		                httpURLcon.setDefaultUseCaches(false);
		                httpURLcon.setDoInput(true);
		                httpURLcon.setDoOutput(true);
		                httpURLcon.setRequestMethod("POST");
		                httpURLcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		                StringBuffer sb = new StringBuffer();
		                sb.append("prgid").append("=").append(prgid).append("&");
		                sb.append("puserid").append("=").append(ch_userid).append("&");
		                sb.append("conType").append("=").append("wifi").append("&");
		                sb.append("region").append("=").append("kr").append("&");
		                sb.append("agent").append("=").append("etc");
		                OutputStream opstrm = httpURLcon.getOutputStream();
		                opstrm.write(sb.toString().getBytes());
		                opstrm.flush();
		                opstrm.close();
		                BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLcon.getInputStream()));
		                String line;
		                String xml = "";
		                while ((line = bf.readLine()) != null) {xml += line;}
		                bf.close();
		                inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		                XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		                XmlPullParser xpp = factory.newPullParser();
		                xpp.setInput(inputStream, "UTF-8"); //euc-kr로 언어를 설정합니다. utf-8로 하니깐 깨지더군요
		                int eventType = xpp.getEventType();
				           while (eventType != XmlPullParser.END_DOCUMENT) {
					        	if (eventType == XmlPullParser.START_DOCUMENT) {
					        	}else if (eventType == XmlPullParser.END_DOCUMENT) {
					        	}else if (eventType == XmlPullParser.START_TAG){
					        		sTag = xpp.getName();
					        		if(sTag.equals("root")){
					        			String total = xpp.getAttributeValue(null, "total") + "";
					        		}else if(sTag.equals("resolution")){
					        			String resolution = xpp.nextText() + "";
					            	}else if(sTag.equals("url")){
					        			high_url = xpp.nextText() + "";
					            	}
					        	} else if (eventType == XmlPullParser.END_TAG){
					            	sTag = xpp.getName();
					            	if(sTag.equals("root")){
					            	}
					            } else if (eventType == XmlPullParser.TEXT) {
					            }
					            eventType = xpp.next();
					        }
			        } catch (MalformedURLException e) {
//			        	return null;
			        } catch (ClientProtocolException e) {
//			        	return null;
			        } catch (IOException e) {
//			        	return null;
			        } catch (NotFoundException e) {
//			        	return null;
			        } catch (XmlPullParserException e) {
//			        	return null;
			        } catch (NullPointerException e) {
//			        	return null;
			        }finally {
			        	if (inputStream != null) {
			        		try {
			        			inputStream.close();
			        		} catch (IOException e) {
			        			e.printStackTrace();
			        		}
			        	}
			        }
			        return high_url;
				}
				@Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            layout_progress.setVisibility(View.VISIBLE);
		        }
				@Override
				protected void onPostExecute(String high_url) {
					super.onPostExecute(high_url);
					try{
						if(high_url != null){
							PlayMedia(high_url);
						}else{
							toast.setText(context.getString(R.string.txt_error));
							toast.show();
//							pandoraAsync_low = new PandoraAsync_Low(videoId);
//							pandoraAsync_low.execute();
						}
					}catch(NullPointerException e){
					}
				}
			}
	
	public class DaumAsync extends AsyncTask<String, Integer, String>{
	 	   String status;
	 	   String videoid;
	 	   String daum_url;
	 	   public DaumAsync(String videoid){
				this.videoid = videoid;
			}
				@Override
				protected String doInBackground(String... params) {
					String sTag;
			         try{
			        	 String str2 = "http://videofarm.daum.net/controller/api/open/v1_2/MovieLocation.apixml?vid=%1$s&profile=MAIN&play_loc=daum_kids";
			        	 Object[] arrayOfObject = new Object[1];
			        	 arrayOfObject[0] = videoid;
			        	 String str3 = String.format(str2, arrayOfObject);
			        	 URL localURL = new URL(str3);
			        	 XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
			        	 HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
			        	 HttpURLConnection.setFollowRedirects(false);
			        	 localHttpURLConnection.setConnectTimeout(15000);
			        	 localHttpURLConnection.setReadTimeout(15000);
			        	 localHttpURLConnection.setRequestMethod("GET");
			        	 localHttpURLConnection.connect();
			        	 xpp.setInput(localHttpURLConnection.getInputStream(), "UTF-8");
			        	 int eventType = xpp.getEventType();
			        	 while (eventType != XmlPullParser.END_DOCUMENT) {
			        		 if (eventType == XmlPullParser.START_DOCUMENT) {
			        		 }else if (eventType == XmlPullParser.END_DOCUMENT) {
			        		 }else if (eventType == XmlPullParser.START_TAG){
			        			 sTag = xpp.getName();
			        			 if(sTag.equals("url")){
			        				 daum_url = xpp.nextText();
			        			 }else if(sTag.equals("status")){
			        				 status = xpp.getAttributeValue(0);
			        			 }
			        		 }else if (eventType == XmlPullParser.END_TAG){
			        			 sTag = xpp.getName();
			        			 if(sTag.equals("videofarm")){
			        			 }
				            }else if (eventType == XmlPullParser.TEXT) {
				            }
				            eventType = xpp.next();
				        }
			         }
			         catch (SocketTimeoutException localSocketTimeoutException)
			         {
//			        	 return null;
			         }
			         catch (ClientProtocolException localClientProtocolException)
			         {
//			        	 return null;
			         }
			         catch (IOException localIOException)
			         {
//			        	 return null;
			         }
			         catch (Resources.NotFoundException localNotFoundException)
			         {
//			        	 return null;
			         }
			         catch (XmlPullParserException localXmlPullParserException)
			         {
//			        	 return null;
			         }
			         catch (NullPointerException NullPointerException)
			         {
//			        	 return null;
			         }
			         return status;
				}
				
				@Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		            layout_progress.setVisibility(View.VISIBLE);
		        }
				@Override
				protected void onPostExecute(String Response) {
					super.onPostExecute(Response);
					Log.i("dsu", "Daum : " + Response);
					try{
						if(Response != null){
							if(Response.equals("200")){
								PlayMedia(daum_url);
							}else{
								toast.setText(context.getString(R.string.txt_error));
								toast.show();
							}
						}else{
							toast.setText(context.getString(R.string.txt_error));
							toast.show();
						}
					}catch(NullPointerException e){
					}
				}
				@Override
				protected void onProgressUpdate(Integer... values) {
					super.onProgressUpdate(values);
				}
			}
	
//	public class YoutubeAsync extends AsyncTask<String, Void, String> {
//		String videoId;
//		String url;
//		public YoutubeAsync(String videoId) {
//			this.videoId = videoId;
//		}
//		@Override
//		protected void onPreExecute() {
//			layout_progress.setVisibility(View.VISIBLE);
//		}
//		@Override
//		protected String doInBackground(String... arg0) {
//			int begin, end;
//			String tmpstr = null;
//			try {
//				DefaultHttpClient client = new DefaultHttpClient();
//				HttpGet request = new HttpGet("http://www.youtube.com/watch?v=" + this.videoId);
//				request.setHeader("User-Agent", "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3");
//				HttpResponse response = client.execute(request);
//				InputStream stream = response.getEntity().getContent();
//				InputStreamReader reader=new InputStreamReader(stream);
//				StringBuffer buffer=new StringBuffer();
//				char[] buf=new char[262144];
//				int chars_read;
//				while ((chars_read = reader.read(buf, 0, 262144)) != -1) {
//					buffer.append(buf, 0, chars_read);
//				}
//				tmpstr=buffer.toString();
// 
//				begin  = tmpstr.indexOf("url_encoded_fmt_stream_map=");
//				end = tmpstr.indexOf("&", begin + 27);
//				if (end == -1) {
//					end = tmpstr.indexOf("\"", begin + 27);
//				}
//				tmpstr = URLDecoder.decode(tmpstr.substring(begin + 27, end), "utf-8");
// 
//			
// 
//			Vector<String> url_encoded_fmt_stream_map = new Vector<String>();
//			begin = 0;
//			end   = tmpstr.indexOf(",");
// 
//			while (end != -1) {
//				url_encoded_fmt_stream_map.add(tmpstr.substring(begin, end));
//				begin = end + 1;
//				end   = tmpstr.indexOf(",", begin);
//			}
// 
//			url_encoded_fmt_stream_map.add(tmpstr.substring(begin, tmpstr.length()));
//			Enumeration<String> url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements();
//			while (url_encoded_fmt_stream_map_enum.hasMoreElements()) {
//				tmpstr = (String)url_encoded_fmt_stream_map_enum.nextElement();
//				begin = tmpstr.indexOf("itag=");
//				if (begin != -1) {
//					end = tmpstr.indexOf("&", begin + 5);
// 
//					if (end == -1) {
//						end = tmpstr.length();
//					}
//					try{
//						int fmt = Integer.parseInt(tmpstr.substring(begin + 5, end));
//						if (fmt == 18 /*35*/) {
//							begin = tmpstr.indexOf("url=");
//							if (begin != -1) {
//								end = tmpstr.indexOf("&", begin + 4);
//								if (end == -1) {
//									end = tmpstr.length();
//								}
//									url = URLDecoder.decode(tmpstr.substring(begin + 4, end), "utf-8");
//									break;
//							}
//						}
//						Log.i("dsu", "fmt = " + fmt);
//					}catch(NumberFormatException e){
//					}catch (UnsupportedEncodingException e) {
//					}
//				}
//			}
//			} catch (MalformedURLException e) {
//				
//			} catch (IOException e) {
//			
//			} catch (IllegalArgumentException e){
//			
//			} catch (Resources.NotFoundException e){
//			
//			} catch (StringIndexOutOfBoundsException e){
//			
//			} catch (RuntimeException e){
//				
//			} 
//			return url;
//		}
// 
//		@Override
//		protected void onPostExecute(String Response) {
//			Log.i("dsu", "Response1 : " + Response);
//			try{
//				if(Response != null){
//					PlayMedia(Response);
//				}else{
//					if(youtubeAsync != null){
//						youtubeAsync.cancel(true);
//					}
//					video_sequence_start(video_num);
//					toast.setText(context.getString(R.string.txt_error));
//					toast.show();
//				}		
//			}catch(NullPointerException e){
//			}
//		}
//	}
	
	public void PlayMedia(String target_url){
		try{
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediacontroller_progress.setOnSeekBarChangeListener(this);
			
			mediaPlayer.reset();
            mediaPlayer.setDataSource(target_url);
            mediaPlayer.prepare();
            
            mediacontroller_progress.setProgress(0);
			mediacontroller_progress.setMax(100);
			mediaPlayer.seekTo(0);
			updateProgressBar();
		}catch (Exception e) {
		}
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!mediaPlayer.isPlaying()){
					mediaPlayer.start();
				}
			}
		},1000);
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int arg1, int arg2) {
//		Toast.makeText(context, context.getString(R.string.txt_error2), Toast.LENGTH_LONG).show();
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekbar) {
		navigator_handler.removeCallbacks(UpdateTimetask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(mediaPlayer != null){
			int totalDuration = mediaPlayer.getDuration();	
			int currentPosition = TimeUtil.progressToTimer(seekBar.getProgress(), totalDuration);
			// forward or backward to certain seconds
			mediaPlayer.seekTo(currentPosition);
			if (mediaPlayer.isPlaying()){
				mediaPlayer.start();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				updateProgressBar();
		    }else{
		    	mediaPlayer.start();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				updateProgressBar();
		    }
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		try{
			txt_description_title.setText(array_music.get(video_num));
			txt_title.setText(array_artist.get(video_num));
//			txt_pubDate.setText(TimeUtil.formatSecondsAsDuration(Long.parseLong(array_playtime.get(video_num))));
			txt_pubDate.setText(array_playtime.get(video_num));
			ImageLoader imgLoader = new ImageLoader(getApplicationContext());
			imgLoader.DisplayImage(array_imageurl.get(video_num), R.drawable.no_image, img_image);
			if(mp != null){
				if(mp.isPlaying()){
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				}else{
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_play));
				}
				mp.start();
				MainActivity.setNotification_continue(context, array_music, array_videoid, array_playtime, array_imageurl, array_artist, video_num );
				layout_progress.setVisibility(View.GONE);
			}
		}catch(Exception e){
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(mp != null){
			if(video_num > 0){
				video_num = video_num - 1;
				video_sequence_start(video_num);
			}else{
				video_num = array_videoid.size()-1;
				video_sequence_start(video_num);
			}	
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int buffering) {
		mediacontroller_progress.setSecondaryProgress(buffering);
	}
	@Override
	public void onClick(View view) {
		if(view == bt_pause){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_play));
			}else{
				mediaPlayer.start();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
			}
		}else if(view == bt_duration_rew){
			int currentPosition = mediaPlayer.getCurrentPosition();
			// check if seekBackward time is greater than 0 sec
			if(currentPosition - seekBackwardTime >= 0){
				// forward song
				mediaPlayer.seekTo(currentPosition - seekBackwardTime); 
			}else{
				// backward to starting position
				mediaPlayer.seekTo(0);
			}
		}else if(view == bt_duration_ffwd){
			int currentPosition = mediaPlayer.getCurrentPosition();
			if(currentPosition + seekForwardtime <= mediaPlayer.getDuration()){
				// forward song
				mediaPlayer.seekTo(currentPosition + seekForwardtime);
			}else{
				// forward to end position
				mediaPlayer.seekTo(mediaPlayer.getDuration());
			}
		}else if(view == bt_rew){
			if(video_num < array_videoid.size() -1){
				mediaPlayer.pause();
				navigator_handler.removeCallbacks(UpdateTimetask);
				video_num = video_num + 1;
				video_sequence_start(video_num);
				Log.i("dsu", "video_num : " + video_num);
			}else{
				Toast.makeText(context, context.getString(R.string.txt_custom_mediaplayer1), Toast.LENGTH_SHORT).show();
			}
		}else if(view == bt_ffwd){
			if(video_num > 0){
				mediaPlayer.pause();
				navigator_handler.removeCallbacks(UpdateTimetask);
				video_num = video_num - 1;
				video_sequence_start(video_num);
			}else{
				mediaPlayer.pause();
				navigator_handler.removeCallbacks(UpdateTimetask);
				video_num = array_videoid.size()-1;
				video_sequence_start(video_num);
			}
		}else if(view == btn_media_continue){
			Toast.makeText(context, context.getString(R.string.media_continue_ment), Toast.LENGTH_LONG).show();
			addInterstitialView();
		}
	}
	
	public String getUrlType(String s, String s1){
    	String as[] = s.split("url=");
    	String s2 = null;
    	int i = 0;
        do
        {
            if(i >= as.length)
                return s2;
            if(as[i].contains("medium") && as[i].contains(s1))
                s2 = removeItag2(removeComma(removeItag(removeCodecs(as[i]))));
//            Log.i("dsu", "getUrlType : " + s2 );
            i++;
        } while(true);
    }
    
    public String removeItag2(String s)
    {
        if(getStringPatternCount(s, "itag=") > 1)
        {
            int i = s.indexOf("itag=");
            int j = s.indexOf("&", i + 1);
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
//        Log.i("dsu", "removeItag2 : " + s);
        return s;
    }
	
    public String removeCodecs(String s){
        if(s.indexOf("codecs") > -1)
        {
            int i = s.indexOf(";");
            int j = s.indexOf("&", i);
            if(j == -1)
                j = -1 + s.length();
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            if(s2.length() == 1)
                s = s1;
            else
                s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
        return s;
    }
    public String removeItag(String s)
    {
        if(getStringPatternCount(s, "&itag=") > 1)
        {
            int i = s.indexOf("&itag=");
            int j = s.indexOf("&", i + 1);
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
        return s;
    }
    public int getStringPatternCount(String s, String s1)
    {
        int i = 0;
        Matcher matcher = Pattern.compile(s1).matcher(s);
        int j = 0;
        do
        {
            if(!matcher.find(i))
                return j;
            j++;
            i = matcher.end();
        } while(true);
    }
    public String removeComma(String s)
    {
        if(s != null && s.endsWith(","))
            s = s.substring(0, -1 + s.length());
//        Log.i("dsu", "removeComma : " + s);
        return s;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(ContinueMediaPlayer.mediaPlayer.isPlaying()){
				ContinueMediaPlayer.mediaPlayer.pause();
			}
			Toast.makeText(context, context.getString(R.string.txt_custom_videoplayer3), Toast.LENGTH_SHORT).show();
			addInterstitialView();
				 handler.postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 if(ContinueMediaPlayer.mediaPlayer.isPlaying()){
							 ContinueMediaPlayer.mediaPlayer.stop();
						 }
						 onDestroy();
						 MainActivity.setNotification_Cancel();
					 }
				 },1500);
				return false;
//			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onInterstitialAdClosed(com.admixer.InterstitialAd arg0) {
		interstialAd = null;
		if(ContinueMediaPlayer.mediaPlayer.isPlaying()){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			com.admixer.InterstitialAd arg2) {
		interstialAd = null;
		if(ContinueMediaPlayer.mediaPlayer.isPlaying()){
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
	}

	@Override
	public void onInterstitialAdReceived(String arg0,
			com.admixer.InterstitialAd arg1) {
		interstialAd = null;
	}

	@Override
	public void onInterstitialAdShown(String arg0,
			com.admixer.InterstitialAd arg1) {
	}

	@Override
	public void onLeftClicked(String arg0, com.admixer.InterstitialAd arg1) {
	}

	@Override
	public void onRightClicked(String arg0, com.admixer.InterstitialAd arg1) {
	}

	@Override
	public void onClickedAd(String arg0, AdView arg1) {
	}

	@Override
	public void onFailedToReceiveAd(int arg0, String arg1, AdView arg2) {
	}

	@Override
	public void onReceivedAd(String arg0, AdView arg1) {
	}
}
