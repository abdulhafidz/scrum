package com.hafidz.stylo.listener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.R.id;
import com.hafidz.stylo.R.layout;
import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;
import com.parse.ParseException;

public class TaskStickerListener implements OnDragListener,
		OnLongClickListener, OnClickListener {

	private Context context;

	public TaskStickerListener(Context context) {
		this.context = context;
	}

	private class AssignOwnerThread implements Runnable {
		private String taskId;
		private String newOwner;

		public AssignOwnerThread(String taskId, String newOwner) {
			super();
			this.taskId = taskId;
			this.newOwner = newOwner;
		}

		public void run() {
			try {
				TaskManager.obtainLock(taskId);
				// TaskManager.assignOwner(context, taskId,
				// MemberManager.load(context, newOwner));
				TaskManager.assignOwner(context, taskId, newOwner);
				TaskManager.releaseLock(taskId);

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();
						Util.showSuccess(context, "Task updated to server.");

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
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (event.getLocalState() instanceof GridLayout) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();

				RelativeLayout taskSticker = (RelativeLayout) v;
				GridLayout memberLayout = (GridLayout) event.getLocalState();
				TextView memberNameTV = (TextView) memberLayout
						.findViewById(R.id.memberName);

				String newOwner = memberNameTV.getText().toString();

				// update task manager
				String taskId = (String) taskSticker.getTag();

				Util.startLoading();
				Thread bgUpdate = new Thread(new AssignOwnerThread(taskId,
						newOwner));
				bgUpdate.start();

				System.out.println("* * * * * member " + newOwner
						+ " dropped to task");

				// task sticker and member sticker UI
				TaskManager.uiUpdateStickerOwner(taskSticker, newOwner,
						memberLayout);

				break;

			}
			return true;
		}

		// non member
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		System.out.println("* * * * * start on long click");

		// shadow
		View.DragShadowBuilder shadow = new DragShadowBuilder(v);

		// hide ori post it
		v.setVisibility(View.INVISIBLE);

		// start drag
		v.startDrag(null, shadow, v, 0);

		Util.showGarbage();

		return true;
	}

	private class LoadThread implements Runnable {
		private String taskId;
		private AlertDialog dialog;
		private ScrollView dialogLayout;

		public LoadThread(String taskId, AlertDialog dialog,
				ScrollView viewTaskLayout) {
			this.taskId = taskId;
			this.dialog = dialog;
			this.dialogLayout = viewTaskLayout;
		}

		public void run() {
			try {
				Task task = TaskManager.load(context, taskId);

				Util.mainActivity.runOnUiThread(new ShowThread(dialog,
						dialogLayout, task));

			} catch (ParseException e) {

				e.printStackTrace();

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();

						dialog.dismiss();

						Util.showError(context,
								"Problem retrieving task from server.");

					}
				});
			}
		}
	}

	private class ShowThread implements Runnable {

		private AlertDialog dialog;
		private ScrollView dialogLayout;
		private Task task;

		public ShowThread(AlertDialog dialog, ScrollView viewTaskLayout,
				Task task) {

			this.dialog = dialog;
			this.dialogLayout = viewTaskLayout;
			this.task = task;
		}

		public void run() {

			// set values
			dialog.setTitle(task.getTitle());
			((TextView) dialogLayout.findViewById(R.id.taskDetailDesc))
					.setText(task.getDescription());
			TextView stat = (TextView) dialogLayout
					.findViewById(R.id.taskDetailStatus);
			switch (task.getStatus()) {
			case Task.STATUS_IN_PROGRESS:
				stat.setText("IN PROGRESS");
				stat.setTextColor(Color.parseColor("#3F51B5"));
				break;

			case Task.STATUS_DONE:
				stat.setText("DONE");
				stat.setTextColor(Color.parseColor("#4CAF50"));
				break;

			case Task.STATUS_TODO:
				stat.setText("TO-DO");
				stat.setTextColor(Color.parseColor("#F44336"));
				break;

			case Task.STATUS_ROAD_BLOCK:
				stat.setText("ROAD BLOCK");
				stat.setTextColor(Color.parseColor("#9C27B0"));
				break;
			}

			// owner
			if (task.getOwner() != null) {
				((TextView) dialogLayout.findViewById(R.id.taskDetailOwner))
						.setText(task.getOwner());

				// add listeners to owner sticker
				TextView owner = (TextView) dialogLayout
						.findViewById(R.id.taskDetailOwner);
				owner.setOnLongClickListener(new OwnerStickerListener());

				// add listeners to view task layout
				dialogLayout.setOnDragListener(new TaskViewListener(context,
						task.getId(), task.getOwner()));
			}

			dialog.setButton(Dialog.BUTTON_POSITIVE, "Edit",
					new onClickEditButtonListener(task.getId(), dialogLayout));
			dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);

			Util.stopLoading();

		}
	}

	@Override
	public void onClick(View v) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ScrollView viewTaskLayout = (ScrollView) inflater.inflate(
				R.layout.sticky_layout, null);

		// // new task, so we bring them directly to edit dialog
		// if (task.getTitle() == null) {
		// showEditDialog(viewTaskLayout, task.getId());
		// return;
		// }

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		// set values
		builder.setTitle("...");
		((TextView) viewTaskLayout.findViewById(R.id.taskDetailDesc))
				.setText("...");
		TextView stat = (TextView) viewTaskLayout
				.findViewById(R.id.taskDetailStatus);
		stat.setText("...");
		stat.setTextColor(Color.parseColor("#3F51B5"));

		builder.setView(viewTaskLayout);

		builder.setNegativeButton("Close", null);
		builder.setPositiveButton("Edit", null);

		AlertDialog dialog = builder.create();

		Util.startLoading();
		Thread loadThread = new Thread(new LoadThread((String) v.getTag(),
				dialog, viewTaskLayout));
		loadThread.start();

		dialog.show();

		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

		// dialog.getWindow().setLayout(Util.toPixelsWidth(context, 90),
		// Util.toPixelsHeight(context, 90));

		// dialog.getWindow().setLayout(
		// Util.toPixelsWidth(context, LayoutParams.WRAP_CONTENT),
		// Util.toPixelsHeight(context, LayoutParams.WRAP_CONTENT));

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener, OnClickListener {

		private ScrollView stickyLayout;
		private String taskId;

		public onClickEditButtonListener(String taskId, ScrollView stickyLayout) {
			this.stickyLayout = stickyLayout;
			this.taskId = taskId;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			showEditDialog(stickyLayout, taskId);

		}

		@Override
		public void onClick(View arg0) {
			showEditDialog(stickyLayout, taskId);

		}

	}

	private class LoadEditDialogThread implements Runnable {

		private String taskId;
		private LinearLayout editTaskLayout;
		private AlertDialog dialog;

		public LoadEditDialogThread(String taskId, LinearLayout editTaskLayout,
				AlertDialog dialog, ScrollView viewLayout) {
			this.taskId = taskId;
			this.editTaskLayout = editTaskLayout;
			this.dialog = dialog;

		}

		@Override
		public void run() {
			try {
				Task task = TaskManager.load(context, taskId);

				Util.mainActivity.runOnUiThread(new ShowEditDialogThread(task,
						editTaskLayout, dialog));
			} catch (ParseException e) {

				e.printStackTrace();

				Util.mainActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Util.stopLoading();

						dialog.dismiss();

						Util.showError(context,
								"Problem retrieving task from server.");

					}
				});

			}

		}

	}

	private class ShowEditDialogThread implements Runnable {

		private Task task;
		private LinearLayout editTaskLayout;
		private AlertDialog editDialog;

		public ShowEditDialogThread(Task task, LinearLayout editTaskLayout,
				AlertDialog editDialog) {
			this.task = task;
			this.editTaskLayout = editTaskLayout;
			this.editDialog = editDialog;

		}

		@Override
		public void run() {
			// pre-populate with value
			((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
					.setText(task.getTitle());
			((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
					.setText(task.getDescription());

			((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
					.setEnabled(true);
			((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
					.setEnabled(true);

			// override dialog listener to normal listener
			Button theButton = editDialog
					.getButton(DialogInterface.BUTTON_POSITIVE);
			theButton.setOnClickListener(new TaskEditListener(context, task
					.getId(), editDialog));

			editDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);

			Util.stopLoading();

		}

	}

	private void showEditDialog(ScrollView viewLayout, String taskId) {

		// edit task dialog show
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout editTaskLayout = (LinearLayout) inflater.inflate(
				R.layout.sticky_edit_layout, null);
		builder.setView(editTaskLayout);
		builder.setPositiveButton("Save", null);
		builder.setNegativeButton("Cancel", null);

		builder.setTitle("Edit Task");

		// // pre-populate with value
		// ((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
		// .setText(task.getTitle());
		// ((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
		// .setText(task.getDescription());

		AlertDialog dialog = builder.create();

		dialog.show();

		// disable stuff
		dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
		((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
				.setEnabled(false);
		((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
				.setEnabled(false);

		// overide save listener because we dont want to auto dismiss dialog
		// after save
		// Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		// theButton.setOnClickListener(new TaskEditListener(context, taskId,
		// stickyLayout, dialog));

		Util.startLoading();
		Thread loadAndShowThread = new Thread(new LoadEditDialogThread(taskId,
				editTaskLayout, dialog, viewLayout));
		loadAndShowThread.start();

	}

}