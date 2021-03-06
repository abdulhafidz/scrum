/**
 * 
 */
package com.hafidz.stylo.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.manager.BoardManager;
import com.hafidz.stylo.model.Board;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * @author johariab
 * 
 */
public class Util {

	public static String VERSION = "0.1";
	public static ImageView garbage;
	public static RelativeLayout whiteboardLayout;
	public static SwipeRefreshLayout mainLayout;
	public static MainActivity mainActivity;
	public static Context context;

	// keji
	public static boolean loginInProgress = false;

	// public static int toPixels(Context context, float dp) {
	// DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	// float fpixels = metrics.density * dp;
	// return (int) (fpixels + 0.5f);
	// }

	// public static float toDP(Context context, float pixels) {
	// DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	//
	// return (pixels - 0.5f) / metrics.density;
	//
	// }

	public static float toPercentageWidth(Context context, float pixels) {
		return pixels * 100 / getScreenWidth(context);
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**
	 * Generate a value suitable for use in {@link #setId(int)}. This value will
	 * not collide with ID values generated at build time by aapt for R.id.
	 * 
	 * @return a generated ID value
	 */
	public static int generateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return metrics.heightPixels;
	}

	public static float toPixelsWidth(Context context, float percentage) {

		return (percentage) * getScreenWidth(context) / 100;

	}

	// height is fix!!!
	public static int toPixelsHeight(Context context, int percentage) {

		return (percentage) * getScreenHeight(context) / 100;

	}

	public static void showGarbage() {
		garbage.setVisibility(View.VISIBLE);
	}

	public static void hideGarbage() {
		garbage.setVisibility(View.GONE);
	}

	public static String generateTaskId() {
		return UUID.randomUUID().toString();
	}

	public static String generateBoardId() {
		return UUID.randomUUID().toString();
	}

	public static void showError(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

	}

	public static void showSuccess(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

	}

	public static void showGuide(Context context) {
		AlertDialog.Builder dialogBuilder = new Builder(context);
		dialogBuilder.setTitle("Welcome to Scrum Board " + VERSION);
		dialogBuilder
				.setMessage("This is a collaborative app where scrum team can view and update the same scrum board in real-time (sort of).\n\n- Long click on empty space to create task.\n\n- Long click on empty space on far left panel to add member.\n\n- Long click and drag member to task sticker to assign owner.\n\n- In view task dialog, long click and drag the owner out to unassign owner.\n\n- Drag sticker to the bin to remove it.\n\n- Swipe down the board to refresh.");
		dialogBuilder.setNegativeButton(android.R.string.ok, null);
		dialogBuilder.create().show();
	}

	public static void showAbout(Context context) {
		AlertDialog.Builder dialogBuilder = new Builder(context);
		dialogBuilder.setTitle("About Scrum Board " + VERSION);
		dialogBuilder
				.setMessage("Interested? Any bugs, Comments or Suggestions?\n\nPlease email to \n\nabdulhafidz@gmail.com");
		dialogBuilder.setNegativeButton(android.R.string.ok, null);
		dialogBuilder.create().show();
	}

	public static void startLoading() {
		mainLayout.setRefreshing(true);
	}

	public static void stopLoading() {
		mainLayout.setRefreshing(false);
	}

	public static void reloadStickers() {
		mainActivity.reloadStickers();
	}

	public static void setPermissions(ParseObject parse) {
		ParseACL parseACL = new ParseACL();

		parseACL.setRoleWriteAccess(BoardManager.getCurrentBoard().getRole(),
				true);
		parseACL.setRoleReadAccess(BoardManager.getCurrentBoard().getRole(),
				true);
		parse.setACL(parseACL);
	}

	public static void exitApp(MainActivity mainActivity) {

		mainActivity.finish();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mainActivity.startActivity(intent);

		// int pid = android.os.Process.myPid();
		// android.os.Process.killProcess(pid);

	}

	private static ProgressDialog progressDialog;

	public static void showProgressDialog(String msg) {
		if (progressDialog == null)
			progressDialog = new ProgressDialog(context);

		progressDialog.setMessage(msg);

		if (!progressDialog.isShowing()) {
			System.out.println("context = " + context);
			progressDialog.show();
		}
	}

	public static void stopProgressDialog() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}

}
