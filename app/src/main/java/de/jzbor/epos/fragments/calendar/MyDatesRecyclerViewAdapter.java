package de.jzbor.epos.fragments.calendar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import de.jzbor.epos.R;
import de.jzbor.epos.data.Calendar;

public class MyDatesRecyclerViewAdapter extends RecyclerView.Adapter<MyDatesRecyclerViewAdapter.ViewHolder> {

    private Calendar calendar;

    public MyDatesRecyclerViewAdapter(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dates_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String key = calendar.getDatesAfter(new Date()).keySet().toArray(new String[0])[position];
        String value = calendar.getDatesAfter(new Date()).get(key);

        holder.date.setText(key);
        holder.subject.setText(value);

        // Marks subs which apply to the student
        // Mark every second item for better UI
        if (position % 2 == 1) {
            double opacity = 0.3;
            int color = holder.view.getResources().getColor(R.color.primaryLightColor);
            holder.view.setBackgroundColor(color);
            holder.view.getBackground().setAlpha((int) (opacity * 256));
        }
    }

    @Override
    public int getItemCount() {
        return calendar.getDatesAfter(new Date()).size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView date;
        public final TextView subject;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            date = view.findViewById(R.id.date);
            subject = view.findViewById(R.id.subject);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + date.getText() + ": " + subject.getText() + "'";
        }
    }
}
