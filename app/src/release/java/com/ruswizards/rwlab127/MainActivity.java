/**
 * Copyright (C) 2014 Rus Wizards
 * <p/>
 * Created: 15.92.2014
 * Vladimir Farafonov
 */
package com.ruswizards.rwlab127;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main activity class
 */
public class MainActivity extends ActionBarActivity {

	private static final String STATE_LIST = "ListView";
	private CustomRecyclerViewAdapter customRecyclerViewAdapter_;
	private List<CustomViewForList> itemsList_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Retain state
		if (savedInstanceState != null) {
			itemsList_ = (List<CustomViewForList>) savedInstanceState.getSerializable(STATE_LIST);
		} else {
			itemsList_ = new ArrayList<>();
			Random generator = new Random();
			for (int i = 0; i < 5; i++) {
				CustomViewForList customViewForList = new CustomViewForList(
						this, randomString(5), randomString(15), generator.nextInt(4));
				itemsList_.add(customViewForList);
			}
		}
		//Set up RecyclerView
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		//Set up item touch listener
		recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
			private float initialX_;                            // First touch coordinates
			private float initialY_;                            // First touch coordinates
			private float summX_;                                // Movement along X axis
			private float summY_;                                // Movement along Y axis
			private float tempX_;
			private float tempY_;

			/**
			 * Monitors touches inside Recycler View. If got an action up motion event, checks if
			 * previously was swiping right gesture. After detecting right swipe gesture calls
			 * {@link #deleteItem(android.support.v7.widget.RecyclerView)} to delete first touched
			 * item.
			 * @param rv Recycler View object
			 * @param event Motion event
			 * @return False to handle motion event to super. True to handle motion event
			 * to onTouchEvent
			 */
			@Override
			public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
				int action = event.getActionMasked();
				if (action == MotionEvent.ACTION_DOWN) {
					// Save coordinates of touch
					initialX_ = event.getX();
					initialY_ = event.getY();
					// Set initial values
					summX_ = 0;
					summY_ = 0;
					tempX_ = event.getX();
					tempY_ = event.getY();
					return false;
				} else if (action == MotionEvent.ACTION_MOVE) {
					// Counts overall movement while touch continues based on previous step
					// coordinates
					summX_ = summX_ + event.getX() - tempX_;
					summY_ = summY_ + event.getY() - tempY_;
					//Saves last coordinates
					tempX_ = event.getX();
					tempY_ = event.getY();
					return false;
				} else if (action == MotionEvent.ACTION_UP) {
					// Delete item if swiped to the right
					if (summX_ > 0 && summX_ > Math.abs(summY_)) {
						deleteItem(rv);
					}
					return false;
				} else {
					return false;
				}
			}

			/**
			 * Deletes item from RecyclerView and notifies RecycleViewAdapter
			 * @param rv RecyclerView object
			 */
			private void deleteItem(RecyclerView rv) {
				View itemView = rv.findChildViewUnder(initialX_, initialY_);
				if (itemView != null) {
					int position = rv.getChildPosition(itemView);
					// Check if animation of items deleting were in progress while user swiped to
					// delete last item in the list. Needed to ensure correct item deleting and
					// avoid ArrayIndexOutOfBoundsException
					if (position == itemsList_.size()) {
						position = itemsList_.size() - 1;
					}

					customRecyclerViewAdapter_.notifyItemRemoved(position);
					itemsList_.remove(position);
				}
			}

			@Override
			public void onTouchEvent(RecyclerView rv, MotionEvent e) {
			}
		});

		// Specify and set up an adapter
		customRecyclerViewAdapter_ = new CustomRecyclerViewAdapter(itemsList_);
		recyclerView.setAdapter(customRecyclerViewAdapter_);
		recyclerView.addItemDecoration(
				new DividersItemDecoration(getResources().getDrawable(R.drawable.divider),
						(int) getResources().getDimension(R.dimen.divider_padding_left))
		);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Puts array with items in out state bundle
		outState.putSerializable(STATE_LIST, (java.io.Serializable) itemsList_);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Generates random string
	 *
	 * @param length Length of the string
	 * @return Generated string
	 */
	private String randomString(int length) {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		char tempChar;
		for (int i = 0; i < length; i++) {
			tempChar = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	/**
	 * Just shows a toast. It is an implemented method similar to onClick but from
	 * {@link com.ruswizards.rwlab127.CustomViewForList} class
	 *
	 * @param v View
	 *//*
	public void onSpecialClick(View v) {
		Toast.makeText(this, getString(R.string.toast_text), Toast.LENGTH_SHORT).show();
	}*/
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.floating_action_button:
				// Add element into RecycleView if floating button was pressed
				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
				int position = 0;
				recyclerView.getLayoutManager().scrollToPosition(position);
				addRandomItem(position);
				customRecyclerViewAdapter_.notifyItemInserted(position);
				break;
		}
	}

	/**
	 * Adds random item to list
	 *
	 * @param position Position of inserting
	 */
	private void addRandomItem(int position) {
		CustomViewForList customViewForList = new CustomViewForList(
				this, randomString(5), randomString(15), new Random().nextInt(4));
		itemsList_.add(position, customViewForList);
	}
}
