package com.aurora.aurora;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.kernel.Kernel;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * The adapter for the RecyclerView of the file-cards
 */
public class CardFileAdapter extends RecyclerView.Adapter<CardFileAdapter.CardFileViewHolder> {

    /**
     * The option to have no selected card in the RecyclerView
     */
    private static final int NO_DETAILS = -1;

    /**
     * The format of a date used in the cards
     */
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("E, dd MMM yyyy");

    /**
     * The amount of cards that the RecyclerView manages
     */
    private int mAmount = 0;

    /**
     * The data of the cached files
     */
    private List<CachedFileInfo> mCachedFileInfoList = null;

    /**
     * The index of the currently selected file (file card that is expanded)
     */
    private int mSelectedIndex = NO_DETAILS;

    /**
     * A reference to the (unique) kernel instance
     */
    private Kernel mKernel;

    /**
     * context for testing purposes
     */
    private Context mContext;

    public CardFileAdapter(Kernel kernel, Context context, @NonNull List<CachedFileInfo> cachedFileInfoList) {
        // TODO: This could take an argument as input (which contains the recent files)
        // TODO: remove context variable if it is not needed by the test example anymore!
        mKernel = kernel;
        mContext = context;
        mCachedFileInfoList = cachedFileInfoList;
        mAmount = mCachedFileInfoList.size();
    }

    /**
     * Replaces the old list of info about the cached files with a new one
     *
     * @param cachedFileInfoList The new list which will replace the old one
     */
    public void updateData(@NonNull List<CachedFileInfo> cachedFileInfoList) {
        mCachedFileInfoList = cachedFileInfoList;
        mAmount = mCachedFileInfoList.size();
        notifyDataSetChanged();
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
     * Retrieve the number of items in the list.
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
    public class CardFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        /* UI related objects */
        private TextView mTitleTextView;
        private TextView mLastOpenedTextView;
        private CardView mCardView;

        /* Data related objects */
        private int index;
        private CachedFileInfo mCachedFileInfo;

        /**
         * Constructor for the ViewHolder.
         * Sets the fields for the contents of the card.
         *
         * @param itemView the card view
         */
        public CardFileViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.cv_file);
            mTitleTextView = itemView.findViewById(R.id.tv_card_title);
            mLastOpenedTextView = itemView.findViewById(R.id.tv_card_last_opened);
            // The card itself is clickable (for details), but also the open button.
            mCardView.setOnClickListener(this);
            mCardView.setOnLongClickListener(this);
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
            mCachedFileInfo = mCachedFileInfoList.get(index);
            String lastOpenedBase = mLastOpenedTextView.getResources().getString(R.string.file_last);

            // Set name of the correct file to the reused container.
            mTitleTextView.setText(mCachedFileInfo.getFileDisplayName());
            StringBuilder bld = new StringBuilder(lastOpenedBase)
                    .append(" ")
                    .append(mDateFormat.format(mCachedFileInfo.getLastOpened()));

            mLastOpenedTextView.setText(bld.toString());

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
         * Defines what happens on click of the card. This can only be the card itself
         *
         * @param view the view that received the click
         */
        @Override
        public void onClick(View view) {
            // TODO remove the 'mContext' variable from this class. It is used solely for demonstration purposes!
            if (view.getId() == mCardView.getId()) {
                String displayName = mCachedFileInfo.getFileDisplayName();
                String fileType = displayName.substring(displayName.lastIndexOf('.'));

                mKernel.getAuroraCommunicator().openFileWithCache(mCachedFileInfo.getFileRef(), fileType,
                        mCachedFileInfo.getUniquePluginName(), mContext);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            boolean clickConsumed = false;
            // If the click happened on the card itself
            if (view.getId() == R.id.cv_file) {

                // TODO: Remove this 'return' if card details should be enabled!
                if (view.getId() != 0) {
                    return true;
                }

                // If the click happened on the card itself
                if (mSelectedIndex == NO_DETAILS) {
                    /*
                    Case no card selected. Sets the selected card and expands the view.
                     */
                    mSelectedIndex = index;
                    expand(view);
                    clickConsumed = true;
                } else if (mSelectedIndex == index) {
                    /*
                    Case the clicked card is the expanded card.
                    Unset the selected card, collapse this card.
                     */
                    mSelectedIndex = NO_DETAILS;
                    collapse(view);
                    clickConsumed = true;
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
                    clickConsumed = true;
                }
                // if the click happened on the open button
            }
            return clickConsumed;
        }
    }

//    TODO: Expanding card when another expanded card is
//    in view results in the card growing upwards instead of down.
//    Scrolling down before expanding "solves" this.
//    Fix This bug :)

    /**
     * Expand the view to show details.
     * Simply sets the visibility of the details to VISIBLE, while setting the original card to GONE
     *
     * @param v view to expand
     */
    public static void expand(final View v) {
        ConstraintLayout detailView = v.findViewById(R.id.cv_fl_detail);
        TextView baseView = v.findViewById(R.id.tv_card_more_details);
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
        ConstraintLayout detailView = v.findViewById(R.id.cv_fl_detail);
        // TODO: Uncomment if details of card should be used!
        // TextView baseView = v.findViewById(R.id.tv_card_more_details)
        detailView.setVisibility(View.GONE);
        // TODO: Uncomment if details of card should be used!
        // baseView.setVisibility(View.VISIBLE)
    }
}
