/**
 * 
 */
package com.hafidz.stylo.manager;

import com.hafidz.stylo.model.User;
import com.hafidz.stylo.util.Util;
import com.parse.ParseException;
import com.parse.ParseRole;
import com.parse.ParseUser;

/**
 * @author hafidz
 * 
 */
public class UserManager {

	private static User currentUser;

	/**
	 * MUST BE RAN FROM BACKGROUND THREAD
	 * 
	 * @param email
	 * @param password
	 * @throws ParseException
	 */
	public static void register(String email, String password, String name)
			throws ParseException {
		ParseUser user = new ParseUser();
		user.setUsername(email);
		user.setPassword(password);
		user.setEmail(email);
		user.put("name", name);
		user.signUp();

		// auto add to whiteboard role
		ParseRole role = ParseRole.getQuery()
				.whereEqualTo("name", Util.getActiveBoard()).getFirst();
		role.getRelation("users").add(user);
		role.save();

	}

	/**
	 * MUST BE RAN FROM BACKGROUND THREAD
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws ParseException
	 */
	public static User signIn(String email, String password)
			throws ParseException {
		ParseUser parseUser = ParseUser.logIn(email, password);

		return new User(parseUser.getUsername(), parseUser.getString("name"),
				parseUser);
	}

	public static User getCurrentUser() {
		if (currentUser == null) {
			ParseUser parse = ParseUser.getCurrentUser();

			if (parse != null)
				currentUser = new User(parse.getUsername(),
						parse.getString("name"), parse);
		}

		return currentUser;
	}

	public static void signOut() {
		currentUser = null;
		ParseUser.logOut();

	}

}
