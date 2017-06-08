package pt.aodispor.android.features.cardstack;

import android.graphics.Typeface;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;

import pt.aodispor.android.R;
import pt.aodispor.android.data.models.aodispor.BasicCardFields;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.UserRequest;
import pt.aodispor.android.utils.HtmlUtil;
import pt.aodispor.android.utils.TypefaceManager;


public class CardStack {

    private static class CardMetaData {
        /**
         * indicates that the card at this position doesn't allow restoration on previous cards
         */
        boolean blocks_backward_iteration;
        BasicCardFields basicCardFields;

        private CardMetaData() {
            basicCardFields = null;
            blocks_backward_iteration = false;
        }

        static public CardMetaData createBlockingCardWithNoInfo() {
            CardMetaData metadata = new CardMetaData();
            metadata.blocks_backward_iteration = true;
            return metadata;
        }

        static public CardMetaData createNONBlockingCardWithNoInfo() {
            return new CardMetaData();
        }

        CardMetaData(BasicCardFields info) {
            basicCardFields = info;
            blocks_backward_iteration = false;
        }
    }

    protected Fragment fragment;
    protected LayoutInflater inflater = null;
    protected RelativeLayout rootView;

    private static Typeface typeface = null;

    void setBasicVariables(Fragment fragment, LayoutInflater inflater, RelativeLayout rootView) {
        if (this.fragment == null) this.fragment = fragment;
        if (this.inflater == null) this.inflater = inflater;
        if (this.rootView == null) this.rootView = rootView;
        if (typeface != null)
            typeface = TypefaceManager.singleton.getTypeFace(TypefaceManager.singleton.load(rootView.getContext(), TypefaceManager.singleton.YANONE[0]));
    }

    @VisibleForTesting
    protected RelativeLayout[] cards = null;
    @VisibleForTesting
    protected CardMetaData[] cards_data = null;

    final static int TOP = 0;
    final static int BOTTOM = 2;

    boolean areCardViewsInitialized() {
        return cards != null;
    }

    RelativeLayout getCardAt(int index) {
        if (cards == null) return null;
        return cards[index];
    }

    BasicCardFields getCardInfoAt(int index) {
        return cards_data[index].basicCardFields;
    }

    private CardMetaData getCardMetaDataAt(int index) {
        return cards_data[index];
    }

    public CardStack() {
    }

    /**
     * makes a 'shallow' clone. arrays are new & not the same even though the elements inside are initially the same
     */
    CardStack(CardStack cardStack) {
        fragment = cardStack.fragment;
        inflater = cardStack.inflater;
        rootView = cardStack.rootView;
        cards = new RelativeLayout[]{
                cardStack.getCardAt(0),
                cardStack.getCardAt(1),
                cardStack.getCardAt(2)};
        cards_data = new CardMetaData[]{
                cardStack.getCardMetaDataAt(0),
                cardStack.getCardMetaDataAt(1),
                cardStack.getCardMetaDataAt(2)};
    }


    void initNewStack() {
        cards = new RelativeLayout[3];
        cards_data = new CardMetaData[3];
    }


    //region CARD POSITIONING/DISPLAY UTILITIES

    /**
     * This method sets a card's margin from the stack so that it gives the illusion of seeing the
     * stack in perspective with the cards on top of each other.
     *
     * @param position the position in the stack of a card.
     */
    private void setCardMargin(int position) {
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

    void updateAllCardsMargins() {
        for (int i = 0; i < 3; ++i)
            if (cards[i] != null) setCardMargin(i);
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
    void removeAllCardViews() {
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
     * @param stackIndex 0,1 or 2 (the higher the index the lower it is on the stack)
     * @param cardData
     * @return
     */
    void addCard(int stackIndex, BasicCardFields cardData) {
        if (stackIndex < 0 || stackIndex > 2)
            return;
        if (cardData == null)
            return;
        cards_data[stackIndex] = new CardMetaData(cardData);
        try {
            cards[stackIndex] =
                    cards_data[stackIndex].basicCardFields.getClass() == Professional.class ?
                            professionalCard((Professional) cards_data[stackIndex].basicCardFields) :
                            requestCard((UserRequest) cards_data[stackIndex].basicCardFields);
        } catch (Exception e) {
            //Execepção aqui nunca deverá acontecer pk qnd a carta não está completa nem sequer é enviada
            //Tipicamente só acontecerá em ambiete de desenvolvimento
            addMessageCard(stackIndex,
                    fragment.getString(R.string.invalid_card_title),
                    fragment.getString(R.string.invalid_card_description),
                    false);
        }
    }

    void clearCard(int card_index) {
        cards[card_index] = null;
        cards_data[card_index] = null;
    }

    void clearCards(int... card_indexes) {
        for (int i : card_indexes) clearCard(i);
    }

    void swapCardsOnStack(int source, int destination) {
        if (source < 0 || source > 2)
            return;
        if (destination < 0 || destination > 2)
            return;
        cards_data[destination] = cards_data[source];
        cards[destination] = cards[source];
    }

    private RelativeLayout professionalCard(Professional professional) {
        return createProfessionalCard(professional.title, professional.location, professional.description, professional.rate, professional.currency, professional.type, professional.avatar_url);
    }

    private RelativeLayout requestCard(UserRequest request) {
        return createRequestCard(request.location, request.description, request.title, request.rate);
    }

    protected RelativeLayout createProfessionalCard(//String fullname_text,
                                                    String profession_text,
                                                    String location_text,
                                                    String description_text,
                                                    String price_value,
                                                    String currency_type,
                                                    String payment_type,
                                                    String avatar_scr) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.professional_card, rootView, false);

        TextView profession = (TextView) card.findViewById(R.id.profession);
        profession.setText(HtmlUtil.fromHtml(profession_text));
        profession.setTypeface(typeface);

        TextView location = (TextView) card.findViewById(R.id.location);
        location.setText(HtmlUtil.fromHtml(location_text));
        location.setTypeface(typeface);

        TextView description = (TextView) card.findViewById(R.id.description);
        description.setText(Html.fromHtml(description_text));
        //description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) card.findViewById(R.id.price);
        price.setTypeface(typeface);
        price.setText(Html.fromHtml(price_value));

        switch (payment_type) {
            case "H":
                price.setText(HtmlUtil.fromHtml(price_value + " " + currency_type + "/h"));
                price.setTextColor(fragment.getResources().getColor(R.color.by_hour));
                break;
            case "S":
                price.setText(HtmlUtil.fromHtml(price_value + " " + currency_type));
                price.setTextColor(fragment.getResources().getColor(R.color.by_service));
                break;
            case "D":
                price.setText(HtmlUtil.fromHtml(price_value + " por dia"));
        }

        ImageView avatar = (ImageView) card.findViewById(R.id.profile_image);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(fragment.getResources().getDimensionPixelSize(R.dimen.image_border))).build();
        imageLoader.displayImage(avatar_scr, avatar, options);

        return card;
    }

    private RelativeLayout createRequestCard(//String fullname_text,
                                             //String profession_text,
                                             String location_text,
                                             String description_text,
                                             String job_text,
                                             String price_value_text
    ) {
        //TODO not yet complete

        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.request_card, rootView, false);

        //expiration date is set on updateCardViews() method

        TextView job = (TextView) card.findViewById(R.id.job);
        job.setText(HtmlUtil.fromHtml(job_text));
        job.setTypeface(typeface);

        TextView location = (TextView) card.findViewById(R.id.location);
        location.setText(HtmlUtil.fromHtml(location_text));
        location.setTypeface(typeface);

        TextView description = (TextView) card.findViewById(R.id.description);
        description.setText(HtmlUtil.fromHtml(description_text));
        //description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) card.findViewById(R.id.price);
        price.setTypeface(typeface);
        price.setText(HtmlUtil.fromHtml(price_value_text));

        return card;
    }

    RelativeLayout addMessageCard(int cardIndex, String title, String message) {
        return addMessageCard(cardIndex, title, message, false);
    }

    private RelativeLayout addMessageCard(int cardIndex, String title, String message, boolean block_backward_iteration) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.message_card, rootView, false);
        ((TextView) card.findViewById(R.id.title)).setText(HtmlUtil.fromHtml(title));
        ((TextView) card.findViewById(R.id.message)).setText(HtmlUtil.fromHtml(message));
        cards[cardIndex] = card;
        //cards_data[cardIndex] = null;
        cards_data[cardIndex] = block_backward_iteration ?
                CardMetaData.createBlockingCardWithNoInfo() :
                CardMetaData.createNONBlockingCardWithNoInfo();
        return card;
    }


    void addNoConnectionCard(
            int cardIndex,
            //final CardFragment.RequestType retryType,
            View.OnClickListener listener) {
        addNoConnectionCard(cardIndex, false, listener);
    }

    void addNoConnectionCard(
            int cardIndex,
            boolean block_backwards_iteration,
            //final CardFragment.RequestType retryType,
            View.OnClickListener listener
    ) {
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        RelativeLayout card = addMessageCard(cardIndex, fragment.getString(R.string.no_conection_title), fragment.getString(R.string.no_conection_msg), block_backwards_iteration);
        //LinearLayout loadingLL = (LinearLayout) card.findViewById(R.id.loadingMessage);
       /* Button retryButton = (Button) card.findViewById(R.id.messagecard_retry_button);
        retryButton.setText(R.string.retry);

        //TODO REMOVED BUTTON... will only be added when completed
        retryButton.setVisibility(View.VISIBLE);

        retryButton.setOnClickListener(listener);*/

        //return loadingLL;
    }

    //endregion

    private void replaceCardAt(BasicCardFields professional, int index) {
        if (professional == null) {
            throw new RuntimeException("Null Professional");
        }
        if (cards[index] != null) rootView.removeView(cards[index]);
        cards_data[index].basicCardFields = professional;
        cards[index] =
                cards_data[index].basicCardFields.getClass() == Professional.class ?
                        professionalCard((Professional) cards_data[index].basicCardFields) :
                        requestCard((UserRequest) cards_data[index].basicCardFields);
    }

    void replaceTopCard(BasicCardFields card) {
        replaceCardAt(card, CardStack.TOP);
        updateAllCardsMargins();
    }

    public void replaceStack(BasicCardFields[] cards) {
        if (cards == null || cards.length != 3) {
            throw new RuntimeException("Array of Professionals not Valid");
        }
        for (int i = 0; i < 3; ++i) replaceCardAt(cards[i], i);
        updateAllCardsMargins();
    }

    /**
     * user cards = Professional Cards & User Requests Cards
     * non user cards = message cards such as: No Connection ; No Results ; etc...
     */
    private boolean isUserCard(int index) {
        return cards[index] != null && cards_data[index] != null && cards_data[index].basicCardFields != null;
    }

    private boolean isRequestCard(int index) {
        return isUserCard(index) && cards_data[index].basicCardFields.getClass() == UserRequest.class;
    }

    boolean canIterateBackwards() {
        return !cards_data[0].blocks_backward_iteration;
    }

    /**
     * @return true if there was something to update
     */
    boolean updateCardViews() {

        //TODO NOT YET TESTED
        //Log.d("updateCardViews", "updating");
        //if (true) return;

        //TODO maybe could also try to fetc image again in case its missing ???
        //TODO separate server date related stuff from this and from HttpRequestTask and place it on some oter class

        boolean ret = false;

        if (cards == null || cards_data == null) return false;

        //update requests time
        //int timeNow = TimeZone.getTimeZone("UTC").getOffset(System.currentTimeMillis());
        long timenow = new Date().getTime();
        for (int i = 0; i < 2; ++i) {
            if (isRequestCard(i)) {
                ret = true;
                Date carddate = ((UserRequest) cards_data[i].basicCardFields).getExpirationDate();
                if (carddate == null) continue;
                long cardTime = carddate.getTime(); //TODO get card date
                Period p = new Period(timenow, cardTime, PeriodType.standard());
                //Days are not supported =( it seems...
                //must do calculations by "hand"
                long difference = cardTime - timenow;
                long days = difference / (24 * 60 * 60 * 1000);
                String daysString = "";
                if (days > 0) daysString = days + " ";
                daysString += fragment.getString(days == 1 ? R.string.day : R.string.days) + " ";
                ((TextView) cards[i].findViewById(R.id.expiration_date)).setText(
                        difference < 0 ? fragment.getString(R.string.request_expired_card_note)
                                :
                                (daysString
                                        + p.getHours()
                                        + ":" + p.getMinutes()
                                        + ":" + p.getSeconds() + " "
                                        + fragment.getString(R.string.left_to_expire)
                                )
                );
            } else break;
        }
        return ret;
    }
}
