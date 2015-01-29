package com.hafidz.stylo;

import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hafidz.stylo.model.Task;
import com.hafidz.stylo.model.TaskManager;

public class WhiteBoardListener implements OnTouchListener, OnDragListener,
		OnLongClickListener {


	// percentage offset
	public static int STICKY_X_OFFSET = 8;
	public static int STICKY_Y_OFFSET = 150;

	public static int MEMBER_Y_OFFSET = 38;

	private float wbTouchX;
	private float wbTouchY;

	private Context context;
	// private Map<String, TextView> members;
	private Map<String, GridLayout> members;

	public WhiteBoardListener(Context context, Map<String, GridLayout> members) {
		this.context = context;
		this.members = members;

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// just to get coordinate of touch
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			wbTouchX = event.getX();
			wbTouchY = event.getY();

		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {

		// create member
		if (wbTouchX <= toPixelsWidth(10)) {

			System.out.println("* * * * * long click create member ");

			// //////////
			RelativeLayout whiteBoardLayout = (RelativeLayout) v;

			GridLayout memberLayout = (GridLayout) LayoutInflater.from(context)
					.inflate(R.layout.member_layout, null);

			String memberName = "Bot#" + members.size();
			TextView memberNameView = (TextView) memberLayout
					.findViewById(R.id.memberName);
			memberNameView.setText(memberName);

			// memberLayout.setHeight(toPixelsHeight(10));
			// memberLayout.setWidth(toPixelsWidth(7));

			LayoutParams memberLayoutParams = new LayoutParams(
					toPixelsWidth(7), 75);
			memberLayout.setLayoutParams(memberLayoutParams);

			memberLayout.setX(toPixelsWidth(2));
			memberLayout.setY(wbTouchY - MEMBER_Y_OFFSET);

			MemberListener memberListener = new MemberListener();
			memberLayout.setOnDragListener(memberListener);
			memberLayout.setOnLongClickListener(memberListener);

			whiteBoardLayout.addView(memberLayout);

			// add to the global list of members
			members.put(memberName, memberLayout);

			return true;
		}

		// new small sticky layout
		LinearLayout stickyLayout = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.sticky_layout_small, null);
		stickyLayout.setX(wbTouchX - toPixelsWidth(STICKY_X_OFFSET));
		stickyLayout.setY(wbTouchY - STICKY_Y_OFFSET);
		int stickyLayoutPadding = toPixelsWidth(2);

		stickyLayout.setPadding(stickyLayoutPadding, stickyLayoutPadding,
				stickyLayoutPadding, stickyLayoutPadding);

		// size
		RelativeLayout.LayoutParams stickyLayoutParams = new RelativeLayout.LayoutParams(
				toPixelsWidth(17), 300);
		stickyLayout.setLayoutParams(stickyLayoutParams);

		// ////////////////////////////////////////

		// add to whiteboard
		RelativeLayout whiteBoard = (RelativeLayout) v;
		whiteBoard.addView(stickyLayout);

		// add listeners
		StickyListener stickyListener = new StickyListener(context);
		// stickyLayout.setOnTouchListener(stickyListener);
		stickyLayout.setOnDragListener(stickyListener);
		stickyLayout.setOnLongClickListener(stickyListener);
		stickyLayout.setOnClickListener(stickyListener);

		// // TODO : drag terus!!!
		// View.DragShadowBuilder shadow = new DragShadowBuilder(stickyLayout);
		// stickyLayout.startDrag(null, shadow, stickyLayout, 0);
		// //stickyLayout.setVisibility(View.INVISIBLE);

		// update manager (for width we use percentage and for y we use fix
		// pixels because whiteboard height is fixed)
		Task task = new Task(stickyLayout.getId(), Util.toPercentageWidth(
				context, stickyLayout.getX()), stickyLayout.getY());
		TaskManager.add(task);

		return true;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {

		switch (event.getAction()) {
		case DragEvent.ACTION_DROP:

			/* task dropped */
			if (event.getLocalState() instanceof LinearLayout) {

				LinearLayout task = (LinearLayout) event.getLocalState();
				float newX = event.getX() - toPixelsWidth(STICKY_X_OFFSET);
				float newY = event.getY() - STICKY_Y_OFFSET;

				// update manager
				TaskManager.obtainLock(task.getId());
				TaskManager.moved(task.getId(),
						Util.toPercentageWidth(context, newX), newY);
				TaskManager.releaseLock(task.getId());

				System.out.println("* * * * * drag dropped on whiteboard : "
						+ task.getId());

				// set post-it to new position
				task.setX(event.getX() - toPixelsWidth(STICKY_X_OFFSET));
				task.setY(event.getY() - STICKY_Y_OFFSET);

				// bring to front
				task.bringToFront();

				// and make it visible
				// postIt.setVisibility(View.VISIBLE);
			}
			/* member dropped */
			else {

				GridLayout memberText = (GridLayout) event.getLocalState();

				if (event.getX() <= toPixelsWidth(10) && event.getY() > 52) {

					// new member location
					memberText.setX(toPixelsWidth(2));
					memberText.setY(event.getY() - MEMBER_Y_OFFSET);

					memberText.bringToFront();

					memberText.setVisibility(View.VISIBLE);

					System.out
							.println("* * * * * non sticky dropped on member section");

					return true;
				} else {
					System.out
							.println("* * * * * non sticky dropped on whiteboard");

					memberText.setVisibility(View.VISIBLE);

					return false;
				}
			}

			break;

		case DragEvent.ACTION_DRAG_ENDED:
			if (event.getLocalState() instanceof LinearLayout) {
				LinearLayout postIt = (LinearLayout) event.getLocalState();
				postIt.setVisibility(View.VISIBLE);
			} else {
				System.out.println("* * * * * non sticky ended on whiteboard");

				return false;
			}
			break;

		}

		return true;

	}

	int toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(context, percentage);
	}

}