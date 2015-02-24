/**
 * 
 */
package com.hafidz.stylo.listener;

import java.util.List;

import com.hafidz.stylo.manager.BoardManager;
import com.hafidz.stylo.model.Board;
import com.hafidz.stylo.util.Util;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author hafidz
 * 
 */
public class BoardListListener implements OnItemClickListener {

	private List<Board> boards;
	private AlertDialog dialog;

	public BoardListListener(List<Board> boards, AlertDialog dialog) {
		this.boards = boards;
		this.dialog = dialog;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

		Board board = boards.get(pos);

		BoardManager.setCurrentBoard(board);

		Util.reloadStickers();

		dialog.dismiss();

	}

}
