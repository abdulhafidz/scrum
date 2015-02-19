package com.hafidz.stylo;

import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hafidz.stylo.listener.DrawerItemListener;
import com.hafidz.stylo.listener.GarbageListener;
import com.hafidz.stylo.listener.LoginListener;
import com.hafidz.stylo.listener.WhiteBoardListener;
import com.hafidz.stylo.manager.MemberManager;
import com.hafidz.stylo.manager.TaskManager;
import com.hafidz.stylo.manager.UserManager;
import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.User;
import com.hafidz.stylo.util.Util;
import com.parse.ParseException;

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
public class MainActivity extends Activity implements OnRefreshListener {

	private View mainActivityLayout;
	private WhiteBoardScroller whiteBoardScroller;
	private RelativeLayout whiteBoardLayout;
	// private WhiteboardView whiteboardView;
	private SwipeRefreshLayout mainLayout;

	private DrawerLayout drawerLayout;
	private ListView drawerList;

	private ProgressDialog progress;

	// determine app is foreground or background
	public static boolean onBackground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// for easy access later
		Util.mainActivity = this;
		Util.context = getApplicationContext();

		// // - - - - - - - - - - - - -
		// // user login [START]
		// // - - - - - - - - - - - - -
		// if (UserManager.getCurrentUser() == null) {
		// // show login dialog
		// AlertDialog.Builder dialogBuilder = new Builder(
		// getApplicationContext());
		// dialogBuilder.setTitle("Sign In");
		// View loginLayout = LayoutInflater.from(getApplicationContext())
		// .inflate(R.layout.login_layout, null);
		// dialogBuilder.setView(loginLayout);
		// dialogBuilder.setPositiveButton(android.R.string.ok,
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface arg0, int arg1) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// dialogBuilder.setNegativeButton(android.R.string.cancel,
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// Util.exitApp(Util.mainActivity);
		//
		// }
		// });
		//
		// AlertDialog loginDialog = dialogBuilder.create();
		// loginDialog.show();
		//
		// // sign up button
		// Button register = (Button) loginLayout
		// .findViewById(R.id.loginRegister);
		// register.setOnClickListener(new LoginListener());
		//
		// }
		//
		// // - - - - - - - - - - - - -
		// // user login [END]
		// // - - - - - - - - - - - - -

		// check first time user or not
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		boolean firstTime = sharedPref.getBoolean("firstTime", true);
		if (firstTime) {
			// show guide
			Util.showGuide(getApplicationContext());

			// change firstTime to false
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putBoolean("firstTime", false);
			editor.commit();

		}

		mainActivityLayout = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.activity_main, null);
		setContentView(mainActivityLayout);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerList = (ListView) findViewById(R.id.drawerList);
		ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				new String[] { "Refresh", "User Guide", "About" });
		drawerList.setAdapter(drawerAdapter);
		drawerList.setOnItemClickListener(new DrawerItemListener(this,
				drawerLayout));

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

		// whiteboardView.addView(whiteBoardScroller);

		// garbage can
		ImageView garbage = new ImageView(getApplicationContext());
		garbage.setImageResource(android.R.drawable.ic_menu_delete);
		garbage.setX(toPixelsWidth(88));
		garbage.setY(Util.toPixelsHeight(getApplicationContext(), 80));
		garbage.setLayoutParams(new LayoutParams(100, 100));
		GarbageListener garbageListener = new GarbageListener(
				getApplicationContext());
		garbage.setOnDragListener(garbageListener);
		// whiteboardView.addView(garbage);
		Util.garbage = garbage;
		Util.hideGarbage();

		// add whiteboard to Util for easy access
		Util.whiteboardLayout = whiteBoardLayout;

		// add to main layout
		mainLayout = (SwipeRefreshLayout) findViewById(R.id.drawerMainContent);
		mainLayout.setOnRefreshListener(this);
		mainLayout.addView(whiteBoardScroller);
		// mainLayout.setColorScheme(Color.parseColor("#3F51B5"),
		// Color.parseColor("#FFEB3B"), Color.parseColor("#3F51B5"),
		// Color.parseColor("#FFEB3B"));
		mainLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		addContentView(garbage, new LayoutParams(100, 100));

		// for easy access later
		Util.mainLayout = mainLayout;

		progress = new ProgressDialog(getApplicationContext());

		// // hamburger
		// ImageView hamburger = new ImageView(getApplicationContext());
		// hamburger.setImageResource(android.R.drawable.ic_menu_sort_by_size);
		// hamburger.setX(10);
		// hamburger.setY(10);
		// whiteBoardLayout.addView(hamburger);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			mainActivityLayout
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	float toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(getApplicationContext(), percentage);
	}

	@Override
	protected void onResume() {
		super.onResume();
		onBackground = false;
		reloadStickers();
	}

	@Override
	protected void onPause() {
		super.onPause();
		onBackground = true;
	}

	public void reloadStickers() {

		mainLayout.setRefreshing(true);
		// progress.setTitle("Loading");
		progress.setMessage("Loading from server...");
		progress.show();

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
		todoText.setPaddingRelative(Math.round(toPixelsWidth(22)), textTop, 0,
				textTop);
		whiteBoardLayout.addView(todoText);

		TextView inProgressText = new TextView(getApplicationContext());
		inProgressText.setText("In Progress");
		inProgressText.setTextSize(15);
		inProgressText.setTypeface(Typeface.DEFAULT_BOLD);
		inProgressText.setTextColor(Color.parseColor("#3F51B5"));
		inProgressText.setPaddingRelative(Math.round(toPixelsWidth(50)),
				textTop, 0, textTop);
		whiteBoardLayout.addView(inProgressText);

		TextView doneText = new TextView(getApplicationContext());
		doneText.setText("Done");
		doneText.setTextSize(15);
		doneText.setTypeface(Typeface.DEFAULT_BOLD);
		doneText.setTextColor(Color.parseColor("#3F51B5"));
		doneText.setPaddingRelative(Math.round(toPixelsWidth(82)), textTop, 0,
				textTop);
		whiteBoardLayout.addView(doneText);

		// load stickers on another thread
		new Thread(new StickerLoader()).start();
	}

	@Override
	public Context getApplicationContext() {

		return this;
	}

	private class StickerLoader implements Runnable {

		@Override
		public void run() {
			Activity activity = (Activity) getApplicationContext();
			try {
				// load stickers from db
				Map<String, Member> members = MemberManager
						.getAll(getApplicationContext());
				Map<String, Task> tasks = TaskManager
						.getAll(getApplicationContext());

				activity.runOnUiThread(new StickerLoaderUI(members, tasks));

			} catch (ParseException e1) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.showError(getApplicationContext(),
								"Problem loading sticker(s) from server.");
						mainLayout.setRefreshing(false);
						progress.dismiss();

					}
				});

			}

		}
	}

	private class StickerLoaderUI implements Runnable {

		private Map<String, Member> members;
		private Map<String, Task> tasks;

		public StickerLoaderUI(Map<String, Member> members,
				Map<String, Task> tasks) {
			this.members = members;
			this.tasks = tasks;
		}

		@Override
		public void run() {
			// add member stickers to whiteboard
			for (Entry<String, Member> entry : members.entrySet()) {
				Member member = entry.getValue();

				MemberManager.UIcreateNewSticker(getApplicationContext(),
						member.getPosY(), member.getName());

			}

			// add tasks stickers to whiteboard
			for (Entry<String, Task> entry : tasks.entrySet()) {
				Task task = entry.getValue();
				RelativeLayout sticker = TaskManager.UICreateEmptySticker(
						getApplicationContext(),
						Util.toPixelsWidth(getApplicationContext(),
								Math.round(task.getPosX())), task.getPosY(),
						task.getId());
				TaskManager.updateSticker(sticker, task.getTitle(),
						task.getDescription());
				if (task.getOwner() != null) {
					TaskManager.uiUpdateStickerOwner(sticker, task.getOwner(),
							null);

					// remove from members pool
					Util.whiteboardLayout.findViewWithTag(task.getOwner())
							.setVisibility(View.GONE);
				}

			}

			mainLayout.setRefreshing(false);

			progress.dismiss();
			// Toast.makeText(getApplicationContext(), "Welcome!",
			// Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public void onRefresh() {
		reloadStickers();

	}
}
