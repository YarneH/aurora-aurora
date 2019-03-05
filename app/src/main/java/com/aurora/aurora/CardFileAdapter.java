package com.aurora.aurora;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * The adapter for the RecyclerView of the file-cards
 */
public class CardFileAdapter extends RecyclerView.Adapter<CardFileAdapter.CardFileViewHolder> {
    private static final int NO_DETAILS = -1;
    // TODO: Remove dummy amount
    private static final int DUMMY_AMOUNT = 100;
    private int mAmount = DUMMY_AMOUNT;
    // value of the currently selected file (file card that is expanded)
    private int mSelectedIndex = NO_DETAILS;

    public CardFileAdapter() {
        // TODO: This could take an argument as input (which contains the recent files)
    }

    /**
     * Called on creation of a card.
     *
     * @param viewGroup view that contains other views. Used to set layout (inflate) of card
     * @param i         not used, index of the card
     * @return the CardFileViewHolder
     */
    @NonNull
    @Override
    public CardFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.card_file, viewGroup, false);
        return new CardFileViewHolder(view);
    }

    /**
     * Called when card comes into view (the screen and some space above and beneath that.
     * Passes the call to bind()
     *
     * @param cardFileViewHolder the card that is bound
     * @param i                  the index of the bound card
     */
    @Override
    public void onBindViewHolder(@NonNull CardFileViewHolder cardFileViewHolder, int i) {
        cardFileViewHolder.bind(i);
    }

    /**
     * Retreive number of items in the list.
     *
     * @return number of items in the list
     */
    @Override
    public int getItemCount() {
        return mAmount;
    }

    /**
     * ViewHolder for the recyclerview. Holds the file cards.
     */
    public class CardFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private CardView mCardView;
        private Button mButton;
        private int index;

        // TODO: add fields for the details about the file

        /**
         * Constructor for the ViewHolder.
         * Sets the fields for the contents of the card.
         *
         * @param itemView the card view
         */
        public CardFileViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cv_file);
            mTextView = (TextView) itemView.findViewById(R.id.tv_card_title);
            mButton = (Button) itemView.findViewById(R.id.button_card_file);
            // The card itself is clickable (for details), but also the open button.
            mCardView.setOnClickListener(this);
            mButton.setOnClickListener(this);
        }

        /**
         * Called by onBindViewHolder in CardFileAdapter. Actions that should be taken when the card
         * is rebound, or when the card is about to come into view. Since containers are reused, make
         * sure this is done correctly.
         *
         * @param i index of the card
         */
        public void bind(int i) {
            index = i;
            // Set name of the correct file to the reused container.
            mTextView.setText(String.format(Locale.getDefault(), "File %d", i));

            /*
            If the card is the selected card, expand it (as it was when it was scrolled away
            from screen). Otherwise collapse. Collapse must happen, because the container might
            still be expanded from the reuse of another container.
             */
            if (i == mSelectedIndex) {
                expand(mCardView);
            } else {
                collapse(mCardView);
            }
        }

        /**
         * Defines what happens on click of the card. This can be a button or the card itself.
         *
         * @param view the view that received the click
         */
        // TODO: onClick for the open button, onClick for the open with different plugin button, delete button
        @Override
        public void onClick(View view) {
            // If the click happened on the card itself
            if (view.getId() == R.id.cv_file) {
                if (mSelectedIndex == NO_DETAILS) {
                    /*
                    Case no card selected. Sets the selected card and expands the view.
                     */
                    mSelectedIndex = index;
                    expand(view);
                } else if (mSelectedIndex == index) {
                    /*
                    Case the clicked card is the expanded card.
                    Unset the selected card, collapse this card.
                     */
                    mSelectedIndex = NO_DETAILS;
                    collapse(view);
                } else {
                    /*
                    Case where a card is selected but a different card is clicked.
                    Find the previously expanded card, and collapse it only if it is in view.
                    Otherwise a nullpointerException might be found (since it does not currently
                    exist).
                    Set the index to the selected card, and expand that card.
                     */
                    RecyclerView recyclerView = (RecyclerView) view.getParent();
                    CardFileViewHolder prev = (CardFileViewHolder) recyclerView.findViewHolderForLayoutPosition(
                            mSelectedIndex);
                    if (prev != null) {
                        collapse(prev.mCardView);
                    }
                    mSelectedIndex = index;
                    expand(view);
                }
                // if the click happened on the open button
            } // TODO add: else if (view.getId() == R.id.button_card_file) {...}
        }
    }

    /**
     * Expand the view to show details.
     * Simply sets the visibility of the details to VISIBLE, while setting the original card to GONE
     *
     * @param v view to expand
     */
    public static void expand(final View v) {
        FrameLayout detailView = v.findViewById(R.id.cv_fl_detail);
        FrameLayout baseView = v.findViewById(R.id.cv_fl_base_card);
        detailView.setVisibility(View.VISIBLE);
        baseView.setVisibility(View.GONE);
    }

    /**
     * Collapse an expanded view.
     * Swaps visibility of the detail view and base view to GONE and VISIBLE respectively.
     *
     * @param v view to collapse
     */
    public static void collapse(final View v) {
        FrameLayout detailView = v.findViewById(R.id.cv_fl_detail);
        FrameLayout baseView = v.findViewById(R.id.cv_fl_base_card);
        detailView.setVisibility(View.GONE);
        baseView.setVisibility(View.VISIBLE);
    }
}
