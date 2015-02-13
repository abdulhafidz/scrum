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
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class TaskManager {

	// cache
	public static Map<String, ParseObject> parseObjects = new HashMap<String, ParseObject>();

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

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @param owner
	 * @throws ParseException
	 */
	public static void assignOwner(Context context, String taskId, Member owner)
			throws ParseException {

		updateToDB(context, taskId, owner.getName());

		// Toast.makeText(context, owner.getName() + " assigned to a task.",
		// Toast.LENGTH_SHORT).show();
	}

	public static void uiUpdateStickerOwner(RelativeLayout sticker,
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

	/**
	 * 
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @param x
	 * @param y
	 * @throws ParseException
	 */
	public static void moved(Context context, String taskId, float x, float y)
			throws ParseException {

		// update status
		// Task task = load(context, taskId);
		int status;
		if (x >= (WhiteBoardScroller.LINE_IN_PROGRESS - WhiteBoardListener.STICKY_X_OFFSET)
				&& x < (WhiteBoardScroller.LINE_DONE - WhiteBoardListener.STICKY_X_OFFSET)) {
			// task.setStatus(Task.STATUS_IN_PROGRESS);
			status = Task.STATUS_IN_PROGRESS;

			// Toast.makeText(context, "Task in progress.", Toast.LENGTH_SHORT)
			// .show();

		} else if (x >= WhiteBoardScroller.LINE_DONE
				- WhiteBoardListener.STICKY_X_OFFSET) {
			// task.setStatus(Task.STATUS_DONE);
			status = Task.STATUS_DONE;

			// Toast.makeText(context, "Well done!", Toast.LENGTH_SHORT).show();

		} else {
			// task.setStatus(Task.STATUS_TODO);
			status = Task.STATUS_TODO;

			// Toast.makeText(context, "Task in TO-DO.", Toast.LENGTH_SHORT)
			// .show();
		}

		updateToDB(context, taskId, x, y, status);
	}

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @param title
	 * @param desc
	 * @param sticker
	 * @throws ParseException
	 */
	public static void updateTask(Context context, String taskId, String title,
			String desc) throws ParseException {

		updateToDB(context, taskId, title, desc);

	}

	public static void updateSticker(RelativeLayout sticker, String title,
			String desc) {

		((TextView) sticker.findViewById(R.id.smallTaskTitle)).setText(title);
		((TextView) sticker.findViewById(R.id.smallTaskDesc)).setText(desc);

	}

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @return
	 * @throws ParseException
	 */
	public static Task load(Context context, String taskId)
			throws ParseException {

		ParseObject result = loadFromDB(context, taskId);

		Task task = new Task(result.getString("id"),
				(float) result.getDouble("posX"),
				(float) result.getDouble("posY"), result);
		task.setTitle(result.getString("title"));
		task.setDescription(result.getString("description"));
		if (result.getString("owner") != null)
			task.setOwner(result.getString("owner"));
		task.setStatus(result.getInt("status"));

		return task;

	}

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param task
	 * @param sticker
	 * @throws ParseException
	 */
	public static void remove(Context context, Task task) throws ParseException {

		freeOwner(context, task);

		deleteFromDB(context, task);

	}

	// /**
	// * MUST BE RUN IN BACKGROUND THREAD!!!!
	// *
	// * @param context
	// * @param taskId
	// * @param taskSticker
	// * @param ownerName
	// * @throws ParseException
	// */
	// public static void freeOwner(Context context, String taskId,
	// RelativeLayout taskSticker, String ownerName) throws ParseException {
	//
	// TextView memberName = (TextView) taskSticker
	// .findViewById(R.id.taskDetailOwner);
	// String recoveredMemberName = memberName.getText().toString();
	// memberName.setText(null);
	//
	// if (ownerName != null) {
	//
	// // task.setOwner(null);
	// updateToDB(context, taskId, null);
	//
	// GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
	// .findViewWithTag(recoveredMemberName);
	// memberSticker.setVisibility(View.VISIBLE);
	// memberSticker.findViewById(R.id.memberName).setVisibility(
	// View.VISIBLE);
	//
	// // toast
	// Toast.makeText(context, ownerName + " is now free.",
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// }

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @param ownerName
	 * @throws ParseException
	 */
	public static void freeOwner(Context context, Task task)
			throws ParseException {

		updateToDB(context, task.getId(), null);

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
		// testObject.save();

		Util.startLoading();
		testObject.saveInBackground(new TaskSaveCallback(context));

	}

	/**
	 * 
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param task
	 * @param newOwner
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String task, String newOwner)
			throws ParseException {

		// ParseObject parseObject = task.getParseObject();

		// we want to update fresh copy
		ParseObject parseObject = loadFromDB(context, task);

		if (newOwner == null) {
			parseObject.remove("owner");
		} else
			parseObject.put("owner", newOwner);
		parseObject.save();

	}

	/**
	 * 
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param task
	 * @param posX
	 * @param posY
	 * @param status
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String taskId, float posX,
			float posY, int status) throws ParseException {

		// ParseObject parseObject = task.getParseObject();

		// we want to update fresh copy
		ParseObject parseObject = loadFromDB(context, taskId);

		parseObject.put("posX", posX);
		parseObject.put("posY", posY);
		parseObject.put("status", status);

		parseObject.save();

	}

	/**
	 * 
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param task
	 * @param title
	 * @param desc
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String taskId,
			String title, String desc) throws ParseException {

		// ParseObject parseObject = task.getParseObject();
		// we want to update fresh copy
		ParseObject parseObject = loadFromDB(context, taskId);

		parseObject.put("title", title);
		parseObject.put("description", desc);
		parseObject.save();

	}

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param task
	 * @throws ParseException
	 */
	private static void deleteFromDB(Context context, Task task)
			throws ParseException {

		task.getParseObject().delete();

	}

	private static Map<String, Task> getAllFromDB(Context context)
			throws ParseException {

		Map<String, Task> tasks = new HashMap<String, Task>();

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
				task.setOwner(result.getString("owner"));
			task.setStatus(result.getInt("status"));

			tasks.put(task.getId(), task);

			// add to cache
			TaskManager.parseObjects.put(task.getId(), result);
		}

		return tasks;

	}

	/**
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @return
	 * @throws ParseException
	 */
	private static ParseObject loadFromDB(Context context, String taskId)
			throws ParseException {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Task");
		query.whereEqualTo("board", Util.getActiveBoard());
		query.whereEqualTo("id", taskId);
		ParseObject result = query.getFirst();

		return result;

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

	public static void removeOwnerFromTaskStickerUI(String taskId) {
		// update small task sticker
		View smallTask = Util.whiteboardLayout.findViewWithTag(taskId);
		TextView smallOwner = (TextView) smallTask
				.findViewById(R.id.taskDetailOwner);
		smallOwner.setText(null);
	}
}
