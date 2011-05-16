

package com.trident.android.tv.si.provider.epg;

import android.R.*;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;
import android.app.ListActivity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.R.layout.*;
import android.content.Intent;

/**
 * 
 * Show the details of certain event and allow to go back to the main EPG 
 * @author Pierr
 *
 */

public class EventDetail extends Activity {
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		Button button = new Button(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			button.setText("Item name = " + bundle.getString("NAME")
					+ " --- Go Back ");
		} else {
			button.setText("Go Back");
		}
		context = this;
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,  EPGProvider.class));
				finish();
			}
		});
		relativeLayout.addView(button);
	//	LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
	//			LayoutParams.FILL_PARENT);
	//	setContentView(relativeLayout, params);
	}
	
}