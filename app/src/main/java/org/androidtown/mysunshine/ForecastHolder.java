package org.androidtown.mysunshine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ChaeYoungDo on 2015-08-15.
 */
public class ForecastHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView textView;
    public ForecastHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.list_item_forecast_textview);
    }
    @Override
    public void onClick(View v) {

    }
}