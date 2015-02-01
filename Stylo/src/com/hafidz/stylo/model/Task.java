/**
 * 
 */
package com.hafidz.stylo.model;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author hafidz
 * 
 */
public class Task {

	public static final int STATUS_TODO = 1;
	public static final int STATUS_IN_PROGRESS = 2;
	public static final int STATUS_DONE = 3;
	public static final int STATUS_ROAD_BLOCK = 0;

	private int id;
	private String title;
	private String description;
	private Member owner;
	private int points;
	private int status;

	private float posX;
	private float posY;

	// reference to the small task
	private RelativeLayout smallTask;

	public Task(int id, float posX, float posY, RelativeLayout smallTask) {
		this.id = id;
		this.setSmallTask(smallTask);
		setPos(posX, posY);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getId() {
		return id;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPos(float x, float y) {
		this.posX = x;
		this.posY = y;

		// TODO : from postion, get the status
		this.status = STATUS_TODO;
	}

	public RelativeLayout getSmallTask() {
		return smallTask;
	}

	public void setSmallTask(RelativeLayout smallTask) {
		this.smallTask = smallTask;
	}

	public Member getOwner() {
		return owner;
	}

	public void setOwner(Member owner) {
		this.owner = owner;
	}

}
