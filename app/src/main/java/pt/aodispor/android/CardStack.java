package pt.aodispor.android;

import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import pt.aodispor.android.api.Professional;


public class CardStack {


    protected Fragment fragment;
    protected LayoutInflater inflater = null;
    protected RelativeLayout rootView;

    public void setBasicVariables(Fragment fragment, LayoutInflater inflater, RelativeLayout rootView) {
        if (this.fragment == null) this.fragment = fragment;
        if (this.inflater == null) this.inflater = inflater;
        if (this.rootView == null) this.rootView = rootView;
    }

    @VisibleForTesting
    protected RelativeLayout[] cards = null;
    @VisibleForTesting
    protected Professional[] cards_professional_data = null;

    final public static int TOP=0;
    final public static int BOTTOM=2;

    public boolean areCardViewsInitialized()
    {
        return cards != null;
    }

    public RelativeLayout getCardAt(int index){
        if (cards==null) return null;
        return cards[index];
    }
    public Professional getCardProfessionalInfoAt(int index){return cards_professional_data[index];}

    public CardStack(){}
    public CardStack(CardStack cardStack){
        fragment = cardStack.fragment;
        inflater = cardStack.inflater;
        rootView = cardStack.rootView;
        cards = new RelativeLayout[]{
                cardStack.getCardAt(0),
                cardStack.getCardAt(1),
                cardStack.getCardAt(2)};
        cards_professional_data = new Professional[] {
                cardStack.getCardProfessionalInfoAt(0),
                cardStack.getCardProfessionalInfoAt(1),
                cardStack.getCardProfessionalInfoAt(2)};
    }

    public void initNewStack(){
        cards = new RelativeLayout[3];
        cards_professional_data = new Professional[3];
    }


    //region CARD POSITIONING/DISPLAY UTILITIES

    /**
     * This method sets a card's margin from the stack so that it gives the illusion of seeing the
     * stack in perspective with the cards on top of each other.
     *
     * @param position the position in the stack of a card.
     */
    public void setCardMargin(int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards[position].getLayoutParams());
        int card_margin_left = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_left);
        int card_margin_top = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_top);
        int card_margin_right = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_right);
        int card_margin_bottom = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        params.setMargins(card_margin_left, card_margin_top, card_margin_right, card_margin_bottom);
        cards[position].setLayoutParams(params);
        cards[position].setTranslationX(fragment.getResources().getDimensionPixelSize(R.dimen.card_offset) * (position + 1));
        cards[position]
                .animate()
                .translationX(fragment.getResources().getDimensionPixelSize(R.dimen.card_offset) * position)
                .setInterpolator(new DecelerateInterpolator());
        cards[position].setTranslationY(fragment.getResources().getDimensionPixelSize(R.dimen.card_offset) * (position + 1));
        cards[position]
                .animate()
                .translationY(fragment.getResources().getDimensionPixelSize(R.dimen.card_offset) * position)
                .setInterpolator(new DecelerateInterpolator());
    }

    public void updateAllCardsMargins()
    {
        for (int i = 0; i<3;++i)
            if(cards[i]!=null) setCardMargin(i);
    }

    /**
     * This method centers the first card of the stack to the center of this fragment.
     */
    /*public void centerFirstCard() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards[0].getLayoutParams());
        int card_margin_left = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_left);
        int card_margin_top = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_top);
        int card_margin_right = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_right);
        int card_margin_bottom = fragment.getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        params.setMargins(card_margin_left, card_margin_top, card_margin_right, card_margin_bottom);
        cards[0].setLayoutParams(params);
    }*/

    /*public void removeCardViews(RelativeLayout rootView, RelativeLayout cards[]) {
        for (int i = cards.length - 1; i >= 0; --i)
            if (cards[i] != null)
                rootView.removeView(cards[i]);
    }*/

    public void removeAllCardViews() {
        for (int i = cards.length - 1; i >= 0; --i)
            if (cards[i] != null)
                rootView.removeView(cards[i]);
    }

    //endregion


    //region CARDS CREATION

    /**
     * Because a professional details are separated from the card display use this auxiliary method
     * to insert new professionals into the card stack
     *
     * @param stackIndex   0,1 or 2 (the higher the index the lower it is on the stack)
     * @param professional
     * @return
     */
    public void addProfessionalCard(int stackIndex, Professional professional) {
        if (stackIndex < 0 || stackIndex > 2)
            return;
        if (professional == null)
            return;
        cards_professional_data[stackIndex] = professional;
        cards[stackIndex] = professionalCard(cards_professional_data[stackIndex]);
    }


    public void clearCard(int card_index) {
        cards[card_index] = null;
        cards_professional_data[card_index] = null;
    }

    public void clearCards(int... card_indexes) {
        for(int i : card_indexes) clearCard(i);
    }

    public void swapCardsOnStack(int source, int destination) {
        if (source < 0 || source > 2)
            return;
        if (destination < 0 || destination > 2)
            return;
        cards_professional_data[destination] = cards_professional_data[source];
        cards[destination] = cards[source];
    }


    private RelativeLayout professionalCard(Professional professional) {
        RelativeLayout card = createProfessionalCard(professional.title, professional.location, professional.description, professional.rate, professional.currency, professional.type, professional.avatar_url);
        return card;
    }

    protected RelativeLayout createProfessionalCard(//String fullname_text,
                                                 String profession_text,
                                                 String location_text,
                                                 String description_text,
                                                 String price_value,
                                                 String currency_type,
                                                 String payment_type,
                                                 String avatar_scr) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.card, rootView, false);

        TextView profession = (TextView) card.findViewById(R.id.profession);
        profession.setText(Html.fromHtml(profession_text));
        profession.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        TextView location = (TextView) card.findViewById(R.id.location);
        location.setText(Html.fromHtml(location_text));
        location.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        TextView description = (TextView) card.findViewById(R.id.description);
        description.setText(Html.fromHtml(description_text));
        //description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) card.findViewById(R.id.price);
        price.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        price.setText(Html.fromHtml(price_value));

        switch (payment_type) {
            case "H":
                price.setText(Html.fromHtml(price_value + " " + currency_type + "/h"));
                price.setTextColor(fragment.getResources().getColor(R.color.by_hour));
                break;
            case "S":
                price.setText(Html.fromHtml(price_value + " " + currency_type));
                price.setTextColor(fragment.getResources().getColor(R.color.by_service));
                break;
            case "D":
                price.setText(Html.fromHtml(price_value + " por dia"));
        }

        ImageView avatar = (ImageView) card.findViewById(R.id.profile_image);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(fragment.getResources().getDimensionPixelSize(R.dimen.image_border))).build();
        imageLoader.displayImage(avatar_scr, avatar, options);

        return card;
    }

    public RelativeLayout addMessageCard(int cardIndex, String title, String message) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.message_card, rootView, false);
        ((TextView) card.findViewById(R.id.title)).setText(Html.fromHtml(title));
        ((TextView) card.findViewById(R.id.message)).setText(Html.fromHtml(message));
        cards[cardIndex] = card;
        cards_professional_data[cardIndex] = null;
        return card;
    }

    public LinearLayout addNoConnectionCard(
            int cardIndex,
            //final CardFragment.RequestType retryType,
            View.OnClickListener listener
    ) {
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        RelativeLayout card = addMessageCard(cardIndex, fragment.getString(R.string.no_conection_title), fragment.getString(R.string.no_conection_msg));
        LinearLayout loadingLL = (LinearLayout) card.findViewById(R.id.loadingMessage);
        Button retryButton = (Button) card.findViewById(R.id.messagecard_retry_button);
        retryButton.setText(R.string.retry);

        //TODO REMOVED BUTTON... will only be added when completed
        retryButton.setVisibility(View.VISIBLE);

        retryButton.setOnClickListener(listener);

        return loadingLL;
    }

    //endregion

}
