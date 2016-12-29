package pt.aodispor.aodispor_android;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pt.aodispor.aodispor_android.API.ApiJSON;
import pt.aodispor.aodispor_android.API.HttpRequestTask;
import pt.aodispor.aodispor_android.API.Links;
import pt.aodispor.aodispor_android.API.HttpRequest;
import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;

import static pt.aodispor.aodispor_android.AppDefinitions.RESTORE_ANIMATION_MILLISECONDS;

/**
 * Class representing a card stack fragment.
 * <p>
 * This class controls all the behaviours of the card stack such as the discarding of a card.
 * This class initializes the stack of cards in an array of RelativeLayout and iterates them.
 * </p>
 */
public class CardFragment extends Fragment implements HttpRequest {

    public void setSearchQuery(String query) {
        searchQuery = query;
    }

    /**
     * used by preparePage and onHttpRequestCompleted to know if the request is to get the previous or next page or an enterily new query
     */
    @VisibleForTesting
    protected enum RequestType {
        prevSet, nextSet, newSet
    }

    /**
     * used to know if the query was successful or not. <br><br>emptySet indicates that an answer was received but no results were found
     */
    @VisibleForTesting
    protected enum QueryResult {
        timeout, emptySet, successful, none
    }

    @VisibleForTesting
    protected RequestType requestType;
    /**
     * contains the previous page data
     */
    @VisibleForTesting
    protected SearchQueryResult previousSet;
    /**
     * contains the current page data
     */
    @VisibleForTesting
    protected SearchQueryResult currentSet;
    /**
     * contains the next page data
     * <br>set it to null if it does not have the next page data
     */
    @VisibleForTesting
    protected SearchQueryResult nextSet;
    @VisibleForTesting
    protected int currentSetCardIndex;
    @VisibleForTesting
    protected RelativeLayout[] cards;
    @VisibleForTesting
    protected Professional[] cards_professional_data;
    /**
     * does not allow discard or recover methods to be called before the previous is finished
     */
    @VisibleForTesting
    protected static boolean blockAccess = false;

    @VisibleForTesting
    protected RelativeLayout rootView;
    @VisibleForTesting
    protected LayoutInflater inflater;
    @VisibleForTesting
    protected ViewGroup container;
    @VisibleForTesting
    protected Activity activity;

    private String lat = "";
    private String lon = "";
    private String searchQuery = "";

    /**
     * Default constructor for CardFragment class.
     */
    public CardFragment() {
        blockAccess = false;
    }

    /**
     * Factory method to create a new instance of CardFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     *
     * @return the CardFragment object created.
     */
    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    /**
     * This method creates the View of this card stack fragment.
     *
     * @param i                  the LayoutInflater object to inflate card_zone.xml and card.xml.
     * @param c                  the root ViewGroup.
     * @param savedInstanceState object with saved states of previously created fragment.
     * @return returns the root view of the fragment. Not to be confused with the root ViewGroup of
     * this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        currentSetCardIndex = 0;
        inflater = i;
        container = c;
        rootView = (RelativeLayout) i.inflate(R.layout.card_zone, container, false);
        activity = getActivity();

        //TODO add button, this might be removed later, for now i need to test the restore ard functionality
        Button button = (Button) rootView.findViewById(R.id.prevCardButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restorePreviousCard();
            }
        });

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> l = locationManager.getProviders(true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String s : l) {
                Location loc = locationManager.getLastKnownLocation(s);
                if (loc != null) {
                    lat = "" + loc.getLatitude();
                    lon = "" + loc.getLongitude();
                    break;
                }
            }
        }

        setupNewStack();

        return rootView;
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
        int px = getResources().getDimensionPixelSize(R.dimen.card_margin);
        params.setMargins(px, px, px, px);
        cards[position].setLayoutParams(params);
        cards[position].setTranslationX(getResources().getDimensionPixelSize(R.dimen.card_offset) * (position + 1));
        cards[position]
                .animate()
                .translationX(getResources().getDimensionPixelSize(R.dimen.card_offset) * position)
                .setInterpolator(new DecelerateInterpolator());
        cards[position].setTranslationY(getResources().getDimensionPixelSize(R.dimen.card_offset) * (position + 1));
        cards[position]
                .animate()
                .translationY(getResources().getDimensionPixelSize(R.dimen.card_offset) * position)
                .setInterpolator(new DecelerateInterpolator());
    }

    /**
     * This method centers the first card of the stack to the center of this fragment.
     */
    private void centerFirstCard() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cards[0].getLayoutParams());
        int px = getResources().getDimensionPixelSize(R.dimen.card_margin);
        params.setMargins(px, px, px, px);
        cards[0].setLayoutParams(params);
    }

    public void removeCardViews(RelativeLayout cards[]) {
        for (int i = cards.length - 1; i >= 0; --i)
            if (cards[i] != null)
                rootView.removeView(cards[i]);
    }

    //endregion

    //region NAVIGATION/PAGINATION

    /**
     * to be called when doing a new search
     */
    public void setupNewStack() {
        if (cards != null)
            removeCardViews(cards);
        else
            cards = new RelativeLayout[3];
        cards_professional_data = new Professional[3];
        switch (prepareNewSearchQuery()) {
            case successful://received answer and it has professionals
                putCardOnStack(0, currentSet.data.get(0));
                if (currentSet.data.size() > 1) {
                    putCardOnStack(1, currentSet.data.get(1));
                    if (currentSet.data.size() > 2)
                        putCardOnStack(2, currentSet.data.get(2));
                    else
                        cards[2] = createMessageCard(getString(R.string.pile_end_title), getString(R.string.pile_end_msg));//TODO missing button
                } else {
                    cards[1] = createMessageCard(getString(R.string.pile_end_title), getString(R.string.pile_end_msg));//TODO missing button
                }

                if (activity instanceof MainActivity) {
                    SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
                    cards[0].setOnTouchListener(listener);
                }

                if (currentSet.data.size() >= 2) {
                    setCardMargin(2);
                    rootView.addView(cards[2]);
                }

                if (currentSet.data.size() >= 1) {
                    setCardMargin(1);
                    rootView.addView(cards[1]);
                }
                break;
            case emptySet: //received answer but there aren't any professionals
                cards[0] = createMessageCard(getString(R.string.no_results_title), getString(R.string.no_results_msg) + "<b>" +
                        (searchQuery.length() > 25 ? (searchQuery.substring(0, 25) + "...") : searchQuery) + "<\\b>");
                break;
            case timeout: //did not receive answer
                cards[0] = createMessageCard(getString(R.string.no_conection_title), getString(R.string.no_conection_msg));//TODO missing button
                break;
            default:
                cards[0] = createMessageCard("ERROR 001", "");//TODO replace with xml defined strings
                break;
        }
        centerFirstCard();
        rootView.addView(cards[0]);
    }

    /**
     * This method discards the top card of the card stack, destroys it and brings the other cards
     * one position further in the stack. After that creates a new card to be on the bottom of the
     * stack.
     * <br>
     * Also responsible for requesting the loading of the next page and updating the currentSet and nextSet.
     */
    public void discardTopCard() {
        //blockAccess = true; -> done in SwipeListener
        currentSetCardIndex++;
        removeCardViews(cards);
        swapCardsOnStack(1, 0);
        swapCardsOnStack(2, 1);
        centerFirstCard();
        if (cards[1] == null) {//already reached end of card pile
            rootView.addView(cards[0]);
            //blockAccess = false; -> done in SwipeListener
            return;
        }
        if (cards[2] != null && cards[2].getTag() != null && cards[2].getTag().equals("msg")) {//only one card left on pile card TODO not sure about this line, there may be a prettier way to to it
            setCardMargin(1);
            rootView.addView(cards[1]);
            rootView.addView(cards[0]);

            if (activity instanceof MainActivity) {
                SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
                cards[0].setOnTouchListener(listener);
            }

            cards[2] = null;
            //blockAccess = false; -> done in SwipeListener
            return;
        }
        if (currentSet == null) {
            Log.d("ERROR 002", "Unexpected state");
            return;
        }//TODO add exception msg here later

        //more than one card on card pile
        if (currentSetCardIndex + 2 < currentSet.data.size()) {
            putCardOnStack(2, currentSet.data.get(currentSetCardIndex + 2));
        } else {
            if (currentSet.meta.pagination.getLinks() != null && currentSet.meta.pagination.getLinks().getNext() != null) {//if there are more pages to show
                if (nextSet != null) { //we already have the next page information
                    currentSetCardIndex = currentSetCardIndex - currentSet.data.size(); //negative when there are still cards from the previous set on the pile
                    Log.d("L155", "currentSetCardIndex: " + Integer.toString(currentSetCardIndex));
                    previousSet = currentSet;
                    currentSet = nextSet;
                    nextSet = null;
                    putCardOnStack(2, currentSet.data.get(currentSetCardIndex + 2));
                } else { //content failed to get next page on time
                    cards[2] = createMessageCard(getString(R.string.no_conection_title), getString(R.string.no_conection_msg));//TODO missing button
                }
            } else { //there are no more pages to show
                cards[2] = createMessageCard(getString(R.string.pile_end_title), getString(R.string.pile_end_msg));//TODO missing button
            }
        }
        setCardMargin(0);
        setCardMargin(1);
        setCardMargin(2);
        rootView.addView(cards[2]);
        rootView.addView(cards[1]);
        rootView.addView(cards[0]);

        if (activity instanceof MainActivity) {
            SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
            cards[0].setOnTouchListener(listener);
        }

        if (nextSet == null && currentSetCardIndex + AppDefinitions.MIN_NUMBER_OFCARDS_2LOAD >= currentSet.data.size()) {
            previousSet = null;
            System.gc();
            prepareNextPage();
        }
        //blockAccess = false; -> done in SwipeListener
    }

    /**
     * Recovers the previous discarded card
     * <also> reponsable for requesting the loading of the previous page and updating the currentSet and nextSet
     */
    public void restorePreviousCard() {
        if (blockAccess)
            return; //don't make anything while animation plays
        if (currentSetCardIndex < -2) {
            Log.d("ERROR 003", "Unexpected state");
        }//TODO not expected throw exception or development warning

        blockAccess = true;

        RelativeLayout[] originalCardsSetup = {cards[0], cards[1], cards[2]};
        Professional[] originalProfessionals = {cards_professional_data[0], cards_professional_data[1], cards_professional_data[2]};
        int originalIndex = currentSetCardIndex;
        swapCardsOnStack(1, 2);
        swapCardsOnStack(0, 1);

        cards[0].setOnTouchListener(null);

        currentSetCardIndex--;
        if (currentSetCardIndex >= 0) { //can get previous card from currentSet
            if (currentSet == null) {
                currentSetCardIndex = originalIndex;
                blockAccess = false;
                return;
            }
            putCardOnStack(0, currentSet.data.get(currentSetCardIndex));
            if (currentSetCardIndex < AppDefinitions.MIN_NUMBER_OFCARDS_2LOAD) {//load in background if possible
                nextSet = null;
                System.gc();//try to keep only 2 sets at maximum
                if (previousSet == null)
                    preparePreviousPage();
            }
        } else if (currentSetCardIndex == -3) {//transfer previousSet to currentSet if more than 2 cards already taken from it
            currentSetCardIndex = previousSet.data.size() - 3;
            nextSet = currentSet;
            currentSet = previousSet;
            previousSet = null;
            System.gc();// no need to keep 3 sets stored
            putCardOnStack(0, currentSet.data.get(currentSetCardIndex));
        } else {
            if (currentSetCardIndex < 0) {//needs to get card from previous set immidiatly.
                nextSet = null;
                System.gc();
                if (previousSet == null) {//if previous set was not yet loaded
                    switch (preparePreviousPageI()) {
                        case successful:
                            break;
                        case emptySet:
                            break;
                        case none: //when reached pile start do not change pile state
                            cards = originalCardsSetup;
                            cards_professional_data = originalProfessionals;
                            currentSetCardIndex = originalIndex;

                            if (activity instanceof MainActivity) {
                                SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
                                cards[0].setOnTouchListener(listener);
                            }

                            blockAccess = false;
                            return;
                        case timeout: //did not receive answer
                            cards[0] = createMessageCard(getString(R.string.no_conection_title), getString(R.string.no_conection_msg));//TODO missing button
                            break;
                        default:
                            break;
                    }
                }
                if (previousSet != null) {//if set was received or was already loaded
                    putCardOnStack(0, previousSet.data.get(previousSet.data.size() + currentSetCardIndex));
                }
            }
        }
        removeCardViews(originalCardsSetup);
        setCardMargin(1);
        if (cards[2] != null)
            setCardMargin(2);
        if (cards[2] != null)
            rootView.addView(cards[2]);
        rootView.addView(cards[1]);
        rootView.addView(cards[0]);

        if (activity instanceof MainActivity) {
            SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
            cards[0].setOnTouchListener(listener);
        }

        cards[0].setX(800 * -1);
        cards[0].setY(700 * -1);
        cards[0].setRotation(40);
        cards[0].animate().rotation(0).translationX(0).translationY(0).
                setDuration(RESTORE_ANIMATION_MILLISECONDS).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                blockAccess = false;
            }
        });
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
    public void putCardOnStack(int stackIndex, Professional professional) {
        if (stackIndex < 0 || stackIndex > 2)
            return;
        if (professional == null)
            return;
        cards_professional_data[stackIndex] = professional;
        cards[stackIndex] = professionalCard(cards_professional_data[stackIndex]);
    }

    public void swapCardsOnStack(int source, int destination) {
        if (source < 0 || source > 2)
            return;
        if (destination < 0 || destination > 2)
            return;
        cards_professional_data[destination] = cards_professional_data[source];
        cards[destination] = cards[source];
    }

    public RelativeLayout createProfessionalCard(//String fullname_text,
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
        description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) card.findViewById(R.id.price);

        price.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        price.setText(Html.fromHtml(price_value));

        switch (payment_type) {
            case "H":
                price.setText(Html.fromHtml(price_value + " " + currency_type + "/h"));
                price.setTextColor(getResources().getColor(R.color.by_hour));
                break;
            case "S":
                price.setText(Html.fromHtml(price_value + " " + currency_type));
                price.setTextColor(getResources().getColor(R.color.by_service));
                break;
            case "D":
                price.setText(Html.fromHtml(price_value + " por dia"));
        }

        ImageView avatar = (ImageView) card.findViewById(R.id.profile_image);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.image_border))).build();
        imageLoader.displayImage(avatar_scr, avatar, options);

        return card;
    }


    public RelativeLayout createMessageCard(String title, String message) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.message_card, rootView, false);
        ((TextView) card.findViewById(R.id.title)).setText(Html.fromHtml(title));
        ((TextView) card.findViewById(R.id.message)).setText(Html.fromHtml(message));
        return card;
    }

    @VisibleForTesting
    protected RelativeLayout professionalCard(Professional p) {
        RelativeLayout card = createProfessionalCard(p.title, p.location, p.description, p.rate, p.currency, p.type, p.avatar_url);
        return card;
    }

    //endregion

    //region API RELATED

    /**
     * send a new search query
     * <br>will wait for task to end or timeout (blocking)
     *
     * @return true if received query result on time
     */
    public QueryResult prepareNewSearchQuery() {
        requestType = RequestType.newSet;//not needed, unlike nextSet, should remain here anyways because it might be useful for debugging later
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, null, "https://api.aodispor.pt/profiles/?query={query}&lat={lat}&lon={lon}", searchQuery, lat, lon);

        SearchQueryResult result;
        try {
            result = (SearchQueryResult) request.execute().get(AppDefinitions.MILISECONDS_TO_TIMEOUT_ON_QUERY, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            //Log.d("L290:EXCP",e.toString());
            return QueryResult.timeout;
        }
        if (result != null && result.data != null && result.data.size() > 0) {
            this.currentSet = result;
            return QueryResult.successful;
        }
        return QueryResult.emptySet;
    }

    /**
     * will try to load next page on background via AsyncTask (nonblocking)
     */
    public void prepareNextPage() {
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null) ;
        requestType = RequestType.nextSet;
        Links links = currentSet.meta.pagination.getLinks();
        if (links == null)
            return;
        String link = links.getNext();
        if (link == null)
            return;
        Log.d("LOAD NEXT BACKGROUND", "STARTED");
        new HttpRequestTask(SearchQueryResult.class, this, link).execute();
    }

    /**
     * will try to load previous page on background via AsyncTask (nonblocking)
     */
    public void preparePreviousPage() {
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null)
            return;
        requestType = RequestType.prevSet;
        Links links = currentSet.meta.pagination.getLinks();
        if (links == null)
            return;
        String link = links.getPrevious();
        if (link == null)
            return;
        Log.d("LOAD PREV BACKGROUND", "STARTED");
        new HttpRequestTask(SearchQueryResult.class, this, link).execute();
    }

    /**
     * try to load previous page immidiatly! will wait for task to end or timeout (blocking)
     */
    public QueryResult preparePreviousPageI() {
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null)
            return QueryResult.none;
        requestType = RequestType.prevSet;
        Links links = currentSet.meta.pagination.getLinks();
        if (links == null)
            return QueryResult.none;
        String link = links.getPrevious();
        if (link == null)
            return QueryResult.none;
        ;
        Log.d("LOAD PREV IMMIDIATE", "STARTED");
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, null, link);

        SearchQueryResult result;
        try {
            result = (SearchQueryResult) request.execute().get(AppDefinitions.MILISECONDS_TO_TIMEOUT_ON_QUERY, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.d("L330:EXCP", e.toString());
            return QueryResult.timeout;
        }
        if (result.data != null && result.data.size() > 0) {
            this.currentSet = result;
            return QueryResult.successful;
        }
        return QueryResult.emptySet;
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {
        if (requestType == RequestType.nextSet)
            nextSet = (SearchQueryResult) answer;
        else if (requestType == RequestType.prevSet)
            previousSet = (SearchQueryResult) answer;
        else if (requestType == RequestType.newSet) { //not used right now
            nextSet = null;
            currentSet = (SearchQueryResult) answer;
        }
    }

    @Override
    public void onHttpRequestFailed() {

    }

    //endregion

    //region MISC

    public Professional getProfessionalOnTop() {
        return cards_professional_data[0];
    }

    //endregion

}
