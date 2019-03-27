package com.aurora.aurora;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.kernel.Kernel;

import java.io.InputStream;
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
            }
            // TODO add the case that view.getId() is R.id.button_card_file
            // TODO update this preliminary code for opening a plugin.

            // TODO this should make an event to open the cached file.
            // BasicPlugin should probably still be able to open this, but Souschef probably not
            if (view.getId() == R.id.button_card_file) {

                //index
                String stubPluginText =
                        "Yield\n" +
                        "    2 servings\n" +
                        "Active Time\n" +
                        "    30 minutes\n" +
                        "Total Time\n" +
                        "    35 minutes\n" +
                        "\n" +
                        "Ingredients\n" +
                        "\n" +
                        "        1 lb. linguine or other long pasta\n" +
                        "        Kosher salt\n" +
                        "        1 (14-oz.) can diced tomatoes\n" +
                        "        1/2 cup extra-virgin olive oil, divided\n" +
                        "        1/4 cup capers, drained\n" +
                        "        6 oil-packed anchovy fillets\n" +
                        "        1 Tbsp. tomato paste\n" +
                        "        1/3 cup pitted Kalamata olives, halved\n" +
                        "        2 tsp. dried oregano\n" +
                        "        1/2 tsp. crushed red pepper flakes\n" +
                        "        6 oz. oil-packed tuna\n" +
                        "\n" +
                        "Preparation\n" +
                        "\n" +
                        "        Cook pasta in a large pot of boiling salted water, stirring " +
                        "occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking " +
                        "liquid; return pasta to pot.\n" +
                        "        While pasta cooks, pour tomatoes into a fine-mesh sieve set over " +
                        "a medium bowl. Shake to release as much juice as possible, then let tomatoes " +
                        "drain in sieve, collecting juices in bowl, until ready to use.\n" +
                        "        Heat 1/4 cup oil in a large deep-sided skillet over medium-high. " +
                        "Add capers and cook, swirling pan occasionally, until they burst and are " +
                        "crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper " +
                        "towel-lined plate, reserving oil in skillet.\n" +
                        "        Combine anchovies, tomato paste, and drained tomatoes in skillet. " +
                        "Cook over medium-high heat, stirring occasionally, until tomatoes begin " +
                        "to caramelize and anchovies start to break down, about 5 minutes. Add " +
                        "collected tomato juices, olives, oregano, and red pepper flakes and bring " +
                        "to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, " +
                        "about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta " +
                        "cooking liquid to pan. Cook over medium heat, stirring and adding remaining " +
                        "1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened " +
                        "and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                        "        Divide pasta among plates. Top with fried capers.\n";


                        //"Here will be the text from file " + index + ".\nRandom sentence.";

                //Context context = MainActivity.this;
                Intent intent = new Intent(Constants.PLUGIN_ACTION);
                intent.putExtra(Constants.PLUGIN_INPUT_TEXT, stubPluginText);

                String title = "Select a plugin";
                // Create intent to show the chooser dialog
                Intent chooser = Intent.createChooser(intent, title);

                // Verify the original intent will resolve to at least one activity
                Context context = view.getContext();
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(chooser);
                } else {
                    Toast.makeText(context, "No plugins available",
                            Toast.LENGTH_LONG).show();
                }

                /*
                Intent intent = new Intent(Constants.PLUGIN_ACTION);


                // Note: this is basically mixed code as a result of a merge. This test code will be removed ASAP
                InputStream docFile = view.getContext().getResources().openRawResource(R.raw.apple_crisp);

                //if (textFile != null) {
                new Kernel().getAuroraCommunicator().openFileWithPlugin("test", docFile, intent, view.getContext());
                //} else {
                //    Toast.makeText(this, "The selected file was null", Snackbar.LENGTH_LONG).show();
                //}

                //Toast.makeText(this, "A file with uri \"" + textFile + "\" was selected.", Snackbar.LENGTH_LONG).show();
                // Use File
                */


            }
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
