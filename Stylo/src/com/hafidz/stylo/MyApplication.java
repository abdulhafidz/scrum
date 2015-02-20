package com.hafidz.stylo;

import android.app.Application;
import android.util.Log;

import com.hafidz.stylo.manager.AnonymousManager;
import com.hafidz.stylo.util.Util;
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

		Util.context = getApplicationContext();

		// Enable Local Datastore.
		// Parse.enableLocalDatastore(this);
		Parse.initialize(this, "rvL9mFoct6KVwjvIfTCV23qRwKBKlcwPrwPVpvPI",
				"dPxpmhhE7ceXzKwGDpkdWBqWKh7IyWIaJJpd7yJl");

		// ParseUser parseUser = ParseInstallation.getCurrentInstallation()
		// .getParseUser("user").fetchIfNeeded();

		ParseInstallation install = ParseInstallation.getCurrentInstallation();
		Log.i("MyApplication.onCreate", "installation = " + install);

		ParseUser user = null;
		try {
			// FIXME : do this in background thread :(
			user = install.getParseUser("user").fetchIfNeeded();
		} catch (Exception e) {
			Log.i("MyApplication.onCreate", "Failed to get ParseUser");
		}

		if (user == null) {

			Log.i("MyApplication.onCreate", "Logging in anonymously");

			AnonymousManager.login(true);

		} else {

			Log.i("MyApplication.onCreate",
					"Cool, user already exists in installation");

			// push notifications
			ParsePush.subscribeInBackground(user.getString("defaultBoard"),
					new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								Log.i("MyApplication.onCreate",
										"successfully subscribed to the broadcast channel.");
							} else {
								Log.e("MyApplication.onCreate",
										"failed to subscribe for push", e);
							}
						}
					});

		}
	}
}
