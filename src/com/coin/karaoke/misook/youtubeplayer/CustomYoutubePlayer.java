package com.coin.karaoke.misook.youtubeplayer;


import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.coin.karaoke.misook.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayerView;


public class CustomYoutubePlayer extends YouTubeFailureRecoveryActivity implements OnFullscreenListener{  
	ArrayList<String> array_url;
	ArrayList<String> plus_array_url;
	public MyPlaylistEventListener playlistEventListener;
	public MyPlayerStateChangeListener playerStateChangeListener;
	public MyPlaybackEventListener playbackEventListener;
	public static YouTubePlayer player;
	public YouTubePlayerView youtube_view;
	public Context context;
	public Handler handler = new Handler();
	@Override    
	public void onCreate(Bundle savedInstanceState){ 
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.custom_youtubeplayer);
		context = this;
		youtube_view = (YouTubePlayerView) findViewById(R.id.youtube_view); 
    	array_url = getIntent().getStringArrayListExtra("array_videoid");
    	for(int i=0; i < array_url.size(); i++){
    		
    	}
		youtube_view.initialize(DeveloperKey.DEVELOPER_KEY, this);
		playlistEventListener = new MyPlaylistEventListener();
	    playerStateChangeListener = new MyPlayerStateChangeListener();
	    playbackEventListener = new MyPlaybackEventListener();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return youtube_view;
	}
	
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
		CustomYoutubePlayer.player = player;
	    player.setPlaylistEventListener(playlistEventListener);
	    player.setPlayerStateChangeListener(playerStateChangeListener);
	    player.setPlaybackEventListener(playbackEventListener);
	    if (!wasRestored) {
	      playVideoAtSelection();
	    }
	  }

	  public void playVideoAtSelection() {
		  player.cueVideos(array_url);
		  player.setPlayerStyle(PlayerStyle.DEFAULT);
		  player.addFullscreenControlFlag(8);
		  player.setOnFullscreenListener(this);
		  player.setFullscreen(false);
	  }
	
	
	public class MyPlaylistEventListener implements PlaylistEventListener {
		@Override
	    public void onNext() {
	    }

	    @Override
	    public void onPrevious() {
	    }

	    @Override
	    public void onPlaylistEnded() {
	      Log.i("dsu", "PLAYLIST ENDED");
	    }
	  }
	
	private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
	    String playerState = "UNINITIALIZED";

	    @Override
	    public void onLoading() {
	      playerState = "LOADING";
	      Log.i("dsu", "onLoading : " + playerState);
	    }

	    @Override
	    public void onLoaded(String videoId) {
	      playerState = String.format("LOADED %s", videoId);
	      Log.i("dsu", "onLoaded : " + videoId);
	      try{
			  player.play();
			 }catch (IllegalStateException e) {
				 e.printStackTrace();
			 }
			 Log.i("dsu", "비디오시작");
	    }
	    
	    @Override
	    public void onAdStarted() {
	      playerState = "AD_STARTED";
	      Log.i("dsu", "onAdStarted : " + playerState);
	    }

	    @Override
	    public void onVideoStarted() {
	      playerState = "VIDEO_STARTED";
	      Log.i("dsu", "onVideoStarted : " + playerState);
	    }

	    @Override
	    public void onVideoEnded() {
	      playerState = "VIDEO_ENDED";
	      if(player.hasNext()){
	    	  player.next();  
	      }else{
	    	  playVideoAtSelection();
	    	  return;
	      }
	    }

	    @Override
	    public void onError(ErrorReason reason) {
	      playerState = "ERROR (" + reason + ")";
	      Log.i("dsu", "onError : " + reason);
	      if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
	        // When this error occurs the player is released and can no longer be used.
	        player = null;
	      }
	    }
	  }
	
	public class MyPlaybackEventListener implements PlaybackEventListener {
	    String playbackState = "NOT_PLAYING";
	    @Override
	    public void onPlaying() {
	      playbackState = "PLAYING";
	      Log.i("dsu", "onPlaying" + playbackState);
	    }

	    @Override
	    public void onBuffering(boolean isBuffering) {
	      Log.i("dsu", "isBuffering" + isBuffering);
	    }

	    @Override
	    public void onStopped() {
	      playbackState = "STOPPED";
	      Log.i("dsu", "onStopped" + playbackState);
	    }

	    @Override
	    public void onPaused() {
	      playbackState = "PAUSED";
	      Log.i("dsu", "onPaused" + playbackState);
	    }

	    @Override
	    public void onSeekTo(int endPositionMillis) {
//	      Log.i("dsu", (String.format("\tSEEKTO: (%s/%s)",
	    }
	}
		@Override
		public void onFullscreen(boolean arg0) {
		}
		
		@Override
		public void onBackPressed() {
			super.onBackPressed();
		}
	}
