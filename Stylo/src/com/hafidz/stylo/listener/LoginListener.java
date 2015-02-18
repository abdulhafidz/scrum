/**
 * 
 */
package com.hafidz.stylo.listener;

import com.hafidz.stylo.R;
import com.hafidz.stylo.Util;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author hafidz
 * 
 * 
 */
public class LoginListener implements OnClickListener {

	@Override
	public void onClick(View v) {

		// register button clicked
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				Util.context);

		dialogBuilder.setTitle("Sign Up");
		dialogBuilder.setView(LayoutInflater.from(Util.context).inflate(
				R.layout.signup_layout, null));

		dialogBuilder.setPositiveButton(android.R.string.ok,
				new SignupListener());
		dialogBuilder.setNegativeButton(android.R.string.cancel, null);

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();

	}

}
