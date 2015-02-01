package com.hafidz.stylo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * TODO : onlongclick edit, overall view, ontouch hand cursor, road block,
 * notifications, roadblock alert to scrum master, drag member zooom, hold..
 * obtain lock, only TODO can create sticky, hold create member, drag member out
 * (free) from task, point drag, create and drag terus not working, drag to bin,
 * free owner, replace owner return owner to hut, how long working counter,
 * guide with arrow
 * 
 * @author hafidz
 * 
 */
public class MainActivity extends Activity {

	private WhiteBoardScroller whiteBoardScroller;
	private RelativeLayout whiteBoardLayout;
	private WhiteboardView whiteboardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.activity_main);
		whiteboardView = new WhiteboardView(getApplicationContext());
		whiteboardView.setBackgroundColor(Color.LTGRAY);
		setContentView(whiteboardView);

		// init widgets
		// whiteBoardLayout = (RelativeLayout) findViewById(R.id.whiteBoard);
		whiteBoardLayout = new RelativeLayout(getApplicationContext());
		whiteBoardLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 2000));

		// scroller
		whiteBoardScroller = new WhiteBoardScroller(getApplicationContext());
		whiteBoardScroller.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		whiteBoardScroller.addView(whiteBoardLayout);

		// white board listeners
		WhiteBoardListener wbListener = new WhiteBoardListener(
				MainActivity.this);
		whiteBoardLayout.setOnDragListener(wbListener);
		whiteBoardLayout.setOnLongClickListener(wbListener);
		whiteBoardLayout.setOnTouchListener(wbListener);
		// whiteBoardLayout.setBackgroundColor(Color.RED);

		// hide default sticky
		// findViewById(R.id.defaultSticky).setVisibility(View.GONE);

		// whiteboardView.addView(whiteBoardLayout);
		whiteboardView.addView(whiteBoardScroller);

		int textTop = 5;

		// whiteboard texts
		TextView todoText = new TextView(getApplicationContext());
		todoText.setText("TO-DO");
		todoText.setTextSize(15);
		todoText.setTypeface(Typeface.DEFAULT_BOLD);
		todoText.setTextColor(Color.BLACK);
		todoText.setPaddingRelative(toPixelsWidth(22), textTop, 0, textTop);
		whiteBoardLayout.addView(todoText);

		TextView inProgressText = new TextView(getApplicationContext());
		inProgressText.setText("In Progress");
		inProgressText.setTextSize(15);
		inProgressText.setTypeface(Typeface.DEFAULT_BOLD);
		inProgressText.setTextColor(Color.BLACK);
		inProgressText.setPaddingRelative(toPixelsWidth(50), textTop, 0,
				textTop);
		whiteBoardLayout.addView(inProgressText);

		TextView doneText = new TextView(getApplicationContext());
		doneText.setText("Done");
		doneText.setTextSize(15);
		doneText.setTypeface(Typeface.DEFAULT_BOLD);
		doneText.setTextColor(Color.BLACK);
		doneText.setPaddingRelative(toPixelsWidth(82), textTop, 0, textTop);
		whiteBoardLayout.addView(doneText);

		// garbage can
		ImageView garbage = new ImageView(getApplicationContext());
		garbage.setImageResource(android.R.drawable.ic_menu_delete);
		garbage.setX(toPixelsWidth(88));
		garbage.setY(Util.toPixelsHeight(getApplicationContext(), 80));
		garbage.setLayoutParams(new LayoutParams(150, 150));
		garbage.setOnDragListener(new GarbageListener(getApplicationContext()));
		whiteboardView.addView(garbage);
		Util.garbage = garbage;
		Util.hideGarbage();

		// add whiteboard to Util for easy access
		Util.whiteboardLayout = whiteBoardLayout;

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			whiteboardView
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	int toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(getApplicationContext(), percentage);
	}

}
