package com.hafidz.stylo.listener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.hafidz.stylo.R;
import com.hafidz.stylo.R.id;
import com.hafidz.stylo.async.DeleteMemberAsyncTask;
import com.hafidz.stylo.manager.MemberManager;
import com.hafidz.stylo.util.Util;
import com.parse.ParseException;

/**
 * @author hafidz
 * 
 */
public class MemberEditListener implements OnClickListener,
		android.content.DialogInterface.OnClickListener, OnFocusChangeListener {

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

		// convert to upper case supaya tak pening kepala
		editName.setText(editName.getText().toString().trim().toUpperCase());

		// ////////////////
		AsyncTask<String, Void, ParseException> bgTask = new AsyncTask<String, Void, ParseException>() {
			@Override
			protected void onPreExecute() {
				Util.startLoading();
			}

			@Override
			protected ParseException doInBackground(String... args) {
				try {
					MemberManager.obtainLock(memberName);
					MemberManager.updateMember(context, memberName, args[0],
							args[1]);
					MemberManager.releaseLock(memberName);
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
					Util.showSuccess(context, "Member updated to the server.");
				}
				// got error
				else {
					Util.showError(context,
							"Problem updating member to the server.");
					Util.reloadStickers();
				}
			}
		};
		bgTask.execute(editName.getText().toString(), editEmail.getText()
				.toString());

		// update UI
		GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
				.findViewWithTag(memberName);
		TextView memberName = (TextView) memberSticker
				.findViewById(R.id.memberName);
		memberName.setText(editName.getText());
		memberSticker.setTag(editName.getText().toString());

		// show back removed UI because......
		memberSticker.setVisibility(View.VISIBLE);

		editDialog.dismiss();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		// cancel button clicked
		if (Dialog.BUTTON_NEGATIVE == which) {

			// if first time and cancel, we delete back the pre created member
			if (firstTime) {

				DeleteMemberAsyncTask bgTask = new DeleteMemberAsyncTask(
						context, memberName);
				bgTask.execute();

			}
		}

	}

	private Button saveButton;
	private EditText nameEditText;

	@Override
	public void onFocusChange(View view, boolean hasFocus) {

		saveButton = editDialog.getButton(Dialog.BUTTON_POSITIVE);
		nameEditText = (EditText) editDialog.findViewById(R.id.memberEditName);

		// once focus always disabled
		if (!nameEditText.getText().toString().equals(memberName))
			saveButton.setEnabled(false);

		// lost focus, we validate name
		if (!hasFocus) {
			nameEditText = (EditText) view;
			String editName = null;
			if ((nameEditText.getText() != null))
				editName = nameEditText.getText().toString();

			if (!memberName.equals(editName)) {

				AsyncTask<String, Void, Boolean> bgTask = new AsyncTask<String, Void, Boolean>() {
					@Override
					protected void onPreExecute() {
						Util.startLoading();
					}

					@Override
					protected Boolean doInBackground(String... args) {
						try {
							if (MemberManager.getAll(context).containsKey(
									args[0])) {
								return false;
							} else
								return true;
						} catch (ParseException e) {
							e.printStackTrace();
							return false;
						}

					}

					@Override
					protected void onPostExecute(Boolean valid) {
						Util.stopLoading();

						if (valid) {
							// enable save button
							saveButton.setEnabled(true);
						} else {
							nameEditText
									.setError("This name is taken. Please try another.");

						}

					}
				};

				bgTask.execute(editName);

			}
		}

	}
}
