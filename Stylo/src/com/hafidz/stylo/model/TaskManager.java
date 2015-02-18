package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.parse.ParsePush;
import com.parse.ParseQuery;

public class TaskManager {

	// cache
	public static Map<String, ParseObject> parseObjects = new HashMap<String, ParseObject>();

	// push
	public static final int PUSH_ACTION_CREATE = 1;
	public static final int PUSH_ACTION_DELETE = 0;
	public static final int PUSH_ACTION_MOVE = 2;
	public static final int PUSH_ACTION_UPDATE_DETAILS = 3;
	public static final int PUSH_ACTION_UPDATE_OWNER = 4;

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
	public static void assignOwner(Context context, String taskId,
			String newOwner) throws ParseException {

		updateToDB(context, taskId, newOwner);

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
	public static int moved(Context context, String taskId, float x, float y)
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

		return status;
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

	private static void push(String taskId, int action, String msg,
			String oriOwner, boolean notification) {
		// push
		try {
			ParsePush push = new ParsePush();
			push.setChannel(Util.getActiveBoard());
			JSONObject json = new JSONObject();
			json.put("type", "TASK");
			json.put("id", taskId);
			json.put("msg", msg);
			json.put("action", action);

			if (oriOwner != null)
				json.put("oriOwner", oriOwner);

			if (notification) {
				json.put("title", "Taskboard Updated");
				json.put("alert", msg);
			}

			// TODO : exclude self

			push.setData(json);
			push.sendInBackground();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void push(String taskId, int action, String msg) {
		push(taskId, action, msg, null, true);
	}

	private static void push(String taskId, int action, String msg,
			boolean notification) {
		push(taskId, action, msg, null, notification);
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
		testObject
				.saveInBackground(new TaskSaveCallback(context, task.getId()));

		// push (we push in TaskSaveCallback to prevent push before object saved
		// in server)
		// push(task.getId(), PUSH_ACTION_CREATE, "New empty task created.");

	}

	/**
	 * 
	 * MUST BE RUN IN BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param taskId
	 * @param newOwner
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String taskId,
			String newOwner) throws ParseException {

		// we want to update fresh copy
		ParseObject parseObject = loadFromDB(context, taskId);

		String oldOwner = parseObject.getString("owner");
		if (newOwner == null) {
			parseObject.remove("owner");
		} else
			parseObject.put("owner", newOwner);
		parseObject.save();

		// push

		String msg = null;
		String title = parseObject.getString("title");
		if (newOwner != null) {
			if (title != null)
				msg = "'" + newOwner + "' assigned to task '" + title + "'";
			else
				msg = "'" + newOwner + "' assigned to empty task.";
		}
		// unassign
		else {
			if (oldOwner != null) {
				if (title != null)
					msg = "'" + oldOwner + "' unassigned from task '" + title
							+ "'";
				else
					msg = "'" + oldOwner + "' unassigned from task.";

			} else {
				// msg = "Task owner unassigned.";
			}
		}

		if (msg != null) {
			push(taskId, PUSH_ACTION_UPDATE_OWNER, msg, oldOwner, true);
		}

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

		String oriOwner = parseObject.getString("owner");

		int oldStatus = parseObject.getInt("status");

		// free owner
		if (status == Task.STATUS_DONE) {
			parseObject.remove("owner");
		}

		parseObject.put("posX", posX);
		parseObject.put("posY", posY);
		parseObject.put("status", status);

		parseObject.save();

		// push
		// String msg = null;
		// String title = parseObject.getString("title");
		// if (title != null) {
		// switch (status) {
		// case Task.STATUS_DONE:
		//
		// msg = "Task '" + parseObject.getString("title") + "' is done!";
		// break;
		//
		// case Task.STATUS_IN_PROGRESS:
		//
		// msg = "Task '" + parseObject.getString("title")
		// + "' is now in progress.";
		// break;
		//
		// case Task.STATUS_TODO:
		//
		// msg = "Task '" + parseObject.getString("title")
		// + "' is back to not started.";
		// break;
		//
		// case Task.STATUS_ROAD_BLOCK:
		//
		// msg = "Task '" + parseObject.getString("title")
		// + "' is in road block.";
		// break;
		// }
		// push(taskId, msg);
		// }

		if (status == Task.STATUS_DONE) {
			if (oldStatus != status)
				push(taskId, TaskManager.PUSH_ACTION_MOVE, "Task moved.",
						oriOwner, true);
			else
				push(taskId, TaskManager.PUSH_ACTION_MOVE, "Task moved.",
						oriOwner, false);
		} else {
			if (oldStatus != status)
				push(taskId, TaskManager.PUSH_ACTION_MOVE, "Task moved.");
			else
				push(taskId, TaskManager.PUSH_ACTION_MOVE, "Task moved.", false);
		}

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

		// push
		push(taskId, PUSH_ACTION_UPDATE_DETAILS,
				"Task '" + parseObject.getString("title")
						+ "' details updated.");

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

		// push
		if (task.getTitle() != null)
			push(task.getId(), PUSH_ACTION_DELETE, "Task '" + task.getTitle()
					+ "' removed.");
		else {
			push(task.getId(), PUSH_ACTION_DELETE, "Task removed.");
		}

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

	public static RelativeLayout UICreateEmptySticker(Context context, float x,
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
				Math.round(Util.toPixelsWidth(context, 14)), 300);
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

	public static void UIremoveOwnerFromTaskSticker(String taskId) {
		// update small task sticker
		View smallTask = Util.whiteboardLayout.findViewWithTag(taskId);
		TextView smallOwner = (TextView) smallTask
				.findViewById(R.id.taskDetailOwner);
		smallOwner.setText(null);
	}
}
