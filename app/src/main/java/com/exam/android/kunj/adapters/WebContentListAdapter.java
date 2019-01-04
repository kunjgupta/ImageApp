package com.exam.android.kunj.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.exam.android.kunj.R;
import com.exam.android.kunj.db.models.WordCountModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */

public class WebContentListAdapter extends RecyclerView.Adapter<WebContentListAdapter.MyViewHolder> {
    private List<WordCountModel> mOriginalWordCountModelList;
    private List<WordCountModel> mFilterWordCountModelList;
    private ItemFilter mFilter = new ItemFilter();

    public WebContentListAdapter(List<WordCountModel> wordCountModelList) {
        this.mOriginalWordCountModelList = wordCountModelList;
        this.mFilterWordCountModelList = wordCountModelList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mWordTextView;
        TextView mCountTextView;

        MyViewHolder(View v) {
            super(v);
            mWordTextView = (TextView) v.findViewById(R.id.word_count_adapter_word_text_view);
            mCountTextView = (TextView) v.findViewById(R.id.word_count_adapter_count_text_view);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        MyViewHolder vh;
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.word_count_list_adapter, parent, false);
        vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        WordCountModel wordCountModel = mFilterWordCountModelList.get(position);
        holder.mCountTextView.setText(""+wordCountModel.getCount());
        holder.mWordTextView.setText(wordCountModel.getWord());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFilterWordCountModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<WordCountModel> list = mOriginalWordCountModelList;

            int count = list.size();
            final List<WordCountModel> nlist = new ArrayList<WordCountModel>(count);

            WordCountModel filterableWordCountModel ;

            for (int i = 0; i < count; i++) {
                filterableWordCountModel = list.get(i);
                if (filterableWordCountModel.getWord().toLowerCase().contains(filterString)) {
                    nlist.add(filterableWordCountModel);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilterWordCountModelList = (ArrayList<WordCountModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
