package com.hafidz.stylo;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (event.getLocalState() instanceof GridLayout) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				Util.hideGarbage();

				try {
					RelativeLayout taskSticker = (RelativeLayout) v;
					GridLayout memberLayout = (GridLayout) event
							.getLocalState();
					TextView memberNameTV = (TextView) memberLayout
							.findViewById(R.id.memberName);

					String newOwner = memberNameTV.getText().toString();

					// update task manager
					String taskId = (String) taskSticker.getTag();
					TaskManager.obtainLock(taskId);
					TaskManager.assignOwner(context,
							TaskManager.load(context, taskId),
							MemberManager.load(context, newOwner));
					TaskManager.releaseLock(taskId);

					System.out.println("* * * * * member " + newOwner
							+ " dropped to task");

					// task sticker and member sticker UI
					TaskManager.updateStickerOwner(taskSticker, newOwner,
							memberLayout);
				} catch (ParseException e) {
					Util.showError(context, "Problem updating the server.");
				}

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

	@Override
	public void onClick(View v) {

		try {
			Task task = TaskManager.load(context, (String) v.getTag());

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ScrollView viewTaskLayout = (ScrollView) inflater.inflate(
					R.layout.sticky_layout, null);

			// new task, so we bring them directly to edit dialog
			if (task.getTitle() == null) {
				showEditDialog(viewTaskLayout, task.getId());
				return;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			// set values
			builder.setTitle(task.getTitle());
			((TextView) viewTaskLayout.findViewById(R.id.taskDetailDesc))
					.setText(task.getDescription());
			TextView stat = (TextView) viewTaskLayout
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

			if (task.getOwner() != null) {
				((TextView) viewTaskLayout.findViewById(R.id.taskDetailOwner))
						.setText(task.getOwner().getName());

				// add listeners to owner sticker
				TextView owner = (TextView) viewTaskLayout
						.findViewById(R.id.taskDetailOwner);
				owner.setOnLongClickListener(new OwnerStickerListener());

				// add listeners to view task layout
				viewTaskLayout.setOnDragListener(new TaskViewListener(context,
						task));
			}

			builder.setView(viewTaskLayout);

			builder.setNegativeButton("Close", null);
			builder.setPositiveButton("Edit", new onClickEditButtonListener(
					task.getId(), viewTaskLayout));

			AlertDialog dialog = builder.create();

			dialog.show();
		} catch (ParseException e) {
			Util.showError(context, "Problem retrieving from server.");
		}

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener {

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

	}

	private void showEditDialog(ScrollView stickyLayout, String taskId) {

		try {
			Task task = TaskManager.load(context, taskId);

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

			// pre-populate with value
			((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
					.setText(task.getTitle());
			((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
					.setText(task.getDescription());

			AlertDialog dialog = builder.create();

			dialog.show();

			// overide save listener because we dont want to auto dismiss dialog
			// after save
			Button theButton = dialog
					.getButton(DialogInterface.BUTTON_POSITIVE);
			theButton.setOnClickListener(new TaskEditListener(context, taskId,
					stickyLayout, dialog));
		} catch (ParseException e) {
			Util.showError(context, "Problem retrieving from server.");
		}
	}

}