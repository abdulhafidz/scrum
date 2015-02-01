package com.hafidz.stylo;

import com.hafidz.stylo.model.Member;
import com.hafidz.stylo.model.MemberManager;
import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	//
	// case MotionEvent.ACTION_DOWN:
	//
	// System.out.println("* * * * * start touch member");
	//
	// // shadow
	// View.DragShadowBuilder shadow = new DragShadowBuilder(v);
	//
	// // hide ori post it
	// // v.setVisibility(View.INVISIBLE);
	//
	// // start drag
	// v.startDrag(null, shadow, v, 0);
	// break;
	//
	// }
	//
	// return true;
	// }

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

	@Override
	public void onClick(View view) {

		GridLayout memberSticker = (GridLayout) view;

		String memberId = ((TextView) memberSticker
				.findViewById(R.id.memberName)).getText().toString();

		Member member = MemberManager.load(memberId);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(member.getName());
		builder.setMessage(member.getEmail());

		builder.setNegativeButton("Close", null);
		builder.setPositiveButton("Edit", new onClickEditButtonListener(
				memberId));

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private class onClickEditButtonListener implements
			android.content.DialogInterface.OnClickListener {

		private String memberName;

		public onClickEditButtonListener(String memberName) {
			this.memberName = memberName;
		}

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			showEditDialog(context, memberName, false);

		}

	}

	public static void showEditDialog(Context context, String memberName,
			boolean firstTime) {
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
		Member member = MemberManager.load(memberName);
		((EditText) editMemberLayout.findViewById(R.id.memberEditName))
				.setText(member.getName());
		((EditText) editMemberLayout.findViewById(R.id.memberEditEmail))
				.setText(member.getEmail());

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
	}
}