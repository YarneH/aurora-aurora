package com.aurora.aurora;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The adapter for the RecyclerView of the file-cards
 */
public class CardFileAdapter extends RecyclerView.Adapter<CardFileAdapter.CardFileViewHolder> {
    private int mAmount = 100;
    private int mSelectedIndex = -1;

    private int mCardHeight;
    private int mExpandHeight;

    public CardFileAdapter(){}

    @NonNull
    @Override
    public CardFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.card_file, viewGroup, false);
        return new CardFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardFileViewHolder cardFileViewHolder, int i) {
        cardFileViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mAmount;
    }

    public class CardFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private CardView mCardView;
        private Button mButton;
        private int index;

        public CardFileViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cv_file);
            mTextView = (TextView) itemView.findViewById(R.id.tv_card_title);
            mButton = (Button) itemView.findViewById(R.id.button_card_file);
            mCardView.setOnClickListener(this);
            mButton.setOnClickListener(this);
        }

        public void bind(int i){
            index = i;
            mTextView.setText("File " + String.valueOf(i));

            if (i == mSelectedIndex) {
                expand(mCardView);
            } else {
                collapse(mCardView);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.cv_file) {
                mSelectedIndex = index;
                expand(view);
            } else if(view.getId() == R.id.button_card_file) {
                Snackbar.make(view, "This will open " + mTextView.getText() + " with the previously selected plugin", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }


    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        CardView mCardView = (CardView) v;
        RecyclerView mRecyclerView = (RecyclerView) mCardView.getParent();
        final int targetHeight = mRecyclerView.getMeasuredHeight();
        final int childHeight = mCardView.getMeasuredHeight();

        mCardView.setMinimumHeight(targetHeight);
    }

    public static void collapse(final View v) {
        CardView mCardView = (CardView) v;
        final int initialHeight = v.getMeasuredHeight();

    }
}
