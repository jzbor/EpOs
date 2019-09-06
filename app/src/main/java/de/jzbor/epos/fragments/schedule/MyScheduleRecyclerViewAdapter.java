package de.jzbor.epos.fragments.schedule;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.data.Schedule;

public class MyScheduleRecyclerViewAdapter extends RecyclerView.Adapter<MyScheduleRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "MSRVA";
    private int day;
    private Schedule schedule;

    public MyScheduleRecyclerViewAdapter(Schedule schedule, int day) {
        this.day = day;
        this.schedule = schedule;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_schedule_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Create new ViewHolder
        String lesson = schedule.getDay(day)[position];
        String[] properties = lesson.split(" ");
        if (lesson.length() == 0 && properties.length < 2) {
            new Exception("parsing error: (" + position + "):" + lesson).printStackTrace();
            // @TODO Error gets thrown at every free lesson
            holder.subject.setText("");
            holder.room.setText("");
        } else if (properties.length < 2) {
            holder.subject.setText(String.format("? %s", lesson));
            holder.room.setText("");
        } else {
            holder.subject.setText(properties[0]);
            holder.room.setText(properties[1]);
        }
        holder.time.setText(schedule.lessonTime(position));
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
        return schedule.getDay(day).length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView time;
        public final TextView subject;
        public final TextView room;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            time = view.findViewById(R.id.time);
            subject = view.findViewById(R.id.subject);
            room = view.findViewById(R.id.room);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + subject.getText() + "'";
        }
    }
}
