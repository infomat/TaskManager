package com.conestogac.mytask;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by infomat on 16-06-11.
 */
public class MySpinner extends Spinner{


    OnItemSelectedListener listener;
    int prevPos = -1;

    public MySpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position)
    {
        super.setSelection(position);

        if (position == getSelectedItemPosition() && prevPos == position) {
            listener.onItemSelected(null, null, position, 0);
        }
        prevPos = position;

    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener)
    {
        this.listener = listener;
    }

}
