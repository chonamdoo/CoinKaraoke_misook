package com.coin.karaoke.misook.mediaplayer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {  
	public static Context _context;
	int SDK_INT = android.os.Build.VERSION.SDK_INT;
	public CustomMediaController(Context context, boolean useFastForward){
		super(context, useFastForward);     
		_context = context;
		}       
	public CustomMediaController(Context context, AttributeSet attrs){
		super(context, attrs); 
		_context = context; 
		}

	public CustomMediaController(Context context){
		super(context);
		_context = context;
		}      
	@Override   
	public void setAnchorView(View view){    
		super.setAnchorView(view);
		/**          * 0 : 컨트롤러 전체           * 1 : 컨트롤 버튼           * 2 : 탐색 바          */   
		LinearLayout layout = (LinearLayout)getChildAt(0);  
		layout.setVisibility(View.GONE);
		
		
//		if (SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //허니콤 버전에서만 실행 가능한 API 사용}
//			LinearLayout control = (LinearLayout)layout.getChildAt(0);
//			control.setVisibility(View.GONE);
//		}else{
//			LinearLayout control = (LinearLayout)layout.getChildAt(1);
//			control.setVisibility(View.GONE);
//		}
//		LinearLayout seekBar = (LinearLayout)layout.getChildAt(1);
//		LayoutInflater inf = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
//		LinearLayout linear = (LinearLayout) inf.inflate(R.layout.custom_mediacontroller, null);     
//		layout.addView(linear);  
	}

	//전체화면모드	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();   
//		if (displayMetrics != null) {
//			setMeasuredDimension(displayMetrics.widthPixels, displayMetrics.heightPixels);   
//
//		}
//	}
}