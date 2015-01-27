package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {
	public static Map<String, Task> allTasks = new HashMap<String, Task>();

	public static void add(Task task) {
		// TODO : server side add task
		allTasks.put(String.valueOf(task.getId()), task);
	}

	public static void assignOwner(int taskId, String owner) {
		// TODO : server side assign owner
		allTasks.get(String.valueOf(taskId)).setOwner(owner);
	}

	// TODO : lock task

	// TODO : unlock task

	public static boolean obtainLock(int taskId) {
		// TODO : implement obtain lock
		return true;
	}

	public static void moved(int taskId, float x, float y) {
		// TODO : server side

		// TODO : update status as well based on position

		allTasks.get(String.valueOf(taskId)).setPos(x, y);
	}

}
