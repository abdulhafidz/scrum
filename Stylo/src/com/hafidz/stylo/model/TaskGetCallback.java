//package com.hafidz.stylo.model;
//
//import android.content.Context;
//
//import com.parse.GetCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//
//public class TaskGetCallback extends GetCallback<ParseObject> {
//
//	private Context context;
//
//	public TaskGetCallback(Context context) {
//		this.context = context;
//	}
//
//	@Override
//	public void done(ParseObject object, ParseException exception) {
//
//		// no error
//		if (exception == null) {
//			Task task = new Task(object.getString("id"),
//					(float) object.getDouble("posX"),
//					(float) object.getDouble("posY"));
//			task.setTitle(object.getString("title"));
//			task.setDescription(object.getString("description"));
//			if (object.getString("owner") != null)
//				task.setOwner(object.getString("owner"));
//			task.setStatus(object.getInt("status"));
//
//			// // update cache
//			// TaskManager.parseObjects.put(task.getId(), object);
//		} else {
//
//		}
//
//	}
//
//}
