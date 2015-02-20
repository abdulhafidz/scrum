package com.hafidz.stylo.model;

import com.parse.ParseUser;

public class User {

	private String email;
	private String name;
	private ParseUser parse;

	// private String defaultBoard;

	public User(String email, String name, ParseUser parse) {

		this.email = email;
		this.parse = parse;
		this.name = name;
		// this.defaultBoard = defaultBoard;
	}

	public String getEmail() {
		return email;
	}

	public ParseUser getParse() {
		return parse;
	}

	public String getName() {
		return name;
	}

	// public String getDefaultBoard() {
	// return this.defaultBoard;
	// }

}
