package de.jzbor.epos.fragments.subplan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.elternportal.Schedule;
import de.jzbor.epos.elternportal.SubstituteDay;

public class MySubplanRecyclerViewAdapter extends RecyclerView.Adapter<MySubplanRecyclerViewAdapter.ViewHolder> {

    private SubstituteDay substituteDay;

    public MySubplanRecyclerViewAdapter(SubstituteDay substituteDay) {
        this.substituteDay = substituteDay;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_subplan_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String[] substitution = substituteDay.getSubstitutions().get(position);
        holder.number.setText(substitution[0]);
        holder.teacher.setText(substitution[1]);
        holder.subject.setText(substitution[2]);
        holder.room.setText(substitution[3]);
        holder.info.setText(substitution[4]);

        // Marks subs which apply to the student
        try {
            Schedule schedule = (Schedule) App.openObject(holder.view.getContext().getCacheDir(), "sccache.sc");
            if (schedule.inClasses(holder.subject.getText().toString())) {
                double opacity = 0.3;
                int color = holder.view.getResources().getColor(R.color.primaryLightColor);
                holder.view.setBackgroundColor(color);
                holder.view.getBackground().setAlpha((int) (opacity * 256));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return substituteDay.getSubstitutions().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView number;
        public final TextView subject;
        public final TextView teacher;
        public final TextView room;
        public final TextView info;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            number = view.findViewById(R.id.number);
            subject = view.findViewById(R.id.subject);
            teacher = view.findViewById(R.id.teacher);
            room = view.findViewById(R.id.room);
            info = view.findViewById(R.id.info);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + subject.getText() + "'";
        }
    }
}
