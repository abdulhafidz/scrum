/**
 * 
 */
package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.parse.ParsePush;
import com.parse.ParseQuery;

/**
 * @author hafidz
 * 
 */
public class MemberManager {
	// push
	public static final int PUSH_ACTION_CREATE = 1;
	public static final int PUSH_ACTION_DELETE = 0;
	public static final int PUSH_ACTION_MOVE = 2;
	public static final int PUSH_ACTION_UPDATE_DETAILS = 3;

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

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param memberName
	 * @param y
	 * @throws ParseException
	 */
	public static void moved(Context context, String memberName, float y)
			throws ParseException {

		updateToDB(context, memberName, y);
	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	public static Member load(Context context, String name)
			throws ParseException {
		return loadFromDB(context, name);
	}

	public static void remove(Context context, String name)
			throws ParseException {

		deleteFromDB(context, name);

	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param oriName
	 * @param newName
	 * @param email
	 * @throws ParseException
	 */
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

			// delete old member
			remove(context, oriName);

		}

	}

	private static void saveToDB(Context context, Member member)
			throws ParseException {

		// push to server
		ParseObject testObject = new ParseObject("Member");
		testObject.put("board", Util.getActiveBoard());
		testObject.put("name", member.getName());
		if (member.getEmail() != null)
			testObject.put("email", member.getEmail());

		testObject.put("posY", member.getPosY());

		// testObject.save();
		Util.startLoading();
		testObject.saveInBackground(new MemberSaveCallback(member, context));

		// TODO: push in callback
		push(member.getName(), MemberManager.PUSH_ACTION_CREATE,
				"New member created.", true);

	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param memberName
	 * @param email
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String memberName,
			String email) throws ParseException {

		Member member = load(context, memberName);

		member.getParseObject().put("email", email);

		// member.getParseObject().save();
		Util.startLoading();
		member.getParseObject().save();

		push(memberName, PUSH_ACTION_UPDATE_DETAILS, "Member '" + memberName
				+ "' email changed", true);
	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param memberName
	 * @param posY
	 * @throws ParseException
	 */
	private static void updateToDB(Context context, String memberName,
			float posY) throws ParseException {

		Member member = load(context, memberName);

		member.getParseObject().put("posY", posY);

		// member.getParseObject().save();
		Util.startLoading();
		member.getParseObject().save();

		push(memberName, PUSH_ACTION_MOVE, "Member moved.", false);

	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!
	 * 
	 * @param context
	 * @param memberName
	 * @throws ParseException
	 */
	private static void deleteFromDB(Context context, String memberName)
			throws ParseException {

		Member member = load(context, memberName);

		member.getParseObject().delete();

		push(memberName, PUSH_ACTION_DELETE, "Member '" + memberName
				+ "' removed.", true);

	}

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @return
	 * @throws ParseException
	 */
	private static Map<String, Member> getAllFromDB(Context context)
			throws ParseException {

		Map<String, Member> members = new HashMap<String, Member>();

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

	/**
	 * MUST BE CALL FROM A BACKGROUND THREAD!!!!
	 * 
	 * @param context
	 * @param name
	 * @return
	 * @throws ParseException
	 */
	private static Member loadFromDB(Context context, String name)
			throws ParseException {

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Member");
		query.whereEqualTo("board", Util.getActiveBoard());
		query.whereEqualTo("name", name);
		ParseObject result = query.getFirst();

		Member member = new Member(result.getString("name"),
				result.getString("email"), false,
				(float) result.getDouble("posY"), result);

		return member;

	}

	public static void UIcreateNewSticker(Context context, float y,
			String memberName) {

		GridLayout memberSticker = (GridLayout) LayoutInflater.from(context)
				.inflate(R.layout.member_layout, null);

		TextView memberNameView = (TextView) memberSticker
				.findViewById(R.id.memberName);
		memberNameView.setText(memberName);

		LayoutParams memberLayoutParams = new LayoutParams(Math.round(Util
				.toPixelsWidth(context, 8)), 75);
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

	}

	public static void push(String memberName, int action, String msg,
			boolean notification) {
		// push
		try {
			ParsePush push = new ParsePush();
			push.setChannel(Util.getActiveBoard());
			JSONObject json = new JSONObject();
			json.put("type", "MEMBER");
			json.put("id", memberName);
			json.put("msg", msg);
			json.put("action", action);
			//
			// if (oriMember != null)
			// json.put("oriMember", oriMember);

			if (notification) {
				json.put("title", "Taskboard Member Updated");
				json.put("alert", msg);
			}

			// TODO : exclude self

			push.setData(json);
			push.sendInBackground();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
