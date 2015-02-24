/**
 * 
 */
package com.hafidz.stylo.listener;

import java.util.List;

import com.hafidz.stylo.MainActivity;
import com.hafidz.stylo.R;
import com.hafidz.stylo.manager.BoardManager;
import com.hafidz.stylo.model.Board;
import com.hafidz.stylo.util.Util;
import com.parse.ParseException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author hafidz
 * 
 */
public class DrawerItemListener implements ListView.OnItemClickListener {

	private MainActivity main;
	private DrawerLayout drawerLayout;
	private ListView boardListView;
	private AlertDialog boardsDialog;

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

		case 1:
			drawerLayout.closeDrawers();
			AlertDialog.Builder dialogBuilder = new Builder(Util.context);
			dialogBuilder.setTitle("Select Board");
			dialogBuilder.setNegativeButton(android.R.string.cancel, null);
			boardListView = (ListView) LayoutInflater.from(Util.context)
					.inflate(R.layout.board_list_layout, null);
			boardListView
					.setAdapter(new ArrayAdapter<String>(Util.context,
							android.R.layout.simple_list_item_1,
							new String[] { "..." }));

			dialogBuilder.setView(boardListView);
			boardsDialog = dialogBuilder.create();
			boardsDialog.show();

			AsyncTask<Object, Void, List<Board>> bgTask = new AsyncTask<Object, Void, List<Board>>() {
				@Override
				protected void onPreExecute() {
					Util.startLoading();
				}

				@Override
				protected List<Board> doInBackground(Object... args) {
					return BoardManager.getAll();
				}

				@Override
				protected void onPostExecute(List<Board> boards) {
					Util.stopLoading();

					ArrayAdapter<Board> adapter = new ArrayAdapter<Board>(
							Util.context, android.R.layout.simple_list_item_1,
							boards);
					boardListView.setAdapter(adapter);
					boardListView.setOnItemClickListener(new BoardListListener(
							boards, boardsDialog));

				}
			};

			bgTask.execute();
			break;

		case 2:
			Util.showGuide(main.getApplicationContext());

			drawerLayout.closeDrawers();
			break;

		case 3:
			Util.showAbout(main.getApplicationContext());

			drawerLayout.closeDrawers();
			break;
		}
	}

}
