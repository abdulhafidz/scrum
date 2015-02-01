/**
 * 
 */
package com.hafidz.stylo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hafidz.stylo.R;

/**
 * @author hafidz
 * 
 */
public class MemberManager {
	public static Map<String, Member> allMembers = new HashMap<String, Member>();

	public static void add(Member task) {
		// TODO : server side add task
		allMembers.put(task.getName(), task);
	}

	// TODO : lock task

	// TODO : unlock task

	public static boolean obtainLock(String taskId) {
		// TODO : implement obtain lock
		return true;
	}

	public static void releaseLock(String taskId) {
	}

	public static void moved(int taskId, float y) {
		// TODO : server side

		// TODO : update status as well based on position

		allMembers.get(String.valueOf(taskId)).setPosY(y);
	}

	public static Member load(String name) {
		return allMembers.get(name);
	}

	public static void remove(Context context, String name) {
		load(name).getMemberSticker().setVisibility(View.GONE);
		allMembers.remove(name);

		Toast.makeText(context, name + " removed.", Toast.LENGTH_SHORT).show();
	}

	public static void updateMember(Context context, String oriName,
			String newName, String email) {

		Member member = load(oriName);

		if (oriName.equals(newName)) {
			member.setEmail(email);
		}

		// id changed
		else {
			Member newMember = new Member(newName, email, false,
					member.getPosY(), member.getMemberSticker());
			add(newMember);

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
			GridLayout memberSticker = newMember.getMemberSticker();
			TextView memberName = (TextView) memberSticker
					.findViewById(R.id.memberName);
			memberName.setText(newName);

			// show back removed UI because......
			member.getMemberSticker().setVisibility(View.VISIBLE);
		}

	}
}
