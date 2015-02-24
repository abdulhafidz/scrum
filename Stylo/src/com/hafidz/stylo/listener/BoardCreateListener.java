/**
 * 
 */
package com.hafidz.stylo.listener;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hafidz.stylo.R;
import com.hafidz.stylo.manager.BoardManager;
import com.hafidz.stylo.manager.UserManager;
import com.hafidz.stylo.model.Board;
import com.hafidz.stylo.util.Util;
import com.parse.ParseException;
import com.parse.ParseRole;

/**
 * @author hafidz
 * 
 */
public class BoardCreateListener implements android.view.View.OnClickListener {

	private Board board;
	private AlertDialog createDialog;
	private String name;

	public BoardCreateListener(AlertDialog dialog) {
		super();
		this.createDialog = dialog;
	}

	@Override
	public void onClick(View arg0) {
		EditText nameEdit = (EditText) createDialog
				.findViewById(R.id.boardCreateName);

		name = nameEdit.getText().toString();

		// validation
		if (name == null || name.trim().isEmpty()) {
			nameEdit.setError("Please fill in the name.");
			return;
		}

		AsyncTask<Object, Void, ParseException> bgTask = new AsyncTask<Object, Void, ParseException>() {
			@Override
			protected void onPreExecute() {
				Util.startLoading();
			}

			@Override
			protected ParseException doInBackground(Object... args) {
				try {
					String id = Util.generateBoardId();

					// create role for board
					ParseRole role = BoardManager.createRole(id);

					// create new board
					board = new Board(id, name, UserManager.getCurrentUser()
							.getParse().getObjectId(), false, role);
					BoardManager.add(board);
				} catch (ParseException e) {
					Log.e("BoardCreateListener.onClick",
							"Problem creating Board.", e);
					return e;
				}

				return null;
			}

			@Override
			protected void onPostExecute(ParseException exception) {
				Util.stopLoading();
				createDialog.dismiss();

				// no error
				if (exception == null) {
					BoardManager.setCurrentBoard(board);

					Util.showSuccess(Util.context, "Board '" + name
							+ "' created.");
				}
				// got error
				else {
					Util.showError(Util.context, "Problem creating board.");

				}

				Util.reloadStickers();
			}
		};

		bgTask.execute();

	}
}
