/**
 * 
 */
package com.hafidz.stylo;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author johariab
 * 
 */
public class Util {

	public static ImageView garbage;
	public static RelativeLayout whiteboardLayout;

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

	public static int toPercentageWidth(Context context, float pixels) {
		return Math.round(pixels) * 100 / getScreenWidth(context);
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

	public static int toPixelsWidth(Context context, int percentage) {

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

}
