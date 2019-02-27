package com.aurora.aurora;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * The adapter for the RecyclerView of the file-cards
 */
public class CardFileAdapter extends RecyclerView.Adapter<CardFileAdapter.CardFileViewHolder> {
    private int mAmount = 100;

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
        private Button mButton;

        public CardFileViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_card_title);
            mButton = (Button) itemView.findViewById(R.id.button_card_file);
            mButton.setOnClickListener(this);
        }

        public void bind(int i){
            mTextView.setText("File " + String.valueOf(i));
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view, "This will open " + mTextView.getText() + " with the previously selected plugin", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
