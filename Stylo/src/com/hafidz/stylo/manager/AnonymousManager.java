/**
 * 
 */
package com.hafidz.stylo.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.hafidz.stylo.model.Board;
import com.hafidz.stylo.util.Util;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
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

		// ParseInstallation.getCurrentInstallation().getParseUser("user");
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

			// success
			if (exception == null) {

				// Util.showProgressDialog("Logged in.");

				AsyncTask<ParseUser, Void, ParseException> bgTask = new AsyncTask<ParseUser, Void, ParseException>() {
					@Override
					protected ParseException doInBackground(ParseUser... user) {
						try {
							// create role, 1 user -> 1 default whiteboard -> 1
							// role
							Board defaultBoard = BoardManager
									.loadDefaultBoard();

							if (defaultBoard == null) {
								// Util.showProgressDialog("Default board not found.");

								String boardId = Util.generateBoardId();

								// create role for board
								ParseRole role = BoardManager
										.createRole(boardId);

								// create default board
								Board newBoard = new Board(boardId,
										"Default Board", user[0].getObjectId(),
										true, role);

								// Util.showProgressDialog("Creating default board.");

								BoardManager.add(newBoard);

								// Util.showProgressDialog("Default board created.");

								defaultBoard = newBoard;
							} else {
								// Util.showProgressDialog("Default board found.");
							}

							// set default board to user object
							// Util.showProgressDialog("Assigning board to user.");
							user[0].put("defaultBoard", defaultBoard.getId());
							user[0].saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e == null) {
										// Util.showProgressDialog("Done assigning board to user.");
										Log.i("AnonymousManager.login",
												"Success saving user.");
									} else {
										// Util.showProgressDialog("Failed assigning board to user.");

										Log.e("AnonymousManager.login",
												"Failed saving user", e);
									}

								}
							});

							if (subscribeBackground) {
								// Util.showProgressDialog("Subscribing push.");
								ParsePush.subscribeInBackground(
										defaultBoard.getId(),
										new SaveCallback() {

											@Override
											public void done(ParseException e) {
												if (e == null) {
													// Util.showProgressDialog("Done subscribing push.");
													Log.i("AnonymousManager.login",
															"Success subscribe in backgroud.");
												} else {
													// Util.showProgressDialog("Failed subscribing push.");
													Log.e("AnonymousManager.login",
															"Failed subscribe in background",
															e);
												}

											}
										});
							}

							// tie user with installation
							// Util.showProgressDialog("Linking user with Installation.");
							ParseInstallation installation = ParseInstallation
									.getCurrentInstallation();
							System.out
									.println("installation 11111111111111111 = "
											+ installation);
							installation.put("user", user[0]);
							installation.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e == null) {
										// Util.showProgressDialog("Done linking user with Installation.");
										Log.i("AnonymousManager.login",
												"Success saving installation.");
									} else {
										// Util.showProgressDialog("Problem linking user with Installation.");
										Log.e("AnonymousManager.login",
												"Failed saving installation", e);
									}

								}
							});

							// for easy access
							BoardManager.defaultBoard = defaultBoard;

							Log.i(AnonymousManager.class.getSimpleName(),
									"BoardManager.defaultBoard.getId()  = = = "
											+ defaultBoard.getId());

						} catch (ParseException e) {

							// Util.showProgressDialog("Problem with new user initialization.");

							Log.e(AnonymousManager.class.getSimpleName(),
									e.getCode() + " : "
											+ e.getLocalizedMessage());
							return e;
						} finally {
							Util.loginInProgress = false;
						}
						return null;
					}

					@Override
					protected void onPostExecute(ParseException exception) {
						if (exception == null)
							Util.reloadStickers();
					}
				};
				bgTask.execute(user);

			} else {
				// Util.showProgressDialog("Problem logging in anonymously.");

				Log.e("AnonymousManager.login", "Problem anonymous login.",
						exception);
				// Util.showError(Util.context,
				// "Problem signing in anonymous user.");
			}

		}
	}
}
