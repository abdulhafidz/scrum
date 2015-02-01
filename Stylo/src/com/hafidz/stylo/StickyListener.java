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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hafidz.stylo.model.MemberManager;
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

				Util.hideGarbage();

				RelativeLayout task = (RelativeLayout) v;
				GridLayout memberLayout = (GridLayout) event.getLocalState();
				TextView memberNameTV = (TextView) memberLayout
						.findViewById(R.id.memberName);

				String newOwner = memberNameTV.getText().toString();

				// update task manager
				TaskManager.obtainLock(task.getId());
				TaskManager.assignOwner(task.getId(),
						MemberManager.load(newOwner));
				TaskManager.releaseLock(task.getId());

				System.out.println("* * * * * member " + newOwner
						+ " dropped to task");

				// sticky note
				TextView ownerText = (TextView) task
						.findViewById(R.id.smallTaskOwner);
				String oriOwner = ownerText.getText().toString();
				ownerText.setText(newOwner);

				// hide member
				memberNameTV.setVisibility(View.GONE);

				// if replace existing owner, show replaced owner
				if (oriOwner != null && !oriOwner.trim().isEmpty()
						&& !oriOwner.equals(newOwner)) {
					// put back the original member to the member pool
					GridLayout memberSticker = MemberManager.load(oriOwner)
							.getMemberSticker();
					memberSticker.setVisibility(View.VISIBLE);
					memberSticker.findViewById(R.id.memberName).setVisibility(
							View.VISIBLE);

					// position put to old position of new owner
					memberSticker.setY(memberLayout.getY());

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

		Task task = TaskManager.load(v.getId());

		System.out
				.println("task.getId() = = = = = = = = = = = = = = = = = = = = = = = = = = ="
						+ task.getId());

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
		((TextView) viewTaskLayout.findViewById(R.id.smallTaskOwner))
				.setText(task.getOwner().getName());

		builder.setView(viewTaskLayout);

		builder.setNegativeButton("Close", null);
		builder.setPositiveButton("Edit",
				new onClickEditButtonListener(task.getId(), viewTaskLayout));

		AlertDialog dialog = builder.create();

		dialog.show();

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener {

		private ScrollView stickyLayout;
		private int taskId;

		public onClickEditButtonListener(int taskId, ScrollView stickyLayout) {
			this.stickyLayout = stickyLayout;
			this.taskId = taskId;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			showEditDialog(stickyLayout, taskId);

		}

	}

	private void showEditDialog(ScrollView stickyLayout, int taskId) {
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
		Task task = TaskManager.load(taskId);
		((EditText) editTaskLayout.findViewById(R.id.taskEditTitle))
				.setText(task.getTitle());
		((EditText) editTaskLayout.findViewById(R.id.taskEditDesc))
				.setText(task.getDescription());

		AlertDialog dialog = builder.create();

		dialog.show();

		// overide save listener because we dont want to auto dismiss dialog
		// after save
		Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		theButton.setOnClickListener(new TaskEditListener(taskId, stickyLayout,
				dialog));
	}

}