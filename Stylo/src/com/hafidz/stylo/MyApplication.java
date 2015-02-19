package com.hafidz.stylo;

import com.hafidz.stylo.manager.AnonymousManager;
import com.hafidz.stylo.manager.UserManager;
import com.hafidz.stylo.util.Util;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Util.context = getApplicationContext();

		// Enable Local Datastore.
		// Parse.enableLocalDatastore(this);
		Parse.initialize(this, "rvL9mFoct6KVwjvIfTCV23qRwKBKlcwPrwPVpvPI",
				"dPxpmhhE7ceXzKwGDpkdWBqWKh7IyWIaJJpd7yJl");

		// login , and subscribe parse push
		if (UserManager.getCurrentUser() == null) {
			System.out.println("logging in anonymously");
			AnonymousManager.login(true);
		} else {
			System.out.println("user already in cache!!!");
		}

		// push notifications
		// ParsePush.subscribeInBackground(Util.getActiveBoard(),
		// new SaveCallback() {
		// @Override
		// public void done(ParseException e) {
		// if (e == null) {
		// Log.d("com.parse.push",
		// "successfully subscribed to the broadcast channel.");
		// } else {
		// Log.e("com.parse.push",
		// "failed to subscribe for push", e);
		// }
		// }
		// });
	}
}
