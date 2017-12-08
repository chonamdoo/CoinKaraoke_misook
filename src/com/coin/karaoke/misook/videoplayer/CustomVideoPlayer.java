package com.coin.karaoke.misook.videoplayer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.admixer.AdAdapter;
import com.admixer.AdInfo;
import com.admixer.AdMixerManager;
import com.admixer.InterstitialAdListener;
import com.coin.karaoke.misook.R;
import com.coin.karaoke.misook.activity.MainActivity;
import com.coin.karaoke.misook.util.TimeUtil;

public class CustomVideoPlayer extends Activity implements OnCompletionListener, OnPreparedListener,android.widget.SeekBar.OnSeekBarChangeListener, OnErrorListener, OnClickListener, OnTouchListener, InterstitialAdListener{
	ArrayList<String> array_videoid, array_subject, array_portal;
	public PandoraAsync_High pandoraAsync_high = null;
//	public PandoraAsync_Low pandoraAsync_low = null;
//	public YoutubeAsync youtubeAsync = null;
	public Mobile_YoutubeAsync mobile_youtubeAsync = null;
	public YoutubeAsync2 youtubeAsync2 = null;
	public int video_num = 0;
	public static CustomVideoView videoView;
	public static LinearLayout layout_progress;
	public static Context context;
	public static SeekBar mediacontroller_progress;
	public static Handler navigator_handler = new Handler();
	public static TextView max_time, current_time, txt_video_title;
	public static ImageButton bt_rew, bt_duration_rew, bt_pause, bt_duration_ffwd, bt_ffwd;
	public int seekBackwardTime = 5000; // 5000 milliseconds
	public int seekForwardtime = 5000; // 5000 milliseconds
	public static ImageView btn_lock, btn_screen_orientation;
	public boolean isLock = false;
	public static LinearLayout layout_control;
	public static RelativeLayout layout_video_View;
	public Handler mHandler = new Handler();
	public int duration_check = 0;
	public static boolean CALL_STATE_OFFHOOK = false;
	public static boolean CALL_STATE_RINGING = false;
	SharedPreferences settings,pref;
	Editor edit;
	public static Toast toast;
	public static RelativeLayout ad_layout;
	public static com.admixer.InterstitialAd interstialAd;
	public Handler handler = new Handler();
	public DaumAsync daumAsync = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_videoplayer);
		context = this;
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMIXER, "ronrhzuy");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB, "ca-app-pub-4637651494513698/8025216969");
		AdMixerManager.getInstance().setAdapterDefaultAppCode(AdAdapter.ADAPTER_ADMOB_FULL, "ca-app-pub-4637651494513698/9501950166");
		layout_video_View = (RelativeLayout)findViewById(R.id.layout_video_View);
		layout_video_View.setOnTouchListener(this);
		videoView = (CustomVideoView)findViewById(R.id.video_View);
		layout_progress = (LinearLayout)findViewById(R.id.layout_progress);
		layout_control = (LinearLayout)findViewById(R.id.layout_control);
		mediacontroller_progress = (SeekBar)findViewById(R.id.mediacontroller_progress);
		max_time = (TextView)findViewById(R.id.max_time);
		current_time = (TextView)findViewById(R.id.current_time);
		txt_video_title = (TextView)findViewById(R.id.txt_video_title);
		bt_rew = (ImageButton)findViewById(R.id.bt_rew);
		bt_duration_rew = (ImageButton)findViewById(R.id.bt_duration_rew);
		bt_pause = (ImageButton)findViewById(R.id.bt_pause);
		bt_duration_ffwd = (ImageButton)findViewById(R.id.bt_duration_ffwd);
		bt_ffwd = (ImageButton)findViewById(R.id.bt_ffwd);
		btn_lock = (ImageView)findViewById(R.id.btn_lock);
		btn_screen_orientation = (ImageView)findViewById(R.id.btn_screen_orientation);
		bt_rew.setOnClickListener(this);
		bt_duration_rew.setOnClickListener(this);
		bt_pause.setOnClickListener(this);
		bt_duration_ffwd.setOnClickListener(this);
		bt_ffwd.setOnClickListener(this);
		btn_lock.setOnClickListener(this);
		btn_screen_orientation.setOnClickListener(this);
		array_videoid = getIntent().getStringArrayListExtra("array_videoid");
		array_subject = getIntent().getStringArrayListExtra("array_subject");
		array_portal = getIntent().getStringArrayListExtra("array_portal");
		video_num = array_subject.size()-1;
		video_sequence_start(video_num);
		toast = Toast.makeText(this, "null", Toast.LENGTH_SHORT);
//		TelephonyManager telephonymanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		telephonymanager.listen(new PhoneStateListener() {
//			public void onCallStateChanged(int state, String incomingNumber) {
//				switch (state) {
//				case TelephonyManager.CALL_STATE_IDLE: 
//					if ((duration_check > 0) && (videoView != null) && (!videoView.isPlaying())){
//						if(duration_check > 0){
//							videoView.seekTo(duration_check);						
//							videoView.start();
//						}
//					}
//				case TelephonyManager.CALL_STATE_OFFHOOK:
//					if ((videoView != null) && (videoView.isPlaying())){
//						videoView.pause();
//						duration_check = videoView.getCurrentPosition();
//					}
//				case TelephonyManager.CALL_STATE_RINGING:
//					if ((videoView != null) && (videoView.isPlaying())){
//						videoView.pause();
//						duration_check = videoView.getCurrentPosition();
//					}
//				default: break;
//				} 
//			}
//		}, PhoneStateListener.LISTEN_CALL_STATE); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if ((videoView != null) && (videoView.isPlaying())){
			videoView.pause();
			duration_check = videoView.getCurrentPosition();
	    }
	}
	@Override
	protected void onStop() {
		super.onStop();
	    try{
	    	videoView = (CustomVideoView)findViewById(R.id.video_View);
			videoView.setKeepScreenOn(true);
	    	if ((videoView != null) && (videoView.isPlaying())){
				videoView.pause();
				duration_check = videoView.getCurrentPosition();
		    }
	    	return;
	    }catch (IllegalStateException localIllegalStateException){
	    }
	    catch (IllegalArgumentException localIllegalArgumentException){
	    }
	    catch (NullPointerException localNullPointerException){
	    }
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		navigator_handler.removeCallbacks(UpdateTimetask);
		videoView = null;
		if(mobile_youtubeAsync != null){
			mobile_youtubeAsync.cancel(true);
		}
		if(youtubeAsync2 != null){
			youtubeAsync2.cancel(true);
		}
		if(pandoraAsync_high != null){
			pandoraAsync_high.cancel(true);
		}
		if(daumAsync != null){
			daumAsync.cancel(true);
		}
		finish();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		try{
			videoView = (CustomVideoView)findViewById(R.id.video_View);
			videoView.setKeepScreenOn(true);
			updateProgressBar();
	    	if(!videoView.isPlaying()){
	    		if(duration_check > 0){
	    			videoView.seekTo(duration_check);
	    			videoView.start();
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
	}
	
	public static String getFiletype(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}
	
	public void video_sequence_start(int video_num){
		if(array_portal.get(video_num).equals("pandora")){
			pandoraAsync_high = new PandoraAsync_High(array_videoid.get(video_num));
			pandoraAsync_high.execute();
		}else if(array_portal.get(video_num).equals("daum")){
			String file_type = getFiletype(array_videoid.get(video_num));
			if(file_type.equalsIgnoreCase("swf")){
				PlayVideo(array_videoid.get(video_num).replace(".swf", ".mp4"));
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
							PlayVideo(high_url);
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
								PlayVideo(daum_url);
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

	public String CheckUrl(String checkUrl)throws IOException{
        HttpURLConnection httpurlconnection;
        httpurlconnection = (HttpURLConnection)(new URL(checkUrl)).openConnection();
        httpurlconnection.setConnectTimeout(15000);
        httpurlconnection.setReadTimeout(15000);
        httpurlconnection.setUseCaches(false);
        httpurlconnection.setInstanceFollowRedirects(false);
        if(httpurlconnection.getResponseCode() != 301 && httpurlconnection.getResponseCode() != 302 && httpurlconnection.getResponseCode() != 303){
        	checkUrl = null;
        }else{
        	checkUrl = httpurlconnection.getHeaderField("Location");
        }
		return checkUrl;
    }
	public class YoutubeAsync extends AsyncTask<String, Void, String> {
		String videoId;
		String url;
		public YoutubeAsync(String videoId) {
			this.videoId = videoId;
		}
		@Override
		protected void onPreExecute() {
			layout_progress.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... arg0) {
			int begin, end;
			String tmpstr = null;
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet("http://www.youtube.com/watch?v=" + this.videoId);
				request.setHeader("User-Agent", "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko ) Version/5.1 Mobile/9B176 Safari/7534.48.3");
				HttpResponse response = client.execute(request);
				InputStream stream = response.getEntity().getContent();
				InputStreamReader reader=new InputStreamReader(stream);
				StringBuffer buffer=new StringBuffer();
				char[] buf=new char[262144];
				int chars_read;
				while ((chars_read = reader.read(buf, 0, 262144)) != -1) {
					buffer.append(buf, 0, chars_read);
				}
				tmpstr=buffer.toString();
 
				begin  = tmpstr.indexOf("url_encoded_fmt_stream_map=");
				end = tmpstr.indexOf("&", begin + 27);
				if (end == -1) {
					end = tmpstr.indexOf("\"", begin + 27);
				}
				tmpstr = URLDecoder.decode(tmpstr.substring(begin + 27, end), "utf-8");
 
			
 
			Vector<String> url_encoded_fmt_stream_map = new Vector<String>();
			begin = 0;
			end   = tmpstr.indexOf(",");
 
			while (end != -1) {
				url_encoded_fmt_stream_map.add(tmpstr.substring(begin, end));
				begin = end + 1;
				end   = tmpstr.indexOf(",", begin);
			}
 
			url_encoded_fmt_stream_map.add(tmpstr.substring(begin, tmpstr.length()));
			Enumeration<String> url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements();
			while (url_encoded_fmt_stream_map_enum.hasMoreElements()) {
				tmpstr = (String)url_encoded_fmt_stream_map_enum.nextElement();
				begin = tmpstr.indexOf("itag=");
				if (begin != -1) {
					end = tmpstr.indexOf("&", begin + 5);
 
					if (end == -1) {
						end = tmpstr.length();
					}
					try{
						int fmt = Integer.parseInt(tmpstr.substring(begin + 5, end));
						if (fmt == 18 /*35*/) {
							begin = tmpstr.indexOf("url=");
							if (begin != -1) {
								end = tmpstr.indexOf("&", begin + 4);
								if (end == -1) {
									end = tmpstr.length();
								}
									url = URLDecoder.decode(tmpstr.substring(begin + 4, end), "utf-8");
									break;
							}
						}
					}catch(NumberFormatException e){
					}catch (UnsupportedEncodingException e) {
					}
				}
			}
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
			
			} catch (IllegalArgumentException e){
			
			} catch (Resources.NotFoundException e){
			
			} catch (StringIndexOutOfBoundsException e){
			
			} catch (RuntimeException e){
				
			} 
			return url;
		}
 
		@Override
		protected void onPostExecute(String Response) {
			try{
				if(Response != null){
					PlayVideo(Response);
				}else{
					toast.setText(context.getString(R.string.txt_error));
					toast.show();
//					mobile_youtubeAsync = new Mobile_YoutubeAsync(videoId);
//					mobile_youtubeAsync.execute();
				}		
			}catch(NullPointerException e){
			}
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
				if(Response != null){
					PlayVideo(Response);
				}else{
					if(mobile_youtubeAsync != null){
						mobile_youtubeAsync.cancel(true);
					}
					youtubeAsync2 = new YoutubeAsync2(videoId);
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
				if(Response != null){
					PlayVideo(Response);
				}else{
					if(youtubeAsync2 != null){
						youtubeAsync2.cancel(true);
					}
					mobile_youtubeAsync = new Mobile_YoutubeAsync(videoId);
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
        return s;
    }
    
    public static void updateProgressBar(){
		navigator_handler.postDelayed(UpdateTimetask, 100);
	}
    
    public static Runnable UpdateTimetask = new Runnable() {
		@Override
		public void run() {
			if(videoView != null){
				if(videoView.isPlaying()){
					layout_progress.setVisibility(View.GONE);
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				}else{
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_play));
				}
				long totalDuration = videoView.getDuration();
				long currentDuration = videoView.getCurrentPosition();
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
	
	public void PlayVideo(String target_url){
		try{
			if(pandoraAsync_high != null){
				pandoraAsync_high.cancel(true);
			}
			if(daumAsync != null){
				daumAsync.cancel(true);
			}
			if(mobile_youtubeAsync != null){
				mobile_youtubeAsync.cancel(true);
			}
			if(youtubeAsync2 != null){
				youtubeAsync2.cancel(true);
			}
			
			videoView.setOnCompletionListener(this);
			videoView.setOnErrorListener(this);
			videoView.setKeepScreenOn(true);
			videoView.setOnPreparedListener(this);
			mediacontroller_progress.setOnSeekBarChangeListener(this);
			CustomMediaController mediaController = new CustomMediaController(context);
			mediaController.setAnchorView(videoView);
			Uri uri = Uri.parse(target_url);           
			videoView.setMediaController(mediaController);
			videoView.setVideoURI(uri);
			videoView.requestLayout();
			videoView.requestFocus();    
			mediacontroller_progress.setProgress(0);
			mediacontroller_progress.setSecondaryProgress(0);
			mediacontroller_progress.setMax(100);
			updateProgressBar();
		}
		catch(Exception e){
		} 
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int arg1, int arg2) {
//		Toast.makeText(context, context.getString(R.string.txt_error2), Toast.LENGTH_LONG).show();
		return true;
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int arg1, boolean arg2) {
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		navigator_handler.removeCallbacks(UpdateTimetask);
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(videoView != null){
			int totalDuration = videoView.getDuration();	
			int currentPosition = TimeUtil.progressToTimer(seekBar.getProgress(), totalDuration);
			// forward or backward to certain seconds
			videoView.seekTo(currentPosition);
			if (videoView.isPlaying()){
				videoView.start();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				updateProgressBar();
		    }else{
		    	videoView.start();
				bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				updateProgressBar();
		    }
		}
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		toggleControl();
		txt_video_title.setText(array_subject.get(video_num));
		settings = getSharedPreferences(context.getString(R.string.txt_main_activity36), MODE_PRIVATE);
		edit = settings.edit();
		edit.putString("array_subject", array_subject.get(video_num));
		edit.putString("array_videoid", array_videoid.get(video_num));
		edit.commit();
		if(!mp.isPlaying()){
    		if(duration_check > 0){
    			mp.seekTo(duration_check);
    			mp.start();
    			duration_check = 0;
    		}else{
    			mp.start();
    		}
    	}
		mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				mediacontroller_progress.setSecondaryProgress(percent);
			}
		});
		
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		navigator_handler.removeCallbacks(UpdateTimetask);
		if(video_num > 0){
			video_num = video_num - 1;
			video_sequence_start(video_num);
		}else{
			video_num = array_portal.size()-1;
			video_sequence_start(video_num);
		}
	}
	
	@Override
	public void onClick(View view) {
		if(view == bt_ffwd){
			try{
				if(video_num > 0){
					videoView.pause();
					navigator_handler.removeCallbacks(UpdateTimetask);
					video_num = video_num - 1;
					video_sequence_start(video_num);
				}else{
					videoView.pause();
					navigator_handler.removeCallbacks(UpdateTimetask);
					video_num = array_portal.size()-1;
					video_sequence_start(video_num);
				}
			}catch(Exception e){
			}
		}else if(view == bt_rew){
			try{
				if(video_num < array_subject.size() -1){
					videoView.pause();
					navigator_handler.removeCallbacks(UpdateTimetask);
					video_num = video_num + 1;
					video_sequence_start(video_num);
				}else{
					Toast.makeText(context, context.getString(R.string.txt_custom_videoplayer1), Toast.LENGTH_SHORT).show();
				}
			}catch(Exception e){
			}
		}else if(view == bt_pause){
			try{
				if(videoView.isPlaying()){
					videoView.pause();
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_pause));
				}else{
					videoView.start();
					bt_pause.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_media_play));
				}
			}catch(Exception e){
			}
		}else if(view == bt_duration_rew){
			try{
				int currentPosition = videoView.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					videoView.seekTo(currentPosition - seekBackwardTime); 
				}else{
					// backward to starting position
					videoView.seekTo(0);
				}
			}catch(Exception e){
			}
		}else if(view == bt_duration_ffwd){
			try{
				int currentPosition = videoView.getCurrentPosition();
				if(currentPosition + seekForwardtime <= videoView.getDuration()){
					// forward song
					videoView.seekTo(currentPosition + seekForwardtime);
				}else{
					// forward to end position
					videoView.seekTo(videoView.getDuration());
				}
			}catch(Exception e){
			}
		}else if(view == btn_lock){
			try{
				if(btn_lock.isSelected()){
					btn_lock.setSelected(false);
					isLock = false;
					btn_lock.setImageResource(R.drawable.ic_media_lock_off);
					btn_lock.setVisibility(View.VISIBLE);
					layout_control.setVisibility(View.VISIBLE);
					txt_video_title.setVisibility(View.VISIBLE);
				}else{
					btn_lock.setSelected(true);
					isLock = true;
					btn_lock.setImageResource(R.drawable.ic_media_lock_on);
					btn_lock.setVisibility(View.VISIBLE);
					layout_control.setVisibility(View.INVISIBLE);
					txt_video_title.setVisibility(View.INVISIBLE);
				}
			}catch(Exception e){
			}
		}else if(view == btn_screen_orientation){
			try{
				if(btn_screen_orientation.isSelected()){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					btn_screen_orientation.setSelected(false);
					btn_screen_orientation.setImageResource(R.drawable.ic_screen_orientation_off);
					btn_screen_orientation.setVisibility(View.VISIBLE);
					if(isLock == true){
						layout_control.setVisibility(View.INVISIBLE);
						txt_video_title.setVisibility(View.INVISIBLE);
					}else{
						layout_control.setVisibility(View.VISIBLE);
						txt_video_title.setVisibility(View.VISIBLE);
					}
					
				}else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
					btn_screen_orientation.setSelected(true);
					btn_screen_orientation.setImageResource(R.drawable.ic_screen_orientation_on);
					btn_screen_orientation.setVisibility(View.VISIBLE);
					if(isLock == true){
						layout_control.setVisibility(View.INVISIBLE);
						txt_video_title.setVisibility(View.INVISIBLE);
					}else{
						layout_control.setVisibility(View.VISIBLE);
						txt_video_title.setVisibility(View.VISIBLE);
					}
				}
			}catch(Exception e){
			}
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(view == layout_video_View){
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				toggleControl();
			}
		}
		return false;
	}
	
	
	public void toggleControl(){
		if(isLock == true){
			if (btn_lock.isShown()){
		    	btn_lock.setVisibility(View.INVISIBLE);
		    	btn_screen_orientation.setVisibility(View.INVISIBLE);
		    	layout_control.setVisibility(View.INVISIBLE);
		    	txt_video_title.setVisibility(View.INVISIBLE);
		    }else{
		    	 btn_lock.setVisibility(View.VISIBLE);
		    	 btn_screen_orientation.setVisibility(View.VISIBLE);
		    	 layout_control.setVisibility(View.INVISIBLE);
		    	 txt_video_title.setVisibility(View.INVISIBLE);
		    	 mHandler.removeCallbacks(hideLock);
		    	 mHandler.postDelayed(hideLock, 8000L);
		    }
		}else{
			if (btn_lock.isShown()){
		    	btn_lock.setVisibility(View.INVISIBLE);
		    	btn_screen_orientation.setVisibility(View.INVISIBLE);
		    	layout_control.setVisibility(View.INVISIBLE);
		    	txt_video_title.setVisibility(View.INVISIBLE);
		    }else{
		    	 btn_lock.setVisibility(View.VISIBLE);
		    	 btn_screen_orientation.setVisibility(View.VISIBLE);
		    	 layout_control.setVisibility(View.VISIBLE);
		    	 txt_video_title.setVisibility(View.VISIBLE);
		    	 mHandler.removeCallbacks(hideControl);
		    	 mHandler.postDelayed(hideControl, 8000L);
		    }
		}
	}
	public Runnable hideLock = new Runnable(){
	    public void run(){
	    	btn_lock.setVisibility(View.INVISIBLE);
	    	btn_screen_orientation.setVisibility(View.INVISIBLE);
	    	layout_control.setVisibility(View.INVISIBLE);
	    	txt_video_title.setVisibility(View.INVISIBLE);
	    }
	};
	public Runnable hideControl = new Runnable(){
	    public void run(){
	    	btn_lock.setVisibility(View.INVISIBLE);
	    	btn_screen_orientation.setVisibility(View.INVISIBLE);
	    	layout_control.setVisibility(View.INVISIBLE);
	    	txt_video_title.setVisibility(View.INVISIBLE);
	    }
	};
	
	public void addInterstitialView() {
    	if(interstialAd == null) {
        	AdInfo adInfo = new AdInfo("uvvf1kk3");
//        	adInfo.setTestMode(false);
        	interstialAd = new com.admixer.InterstitialAd(this);
        	interstialAd.setAdInfo(adInfo, this);
        	interstialAd.setInterstitialAdListener(this);
        	interstialAd.startInterstitial();
    	}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(isLock == true){
				Toast.makeText(context, context.getString(R.string.txt_custom_videoplayer2), Toast.LENGTH_SHORT).show();
				return false;
			}else{
				Toast.makeText(context, context.getString(R.string.txt_custom_videoplayer3), Toast.LENGTH_SHORT).show();
				addInterstitialView();
				 handler.postDelayed(new Runnable() {
					 @Override
					 public void run() {
						 onDestroy();
					 }
				 },2500);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onInterstitialAdClosed(com.admixer.InterstitialAd arg0) {
		interstialAd = null;
		onDestroy();
	}

	@Override
	public void onInterstitialAdFailedToReceive(int arg0, String arg1,
			com.admixer.InterstitialAd arg2) {
		interstialAd = null;
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
}
