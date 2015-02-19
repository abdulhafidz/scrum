/**
 * 
 */
package com.hafidz.stylo.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.hafidz.stylo.util.Util;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * @author hafidz
 * 
 */
public class AnonymousManager {

	public static void login() {
		// Util.startLoading();
		// Util.startProgressDialog("Signing in anonymous user...");
		Util.loginInProgress = true;
		ParseAnonymousUtils.logIn(new SignInCallback(false));
	}

	public static void login(boolean subscribeBackground) {
		// Util.startLoading();
		// Util.startProgressDialog("Signing in anonymous user...");
		Util.loginInProgress = true;
		ParseAnonymousUtils.logIn(new SignInCallback(subscribeBackground));
	}

	public static class SignInCallback extends LogInCallback {
		private boolean subscribeBackground;

		public SignInCallback(boolean subscribeBackground) {
			super();
			this.subscribeBackground = subscribeBackground;
		}

		@Override
		public void done(ParseUser user, ParseException exception) {

			// /////////////////////////////////////////////
			// ROLE [START]
			// /////////////////////////////////////////////
			AsyncTask<ParseUser, Void, ParseException> bgTask = new AsyncTask<ParseUser, Void, ParseException>() {
				@Override
				protected ParseException doInBackground(ParseUser... user) {
					try {
						// create role, 1 user -> 1 default whiteboard -> 1 role
						ParseRole role = null;

						try {
							role = ParseRole
									.getQuery()
									.whereEqualTo("name", user[0].getObjectId())
									.getFirst();
						} catch (Exception e) {
							e.printStackTrace();
							System.err
									.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx");
						}

						if (role == null) {
							role = new ParseRole(user[0].getObjectId());
							role.setName(user[0].getObjectId());

							ParseACL parseACL = new ParseACL();
							parseACL.setReadAccess(user[0], true);
							parseACL.setWriteAccess(user[0], true);
							role.setACL(parseACL);
						}
						role.getRelation("users").add(user[0]);
						role.save();
					} catch (ParseException e) {
						e.printStackTrace();
						return e;
					} finally {
						Util.loginInProgress = false;
					}
					return null;
				}
			};
			bgTask.execute(user);
			// /////////////////////////////////////////////
			// ROLE [END]
			// /////////////////////////////////////////////

			if (subscribeBackground) {
				ParsePush.subscribeInBackground(Util.getActiveBoard(),
						new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e == null) {
									Log.d("com.parse.push",
											"successfully subscribed to the broadcast channel.");
								} else {
									Log.e("com.parse.push",
											"failed to subscribe for push", e);
								}
							}
						});
			}
			// Util.stopLoading();
			// Util.stopProgressDialog();
			// success
			if (exception == null) {
				// if (!subscribeBackground)
				Util.reloadStickers();
			} else {
				Util.showError(Util.context,
						"Problem signing in anonymous user.");
			}

		}
	}
}
