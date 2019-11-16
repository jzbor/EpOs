package de.jzbor.epos.fragments.news;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.fragments.RecyclerViewAdapter;
import de.jzbor.hgvinfo.model.Notifications;

public class MyNewsRecyclerViewAdapter
        extends RecyclerViewAdapter<Notifications, MyNewsRecyclerViewAdapter.ViewHolder> {

    public MyNewsRecyclerViewAdapter(Notifications dataObject) {
        super(dataObject, R.layout.fragment_news_item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = dataObject.get(position).getTitle();
        String content = dataObject.get(position).getContent();

        holder.title.setText(title);
        holder.content.setText(content);

        handleItemHighlighting(holder, position);
    }

    @Override
    public int getItemCount() {
        return dataObject.size();
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView content;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + ": " + content.getText() + "'";
        }
    }
}
