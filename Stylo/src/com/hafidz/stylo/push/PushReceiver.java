/**
 * 
 */
package com.hafidz.stylo.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RelativeLayout;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;

/**
 * @author hafidz
 * 
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

	@Override
	protected void onPushReceive(Context context, Intent intent) {

		try {
			String jsonData = intent.getExtras().getString("com.parse.Data");

			JSONObject json = new JSONObject(jsonData);

			Util.showSuccess(context, "Just in: " + json.getString("msg"));

			String type = json.getString("type");
			String id = json.getString("id");

			// update UI
			if (!MainActivity.onBackground) {
				if ("TASK".equals(type)) {

					// find small sticker
					RelativeLayout taskSticker = (RelativeLayout) Util.whiteboardLayout
							.findViewWithTag(id);

					if (taskSticker != null) {

						AsyncTask<String, Void, Task> bgTask = new AsyncTask<String, Void, Task>() {
							@Override
							protected void onPreExecute() {
								Util.startLoading();
							}

							@Override
							protected Task doInBackground(String... args) {
								try {

									return TaskManager.load(Util.mainActivity
											.getApplicationContext(), args[0]);
								} catch (ParseException e) {
									e.printStackTrace();
								}

								return null;
							}

							@Override
							protected void onPostExecute(Task task) {
								Util.stopLoading();

								if (task != null) {
									// update UI

									// postition

								} else {
									// delete
								}
							}
						};

						bgTask.execute(id);
					}

				} else if ("MEMBER".equals(type)) {

				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// disable notification if app is in foreground
		if (!MainActivity.onBackground) {
			return;
		}

		super.onPushReceive(context, intent);
	}

	@Override
	protected Notification getNotification(Context context, Intent intent) {

		return super.getNotification(context, intent);
	}

}
