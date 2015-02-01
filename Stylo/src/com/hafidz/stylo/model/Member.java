package com.hafidz.stylo.model;

import android.widget.GridLayout;

public class Member {
	
	//name is unique
	private String name;
	private String email;
	private boolean me;

	private float posY;

	// Reference to member UI
	private GridLayout memberSticker;

	public Member(String name, String email, boolean me, float posY,
			GridLayout memberSticker) {
		super();
		this.name = name;
		this.email = email;
		this.me = me;
		this.posY = posY;
		this.memberSticker = memberSticker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public GridLayout getMemberSticker() {
		return memberSticker;
	}

	public void setMemberSticker(GridLayout memberSticker) {
		this.memberSticker = memberSticker;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isMe() {
		return me;
	}

	public void setMe(boolean me) {
		this.me = me;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

}
