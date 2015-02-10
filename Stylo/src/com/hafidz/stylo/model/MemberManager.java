/**
 * 
 */
package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hafidz.stylo.MemberListener;
import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * @author hafidz
 * 
 */
public class MemberManager {
	// public static Map<String, Member> allMembers = new HashMap<String,
	// Member>();

	public static Map<String, Member> getAll(Context context)
			throws ParseException {
		return getAllFromDB(context);
	}

	public static void add(Context context, Member member)
			throws ParseException {

		saveToDB(context, member);
	}

	public static boolean obtainLock(String taskId) {
		// TODO : implement obtain lock
		return true;
	}

	public static void releaseLock(String taskId) {
	}

	public static void moved(Context context, String memberName, float y)
			throws ParseException {

		updateToDB(context, memberName, y);
	}

	public static Member load(Context context, String name)
			throws ParseException {
		return loadFromDB(context, name);
	}

	public static void remove(Context context, String name)
			throws ParseException {

		deleteFromDB(context, name);

		Util.whiteboardLayout.findViewWithTag(name).setVisibility(View.GONE);

		Toast.makeText(context, name + " removed.", Toast.LENGTH_SHORT).show();
	}

	public static void updateMember(Context context, String oriName,
			String newName, String email) throws ParseException {

		Member member = load(context, oriName);

		if (oriName.equals(newName)) {
			// member.setEmail(email);
			updateToDB(context, oriName, email);
		}

		// id changed
		else {
			Member newMember = new Member(newName, email, false,
					member.getPosY(), null);
			add(context, newMember);

			// // reassign task to new member
			// if (TaskManager.allTasks != null) {
			// for (Entry<String, Task> taskEntry : TaskManager.allTasks
			// .entrySet()) {
			// if (taskEntry.getValue().getOwner().equals(oriName)) {
			// TaskManager.obtainLock(taskEntry.getValue().getId());
			// taskEntry.getValue().setOwner(newName);
			// TaskManager.releaseLock(taskEntry.getValue().getId());
			// }
			// }
			// }

			// delete old member
			remove(context, oriName);

			// update UI
			GridLayout memberSticker = (GridLayout) Util.whiteboardLayout
					.findViewWithTag(oriName);
			TextView memberName = (TextView) memberSticker
					.findViewById(R.id.memberName);
			memberName.setText(newName);
			memberSticker.setTag(newName);

			// show back removed UI because......
			memberSticker.setVisibility(View.VISIBLE);
		}

	}

	private static void saveToDB(Context context, Member task)
			throws ParseException {

		// MemberDB memberDB = new MemberDB(context);
		//
		// SQLiteDatabase db = memberDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// values.put("NAME", member.getName());
		// values.put("EMAIL", member.getEmail());
		// values.put("POS_Y", member.getPosY());
		//
		// long newRowId;
		// newRowId = db.insert(MemberDB.TABLE_NAME, null, values);

		// push to server
		ParseObject testObject = new ParseObject("Member");
		testObject.put("board", Util.getActiveBoard());
		testObject.put("name", task.getName());
		if (task.getEmail() != null)
			testObject.put("email", task.getEmail());

		testObject.put("posY", task.getPosY());

		testObject.save();

	}

	private static void updateToDB(Context context, String memberName,
			String email) throws ParseException {

		// MemberDB memberDB = new MemberDB(context);
		// SQLiteDatabase db = memberDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// values.put("EMAIL", email);
		//
		// // Which row to update, based on the ID
		// String selection = "NAME LIKE ?";
		// String[] selectionArgs = { memberName };
		//
		// int count = db.update(MemberDB.TABLE_NAME, values, selection,
		// selectionArgs);

		Member member = load(context, memberName);

		member.getParseObject().put("email", email);

		member.getParseObject().save();

	}

	private static void updateToDB(Context context, String memberName,
			float posY) throws ParseException {

		// MemberDB memberDB = new MemberDB(context);
		// SQLiteDatabase db = memberDB.getWritableDatabase();
		//
		// ContentValues values = new ContentValues();
		// values.put("POS_Y", posY);
		//
		// // Which row to update, based on the ID
		// String selection = "NAME LIKE ?";
		// String[] selectionArgs = { memberName };
		//
		// int count = db.update(MemberDB.TABLE_NAME, values, selection,
		// selectionArgs);

		Member member = load(context, memberName);

		member.getParseObject().put("posY", posY);

		member.getParseObject().save();

	}

	private static void deleteFromDB(Context context, String memberName)
			throws ParseException {
		// MemberDB taskDB = new MemberDB(context);
		//
		// SQLiteDatabase db = taskDB.getWritableDatabase();
		//
		// // Define 'where' part of query.
		// String selection = "NAME LIKE ?";
		// // Specify arguments in placeholder order.
		// String[] selectionArgs = { memberName };
		// // Issue SQL statement.
		// db.delete(MemberDB.TABLE_NAME, selection, selectionArgs);

		Member member = load(context, memberName);

		member.getParseObject().delete();

	}

	private static Map<String, Member> getAllFromDB(Context context)
			throws ParseException {

		Map<String, Member> members = new HashMap<String, Member>();

		// MemberDB taskDB = new MemberDB(context);
		// SQLiteDatabase db = taskDB.getReadableDatabase();
		//
		// String[] projection = { "NAME", "EMAIL", "POS_Y" };
		//
		// Cursor cursor = db.query(MemberDB.TABLE_NAME, // The table to query
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
		// Member member = new Member(cursor.getString(0),
		// cursor.getString(1), false, cursor.getFloat(2));
		//
		// members.put(member.getName(), member);
		// cursor.moveToNext();
		// }
		//
		// return members;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Member");
		query.whereEqualTo("board", Util.getActiveBoard());
		List<ParseObject> results = query.find();

		for (ParseObject result : results) {
			Member member = new Member(result.getString("name"),
					result.getString("email"), false,
					(float) result.getDouble("posY"), result);

			members.put(member.getName(), member);
		}
		return members;

	}

	private static Member loadFromDB(Context context, String name)
			throws ParseException {

		// MemberDB memberDB = new MemberDB(context);
		// SQLiteDatabase db = memberDB.getReadableDatabase();
		//
		// String[] projection = { "NAME", "EMAIL", "POS_Y" };
		//
		// Cursor cursor = db.query(MemberDB.TABLE_NAME, // The table to query
		// projection, // The columns to return
		// "NAME like ?", // The columns for the WHERE clause
		// new String[] { name }, // The values for the WHERE clause
		// null, // don't group the rows
		// null, // don't filter by row groups
		// null // The sort order
		// );
		//
		// // Iterate
		//
		// System.out.println("cursor.getCount() = = = = " + cursor.getCount());
		//
		// // /////
		// // for(Entry<String,Member>ent : getAllFromDB(context).entrySet())
		// // {
		// // System.out.println("ent.getValue().getName() = = = " +
		// // ent.getValue().getName());
		// // }
		// // /////
		//
		// cursor.moveToFirst();
		//
		// if (cursor.isFirst())
		// return new Member(cursor.getString(0), cursor.getString(1), false,
		// cursor.getFloat(2));
		//
		// else
		// return null;

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Member");
		query.whereEqualTo("board", Util.getActiveBoard());
		query.whereEqualTo("name", name);
		ParseObject result = query.getFirst();

		Member member = new Member(result.getString("name"),
				result.getString("email"), false,
				(float) result.getDouble("posY"), result);

		return member;

	}

	public static void createNewSticker(Context context, float y,
			String memberName) {

		GridLayout memberSticker = (GridLayout) LayoutInflater.from(context)
				.inflate(R.layout.member_layout, null);

		// String memberName = "#" + MemberManager.getAll(context).size();
		TextView memberNameView = (TextView) memberSticker
				.findViewById(R.id.memberName);
		memberNameView.setText(memberName);

		// memberLayout.setHeight(toPixelsHeight(10));
		// memberLayout.setWidth(toPixelsWidth(7));

		LayoutParams memberLayoutParams = new LayoutParams(Util.toPixelsWidth(
				context, 8), 75);
		memberSticker.setLayoutParams(memberLayoutParams);

		memberSticker.setX(Util.toPixelsWidth(context, 1));
		memberSticker.setY(y);

		MemberListener memberListener = new MemberListener(context);
		memberSticker.setOnDragListener(memberListener);
		memberSticker.setOnLongClickListener(memberListener);
		memberSticker.setOnClickListener(memberListener);

		// bind with id
		memberSticker.setTag(memberName);

		Util.whiteboardLayout.addView(memberSticker);

		// return memberSticker;
	}
}
