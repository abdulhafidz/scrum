package com.hafidz.stylo.async;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.MemberManager;
import com.parse.ParseException;

public class LoadMemberAsyncTask extends AsyncTask<Object, Void, Member> {

	private AlertDialog editDialog;
	private Context context;

	public LoadMemberAsyncTask(Context context, AlertDialog editDialog) {
		this.editDialog = editDialog;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		Util.startLoading();
	}

	@Override
	protected Member doInBackground(Object... args) {
		Member member = null;

		try {
			member = MemberManager.load((Context) args[1], (String) args[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return member;
	}

	@Override
	protected void onPostExecute(Member member) {
		Util.stopLoading();

		if (member != null) {

			// pre-populate with value
			((EditText) editDialog.findViewById(R.id.memberEditName))
					.setText(member.getName());
			((EditText) editDialog.findViewById(R.id.memberEditEmail))
					.setText(member.getEmail());

			editDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
			((EditText) editDialog.findViewById(R.id.memberEditName))
					.setEnabled(true);
			((EditText) editDialog.findViewById(R.id.memberEditEmail))
					.setEnabled(true);

		} else {
			editDialog.dismiss();
			Util.showError(context, "Member not found on the server.");
			Util.reloadStickers();
		}

	}
}