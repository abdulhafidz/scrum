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

	// public static int STICKY_X_OFFSET = 70;
	// public static int STICKY_Y_OFFSET = 100;

	// percentage offset
	public static int STICKY_X_OFFSET = 9;
	public static int STICKY_Y_OFFSET = 19;


	public static int MEMBER_Y_OFFSET = 5;

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
					toPixelsWidth(7), toPixelsHeight(10));
			memberLayout.setLayoutParams(memberLayoutParams);

			memberLayout.setX(toPixelsWidth(2));
			memberLayout.setY(wbTouchY - toPixelsHeight(MEMBER_Y_OFFSET));

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
		stickyLayout.setY(wbTouchY - toPixelsHeight(STICKY_Y_OFFSET));
		int stickyLayoutPadding = toPixelsWidth(2);
//		if(stickyLayoutPadding > 25)
//			stickyLayoutPadding = 25;
		stickyLayout.setPadding(stickyLayoutPadding, stickyLayoutPadding, stickyLayoutPadding, stickyLayoutPadding);

		// size
		RelativeLayout.LayoutParams stickyLayoutParams = new RelativeLayout.LayoutParams(
				toPixelsWidth(17), toPixelsHeight(37));
		stickyLayout.setLayoutParams(stickyLayoutParams);

		// ////////////////////////////////////////

		// // sticky layout
		// RelativeLayout stickyLayout = new RelativeLayout(context);
		// stickyLayout.setId(Util.generateViewId());
		// stickyLayout.setBackgroundResource(R.drawable.post_it);
		// // stickyLayout.setX(wbTouchX - toPixels(STICKY_X_OFFSET));
		// // stickyLayout.setY(wbTouchY - toPixels(STICKY_Y_OFFSET));
		// stickyLayout.setX(wbTouchX - toPixelsWidth(STICKY_X_OFFSET));
		// stickyLayout.setY(wbTouchY - toPixelsHeight(STICKY_Y_OFFSET));
		//
		// // size
		// RelativeLayout.LayoutParams stickyLayoutParams = new
		// RelativeLayout.LayoutParams(
		// toPixelsWidth(15), toPixelsHeight(30));
		// stickyLayout.setLayoutParams(stickyLayoutParams);
		//
		// // sticky title
		// TextView stickyTitle = new TextView(context);
		// stickyTitle.setId(Util.generateViewId());
		// RelativeLayout.LayoutParams stickyTitleLayoutParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// stickyTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
		// RelativeLayout.TRUE);
		// stickyTitleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
		// RelativeLayout.TRUE);
		// stickyTitleLayoutParams.setMargins(toPixels(20), toPixels(20), 0, 0);
		// stickyTitle.setLayoutParams(stickyTitleLayoutParams);
		// stickyTitle.setEllipsize(TextUtils.TruncateAt.END);
		// stickyTitle.setHeight(toPixels(25));
		// stickyTitle.setWidth(toPixels(115));
		// stickyTitle.setMaxLines(1);
		// stickyTitle.setSingleLine(true);
		// stickyTitle.setTextColor(Color.BLACK);
		// stickyTitle.setTextSize(17);
		// stickyTitle.setTypeface(Typeface.DEFAULT_BOLD);
		// String title = "Title Lorem Ipsum dolor sit amet.";
		// stickyTitle.setText(Html.fromHtml("<u>" + title + "</u>"));
		// stickyLayout.addView(stickyTitle);
		//
		// // sticky description
		// TextView stickyDesc = new TextView(context);
		// stickyDesc.setId(Util.generateViewId());
		// RelativeLayout.LayoutParams stickyDescLayoutParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// stickyDescLayoutParams.addRule(RelativeLayout.ALIGN_LEFT,
		// stickyTitle.getId());
		// stickyDescLayoutParams.addRule(RelativeLayout.BELOW,
		// stickyTitle.getId());
		// stickyDesc.setLayoutParams(stickyDescLayoutParams);
		// stickyDesc.setHeight(toPixels(70));
		// stickyDesc.setWidth(toPixels(115));
		// // stickyDesc
		// //
		// .setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla facilisis felis suscipit quam mattis sodales. Aliquam placerat vehicula feugiat. Mauris in tellus id nunc sodales.");
		// stickyDesc.setTextColor(Color.BLACK);
		// stickyLayout.addView(stickyDesc);
		//
		// // sticky owner
		// TextView stickyOwner = new TextView(context);
		// stickyOwner.setId(Util.generateViewId());
		// RelativeLayout.LayoutParams stickyOwnerLayoutParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// stickyOwnerLayoutParams.setMargins(0, toPixels(5), 0, 0);
		// stickyOwnerLayoutParams.addRule(RelativeLayout.ALIGN_LEFT,
		// stickyDesc.getId());
		// stickyOwnerLayoutParams.addRule(RelativeLayout.BELOW,
		// stickyDesc.getId());
		// stickyOwner.setLayoutParams(stickyOwnerLayoutParams);
		// // stickyOwner.setText("");
		// stickyOwner.setTextColor(Color.WHITE);
		// stickyOwner.setTextSize(16);
		// stickyOwner.setBackgroundColor(Color.BLUE);
		// stickyOwner.setTypeface(Typeface.DEFAULT_BOLD);
		// stickyLayout.addView(stickyOwner);
		//
		// // sticky points
		// TextView stickyPoints = new TextView(context);
		// RelativeLayout.LayoutParams stickyPointsLayoutParams = new
		// RelativeLayout.LayoutParams(
		// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// stickyPointsLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE,
		// stickyOwner.getId());
		// stickyPointsLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,
		// stickyOwner.getId());
		// stickyPointsLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT,
		// stickyDesc.getId());
		// stickyPoints.setLayoutParams(stickyPointsLayoutParams);
		// // stickyPoints.setText("13");
		// stickyPoints.setTextColor(Color.RED);
		// stickyPoints.setTextSize(20);
		// stickyPoints.setTypeface(Typeface.DEFAULT_BOLD);
		// stickyLayout.addView(stickyPoints);

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

		// update manager
		Task task = new Task(stickyLayout.getId(), Util.toDP(context,
				stickyLayout.getX()), Util.toDP(context, stickyLayout.getY()));
		TaskManager.add(task);

		return true;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {

		switch (event.getAction()) {
		case DragEvent.ACTION_DROP:

			/* task dropped */
			if (event.getLocalState() instanceof LinearLayout) {

				LinearLayout postIt = (LinearLayout) event.getLocalState();
				float newX = event.getX() - toPixelsWidth(STICKY_X_OFFSET);
				float newY = event.getY() - toPixelsHeight(STICKY_Y_OFFSET);

				// update manager
				TaskManager.moved(postIt.getId(), Util.toDP(context, newX),
						Util.toDP(context, newY));

				System.out.println("* * * * * drag dropped on whiteboard : "
						+ postIt.getId());

				// set post-it to new position
				postIt.setX(event.getX() - toPixelsWidth(STICKY_X_OFFSET));
				postIt.setY(event.getY() - toPixelsHeight(STICKY_Y_OFFSET));

				// bring to front
				postIt.bringToFront();

				// and make it visible
				// postIt.setVisibility(View.VISIBLE);
			}
			/* member dropped */
			else {

				GridLayout memberText = (GridLayout) event.getLocalState();

				if (event.getX() <= toPixelsWidth(10)
						&& event.getY() > toPixelsHeight(13)) {

					// new member location
					memberText.setX(toPixelsWidth(2));
					memberText.setY(event.getY()
							- toPixelsHeight(MEMBER_Y_OFFSET));

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

	int toPixels(float dp) {
		return Util.toPixels(context, dp);
	}

	int toPixelsWidth(int percentage) {
		return Util.toPixelsWidth(context, percentage);
	}

	int toPixelsHeight(int percentage) {
		return Util.toPixelsHeight(context, percentage);
	}

	// int toPixelsWidth(Float percentage) {
	//
	// return Util.toPixelsWidth(context, Math.round(percentage));
	// }
	//
	// int toPixelsHeight(Float percentage) {
	// return Util.toPixelsHeight(context, Math.round(percentage));
	// }

}