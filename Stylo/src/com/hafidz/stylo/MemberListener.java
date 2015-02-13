package com.hafidz.stylo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.hafidz.stylo.async.LoadMemberAsyncTask;
import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.MemberManager;
import com.parse.ParseException;

public class MemberListener implements OnDragListener, OnLongClickListener,
		OnClickListener {

	private Context context;

	public MemberListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_ENDED:
			Util.hideGarbage();
			v.setVisibility(View.VISIBLE);
			return true;

		case DragEvent.ACTION_DRAG_LOCATION:
			return true;
		}

		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		System.out.println("* * * * * start touch member");

		// shadow
		View.DragShadowBuilder shadow = new DragShadowBuilder(v);

		// hide ori post it
		v.setVisibility(View.INVISIBLE);

		// start drag
		v.startDrag(null, shadow, v, 0);

		Util.showGarbage();

		return true;
	}

	private AlertDialog viewDialog;

	@Override
	public void onClick(View view) {

		GridLayout memberSticker = (GridLayout) view;

		String memberId = ((TextView) memberSticker
				.findViewById(R.id.memberName)).getText().toString();

		// ////////////////////////////
		AsyncTask<String, Void, Member> bgTask = new AsyncTask<String, Void, Member>() {
			@Override
			protected void onPreExecute() {
				Util.startLoading();
			}

			@Override
			protected Member doInBackground(String... args) {
				Member member = null;

				try {
					member = MemberManager.load(context, args[0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				return member;
			}

			@Override
			protected void onPostExecute(Member member) {
				Util.stopLoading();

				if (member != null) {
					viewDialog.setTitle(member.getName());
					viewDialog.setMessage(member.getEmail());
					viewDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(
							true);
				} else {
					viewDialog.dismiss();
					Util.showError(context, "Member cannot be found.");
				}
			}
		};

		bgTask.execute(memberId);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("...");
		builder.setMessage("...");

		builder.setNegativeButton("Close", null);
		builder.setPositiveButton("Edit", new onClickEditButtonListener(
				memberId));

		viewDialog = builder.create();
		viewDialog.show();

		viewDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener {

		private String memberName;

		public onClickEditButtonListener(String memberName) {
			this.memberName = memberName;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			try {
				showEditDialog(context, memberName, false);
			} catch (ParseException e) {
				Util.showError(context, "Problem retrieving from server.");
			}

		}

	}

	public static void showEditDialog(Context context, String memberName,
			boolean firstTime) throws ParseException {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout editMemberLayout = (LinearLayout) inflater.inflate(
				R.layout.member_edit_layout, null);

		builder.setView(editMemberLayout);

		builder.setTitle("Edit Member");
		builder.setPositiveButton("Save", null);
		builder.setNegativeButton("Cancel", new MemberEditListener(memberName,
				null, firstTime, context));

		// pre-populate with value
		if (!firstTime) {
			((EditText) editMemberLayout.findViewById(R.id.memberEditName))
					.setText("...");
			((EditText) editMemberLayout.findViewById(R.id.memberEditEmail))
					.setText("...");
		}

		if (!firstTime) {
			((EditText) editMemberLayout.findViewById(R.id.memberEditName))
					.setEnabled(false);
			((EditText) editMemberLayout.findViewById(R.id.memberEditEmail))
					.setEnabled(false);
		}

		// ((EditText) editMemberLayout.findViewById(R.id.memberEditEmail))
		// .setOnFocusChangeListener(new MemberListener(context));

		if (firstTime)
			((EditText) editMemberLayout.findViewById(R.id.memberEditName))
					.setText("");

		AlertDialog dialog = builder.create();

		dialog.show();

		// overide save listener because we dont want to auto dismiss dialog
		// after save
		Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		theButton.setOnClickListener(new MemberEditListener(memberName, dialog,
				firstTime, context));
		if (!firstTime) {
			theButton.setEnabled(false);
		}

		// add focus listener to validate name
		((EditText) editMemberLayout.findViewById(R.id.memberEditName))
				.setOnFocusChangeListener(new MemberEditListener(memberName,
						dialog, firstTime, context));

		// ///////////////////////////////////
		if (!firstTime) {
			AsyncTask<Object, Void, Member> bgTask = new LoadMemberAsyncTask(
					context, dialog);
			bgTask.execute(memberName, context);
		}
		// //////////////////////////////////
	}

}