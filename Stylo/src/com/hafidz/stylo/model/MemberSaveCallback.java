/**
 * 
 */
package com.hafidz.stylo.model;

import android.app.Activity;
import android.content.Context;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.Util;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * @author hafidz
 * 
 */
public class MemberSaveCallback extends SaveCallback {

	private Member member;
	private Context context;

	public MemberSaveCallback(Member member, Context context) {
		super();
		this.member = member;
		this.context = context;
	}

	@Override
	public void done(ParseException error) {
		if (error != null) {
			((Activity) context).runOnUiThread(new UIThreadError(error,
					(MainActivity) context, member));

		} else {

			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Util.stopLoading();
					Util.showSuccess(context, "Member (" + member.getName()
							+ ") saved to the server.");

				}
			});

		}

	}

	private class UIThreadError implements Runnable {
		private ParseException error;
		private MainActivity mainActivity;
		private Member member;

		public UIThreadError(ParseException error, MainActivity mainActivity,
				Member member) {
			this.error = error;
			this.mainActivity = mainActivity;
			this.member = member;
		}

		public void run() {
			Util.stopLoading();
			// Util.whiteboardLayout.removeView(Util.whiteboardLayout
			// .findViewWithTag(task.getId()));

			// just simply refresh all stickers
			mainActivity.reloadStickers();

			Util.showError(context,
					"Problem saving member (" + member.getName()
							+ ") to server. - " + error.getCode() + ": "
							+ error.getLocalizedMessage());
		}
	}

}