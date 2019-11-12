package de.jzbor.epos.fragments.news;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import de.jzbor.epos.R;
import de.jzbor.hgvinfo.model.Calendar;
import de.jzbor.hgvinfo.model.Notifications;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    private Notifications news;

    public MyNewsRecyclerViewAdapter(Notifications news) {
        this.news = news;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // @TODO Add own layout for fragment
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = news.get(position).getTitle();
        String content = news.get(position).getContent();

        holder.title.setText(title);
        holder.content.setText(content);

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
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView content;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + ": " + content.getText() + "'";
        }
    }
}
