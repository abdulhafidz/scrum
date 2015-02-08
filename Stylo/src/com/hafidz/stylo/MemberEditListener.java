package com.hafidz.stylo;

import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.TaskManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author hafidz
 * 
 */
public class MemberEditListener implements OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private AlertDialog editDialog;
	private String memberName;
	private boolean firstTime;
	private Context context;

	public MemberEditListener(String memberName, AlertDialog dialog,
			boolean firstTime, Context context) {
		this.editDialog = dialog;
		this.memberName = memberName;
		this.firstTime = firstTime;
		this.context = context;
	}

	@Override
	public void onClick(View arg0) {

		// save button clicked

		EditText editName = (EditText) editDialog
				.findViewById(R.id.memberEditName);
		EditText editEmail = (EditText) editDialog
				.findViewById(R.id.memberEditEmail);

		// validation
		if (editName.getText() == null
				|| editName.getText().toString().trim().isEmpty()) {
			editName.setError("Please insert a name.");
			return;
		}
		if (!memberName.equals(editName.getText().toString())) {
			if (MemberManager.getAll(context).containsKey(
					editName.getText().toString())) {
				editName.setError("Name is already taken. Please try other name.");
				return;
			}
		}

		// update manager
		MemberManager.obtainLock(memberName);
		MemberManager.updateMember(context, memberName, editName.getText()
				.toString(), editEmail.getText().toString());
		MemberManager.releaseLock(memberName);

		editDialog.dismiss();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		System.out.println("11111111111111111111111111111111111111111111111");
		// cancel button clicked
		if (Dialog.BUTTON_NEGATIVE == which) {
			System.out.println("222222222222222222222222222222222222222222222");
			// if first time and cancel, we delete back the pre created member
			if (firstTime) {
				System.out.println("3333333333333333333333333333333333333333");
				MemberManager.obtainLock(memberName);
				MemberManager.remove(context, memberName);
				MemberManager.releaseLock(memberName);

			}
		}

	}
}