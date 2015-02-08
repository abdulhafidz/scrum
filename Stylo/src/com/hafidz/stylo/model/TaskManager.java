package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hafidz.stylo.R;
import com.hafidz.stylo.StickyListener;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.WhiteBoardListener;
import com.hafidz.stylo.WhiteBoardScroller;
import com.hafidz.stylo.sql.TaskDB;

public class TaskManager {
	// public static Map<String, Task> allTasks = new HashMap<String, Task>();

	public static Map<String, Task> getAll(Context context) {
		return getAllFromDB(context);
	}

	public static void add(Context context, Task task) {
		// TODO : server side add task
		// allTasks.put(String.valueOf(task.getId()), task);

		// update status
		float x = task.getPosX();
		if (x >= (WhiteBoardScroller.LINE_IN_PROGRESS - WhiteBoardListener.STICKY_X_OFFSET)
				&& x < (WhiteBoardScroller.LINE_DONE - WhiteBoardListener.STICKY_X_OFFSET)) {
			task.setStatus(Task.STATUS_IN_PROGRESS);

		} else if (x >= WhiteBoardScroller.LINE_DONE
				- WhiteBoardListener.STICKY_X_OFFSET) {
			task.setStatus(Task.STATUS_IN_PROGRESS);

		} else {
			task.setStatus(Task.STATUS_TODO);

		}

		saveToDB(context, task);
	}

	public static void assignOwner(Context context, String taskId, Member owner) {
		// TODO : server side assign owner
		// allTasks.get(taskId).setOwner(owner);

		updateToDB(context, taskId, owner.getName());
	}

	public static void updateStickerOwner(RelativeLayout sticker,
			String newOwner, GridLayout memberSticker) {
		// sticky note
		TextView ownerText = (TextView) sticker
				.findViewById(R.id.smallTaskOwner);
		String oriOwner = ownerText.getText().toString();
		ownerText.setText(newOwner);

		// hide member
		if (memberSticker != null) {
			memberSticker.setVisibility(View.GONE);

			// if replace existing owner, show replaced owner
			if (oriOwner != null && !oriOwner.trim().isEmpty()
					&& !oriOwner.equals(newOwner)) {
				// put back the original member to the member pool
				// GridLayout oriSticker = MemberManager.load(oriOwner)
				// .getMemberSticker();
				GridLayout oriSticker = (GridLayout) Util.whiteboardLayout
						.findViewWithTag(oriOwner);
				oriSticker.setVisibility(View.VISIBLE);
				// oriSticker.findViewById(R.id.memberName).setVisibility(
				// View.VISIBLE);

				// position put to old position of new owner
				oriSticker.setY(memberSticker.getY());

			}
		}
	}

	public static boolean obtainLock(String taskId) {
		// TODO : implement obtain lock
		return true;
	}

	public static void releaseLock(String taskId) {
	}

	public static void moved(Context context, String taskId, float x, float y) {
		// TODO : server side

		// update status
		Task task = load(context, taskId);
		if (x >= (WhiteBoardScroller.LINE_IN_PROGRESS - WhiteBoardListener.STICKY_X_OFFSET)
				&& x < (WhiteBoardScroller.LINE_DONE - WhiteBoardListener.STICKY_X_OFFSET)) {
			task.setStatus(Task.STATUS_IN_PROGRESS);

			Toast.makeText(context, "Task in progress.", Toast.LENGTH_SHORT)
					.show();

		} else if (x >= WhiteBoardScroller.LINE_DONE
				- WhiteBoardListener.STICKY_X_OFFSET) {
			task.setStatus(Task.STATUS_IN_PROGRESS);

			Toast.makeText(context, "Well done!", Toast.LENGTH_SHORT).show();

		} else {
			task.setStatus(Task.STATUS_TODO);

			Toast.makeText(context, "Task in TO-DO.", Toast.LENGTH_SHORT)
					.show();
		}

		// allTasks.get(taskId).setPos(x, y);
		updateToDB(context, taskId, x, y, task.getStatus());
	}

	public static void updateTask(Context context, String taskId, String title,
			String desc, RelativeLayout sticker) {
		// Task task = allTasks.get(taskId);
		// task.setTitle(title);
		// task.setDescription(desc);

		// update ui (small task)
		// RelativeLayout smallTask = task.getSmallTask();
		RelativeLayout smallTask = sticker;
		updateSticker(sticker, title, desc);

		// update ui (big view task)

		updateToDB(context, taskId, title, desc);

	}

	public static void updateSticker(RelativeLayout sticker, String title,
			String desc) {
		// ((TextView) smallTask.findViewById(R.id.smallTaskTitle)).setText(Html
		// .fromHtml("<u>" + title + "</u>"));
		((TextView) sticker.findViewById(R.id.smallTaskTitle)).setText(title);
		((TextView) sticker.findViewById(R.id.smallTaskDesc)).setText(desc);

	}

	public static Task load(Context context, String taskId) {
		// return allTasks.get(taskId);
		return loadFromDB(context, taskId);

	}

	public static void remove(Context context, String taskId,
			RelativeLayout sticker) {
		Task task = load(context, taskId);

		String id = task.getId();

		freeOwner(task, sticker);

		// RelativeLayout sticker = task.getSmallTask();

		// sticker.setVisibility(View.GONE);
		Util.whiteboardLayout.removeView(sticker);

		// allTasks.remove(taskId);

		deleteFromDB(context, id);

		Toast.makeText(context, "Task removed.", Toast.LENGTH_SHORT).show();
	}

	public static void freeOwner(Task task, RelativeLayout taskSticker) {

		// RelativeLayout taskSticker = task.getSmallTask();
		TextView memberName = (TextView) taskSticker
				.findViewById(R.id.smallTaskOwner);
		memberName.setText(null);

		Member member = task.getOwner();
		if (member != null) {
			// GridLayout memberSticker = member.getMemberSticker();

			GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
					.findViewWithTag(memberName);
			memberSticker.setVisibility(View.VISIBLE);
			memberSticker.findViewById(R.id.memberName).setVisibility(
					View.VISIBLE);

			task.setOwner(null);
		}

	}

	private static void saveToDB(Context context, Task task) {

		TaskDB taskDB = new TaskDB(context);

		SQLiteDatabase db = taskDB.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("TASK_ID", task.getId());
		values.put("TITLE", task.getTitle());
		values.put("DESC", task.getDescription());
		if (task.getOwner() != null)
			values.put("OWNER", task.getOwner().getName());
		values.put("POS_X", task.getPosX());
		values.put("POS_Y", task.getPosY());
		values.put("STATUS", task.getStatus());

		long newRowId;
		newRowId = db.insert(TaskDB.TABLE_NAME, null, values);

	}

	// private static void updateToDB(Context context, Task task) {
	//
	// TaskDB taskDB = new TaskDB(context);
	//
	// SQLiteDatabase db = taskDB.getWritableDatabase();
	//
	// ContentValues values = new ContentValues();
	// // values.put("TASK_ID", task.getId());
	// values.put("TITLE", task.getTitle());
	// values.put("DESC", task.getDescription());
	// values.put("OWNER", task.getOwner().getName());
	// values.put("POS_X", task.getPosX());
	// values.put("POS_Y", task.getPosY());
	//
	// // Which row to update, based on the ID
	// String selection = "TASK_ID LIKE ?";
	// String[] selectionArgs = { task.getId() };
	//
	// int count = db.update(TaskDB.TABLE_NAME, values, selection,
	// selectionArgs);
	//
	// }

	private static void updateToDB(Context context, String taskId,
			String newOwner) {

		TaskDB taskDB = new TaskDB(context);

		SQLiteDatabase db = taskDB.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("OWNER", newOwner);

		// Which row to update, based on the ID
		String selection = "TASK_ID LIKE ?";
		String[] selectionArgs = { taskId };

		int count = db.update(TaskDB.TABLE_NAME, values, selection,
				selectionArgs);

	}

	private static void updateToDB(Context context, String taskId, float posX,
			float posY, int status) {

		TaskDB taskDB = new TaskDB(context);

		SQLiteDatabase db = taskDB.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("POS_X", posX);
		values.put("POS_Y", posY);
		values.put("STATUS", status);

		// Which row to update, based on the ID
		String selection = "TASK_ID LIKE ?";
		String[] selectionArgs = { taskId };

		int count = db.update(TaskDB.TABLE_NAME, values, selection,
				selectionArgs);

	}

	private static void updateToDB(Context context, String id, String title,
			String desc) {

		TaskDB taskDB = new TaskDB(context);

		SQLiteDatabase db = taskDB.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put("TASK_ID", task.getId());
		values.put("TITLE", title);
		values.put("DESC", desc);

		// Which row to update, based on the ID
		String selection = "TASK_ID LIKE ?";
		String[] selectionArgs = { id };

		int count = db.update(TaskDB.TABLE_NAME, values, selection,
				selectionArgs);

	}

	private static void deleteFromDB(Context context, String taskId) {
		TaskDB taskDB = new TaskDB(context);

		SQLiteDatabase db = taskDB.getWritableDatabase();

		// Define 'where' part of query.
		String selection = "TASK_ID LIKE ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { taskId };
		// Issue SQL statement.
		db.delete(TaskDB.TABLE_NAME, selection, selectionArgs);
	}

	private static Map<String, Task> getAllFromDB(Context context) {

		Map<String, Task> tasks = new HashMap<String, Task>();

		TaskDB taskDB = new TaskDB(context);
		SQLiteDatabase db = taskDB.getReadableDatabase();

		String[] projection = { "TASK_ID", "TITLE", "DESC", "OWNER", "POS_X",
				"POS_Y", "STATUS" };

		Cursor cursor = db.query(TaskDB.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		// Iterate

		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {

			Task task = new Task(cursor.getString(0), cursor.getFloat(4),
					cursor.getFloat(5));
			task.setTitle(cursor.getString(1));
			task.setDescription(cursor.getString(2));
			if (cursor.getString(3) != null) {
				System.out.println("cursor.getString(3) = = = = = = = = = = "
						+ cursor.getString(3));

				task.setOwner(MemberManager.load(context, cursor.getString(3)));
			}
			task.setStatus(cursor.getInt(6));
			tasks.put(task.getId(), task);
			cursor.moveToNext();
		}

		return tasks;

	}

	private static Task loadFromDB(Context context, String taskId) {

		TaskDB taskDB = new TaskDB(context);
		SQLiteDatabase db = taskDB.getReadableDatabase();

		String[] projection = { "TASK_ID", "TITLE", "DESC", "OWNER", "POS_X",
				"POS_Y", "STATUS" };

		Cursor cursor = db.query(TaskDB.TABLE_NAME, // The table to query
				projection, // The columns to return
				"TASK_ID like ?", // The columns for the WHERE clause
				new String[] { taskId }, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		// Iterate

		cursor.moveToFirst();
		Task task = new Task(cursor.getString(0), cursor.getFloat(4),
				cursor.getFloat(5));
		task.setTitle(cursor.getString(1));
		task.setDescription(cursor.getString(2));
		if (cursor.getString(3) != null)
			task.setOwner(MemberManager.load(context, cursor.getString(3)));
		task.setStatus(cursor.getInt(6));
		return task;

	}

	public static RelativeLayout createEmptySticker(Context context, float x,
			float y, String taskId) {
		// new small sticky layout
		RelativeLayout stickyLayout = (RelativeLayout) LayoutInflater.from(
				context).inflate(R.layout.sticky_layout_small, null);
		stickyLayout.setId(Util.generateViewId());
		stickyLayout.setX(x);
		stickyLayout.setY(y);
		// int stickyLayoutPadding = toPixelsWidth(1);
		//
		// stickyLayout.setPadding(stickyLayoutPadding, stickyLayoutPadding,
		// stickyLayoutPadding, stickyLayoutPadding);

		// size
		RelativeLayout.LayoutParams stickyLayoutParams = new RelativeLayout.LayoutParams(
				Util.toPixelsWidth(context, 14), 300);
		stickyLayout.setLayoutParams(stickyLayoutParams);

		// ////////////////////////////////////////

		// add to whiteboard
		RelativeLayout whiteBoard = Util.whiteboardLayout;
		whiteBoard.addView(stickyLayout);

		// add listeners
		StickyListener stickyListener = new StickyListener(context);
		// stickyLayout.setOnTouchListener(stickyListener);
		stickyLayout.setOnDragListener(stickyListener);
		stickyLayout.setOnLongClickListener(stickyListener);
		stickyLayout.setOnClickListener(stickyListener);

		stickyLayout.setTag(taskId);

		return stickyLayout;
	}
}
