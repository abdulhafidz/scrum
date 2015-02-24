/**
 * 
 */
package com.hafidz.stylo.manager;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.hafidz.stylo.model.Board;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;

/**
 * @author hafidz
 * 
 */
public class BoardManager {

	private static Board defaultBoard;
	private static Board currentBoard;

	public static ParseRole createRole(String name) throws ParseException {
		// role
		ParseRole role = new ParseRole(name);
		role.getRelation("users").add(UserManager.getCurrentUser().getParse());

		// permission
		ParseACL parseACL = new ParseACL();
		parseACL.setReadAccess(UserManager.getCurrentUser().getParse(), true);
		parseACL.setWriteAccess(UserManager.getCurrentUser().getParse(), true);
		role.setACL(parseACL);

		role.save();

		return role;
	}

	/**
	 * MUST BE RUNNING IN BACKGROUND THREAD!!!
	 * 
	 * @param board
	 * @throws ParseException
	 */
	public static void add(Board board) throws ParseException {

		// board
		ParseObject parseBoard = new ParseObject("Board");
		parseBoard.put("id", board.getId());
		parseBoard.put("owner", UserManager.getCurrentUser().getParse()
				.getObjectId());
		parseBoard.put("default", board.isDefaultBoard());
		parseBoard.put("name", board.getName());

		ParseACL boardACL = new ParseACL();
		boardACL.setRoleReadAccess(board.getRole(), true); // all under role can
															// read board
		boardACL.setWriteAccess(UserManager.getCurrentUser().getParse(), true); // only
																				// owner
																				// can
																				// edit
																				// board
		parseBoard.setACL(boardACL);

		parseBoard.save();
	}

	/**
	 * 
	 * MUST BE RUNNING IN BACKGROUND THREAD!!!
	 * 
	 * @param id
	 * @return
	 * @throws ParseException
	 */
	public static Board load(String id) {

		Board board = null;

		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Board");
			query.whereEqualTo("id", id);
			ParseObject result = query.getFirst();

			// load role
			ParseRole role = ParseRole.getQuery().whereEqualTo("name", id)
					.getFirst();

			board = new Board(result.getObjectId(), result.getString("name"),
					result.getString("owner"), result.getBoolean("default"),
					role);
		} catch (Exception e) {
			Log.i(BoardManager.class.getSimpleName(), "Board with id " + id
					+ " not found");
		}

		return board;

	}

	/**
	 * 
	 * MUST BE RUNNING IN BACKGROUND THREAD!!!
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Board loadDefaultBoard() throws ParseException {
		Board board = null;

		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Board");
			query.whereEqualTo("owner", UserManager.getCurrentUser().getParse()
					.getObjectId());
			query.whereEqualTo("default", true);
			ParseObject result = query.getFirst();

			board = new Board(result.getObjectId(), result.getString("name"),
					result.getString("owner"), result.getBoolean("default"),
					null);
		} catch (Exception e) {
			Log.i(BoardManager.class.getSimpleName(), "default board not found");
			// e.printStackTrace();
		}

		return board;

	}

	public static void setDefaultBoard(Board board) {
		defaultBoard = board;
	}

	/**
	 * MUST BE CALLED IN BACKGROUND TREAD ONLY!!!
	 * 
	 * @return
	 */
	public static Board getDefaultBoard() {

		if (BoardManager.defaultBoard == null) {
			try {
				BoardManager.defaultBoard = BoardManager.load(ParseInstallation
						.getCurrentInstallation().getParseUser("user")
						.fetchIfNeeded().getString("defaultBoard"));
			} catch (ParseException e) {
				Log.i("BoardManager.getDefaultBoard",
						"Problem loading default board from server.");
			}
		}

		Log.i("BoardManager.getDefaultBoard", "Default Board ID = "
				+ defaultBoard.getId());

		return BoardManager.defaultBoard;
	}

	/**
	 * MUST BE CALLED IN BACKGROUND TREAD ONLY!!!
	 * 
	 * @return
	 */
	public static Board getCurrentBoard() {
		if (currentBoard == null) {
			Log.i("BoardManager.getCurrentBoard",
					"Current Board is not available, assigning default board to current board.");
			currentBoard = getDefaultBoard();
		}
		return currentBoard;
	}

	public static void setCurrentBoard(Board board) {
		Log.i("BoardManager.setCurrentBoard",
				"Current Board is now " + board.getName());
		BoardManager.currentBoard = board;
	}

	/**
	 * MUST BE CALLED IN BACKGROUND TREAD ONLY!!!
	 */
	public static List<Board> getAll() {

		List<Board> boards = new ArrayList<Board>();

		try {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Board");
			List<ParseObject> results = query.find();

			for (ParseObject obj : results) {

				Board board = new Board(obj.getObjectId(),
						obj.getString("name"), obj.getString("owner"),
						obj.getBoolean("defaultBoard"), null);

				boards.add(board);
			}

		} catch (ParseException e) {
			Log.i("BoardManager.getAll", "No Board found.");
		}

		return boards;
	}

}
