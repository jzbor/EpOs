package de.jzbor.epos.fragments.schedule;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.fragments.RecyclerViewAdapter;
import de.jzbor.hgvinfo.model.Schedule;

public class MyScheduleRecyclerViewAdapter
        extends RecyclerViewAdapter<Schedule, MyScheduleRecyclerViewAdapter.ViewHolder> {

    private int day;

    public MyScheduleRecyclerViewAdapter(Schedule dataObject, int day) {
        super(dataObject, R.layout.fragment_schedule_lesson);
        this.day = day;
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyScheduleRecyclerViewAdapter.ViewHolder holder, int position) {
        // Create new ViewHolder
        String lesson = dataObject.getDay(day)[position];
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
        holder.time.setText(dataObject.lessonTime(position));

        handleItemHighlighting(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataObject.getDay(day).length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView time;
        public final TextView subject;
        public final TextView room;

        public ViewHolder(View view) {
            super(view);
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
