package com.hafidz.stylo;

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

		// Enable Local Datastore.
		// Parse.enableLocalDatastore(this);
		Parse.initialize(this, "rvL9mFoct6KVwjvIfTCV23qRwKBKlcwPrwPVpvPI",
				"dPxpmhhE7ceXzKwGDpkdWBqWKh7IyWIaJJpd7yJl");

		// push notifications
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
}
