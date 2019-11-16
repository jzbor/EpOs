package de.jzbor.epos.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import de.jzbor.epos.R;

public abstract class RecyclerViewAdapter<T extends Serializable, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected T dataObject;
    private int vhLayoutId;

    public RecyclerViewAdapter(T dataObject, int vhLayoutId) {
        this.dataObject = dataObject;
        this.vhLayoutId = vhLayoutId;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(vhLayoutId, parent, false);
        return createViewHolder(view);
    }

    protected void handleItemHighlighting(VH viewHolder, int position) {
        if (markItem(viewHolder, position)) {
            double opacity = 0.3;
            int color = viewHolder.itemView.getResources().getColor(R.color.primaryLightColor);
            viewHolder.itemView.setBackgroundColor(color);
            viewHolder.itemView.getBackground().setAlpha((int) (opacity * 256));
        }
    }

    protected abstract VH createViewHolder(View view); // e.g. new MyDatesRecyclerViewAdapter.ViewHolder(view)

    protected boolean markItem(VH viewHolder, int position) {
        return isOdd(position);
    }

    protected boolean isOdd(int i) {
        return i % 2 == 1;
    }
}
