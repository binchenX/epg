package com.trident.android.tv.si.provider.epg;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EventCursorAdaptor extends SimpleCursorAdapter {

	private int[] mFrom;
	private int[] mTo;
	private Cursor mCursor;
	private int start_time_index = -1;
	
	public EventCursorAdaptor(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		mTo = to;
		mCursor = c;
		findColumns(from);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewBinder binder = getViewBinder();
		final int count = mTo.length;
		final int[] from = mFrom;
		final int[] to = mTo;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, cursor, from[i]);
				}

				if (!bound) {
				
					//convert the UTC time to a human readable Time LocalTime
					String text;
					
					
					
					if(i == start_time_index)
					{
						
						//Note that start_time * 1000 will be overflow the int
						long start_time = cursor.getInt(from[i]);
					    text = (new Date(start_time * 1000 )).toString();
						
					} else {
						text = cursor.getString(from[i]);
						
					}
					
					if (text == null) {
						text = "";
					}

					if (v instanceof TextView) {
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						setViewImage((ImageView) v, text);
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleCursorAdapter");
					}
				}
			}
		}
	}
	
    /**
     * Create a map from an array of strings to an array of column-id integers in mCursor.
     * If mCursor is null, the array will be discarded.
     * 
     * @param from the Strings naming the columns of interest
     */
    private void findColumns(String[] from) {
        if (mCursor != null) {
            int i;
            int count = from.length;
            if (mFrom == null || mFrom.length != count) {
                mFrom = new int[count];
            }
            for (i = 0; i < count; i++) {
                mFrom[i] = mCursor.getColumnIndexOrThrow(from[i]);
                //System.out.println("i:" + i + " mFrom[i] " + mFrom[i] + " col " + from[i]);
                if(from[i].equals(Events.START_TIME))
                {
                	start_time_index = i;
                }
            }
        } else {
            mFrom = null;
        }
    }

}