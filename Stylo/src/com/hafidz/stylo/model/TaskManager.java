package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hafidz.stylo.R;
import com.hafidz.stylo.TaskStickerListener;
import com.hafidz.stylo.Util;
import com.hafidz.stylo.WhiteBoardListener;
import com.hafidz.stylo.WhiteBoardScroller;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TaskManager {
	// public static Map<String, Task> allTasks = new HashMap<String, Task>();

	public static Map<String, Task> getAll(Context context)
			throws ParseException {
		return getAllFromDB(context);
	}

	public static void add(Context context, Task task) throws ParseException {

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

	public static void assignOwner(Context context, Task task, Member owner)
			throws ParseException {

		updateToDB(context, task, owner.getName());

		Toast.makeText(context, owner.getName() + " assigned to a task.",
				Toast.LENGTH_SHORT).show();
	}

	public static void updateStickerOwner(RelativeLayout sticker,
			String newOwner, GridLayout memberSticker) {
		// sticky note
		TextView ownerText = (TextView) sticker
				.findViewById(R.id.taskDetailOwner);
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

	public static void moved(Context context, Task task, float x, float y)
			throws ParseException {

		// update status
		// Task task = load(context, taskId);
		if (x >= (WhiteBoardScroller.LINE_IN_PROGRESS - WhiteBoardListener.STICKY_X_OFFSET)
				&& x < (WhiteBoardScroller.LINE_DONE - WhiteBoardListener.STICKY_X_OFFSET)) {
			task.setStatus(Task.STATUS_IN_PROGRESS);

			Toast.makeText(context, "Task in progress.", Toast.LENGTH_SHORT)
					.show();

		} else if (x >= WhiteBoardScroller.LINE_DONE
				- WhiteBoardListener.STICKY_X_OFFSET) {
			task.setStatus(Task.STATUS_DONE);

			Toast.makeText(context, "Well done!", Toast.LENGTH_SHORT).show();

		} else {
			task.setStatus(Task.STATUS_TODO);

			Toast.makeText(context, "Task in TO-DO.", Toast.LENGTH_SHORT)
					.show();
		}

		updateToDB(context, task, x, y, task.getStatus());
	}

	public static void updateTask(Context context, Task task, String title,
			String desc, RelativeLayout sticker) throws ParseException {
		// Task task = allTasks.get(taskId);
		// task.setTitle(title);
		// task.setDescription(desc);

		// update ui (small task)
		// RelativeLayout smallTask = task.getSmallTask();
		RelativeLayout smallTask = sticker;
		updateSticker(sticker, title, desc);

		// update ui (big view task)

		updateToDB(context, task, title, desc);

	}

	public static void updateSticker(RelativeLayout sticker, String title,
			String desc) {
		// ((TextView) smallTask.findViewById(R.id.smallTaskTitle)).setText(Html
		// .fromHtml("<u>" + title + "</u>"));
		((TextView) sticker.findViewById(R.id.smallTaskTitle)).setText(title);
		((TextView) sticker.findViewById(R.id.smallTaskDesc)).setText(desc);

	}

	public static Task load(Context context, String taskId)
			throws ParseException {
		// return allTasks.get(taskId);
		return loadFromDB(context, taskId);

	}

	public static void remove(Context context, Task task, RelativeLayout sticker)
			throws ParseException {

		freeOwner(context, task, sticker);

		Util.whiteboardLayout.removeView(sticker);

		deleteFromDB(context, task);

		Toast.makeText(context, "Task removed.", Toast.LENGTH_SHORT).show();
	}

	public static void freeOwner(Context context, Task task,
			RelativeLayout taskSticker) throws ParseException {

		TextView memberName = (TextView) taskSticker
				.findViewById(R.id.taskDetailOwner);
		String recoveredMemberName = memberName.getText().toString();
		memberName.setText(null);

		Member member = task.getOwner();
		if (member != null) {

			task.setOwner(null);
			updateToDB(context, task, null);

			String name = member.getName();

			GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
					.findViewWithTag(recoveredMemberName);
			memberSticker.setVisibility(View.VISIBLE);
			memberSticker.findViewById(R.id.memberName).setVisibility(
					View.VISIBLE);

			// toast
			Toast.makeText(context, name + " is now free.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public static void freeOwner(Context context, Task task)
			throws ParseException {

		Member member = task.getOwner();
		if (member != null) {
			updateToDB(context, task, null);

			String name = task.getOwner().getName();
			// GridLayout memberSticker = member.getMemberSticker();

			GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
					.findViewWithTag(member.getName());
			memberSticker.setVisibility(View.VISIBLE);

			task.setOwner(null);

			// update small task sticker
			View smallTask = Util.whiteboardLayout
					.findViewWithTag(task.getId());
			TextView smallOwner = (TextView) smallTask
					.findViewById(R.id.taskDetailOwner);
			smallOwner.setText(null);

			// toast
			Toast.makeText(context, name + " is now free.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private static void saveToDB(Context context, Task task)
			throws ParseException {

		// push to server
		ParseObject testObject = new ParseObject("Task");
		testObject.put("board", Util.getActiveBoard());
		testObject.put("id", task.getId());
		if (task.getTitle() != null)
			testObject.put("title", task.getTitle());
		if (task.getDescription() != null)
			testObject.put("description", task.getDescription());
		if (task.getOwner() != null)
			testObject.put("owner", task.getOwner());
		testObject.put("status", task.getStatus());
		testObject.put("posX", task.getPosX());
		testObject.put("posY", task.getPosY());
		testObject.save();

		// // save to DB
		// TaskDB taskDB = new TaskDB(context);
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		// ContentValues values = new ContentValues();
		// values.put("TASK_ID", task.getId());
		// values.put("TITLE", task.getTitle());
		// values.put("DESC", task.getDescription());
		// if (task.getOwner() != null)
		// values.put("OWNER", task.getOwner().getName());
		// values.put("POS_X", task.getPosX());
		// values.put("POS_Y", task.getPosY());
		// values.put("STATUS", task.getStatus());
		// long newRowId;
		// newRowId = db.insert(TaskDB.TABLE_NAME, null, values);

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

	private static void updateToDB(Context context, Task task, String newOwner)
			throws ParseException {

		// TaskDB taskDB = new TaskDB(context);
		//
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// values.put("OWNER", newOwner);
		//
		// // Which row to update, based on the ID
		// String selection = "TASK_ID LIKE ?";
		// String[] selectionArgs = { taskId };
		//
		// int count = db.update(TaskDB.TABLE_NAME, values, selection,
		// selectionArgs);

		ParseObject parseObject = task.getParseObject();

		// TODO : sync or lock before updating

		if (newOwner == null) {
			parseObject.remove("owner");
		} else
			parseObject.put("owner", newOwner);
		parseObject.save();

	}

	private static void updateToDB(Context context, Task task, float posX,
			float posY, int status) throws ParseException {

		// TaskDB taskDB = new TaskDB(context);
		//
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// values.put("POS_X", posX);
		// values.put("POS_Y", posY);
		// values.put("STATUS", status);
		//
		// // Which row to update, based on the ID
		// String selection = "TASK_ID LIKE ?";
		// String[] selectionArgs = { taskId };
		//
		// int count = db.update(TaskDB.TABLE_NAME, values, selection,
		// selectionArgs);

		ParseObject parseObject = task.getParseObject();

		// TODO : sync or lock before updating

		parseObject.put("posX", posX);
		parseObject.put("posY", posY);

		parseObject.save();

	}

	private static void updateToDB(Context context, Task task, String title,
			String desc) throws ParseException {

		// TaskDB taskDB = new TaskDB(context);
		//
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// // values.put("TASK_ID", task.getId());
		// values.put("TITLE", title);
		// values.put("DESC", desc);
		//
		// // Which row to update, based on the ID
		// String selection = "TASK_ID LIKE ?";
		// String[] selectionArgs = { id };
		//
		// int count = db.update(TaskDB.TABLE_NAME, values, selection,
		// selectionArgs);

		ParseObject parseObject = task.getParseObject();

		// TODO : sync or lock before updating

		parseObject.put("title", title);
		parseObject.put("description", desc);
		parseObject.save();

	}

	private static void deleteFromDB(Context context, Task task)
			throws ParseException {
		// TaskDB taskDB = new TaskDB(context);
		//
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		//
		// // Define 'where' part of query.
		// String selection = "TASK_ID LIKE ?";
		// // Specify arguments in placeholder order.
		// String[] selectionArgs = { taskId };
		// // Issue SQL statement.
		// db.delete(TaskDB.TABLE_NAME, selection, selectionArgs);

		// TODO : lock first or sync
		task.getParseObject().delete();
	}

	private static Map<String, Task> getAllFromDB(Context context)
			throws ParseException {

		Map<String, Task> tasks = new HashMap<String, Task>();

		// TaskDB taskDB = new TaskDB(context);
		// SQLiteDatabase db = taskDB.getReadableDatabase();
		//
		// String[] projection = { "TASK_ID", "TITLE", "DESC", "OWNER", "POS_X",
		// "POS_Y", "STATUS" };
		//
		// Cursor cursor = db.query(TaskDB.TABLE_NAME, // The table to query
		// projection, // The columns to return
		// null, // The columns for the WHERE clause
		// null, // The values for the WHERE clause
		// null, // don't group the rows
		// null, // don't filter by row groups
		// null // The sort order
		// );
		//
		// // Iterate
		//
		// cursor.moveToFirst();
		// while (cursor.isAfterLast() == false) {
		//
		// Task task = new Task(cursor.getString(0), cursor.getFloat(4),
		// cursor.getFloat(5));
		// task.setTitle(cursor.getString(1));
		// task.setDescription(cursor.getString(2));
		// if (cursor.getString(3) != null) {
		// System.out.println("cursor.getString(3) = = = = = = = = = = "
		// + cursor.getString(3));
		//
		// task.setOwner(MemberManager.load(context, cursor.getString(3)));
		// }
		// task.setStatus(cursor.getInt(6));
		// tasks.put(task.getId(), task);
		// cursor.moveToNext();
		// }
		//
		// return tasks;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");

		query.whereEqualTo("board", Util.getActiveBoard());
		List<ParseObject> results = query.find();

		for (ParseObject result : results) {
			Task task = new Task(result.getString("id"),
					(float) result.getDouble("posX"),
					(float) result.getDouble("posY"), result);
			task.setTitle(result.getString("title"));
			task.setDescription(result.getString("description"));
			if (result.getString("owner") != null)
				task.setOwner(MemberManager.load(context,
						result.getString("owner")));
			task.setStatus(result.getInt("status"));

			tasks.put(task.getId(), task);
		}

		return tasks;

	}

	private static Task loadFromDB(Context context, String taskId)
			throws ParseException {

		// TaskDB taskDB = new TaskDB(context);
		// SQLiteDatabase db = taskDB.getReadableDatabase();
		//
		// String[] projection = { "TASK_ID", "TITLE", "DESC", "OWNER", "POS_X",
		// "POS_Y", "STATUS" };
		//
		// Cursor cursor = db.query(TaskDB.TABLE_NAME, // The table to query
		// projection, // The columns to return
		// "TASK_ID like ?", // The columns for the WHERE clause
		// new String[] { taskId }, // The values for the WHERE clause
		// null, // don't group the rows
		// null, // don't filter by row groups
		// null // The sort order
		// );
		//
		// // Iterate
		//
		// cursor.moveToFirst();
		// Task task = new Task(cursor.getString(0), cursor.getFloat(4),
		// cursor.getFloat(5));
		// task.setTitle(cursor.getString(1));
		// task.setDescription(cursor.getString(2));
		// if (cursor.getString(3) != null)
		// task.setOwner(MemberManager.load(context, cursor.getString(3)));
		// task.setStatus(cursor.getInt(6));
		// return task;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
		query.whereEqualTo("board", Util.getActiveBoard());
		query.whereEqualTo("id", taskId);
		ParseObject result = query.getFirst();

		Task task = new Task(result.getString("id"),
				(float) result.getDouble("posX"),
				(float) result.getDouble("posY"), result);
		task.setTitle(result.getString("title"));
		task.setDescription(result.getString("description"));
		if (result.getString("owner") != null)
			task.setOwner(MemberManager.load(context, result.getString("owner")));
		task.setStatus(result.getInt("status"));
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
		TaskStickerListener stickyListener = new TaskStickerListener(context);
		// stickyLayout.setOnTouchListener(stickyListener);
		stickyLayout.setOnDragListener(stickyListener);
		stickyLayout.setOnLongClickListener(stickyListener);
		stickyLayout.setOnClickListener(stickyListener);

		stickyLayout.setTag(taskId);

		return stickyLayout;
	}
}
