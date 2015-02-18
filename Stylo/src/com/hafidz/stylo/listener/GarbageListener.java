package com.hafidz.stylo.listener;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.R.id;
import com.hafidz.stylo.async.DeleteMemberAsyncTask;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

public class GarbageListener implements OnDragListener {

	private Context context;

	public GarbageListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		ImageView garbage = (ImageView) v;
		// member deleted
		if (event.getLocalState() instanceof GridLayout) {
			GridLayout memberSticker = (GridLayout) event.getLocalState();

			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();
				garbage.setColorFilter(null);

				TextView tv = (TextView) memberSticker
						.findViewById(R.id.memberName);
				String name = tv.getText().toString();

				DeleteMemberAsyncTask bgTask = new DeleteMemberAsyncTask(
						context, name);
				bgTask.execute();

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.parseColor("#F44336"));

				memberSticker = (GridLayout) event.getLocalState();

				break;

			case DragEvent.ACTION_DRAG_EXITED:

				garbage.setColorFilter(null);

				break;
			}

			return true;
		}

		// task deleted
		if (event.getLocalState() instanceof RelativeLayout) {

			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();
				garbage.setColorFilter(null);

				RelativeLayout taskSticker = (RelativeLayout) event
						.getLocalState();

				String taskId = (String) taskSticker.getTag();

				Util.startLoading();
				Thread deleteThread = new Thread(new DeleteThread(taskId));
				deleteThread.start();

				// update UI
				Util.whiteboardLayout.removeView(taskSticker);

				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				garbage.setColorFilter(Color.parseColor("#F44336"));

				break;

			case DragEvent.ACTION_DRAG_EXITED:

				garbage.setColorFilter(null);

				break;
			}

			return true;
		}

		return false;
	}

	private class DeleteThreadUI implements Runnable {

		private Task task;

		public DeleteThreadUI(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			if (task.getOwner() != null) {
				GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
						.findViewWithTag(task.getOwner());
				memberSticker.setVisibility(View.VISIBLE);

				Util.showSuccess(context, task.getOwner() + " is now free.");
			}

			Util.stopLoading();
			Util.showSuccess(context, "Task deleted at server.");

		}

	}

	private class DeleteThread implements Runnable {

		private String taskId;

		public DeleteThread(String taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			try {
				Task task = TaskManager.load(context, taskId);

				TaskManager.obtainLock(taskId);
				TaskManager.remove(context, task);
				TaskManager.releaseLock(taskId);

				Util.mainActivity.runOnUiThread(new DeleteThreadUI(task));

			} catch (ParseException e) {
				e.printStackTrace();

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();
						Util.showSuccess(context,
								"Problem deleting task at server.");
						Util.reloadStickers();
					}
				});
			}

		}

	}
}
