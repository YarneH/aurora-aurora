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
import android.widget.FrameLayout;
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

    public CardFileAdapter() {
    }

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

        public void bind(int i) {
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
            if (view.getId() == R.id.cv_file) {
                RecyclerView recyclerView = (RecyclerView) view.getParent();
                if (mSelectedIndex == -1) {
                    mSelectedIndex = index;
                    expand(view);
                    recyclerView.scrollToPosition(index);
                } else if (mSelectedIndex == index) {
                    mSelectedIndex = -1;
                    collapse(view);
                    recyclerView.scrollToPosition(index);
                } else {
                    CardFileViewHolder prev = (CardFileViewHolder) recyclerView.findViewHolderForLayoutPosition(mSelectedIndex);
                    if(prev != null) {
                        collapse(prev.mCardView);
                    }
                    mSelectedIndex = index;
                    expand(view);
                    recyclerView.scrollToPosition(index);
                }
            } else if (view.getId() == R.id.button_card_file) {

            }
        }
    }


    public static void expand(final View v) {
        FrameLayout detailView = (FrameLayout) v.findViewById(R.id.cv_fl_detail);
        FrameLayout baseView = (FrameLayout) v.findViewById(R.id.cv_fl_base_card);
        detailView.setVisibility(View.VISIBLE);
        baseView.setVisibility(View.GONE);
    }

    public static void collapse(final View v) {
        FrameLayout detailView = (FrameLayout) v.findViewById(R.id.cv_fl_detail);
        FrameLayout baseView = (FrameLayout) v.findViewById(R.id.cv_fl_base_card);
        detailView.setVisibility(View.GONE);
        baseView.setVisibility(View.VISIBLE);
    }
}
