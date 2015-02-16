/**
 * 
 */
package com.hafidz.stylo.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
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

	// what if this class is singleton???!!
	private Context myContext;
	private String id;
	private int action;
	private JSONObject json;

	@Override
	protected void onPushReceive(Context context, Intent intent) {

		myContext = context;

		try {
			String jsonData = intent.getExtras().getString("com.parse.Data");

			json = new JSONObject(jsonData);

			String type = json.getString("type");
			id = json.getString("id");

			action = json.getInt("action");

			// update UI
			if (!MainActivity.onBackground) {
				// if (true) {

				Util.showSuccess(context,
						"Incoming Push: " + json.getString("msg"));

				if ("TASK".equals(type)) {

					switch (action) {

					case TaskManager.PUSH_ACTION_CREATE:
						AsyncTask<String, Void, Task> bgTaskCreate = new AsyncTask<String, Void, Task>() {
							@Override
							protected void onPreExecute() {
								Util.startLoading();
							}

							@Override
							protected Task doInBackground(String... args) {
								try {

									return TaskManager.load(myContext, id);
								} catch (ParseException e) {
									e.printStackTrace();

								}

								return null;
							}

							@Override
							protected void onPostExecute(Task task) {

								Util.stopLoading();

								if (task != null) {
									TaskManager.createEmptySticker(
											myContext,
											Util.toPixelsWidth(myContext,
													task.getPosX()),
											task.getPosY(), task.getId());
								}

							}
						};

						bgTaskCreate.execute();
						break;

					case TaskManager.PUSH_ACTION_DELETE:
						View taskSticker = Util.whiteboardLayout
								.findViewWithTag(id);
						if (taskSticker != null) {
							Util.whiteboardLayout.removeView(taskSticker);
						}

						// free owner
						if (json.has("oriOwner")) {
							String oriOwner = json.getString("oriOwner");
							if (oriOwner != null) {
								View memberSticker = Util.whiteboardLayout
										.findViewWithTag(oriOwner);
								if (memberSticker != null) {
									memberSticker.setVisibility(View.VISIBLE);
								}
							}
						}
						break;

					case TaskManager.PUSH_ACTION_MOVE:
					case TaskManager.PUSH_ACTION_UPDATE_DETAILS:
					case TaskManager.PUSH_ACTION_UPDATE_OWNER:

						AsyncTask<String, Void, Task> bgTaskUpdate = new AsyncTask<String, Void, Task>() {
							@Override
							protected void onPreExecute() {
								Util.startLoading();
							}

							@Override
							protected Task doInBackground(String... args) {
								try {

									return TaskManager.load(myContext, id);
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
									View taskSticker = null;

									taskSticker = Util.whiteboardLayout
											.findViewWithTag(task.getId());

									if (taskSticker != null) {

										// details
										TextView titleTV = (TextView) taskSticker
												.findViewById(R.id.smallTaskTitle);
										titleTV.setText(task.getTitle());
										TextView descTV = (TextView) taskSticker
												.findViewById(R.id.smallTaskDesc);
										descTV.setText(task.getDescription());

										// postition
										taskSticker.setY(task.getPosY());
										taskSticker.setX(Util.toPixelsWidth(
												myContext, task.getPosX()));

										if (TaskManager.PUSH_ACTION_UPDATE_OWNER == action) {

											System.out
													.println("task.getOwner() = = = = "
															+ task.getOwner());

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
											// add member back to pool
											else {
												if (json.has("oriOwner")) {
													try {
														String oriOwner = json
																.getString("oriOwner");
														System.out
																.println("oriOwner = = = "
																		+ oriOwner);
														View memberSticker = Util.whiteboardLayout
																.findViewWithTag(oriOwner);
														if (memberSticker != null) {
															memberSticker
																	.setVisibility(View.VISIBLE);
														}
													} catch (JSONException e) {
														e.printStackTrace();
													}
												}
											}

										}

									}

								}
							}
						};

						bgTaskUpdate.execute();
						break;

					}

				} else if ("MEMBER".equals(type)) {

				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		// disable notification if app is in foreground
		if (!MainActivity.onBackground) {
			return;
		}

		super.onPushReceive(context, intent);
	}

}
