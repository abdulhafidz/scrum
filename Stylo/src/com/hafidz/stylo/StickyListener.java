package com.hafidz.stylo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;

public class StickyListener implements OnDragListener, OnLongClickListener,
		OnClickListener {

	private Context context;

	public StickyListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		if (event.getLocalState() instanceof GridLayout) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DROP:

				LinearLayout task = (LinearLayout) v;
				GridLayout memberLayout = (GridLayout) event.getLocalState();
				TextView memberNameTV = (TextView) memberLayout
						.findViewById(R.id.memberName);

				String newOwner = memberNameTV.getText().toString();

				// update task manager
				TaskManager.assignOwner(task.getId(), newOwner);

				System.out.println("* * * * * member " + newOwner
						+ " dropped to task");

				// sticky note

				TextView ownerText = (TextView) task
						.findViewById(R.id.taskOwner);
				String oriOwner = ownerText.getText().toString();
				ownerText.setText(newOwner);

				// hide member
				memberNameTV.setVisibility(View.GONE);

				// if replace existing owner, show replaced owner
				if (oriOwner != null && !oriOwner.trim().isEmpty()
						&& !oriOwner.equals(newOwner)) {
					// TODO
				}

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

		return true;
	}

	@Override
	public void onClick(View v) {
		System.out.println("* * * * * * * * * * on click sticky");

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout viewTaskLayout = (LinearLayout) inflater.inflate(
				R.layout.sticky_layout, null);

		// set values
		Task task = TaskManager.load(v.getId());
		builder.setTitle(task.getTitle());
		((TextView) viewTaskLayout.findViewById(R.id.taskDetailDesc))
				.setText(task.getDescription());

		builder.setView(viewTaskLayout);

		builder.setNegativeButton("Close", null);
		builder.setPositiveButton("Edit",
				new onClickEditButtonListener(v.getId(), viewTaskLayout));

		AlertDialog dialog = builder.create();

		dialog.show();

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener {

		private LinearLayout stickyLayout;
		private int taskId;

		public onClickEditButtonListener(int taskId, LinearLayout stickyLayout) {
			this.stickyLayout = stickyLayout;
			this.taskId = taskId;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			// edit task dialog show
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			LinearLayout editTaskLayout = (LinearLayout) inflater.inflate(
					R.layout.sticky_edit_layout, null);
			builder.setView(editTaskLayout);
			builder.setPositiveButton("Save", new TaskEditListener(taskId,
					stickyLayout));
			builder.setNegativeButton("Cancel", null);

			builder.setTitle("Edit Task");
			
			//pre-populate with value
			Task task = TaskManager.load(taskId);
			((EditText)editTaskLayout.findViewById(R.id.taskEditTitle)).setText(task.getTitle());
			((EditText)editTaskLayout.findViewById(R.id.taskEditDesc)).setText(task.getDescription());
			

			AlertDialog dialog = builder.create();

			dialog.show();

		}

	}

}