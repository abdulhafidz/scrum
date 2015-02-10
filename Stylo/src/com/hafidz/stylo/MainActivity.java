package com.hafidz.stylo;

import java.util.Map;
import java.util.Map.Entry;

import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.Parse;
import com.parse.ParseException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
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

		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, "rvL9mFoct6KVwjvIfTCV23qRwKBKlcwPrwPVpvPI",
				"dPxpmhhE7ceXzKwGDpkdWBqWKh7IyWIaJJpd7yJl");

		// setContentView(R.layout.activity_main);
		whiteboardView = new WhiteboardView(getApplicationContext());
		whiteboardView.setBackgroundColor(Color.parseColor("#F5F5F5"));
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

		// garbage can
		ImageView garbage = new ImageView(getApplicationContext());
		garbage.setImageResource(android.R.drawable.ic_menu_delete);
		garbage.setX(toPixelsWidth(88));
		garbage.setY(Util.toPixelsHeight(getApplicationContext(), 80));
		garbage.setLayoutParams(new LayoutParams(100, 100));
		GarbageListener garbageListener = new GarbageListener(
				getApplicationContext());
		garbage.setOnDragListener(garbageListener);
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

	@Override
	protected void onResume() {
		super.onResume();

		// clear all sticker
		Util.whiteboardLayout.removeAllViews();

		// Whiteboard headers
		int textTop = 5;

		// whiteboard texts
		TextView todoText = new TextView(getApplicationContext());
		todoText.setText("TO-DO");
		todoText.setTextSize(15);
		todoText.setTypeface(Typeface.DEFAULT_BOLD);
		todoText.setTextColor(Color.parseColor("#3F51B5"));
		todoText.setPaddingRelative(toPixelsWidth(22), textTop, 0, textTop);
		whiteBoardLayout.addView(todoText);

		TextView inProgressText = new TextView(getApplicationContext());
		inProgressText.setText("In Progress");
		inProgressText.setTextSize(15);
		inProgressText.setTypeface(Typeface.DEFAULT_BOLD);
		inProgressText.setTextColor(Color.parseColor("#3F51B5"));
		inProgressText.setPaddingRelative(toPixelsWidth(50), textTop, 0,
				textTop);
		whiteBoardLayout.addView(inProgressText);

		TextView doneText = new TextView(getApplicationContext());
		doneText.setText("Done");
		doneText.setTextSize(15);
		doneText.setTypeface(Typeface.DEFAULT_BOLD);
		doneText.setTextColor(Color.parseColor("#3F51B5"));
		doneText.setPaddingRelative(toPixelsWidth(82), textTop, 0, textTop);
		whiteBoardLayout.addView(doneText);

		try {
			// load members from db
			Map<String, Member> members = MemberManager
					.getAll(getApplicationContext());

			// add member stickers to whiteboard
			for (Entry<String, Member> entry : members.entrySet()) {
				Member member = entry.getValue();

				MemberManager.createNewSticker(getApplicationContext(),
						member.getPosY(), member.getName());

				System.out.println("member.getName() -> " + member.getName());

			}

			Toast.makeText(getApplicationContext(), "All member(s) loaded.",
					Toast.LENGTH_SHORT).show();

		} catch (ParseException e1) {
			Util.showError(getApplicationContext(),
					"Problem getting member(s) from server.");
		}

		try {
			// load tasks from db
			System.out
					.println("loading tasksssss = = = = ======================== = = =  11111");
			Map<String, Task> tasks = TaskManager
					.getAll(getApplicationContext());
			System.out
					.println("loading taskssss = = = = ======================== = = =  22222");

			System.out.println(tasks.size());

			// add tasks stickers to whiteboard
			for (Entry<String, Task> entry : tasks.entrySet()) {
				Task task = entry.getValue();
				RelativeLayout sticker = TaskManager.createEmptySticker(
						getApplicationContext(),
						Util.toPixelsWidth(getApplicationContext(),
								Math.round(task.getPosX())), task.getPosY(),
						task.getId());
				TaskManager.updateSticker(sticker, task.getTitle(),
						task.getDescription());
				if (task.getOwner() != null) {
					TaskManager.updateStickerOwner(sticker, task.getOwner()
							.getName(), null);

					// remove from members pool
					Util.whiteboardLayout.findViewWithTag(
							task.getOwner().getName()).setVisibility(View.GONE);
				}

			}

			// toast!
			Toast.makeText(getApplicationContext(), "All tasks(s) loaded.",
					Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			e.printStackTrace();
			Util.showError(this.getApplicationContext(),
					"Problem getting task(s) from server.");
		}
	}

	@Override
	public Context getApplicationContext() {

		return this;
	}
}
