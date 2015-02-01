package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.Map;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManager {
	public static Map<String, Task> allTasks = new HashMap<String, Task>();

	public static void add(Task task) {
		// TODO : server side add task
		allTasks.put(String.valueOf(task.getId()), task);
	}

	public static void assignOwner(int taskId, Member owner) {
		// TODO : server side assign owner
		allTasks.get(String.valueOf(taskId)).setOwner(owner);
	}

	// TODO : lock task

	// TODO : unlock task

	public static boolean obtainLock(int taskId) {
		// TODO : implement obtain lock
		return true;
	}

	public static void releaseLock(int taskId) {
	}

	public static void moved(int taskId, float x, float y) {
		// TODO : server side

		// TODO : update status as well based on position

		allTasks.get(String.valueOf(taskId)).setPos(x, y);
	}

	public static void updateTask(int taskId, String title, String desc) {
		Task task = allTasks.get(String.valueOf(taskId));
		task.setTitle(title);
		task.setDescription(desc);

		// update ui (small task)
		RelativeLayout smallTask = task.getSmallTask();

		((TextView) smallTask.findViewById(R.id.smallTaskTitle)).setText(Html
				.fromHtml("<u>" + title + "</u>"));
		((TextView) smallTask.findViewById(R.id.smallTaskDesc)).setText(desc);

		// update ui (big view task)

	}

	public static Task load(int taskId) {
		return allTasks.get(String.valueOf(taskId));
	}

	public static void remove(Context context, int taskId) {
		Task task = load(taskId);

		freeOwner(task);

		RelativeLayout sticker = task.getSmallTask();

		// sticker.setVisibility(View.GONE);
		Util.whiteboardLayout.removeView(sticker);

		allTasks.remove(taskId);

		Toast.makeText(context, "Task removed.", Toast.LENGTH_SHORT).show();
	}

	public static void freeOwner(Task task) {

		RelativeLayout taskSticker = task.getSmallTask();
		TextView memberName = (TextView) taskSticker
				.findViewById(R.id.smallTaskOwner);
		memberName.setText(null);

		Member member = task.getOwner();
		GridLayout memberSticker = member.getMemberSticker();

		memberSticker.setVisibility(View.VISIBLE);
		memberSticker.findViewById(R.id.memberName).setVisibility(View.VISIBLE);

		task.setOwner(null);

	}

}
