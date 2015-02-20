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
import com.hafidz.stylo.manager.MemberManager;
import com.hafidz.stylo.manager.TaskManager;
import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.util.Util;
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

								return TaskManager.load(myContext, id);

							}

							@Override
							protected void onPostExecute(Task task) {

								Util.stopLoading();

								if (task != null) {

									// check sticker already exist or not first
									if (Util.whiteboardLayout
											.findViewWithTag(task.getId()) == null) {

										TaskManager.UICreateEmptySticker(
												myContext, Util.toPixelsWidth(
														myContext,
														task.getPosX()), task
														.getPosY(), task
														.getId());
									}
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

								return TaskManager.load(myContext, id);

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

										// owner
										System.out
												.println("task.getOwner() xxxxxxxxxxxxxxxxxxxx uuuuuuuuuuuu = "
														+ task.getOwner());
										TextView ownerTV = (TextView) taskSticker
												.findViewById(R.id.taskDetailOwner);
										ownerTV.setText(task.getOwner());

										if (TaskManager.PUSH_ACTION_UPDATE_OWNER == action) {

											// remove owner from pool
											Float newOwnerPoolY = null;
											if (task.getOwner() != null) {
												View memberSticker = Util.whiteboardLayout
														.findViewWithTag(task
																.getOwner());
												if (memberSticker != null) {
													newOwnerPoolY = memberSticker
															.getY();
													memberSticker
															.setVisibility(View.GONE);

												}

											}
											// add member back to pool

											if (json.has("oriOwner")) {
												try {
													String oriOwner = json
															.getString("oriOwner");

													View memberSticker = Util.whiteboardLayout
															.findViewWithTag(oriOwner);
													if (memberSticker != null) {
														memberSticker
																.setVisibility(View.VISIBLE);

														// replace position of
														// new owner
														if (newOwnerPoolY != null)
															memberSticker
																	.setY(newOwnerPoolY);
													}
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}

										} else if (TaskManager.PUSH_ACTION_MOVE == action) {

											if (task.getStatus() == Task.STATUS_DONE
													&& json.has("oriOwner")) {
												try {
													String oriOwner = json
															.getString("oriOwner");

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
						};

						bgTaskUpdate.execute();
						break;

					}

				} else if ("MEMBER".equals(type)) {
					switch (action) {

					case MemberManager.PUSH_ACTION_CREATE:
						AsyncTask<String, Void, Member> bgTaskCreate = new AsyncTask<String, Void, Member>() {
							@Override
							protected void onPreExecute() {
								Util.startLoading();
							}

							@Override
							protected Member doInBackground(String... args) {
								try {

									return MemberManager.load(myContext, id);
								} catch (ParseException e) {
									e.printStackTrace();

								}

								return null;
							}

							@Override
							protected void onPostExecute(Member member) {

								Util.stopLoading();

								if (member != null) {

									// check sticker already exist or not first
									if (Util.whiteboardLayout
											.findViewWithTag(member.getName()) == null)

									{

										MemberManager.UIcreateNewSticker(
												myContext, member.getPosY(),
												member.getName());
									}
								}

							}
						};

						bgTaskCreate.execute();
						break;

					case MemberManager.PUSH_ACTION_DELETE:
						View memberStricker = Util.whiteboardLayout
								.findViewWithTag(id);
						if (memberStricker != null) {
							Util.whiteboardLayout.removeView(memberStricker);
						}

						// free owner???

						break;

					case MemberManager.PUSH_ACTION_MOVE:
					case MemberManager.PUSH_ACTION_UPDATE_DETAILS:

						AsyncTask<String, Void, Member> bgTaskUpdate = new AsyncTask<String, Void, Member>() {
							@Override
							protected void onPreExecute() {
								Util.startLoading();
							}

							@Override
							protected Member doInBackground(String... args) {
								try {

									return MemberManager.load(myContext, id);
								} catch (ParseException e) {
									e.printStackTrace();

								}

								return null;
							}

							@Override
							protected void onPostExecute(Member member) {

								Util.stopLoading();

								if (member != null) {

									// update UI
									View taskSticker = null;

									taskSticker = Util.whiteboardLayout
											.findViewWithTag(member.getName());

									if (taskSticker != null) {

										// details
										// TextView nameTV = (TextView)
										// taskSticker.findViewById(R.id.memberName);
										// nameTV.setText(member.getName());

										// postition
										taskSticker.setY(member.getPosY());

									}

								}
							}
						};

						bgTaskUpdate.execute();
						break;

					}
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
