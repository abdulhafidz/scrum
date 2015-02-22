package com.hafidz.stylo;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.hafidz.stylo.manager.AnonymousManager;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Util.context = MyApplication.this;

		// Parse.enableLocalDatastore(this);
		Parse.initialize(this, "rvL9mFoct6KVwjvIfTCV23qRwKBKlcwPrwPVpvPI",
				"dPxpmhhE7ceXzKwGDpkdWBqWKh7IyWIaJJpd7yJl");

		ParseInstallation install = ParseInstallation.getCurrentInstallation();
		Log.i("MyApplication.onCreate", "installation = " + install);

		// Util.showProgressDialog("Getting anonymous user.");
		AsyncTask<ParseInstallation, Void, ParseUser> bgTask = new AsyncTask<ParseInstallation, Void, ParseUser>() {
			@Override
			protected void onPreExecute() {
				// Util.startLoading();
			}

			@Override
			protected ParseUser doInBackground(ParseInstallation... args) {
				ParseUser user = null;
				try {
					// FIXME : do this in background thread :(
					user = args[0].getParseUser("user").fetchIfNeeded();
				} catch (Exception e) {
					Log.i("MyApplication.onCreate",
							"Cannot get the User from the Installation");
				}
				return user;
			}

			@Override
			protected void onPostExecute(ParseUser user) {
				// Util.stopLoading();

				// got user
				if (user != null) {

					// Util.showProgressDialog("Anonymous user loaded.");

					Log.i("MyApplication.onCreate",
							"Cool, user already exists in installation");

					// push notifications
					ParsePush.subscribeInBackground(
							user.getString("defaultBoard"), new SaveCallback() {
								@Override
								public void done(ParseException e) {
									if (e == null) {
										Log.i("MyApplication.onCreate",
												"successfully subscribed to the broadcast channel.");
									} else {
										Log.e("MyApplication.onCreate",
												"failed to subscribe for push",
												e);
									}
								}
							});
				}

				// no user
				else {
					// Util.showProgressDialog("Logging in anonymously.");
					Log.i("MyApplication.onCreate", "Logging in anonymously");
					AnonymousManager.login(true);
				}
			}
		};
		bgTask.execute(install);

	}
}
