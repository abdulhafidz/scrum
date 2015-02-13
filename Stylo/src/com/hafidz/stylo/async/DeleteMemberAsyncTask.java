/**
 * 
 */
package com.hafidz.stylo.async;

import com.hafidz.stylo.Util;
import com.hafidz.stylo.model.MemberManager;
import com.parse.ParseException;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author hafidz
 * 
 */
public class DeleteMemberAsyncTask extends
		AsyncTask<String, Void, ParseException> {

	private Context context;
	private String memberId;

	public DeleteMemberAsyncTask(Context context, String memberId) {
		super();
		this.context = context;
		this.memberId = memberId;
	}

	@Override
	protected void onPreExecute() {
		Util.startLoading();
	}

	@Override
	protected ParseException doInBackground(String... args) {
		try {

			MemberManager.obtainLock(memberId);
			MemberManager.remove(context, memberId);
			MemberManager.releaseLock(memberId);
		} catch (ParseException e) {

			e.printStackTrace();
			return e;
		}

		return null;
	}

	@Override
	protected void onPostExecute(ParseException exception) {
		Util.stopLoading();
		// no error
		if (exception == null) {

			// remove sticker
			Util.whiteboardLayout.removeView(Util.whiteboardLayout
					.findViewWithTag(memberId));

			Util.showSuccess(context, "Member removed from server.");
		}
		// got error
		else {
			Util.showError(context, "Problem removing member from server.");
			Util.reloadStickers();
		}
	}

}
