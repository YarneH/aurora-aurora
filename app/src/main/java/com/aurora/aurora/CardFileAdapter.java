package com.aurora.aurora;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.kernel.Kernel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The adapter for the RecyclerView of the file-cards
 */
public class CardFileAdapter extends RecyclerView.Adapter<CardFileAdapter.CardFileViewHolder> {
    private static final String LOG_TAG = CardFileAdapter.class.getSimpleName();

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
    private List<CachedFileInfo> mCachedFileInfoList = new ArrayList<>();

    /**
     * The context of the application
     */
    private Context mContext = null;

    /**
     * A reference to the (unique) kernel instance
     */
    private Kernel mKernel;

    public CardFileAdapter(Kernel kernel, Context context, @NonNull List<CachedFileInfo> cachedFileInfoList) {
        mKernel = kernel;
        mCachedFileInfoList = cachedFileInfoList;
        mAmount = mCachedFileInfoList.size();
        mContext = context;
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
        /**
         * The Title TextView
         */
        private TextView mTitleTextView;
        /**
         * The TextView showing the date on which the file was last opend
         */
        private TextView mLastOpenedTextView;
        /**
         * The complete card of the current file
         */
        private CardView mCardView;
        /**
         * The ImageView showing the icon of the plugin
         */
        private ImageView mIconImageView;

        /**
         * The index of the current file
         */
        private int index;
        /**
         * The CachedFileInfo about the current file
         */
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
            mIconImageView = itemView.findViewById(R.id.iv_icon);
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

            try {
                Drawable icon = mContext.getPackageManager().getApplicationIcon(
                        mCachedFileInfo.getUniquePluginName());
                mIconImageView.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                // The plugin is no longer installed, so the file should be removed from the cache
                removeCard(index);
                return;
            }

            // Set name of the correct file to the reused container.
            mTitleTextView.setText(mCachedFileInfo.getFileDisplayName());

            String bld = lastOpenedBase +
                    " " +
                    mDateFormat.format(mCachedFileInfo.getLastOpened());
            mLastOpenedTextView.setText(bld);

            try {
                Drawable icon = mContext.getPackageManager().getApplicationIcon(
                        mCachedFileInfo.getUniquePluginName());
                mIconImageView.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, "Package not found when getting icon for packageName: " +
                        mCachedFileInfo.getUniquePluginName(), e);
            }
        }

        /**
         * Defines what happens on click of the card. This can only be the card itself
         *
         * @param view the view that received the click
         */
        @Override
        public void onClick(View view) {
            if (view.getId() == mCardView.getId()) {
                mKernel.getAuroraCommunicator().openFileWithCache(mCachedFileInfo.getFileRef(),
                        mCachedFileInfo.getUniquePluginName());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            // If the click happened on the card itself
            if (view.getId() == R.id.cv_file && view.getId() != 0) {
                    return true;
                // if the click happened on the open button
            }
            return false;
        }
    }

    public void removeCard(int i) {
        CachedFileInfo current = mCachedFileInfoList.remove(i);
        mKernel.getAuroraCommunicator().removeFileFromCache(current.getFileRef(), current.getUniquePluginName());
        mAmount--;
    }
}
