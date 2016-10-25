package pt.aodispor.aodispor_android;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *  Class representing a card stack fragment.
 *  <p>
 *      This class controls all the behaviours of the card stack such as the discarding of a card.
 *      This class initializes the stack of cards in an array of RelativeLayout and iterates them.
 *  </p>
 */
public class CardFragment extends Fragment {
    private RelativeLayout[] cards;
    private RelativeLayout rootView;
    private LayoutInflater inflater;
    private ViewGroup container;

    /**
     * Default constructor for CardFragment class.
     */
    public CardFragment() {

    }

    /**
     * Factory method to create a new instance of CardFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     * @return the CardFragment object created.
     */
    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    /**
     * This method creates the View of this card stack fragment.
     * @param i the LayoutInflater object to inflate card_zone.xml and card.xml.
     * @param c the root ViewGroup.
     * @param savedInstanceState object with saved states of previously created fragment.
     * @return returns the root view of the fragment. Not to be confused with the root ViewGroup of
     * this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        inflater = i;
        container = c;
        rootView = (RelativeLayout) i.inflate(R.layout.card_zone, container, false);

        cards = new RelativeLayout[3];

        cards[0] = createCard("test","test","teste","test","test");
        cards[1] = createCard("test","test","teste","test","test");
        cards[2] = createCard("test","test","teste","test","test");

        SwipeListener listener = new SwipeListener(cards[0],((MainActivity)getActivity()).getViewPager(),this);
        cards[0].setOnTouchListener(listener);

        setCardMargin(1);
        setCardMargin(2);

        rootView.addView(cards[2]);
        rootView.addView(cards[1]);
        rootView.addView(cards[0]);

        return rootView;
    }

    /**
     * This method sets a card's margin from the stack so that it gives the illusion of seeing the
     * stack in perspective with the cards on top of each other.
     * @param position the position in the stack of a card.
     */
    private void setCardMargin(int position){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards[position].getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_LEFT,cards[position-1].getId());
        params.addRule(RelativeLayout.ALIGN_TOP,cards[position-1].getId());
        params.topMargin = dpToPx(5*position);
        params.leftMargin = dpToPx(5*position);
        cards[position].setLayoutParams(params);
    }

    /**
     *  This method centers the first card of the stack to the center of this fragment.
     */
    private void centerFirstCard(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards[1].getLayoutParams());
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_LEFT,RelativeLayout.NO_ID);
        params.addRule(RelativeLayout.ALIGN_TOP,RelativeLayout.NO_ID);
        params.topMargin = 0;
        params.leftMargin = 0;
        cards[1].setLayoutParams(params);
    }

    /**
     * Auxiliary method to convert density independent pixels to actual pixels on the screen
     * depending on the systems metrics.
     * @param dp the number of density independent pixels.
     * @return the number of actual pixels on the screen.
     */
    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * This method discards the top card of the card stack, destroys it and brings the other cards
     * one position further in the stack. After that creates a new card to be on the bottom of the
     * stack.
     */
    public void discardTopCard(){
        centerFirstCard();
        rootView.removeAllViews();
        cards[0] = cards[1];
        cards[1] = cards[2];

        cards[2] = createCard("test","test","teste","test","test");

        setCardMargin(1);
        setCardMargin(2);

        rootView.addView(cards[2]);
        rootView.addView(cards[1]);
        rootView.addView(cards[0]);

        SwipeListener listener = new SwipeListener(cards[0],((MainActivity)getActivity()).getViewPager(),this);
        cards[0].setOnTouchListener(listener);
    }

    /**
     * This method creates a new card based on the information provided.
     * @param n name of the professional.
     * @param p profession of the professional.
     * @param l location of the professional.
     * @param d description of the professional.
     * @param pr price set by the professional.
     * @return the card created with the information above.
     */
    private RelativeLayout createCard(String n, String p, String l, String d, String pr){
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.card, rootView, false);
        TextView name = (TextView) card.findViewById(R.id.name);
        name.setText(n);
        TextView profession = (TextView) card.findViewById(R.id.profession);
        profession.setText(p);
        TextView location = (TextView) card.findViewById(R.id.location);
        location.setText(l);
        TextView description = (TextView) card.findViewById(R.id.description);
        description.setText(d);
        TextView price = (TextView) card.findViewById(R.id.price);
        price.setText(pr);
        return card;
    }
}
