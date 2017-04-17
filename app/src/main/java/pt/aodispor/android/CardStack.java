package pt.aodispor.android;

import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Date;

import pt.aodispor.android.api.BasicCardFields;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.UserRequest;


public class CardStack {

    final static int delayBetweenLayoutUpdates = 1000;

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
    protected BasicCardFields[] cards_data = null;

    final public static int TOP = 0;
    final public static int BOTTOM = 2;

    public boolean areCardViewsInitialized() {
        return cards != null;
    }

    public RelativeLayout getCardAt(int index) {
        if (cards == null) return null;
        return cards[index];
    }

    public BasicCardFields getCardInfoAt(int index) {
        return cards_data[index];
    }

    public CardStack() {
    }

    public CardStack(CardStack cardStack) {
        fragment = cardStack.fragment;
        inflater = cardStack.inflater;
        rootView = cardStack.rootView;
        cards = new RelativeLayout[]{
                cardStack.getCardAt(0),
                cardStack.getCardAt(1),
                cardStack.getCardAt(2)};
        cards_data = new BasicCardFields[]{
                cardStack.getCardInfoAt(0),
                cardStack.getCardInfoAt(1),
                cardStack.getCardInfoAt(2)};
    }


    static private Handler handler;
    static private CardStack active;//TODO probably a bad solution but should work 4 now

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            active.updateCards();
            handler.postDelayed(this, delayBetweenLayoutUpdates);
        }
    };

    public void initNewStack() {
        cards = new RelativeLayout[3];
        cards_data = new BasicCardFields[3];
        //prepare card update thread
        active = this;
        if (handler == null) handler = new Handler();
        handler.postDelayed(runnable, delayBetweenLayoutUpdates);
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

    public void updateAllCardsMargins() {
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
     * @param stackIndex 0,1 or 2 (the higher the index the lower it is on the stack)
     * @param cardData
     * @return
     */
    public void addCard(int stackIndex, BasicCardFields cardData) {
        if (stackIndex < 0 || stackIndex > 2)
            return;
        if (cardData == null)
            return;
        cards_data[stackIndex] = cardData;
        try {
            cards[stackIndex] =
                    cards_data[stackIndex].getClass() == Professional.class ?
                            professionalCard((Professional) cards_data[stackIndex]) :
                            requestCard((UserRequest) cards_data[stackIndex]);
        } catch (Exception e) {
            //Execepção aqui nunca deverá acontecer pk qnd a carta não está completa nem sequer é enviada
            //Tipicamente só acontecerá em ambiete de desenvolvimento
            addMessageCard(stackIndex,
                    fragment.getString(R.string.invalid_card_title),
                    fragment.getString(R.string.invalid_card_description));
        }
    }

    public void clearCard(int card_index) {
        cards[card_index] = null;
        cards_data[card_index] = null;
    }

    public void clearCards(int... card_indexes) {
        for (int i : card_indexes) clearCard(i);
    }

    public void swapCardsOnStack(int source, int destination) {
        if (source < 0 || source > 2)
            return;
        if (destination < 0 || destination > 2)
            return;
        cards_data[destination] = cards_data[source];
        cards[destination] = cards[source];
    }

    private RelativeLayout professionalCard(Professional professional) {
        RelativeLayout card = createProfessionalCard(professional.title, professional.location, professional.description, professional.rate, professional.currency, professional.type, professional.avatar_url);
        return card;
    }

    private RelativeLayout requestCard(UserRequest request) {
        RelativeLayout card = createRequestCard(request.location, request.description, request.title, request.rate);
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
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.professional_card, rootView, false);

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

    protected RelativeLayout createRequestCard(//String fullname_text,
                                               //String profession_text,
                                               String location_text,
                                               String description_text,
                                               String job_text,
                                               String price_value_text
    ) {
        //TODO not yet complete

        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.request_card, rootView, false);

        //expiration date is set on updateCards() method

        TextView job = (TextView) card.findViewById(R.id.job);
        job.setText(Html.fromHtml(job_text));
        job.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        TextView location = (TextView) card.findViewById(R.id.location);
        location.setText(Html.fromHtml(location_text));
        location.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        TextView description = (TextView) card.findViewById(R.id.description);
        description.setText(Html.fromHtml(description_text));
        //description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) card.findViewById(R.id.price);
        price.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        price.setText(Html.fromHtml(price_value_text));

        return card;
    }

    public RelativeLayout addMessageCard(int cardIndex, String title, String message) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.message_card, rootView, false);
        ((TextView) card.findViewById(R.id.title)).setText(Html.fromHtml(title));
        ((TextView) card.findViewById(R.id.message)).setText(Html.fromHtml(message));
        cards[cardIndex] = card;
        cards_data[cardIndex] = null;
        return card;
    }


    public void addNoConnectionCard(
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
        //LinearLayout loadingLL = (LinearLayout) card.findViewById(R.id.loadingMessage);
        Button retryButton = (Button) card.findViewById(R.id.messagecard_retry_button);
        retryButton.setText(R.string.retry);

        //TODO REMOVED BUTTON... will only be added when completed
        retryButton.setVisibility(View.VISIBLE);

        retryButton.setOnClickListener(listener);

        //return loadingLL;
    }

    //endregion

    public void replaceCardAt(BasicCardFields professional, int index) {
        if (professional == null) {
            throw new RuntimeException("Null Professional");
        }
        if (cards[index] != null) rootView.removeView(cards[index]);
        cards_data[index] = professional;
        cards[index] =
                cards_data[index].getClass() == Professional.class ?
                        professionalCard((Professional) cards_data[index]) :
                        requestCard((UserRequest) cards_data[index]);
    }

    public void replaceTopCard(BasicCardFields card) {
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
    public boolean isAUserCard(int index) {
        return cards_data[index] == null;
    }


    public void updateCards() {
        //TODO NOT YET TESTED
        //Log.d("updateCards", "updating");
        //if (true) return;

        //TODO maybe could also try to fetc image again in case its missing ???
        //TODO separate server date related stuff from this and from HttpRequestTask and place it on some oter class

        if (cards == null || cards_data == null) return;
        //update requests time
        //int timeNow = TimeZone.getTimeZone("UTC").getOffset(System.currentTimeMillis());
        long timenow = new Date().getTime();
        for (int i = 0; i < 2; ++i) {
            if (cards[i] != null && cards_data[i] != null && cards_data[i].getClass() == UserRequest.class) {
                Date carddate = ((UserRequest) cards_data[i]).getExpirationDate();
                if (carddate == null) continue;
                long cardTime = carddate.getTime(); //TODO get card date
                Period p = new Period(timenow, cardTime, PeriodType.standard());
                //Days are not supported =( it seems...
                //must do calculations by "hand"
                long diference = cardTime - timenow;
                long days = diference / (24 * 60 * 60 * 1000);
                String daysString = "";
                if (days > 0) daysString = days + " ";
                daysString += fragment.getString(days == 1 ? R.string.day : R.string.days) + " ";
                ((TextView) cards[i].findViewById(R.id.expiration_date)).setText(
                        diference < 0 ? fragment.getString(R.string.request_expired_card_note)
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

    }
}
