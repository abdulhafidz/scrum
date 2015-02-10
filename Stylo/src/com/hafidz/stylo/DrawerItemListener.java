/**
 * 
 */
package com.hafidz.stylo;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author hafidz
 * 
 */
public class DrawerItemListener implements ListView.OnItemClickListener {

	private MainActivity main;
	private DrawerLayout drawerLayout;

	public DrawerItemListener(MainActivity mainActivity,
			DrawerLayout drawerLayout) {
		this.main = mainActivity;
		this.drawerLayout = drawerLayout;
	}

	@Override
	public void onItemClick(AdapterView parent, View view, int pos, long id) {

		switch (pos) {
		case 0:
			main.reloadStickers();

			drawerLayout.closeDrawers();
			break;
		}
	}

}
