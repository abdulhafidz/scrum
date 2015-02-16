package com.hafidz.stylo;

import android.content.Context;
import android.os.AsyncTask;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

public class WhiteBoardListener implements OnTouchListener, OnDragListener,
		OnLongClickListener {

	// percentage offset
	public static final int STICKY_X_OFFSET = 7;
	public static final int STICKY_Y_OFFSET = 150;

	public static final int MEMBER_Y_OFFSET = 38;

	private float wbTouchX;
	private float wbTouchY;

	private Context context;

	// private Map<String, TextView> members;
	// private Map<String, GridLayout> members;

	public WhiteBoardListener(Context context) {
		this.context = context;
		// this.members = members;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// just to get coordinate of touch
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			wbTouchX = event.getX();
			wbTouchY = event.getY();

		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {

		// create member
		if (wbTouchX <= toPixelsWidth(10)) {

			try {
				System.out.println("* * * * * long click create member ");

				String memberName = "#" + MemberManager.getAll(context).size();

				// add to the global list of members
				Member member = new Member(memberName, "", false, wbTouchY
						- MEMBER_Y_OFFSET, null);
				MemberManager.add(context, member);

				MemberManager.UIcreateNewSticker(context, wbTouchY
						- MEMBER_Y_OFFSET, memberName);

				MemberListener.showEditDialog(context, memberName, true);

				return true;
			} catch (ParseException e) {
				Util.showError(context, "Problem updating server.");
			}
		}

		// ProgressDialog progress = new ProgressDialog(context);
		try {

			// progress.setTitle("Loading");
			// progress.setMessage("Wait while loading...");
			// progress.show();

			// new small sticky layout
			String id = Util.generateTaskId();
			float x = wbTouchX - toPixelsWidth(STICKY_X_OFFSET);
			float y = wbTouchY - STICKY_Y_OFFSET;

			Task task = new Task(id, Util.toPercentageWidth(context, x), y,
					null);

			TaskManager.createEmptySticker(context, x, y, id);

			TaskManager.add(context, task);

		} catch (ParseException e) {
			Util.showError(context, "Problem updating to the server.");
		} finally {
			// progress.dismiss();
		}

		return true;
	}

	private class TaskMovedThread implements Runnable {

		private String taskId;
		private float newX;
		private float newY;

		private int newStatus;

		public TaskMovedThread(String taskId, float newX, float newY) {
			this.taskId = taskId;
			this.newX = newX;
			this.newY = newY;
		}

		@Override
		public void run() {
			try {
				TaskManager.obtainLock(taskId);
				newStatus = TaskManager.moved(context, taskId,
						Util.toPercentageWidth(context, newX), newY);
				TaskManager.releaseLock(taskId);

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();
						Util.showSuccess(context, "Task updated in server.");

						// update UI (free owner on task sticker and return back
						// owner to member pool)
						if (Task.STATUS_DONE == newStatus) {
							RelativeLayout taskSticker = (RelativeLayout) Util.whiteboardLayout
									.findViewWithTag(taskId);
							TextView memberView = (TextView) taskSticker
									.findViewById(R.id.taskDetailOwner);
							String memberID = memberView.getText() == null ? null
									: memberView.getText().toString();
							if (memberID != null && !memberID.isEmpty()) {
								TaskManager.uiUpdateStickerOwner(taskSticker,
										null,
										(GridLayout) Util.whiteboardLayout
												.findViewWithTag(memberID));
								Util.showSuccess(context, memberID
										+ " is now free.");
							}
						}

					}
				});
			} catch (ParseException e) {
				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();

						Util.showError(context,
								"Problem saving task to server.");

						Util.reloadStickers();

					}
				});
			}

		}
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {

		switch (event.getAction()) {
		case DragEvent.ACTION_DROP:

			/* task dropped */
			if (event.getLocalState() instanceof RelativeLayout) {

				RelativeLayout task = (RelativeLayout) event.getLocalState();
				float newX = event.getX() - toPixelsWidth(STICKY_X_OFFSET);
				float newY = event.getY() - STICKY_Y_OFFSET;

				// update manager
				String taskId = (String) task.getTag();

				Util.startLoading();
				Thread bgUpdate = new Thread(new TaskMovedThread(taskId, newX,
						newY));
				bgUpdate.start();

				System.out.println("* * * * * drag dropped on whiteboard : "
						+ task.getId());

				// set post-it to new position
				task.setX(event.getX() - toPixelsWidth(STICKY_X_OFFSET));
				task.setY(event.getY() - STICKY_Y_OFFSET);

				// bring to front
				task.bringToFront();

			}
			/* member dropped */
			else {

				GridLayout memberText = (GridLayout) event.getLocalState();

				if (event.getX() <= toPixelsWidth(10) && event.getY() > 70) {

					AsyncTask<Object, Void, ParseException> bgTask = new AsyncTask<Object, Void, ParseException>() {
						@Override
						protected void onPreExecute() {
							Util.startLoading();
						}

						@Override
						protected ParseException doInBackground(Object... args) {
							try {
								MemberManager.moved(context, (String) args[0],
										(Float) args[1] - MEMBER_Y_OFFSET);
							} catch (ParseException e) {
								return e;
							}

							return null;
						}

						@Override
						protected void onPostExecute(ParseException exception) {
							Util.stopLoading();
							// no error
							if (exception == null) {
								Util.showSuccess(context,
										"Member updated to the server.");
							}
							// got error
							else {
								Util.showError(context,
										"Problem updating member to the server.");
								Util.reloadStickers();
							}
						}
					};

					bgTask.execute((String) memberText.getTag(), event.getY());

					// /////////////////

					// new member location
					memberText.setX(toPixelsWidth(1));
					memberText.setY(event.getY() - MEMBER_Y_OFFSET);

					memberText.bringToFront();

					memberText.setVisibility(View.VISIBLE);

					return true;

				} else {
					System.out
							.println("* * * * * non sticky dropped on whiteboard");

					memberText.setVisibility(View.VISIBLE);

					return false;
				}
			}

			break;

		case DragEvent.ACTION_DRAG_ENDED:
			Util.hideGarbage();
			if (event.getLocalState() instanceof RelativeLayout) {
				RelativeLayout postIt = (RelativeLayout) event.getLocalState();
				postIt.setVisibility(View.VISIBLE);
			} else {
				System.out.println("* * * * * non sticky ended on whiteboard");

				return false;
			}
			break;

		}

		return true;

	}

	int toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(context, percentage);
	}

}