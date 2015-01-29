/**
 * 
 */
package com.hafidz.stylo;

import com.hafidz.stylo.model.TaskManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author hafidz
 * 
 */
public class TaskEditListener implements OnClickListener {

	private LinearLayout stickyLayout;
	private int taskId;

	public TaskEditListener(int taskId, LinearLayout stickyLayout) {
		this.stickyLayout = stickyLayout;
		this.taskId = taskId;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		switch (which) {
		// save
		case DialogInterface.BUTTON_POSITIVE:

			// TODO: validation

			Dialog editDialog = (Dialog) dialog;
			EditText editTitle = (EditText) editDialog
					.findViewById(R.id.taskEditTitle);
			EditText editDesc = (EditText) editDialog
					.findViewById(R.id.taskEditDesc);

			// TextView taskDetailTitle = (TextView) stickyLayout
			// .findViewById(R.id.taskDetailTitle);
			// taskDetailTitle.setText(editTitle.getText());

			TextView taskDetailDesc = (TextView) stickyLayout
					.findViewById(R.id.taskDetailDesc);

			taskDetailDesc.setText(editDesc.getText());

			// update manager
			TaskManager.obtainLock(taskId);
			TaskManager.updateTask(taskId, editTitle.getText().toString(),
					editDesc.getText().toString());
			TaskManager.releaseLock(taskId);

			break;

		default:
			break;
		}

	}
}
