/**
 * 
 */
package com.hafidz.stylo.model;

import com.parse.Parse;
import com.parse.ParseRole;

/**
 * @author hafidz
 * 
 */
public class Board {

	private String id;
	private String name;
	private String owner;
	boolean defaultBoard;
	private ParseRole role;

	public Board(String id, String name, String owner, boolean defaultBoard,
			ParseRole role) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.defaultBoard = defaultBoard;
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isDefaultBoard() {
		return defaultBoard;
	}

	public ParseRole getRole() {
		return role;
	}

}
