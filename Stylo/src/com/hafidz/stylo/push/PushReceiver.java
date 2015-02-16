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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;

/**
 * @author hafidz
 * 
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

	private String id;
	private String oriOwner;

	@Override
	protected void onPushReceive(Context context, Intent intent) {

		try {
			String jsonData = intent.getExtras().getString("com.parse.Data");

			JSONObject json = new JSONObject(jsonData);

			String type = json.getString("type");
			id = json.getString("id");

			// for delete
			if (!json.isNull("oriOwner"))
				oriOwner = json.getString("oriOwner");

			// update UI
			if (!MainActivity.onBackground) {
				// if (true) {

				Util.showSuccess(context, "Just in: " + json.getString("msg"));

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
											.getApplicationContext(), id);
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
									View taskSticker = Util.whiteboardLayout
											.findViewWithTag(task.getId());
									if (taskSticker != null) {
										// details
										TextView titleTV = (TextView) taskSticker
												.findViewById(R.id.smallTaskTitle);
										titleTV.setText(task.getTitle());
										TextView descTV = (TextView) taskSticker
												.findViewById(R.id.smallTaskDesc);
										descTV.setText(task.getDescription());
										TextView ownerTV = (TextView) taskSticker
												.findViewById(R.id.taskDetailOwner);
										ownerTV.setText(task.getOwner());

										// remove owner from pool
										if (task.getOwner() != null) {
											View memberSticker = Util.whiteboardLayout
													.findViewWithTag(task
															.getOwner());
											if (memberSticker != null) {
												memberSticker
														.setVisibility(View.GONE);
											}

										}

										// postition
										taskSticker.setY(task.getPosY());
										taskSticker
												.setX(Util.toPixelsWidth(
														Util.mainActivity
																.getApplicationContext(),
														task.getPosX()));
									}
									// create new sticker
									else {
										// FIXME : not working
										taskSticker = TaskManager
												.createEmptySticker(
														Util.mainActivity
																.getApplicationContext(),
														Util.toPixelsWidth(
																Util.mainActivity
																		.getApplicationContext(),
																task.getPosX()),
														task.getPosY(), task
																.getId());

										// why is this not working

										// TextView titleTV = (TextView)
										// taskSticker
										// .findViewById(R.id.smallTaskTitle);
										// titleTV.setText(task.getTitle());
										// TextView descTV = (TextView)
										// taskSticker
										// .findViewById(R.id.smallTaskDesc);
										// descTV.setText(task.getDescription());
										// Util.whiteboardLayout
										// .addView(taskSticker);

									}

								} else {
									// delete
									View taskSticker = Util.whiteboardLayout
											.findViewWithTag(id);
									if (taskSticker != null) {
										Util.whiteboardLayout
												.removeView(taskSticker);
									}

									// free owner
									View memberSticker = Util.whiteboardLayout
											.findViewWithTag(oriOwner);
									if (memberSticker != null) {
										memberSticker
												.setVisibility(View.VISIBLE);
									}
								}
							}
						};

						bgTask.execute();
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

}
