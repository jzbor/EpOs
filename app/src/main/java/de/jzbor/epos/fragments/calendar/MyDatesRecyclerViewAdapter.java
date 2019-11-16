package de.jzbor.epos.fragments.calendar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import de.jzbor.epos.R;
import de.jzbor.epos.fragments.RecyclerViewAdapter;
import de.jzbor.hgvinfo.model.Calendar;

public class MyDatesRecyclerViewAdapter
        extends RecyclerViewAdapter<Calendar, MyDatesRecyclerViewAdapter.ViewHolder> {

    public MyDatesRecyclerViewAdapter(Calendar dataObject) {
        super(dataObject, R.layout.fragment_dates_date);
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyDatesRecyclerViewAdapter.ViewHolder holder, int position) {
        String key = dataObject.getDatesAfter(new Date()).keySet().toArray(new String[0])[position];
        String value = dataObject.getDatesAfter(new Date()).get(key);

        holder.date.setText(key);
        holder.subject.setText(value);

        handleItemHighlighting(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataObject.getDatesAfter(new Date()).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView date;
        public final TextView subject;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            subject = view.findViewById(R.id.subject);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + date.getText() + ": " + subject.getText() + "'";
        }
    }
}
