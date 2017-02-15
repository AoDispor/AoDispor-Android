package pt.aodispor.android;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.github.karthyks.runtimepermissions.PermissionActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.concurrent.TimeUnit;

import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Links;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;

import static pt.aodispor.android.AppDefinitions.RESTORE_ANIMATION_MILLISECONDS;

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

    //private LocationManager locationManager;
    public static final int REQUEST_CODE = 111;//TODO document what request_code is...

    private static final String queryProfilesURL = "https://api.aodispor.pt/profiles/?query={query}&lat={lat}&lon={lon}";

    /**
     * used by preparePage and onHttpRequestCompleted to know if the request is to get the previous or next page or an enterily new query
     */
    @VisibleForTesting
    protected enum RequestType {
        prevSet, nextSet, newSet,
        retry_prevSet, retry_nextSet, retry_newSet;
    }

    RequestType retryRequestType;

    /**
     * used to know if the query was successful or not. <br><br>emptySet indicates that an answer was received but no results were found
     */
    @VisibleForTesting
    protected enum QueryResult {
        error, emptySet, successful, none
    }

    QueryResult queryResult;

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

    private LoadingWidget loadingWidget;
    private LinearLayout loadingLL;

    private GeoLocation geoLocation;
    public void updateGeoLocation() {geoLocation.updateLatLon(getContext());}
    private String searchQuery = "";

    /**
     * Default constructor for CardFragment class.
     */
    public CardFragment() {
        blockAccess = false;
        loadingWidget = new LoadingWidget();
        geoLocation = new GeoLocation();
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

        updateGeoLocation();

        ImageButton returnButton = (ImageButton) rootView.findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restorePreviousCard();
            }
        });

        ImageButton callButton = (ImageButton) rootView.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cards_professional_data[0] == null) return;
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    Permission.requestPermission(getActivity(), AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
                    return;
                }

                callProfessional();
            }
        });

        ImageButton smsButton = (ImageButton) rootView.findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cards_professional_data[0] == null) return;
                Professional p = cards_professional_data[0];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + p.phone));
                intent.putExtra("sms_body", getString(R.string.sms_text));
                Answers.getInstance().logCustom(new CustomEvent("Envio de SMS").putCustomAttribute("string_id", p.string_id));
                startActivity(intent);
            }
        });

        ImageButton shareButton = (ImageButton) rootView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cards_professional_data[0] == null) return;
                Professional p = cards_professional_data[0];
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.aodispor.pt/" + p.string_id);
                sendIntent.setType("text/plain");
                Answers.getInstance().logShare(new ShareEvent().putCustomAttribute("string_id", p.string_id));
                startActivity(sendIntent);
            }
        });

        setupNewStack(prepareNewStack());//TODO consider also doing this in background

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case PermissionActivity.PERMISSION_GRANTED:
                    //Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                    updateGeoLocation();//setupLocationManager();
                    setupNewStack(prepareNewStack());//TODO consider also doing this in background
                    break;
                case PermissionActivity.PERMISSION_DENIED:
                    //Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionActivity.PERMISSION_PERMANENTLY_DENIED:
                    //Toast.makeText(this, "Permanently denied", Toast.LENGTH_SHORT).show();
                    //PermissionUtil.openAppSettings(this.getActivity().);
                    break;
                default:
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        int card_margin_left = getResources().getDimensionPixelSize(R.dimen.card_margin_left);
        int card_margin_top = getResources().getDimensionPixelSize(R.dimen.card_margin_top);
        int card_margin_right = getResources().getDimensionPixelSize(R.dimen.card_margin_right);
        int card_margin_bottom = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        params.setMargins(card_margin_left, card_margin_top, card_margin_right, card_margin_bottom);
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
        int card_margin_left = getResources().getDimensionPixelSize(R.dimen.card_margin_left);
        int card_margin_top = getResources().getDimensionPixelSize(R.dimen.card_margin_top);
        int card_margin_right = getResources().getDimensionPixelSize(R.dimen.card_margin_right);
        int card_margin_bottom = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        params.setMargins(card_margin_left, card_margin_top, card_margin_right, card_margin_bottom);
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
    public QueryResult prepareNewStack() {
        return prepareNewStack(true);
    }

    /**
     * to be called when doing a new search or retrying to load the same search
     */
    public QueryResult prepareNewStack(boolean retry) {
        currentSetCardIndex = 0;
        nextSet = null;
        previousSet = null;
        return prepareNewSearchQuery(retry);
    }

    public void setupNewStack(QueryResult queryResult) {
        if (cards != null)
            removeCardViews(cards);
        else
            cards = new RelativeLayout[3];
        cards_professional_data = new Professional[3];

        switch (queryResult) {
            case successful://received answer and it has professionals
                putCardOnStack(0, currentSet.data.get(0));
                if (currentSet.data.size() > 1) {
                    putCardOnStack(1, currentSet.data.get(1));
                    if (currentSet.data.size() > 2)
                        putCardOnStack(2, currentSet.data.get(2));
                    else
                        createMessageCard(2, getString(R.string.pile_end_title), getString(R.string.pile_end_msg));//TODO missing button
                } else {
                    cards[1] = createMessageCard(1, getString(R.string.pile_end_title), getString(R.string.pile_end_msg));//TODO missing button
                    clearCard(2);
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
                createMessageCard(0, getString(R.string.no_results_title), getString(R.string.no_results_msg) + "<b>" +
                        (searchQuery.length() > 25 ? (searchQuery.substring(0, 25) + "...") : searchQuery) + "<\\b>");
                clearCard(1);
                clearCard(2);
                break;
            case error: //did not receive answer
                createNoConnectionMessageCard(0,
                        (requestType == RequestType.newSet && requestType == RequestType.retry_newSet) ? RequestType.retry_newSet :
                                (requestType == RequestType.nextSet && requestType == RequestType.retry_newSet) ? RequestType.retry_newSet :
                                        RequestType.retry_newSet //this last case should never happen
                );
                clearCard(1);
                clearCard(2);
                break;
            default:
                createMessageCard(0, "ERRO", "");//TODO replace with xml defined strings
                clearCard(1);
                clearCard(2);
                break;
        }
        centerFirstCard();
        rootView.addView(cards[0]);
    }


    /**
     * <p>evaluate state and update card stack accordingly</p>
     * <p>first layer indicates how many cards are loaded/visible to the user</p>
     * <p><b>inset</b> indicates that the index is inside the current card set and all the card shown are also inside the set</p>
     * <p><b>loaded</b> indicates that the next set has already been loaded and <b>missing</b> that hasn't been loaded yet </p>
     * +------------+----+
     * |            |    |
     * +--+-----+    +++  +++
     * |>1      |    |1|  |0| (number of cards below the top card)
     * +----------+  +-+  +-+
     * |         |
     * |         |
     * +-----+   +------+
     * |INSET|   |OUTSET|
     * +-----+   +---+--+
     * |   |
     * +----+   +------+
     * |LAST|   |      |
     * |PAGE|   ++PAGES|
     * +----+   +--+---+
     * |  |
     * +------+  +-------+
     * |LOADED|  |MISSING|
     * +------+  +-------+
     */
    private enum CardStackStateOnDiscard {
        /**
         * <p>Only a card (the top one) left in the stack. </p>
         * <p>There may be more sets left to explore in case connection was lost</p>
         */
        ZERO {
            public void updateCardStack(CardFragment cf) {
                cf.rootView.addView(cf.cards[0]);
                //blockAccess = false; -> done in SwipeListener
            }
        }
        /**<p>Only two cards left in the stack. </p>
         * <p>There may be more sets left to explore in case connection was lost</p>*/
        , ONE {
            public void updateCardStack(CardFragment cf) {
                cf.setCardMargin(1);
                cf.rootView.addView(cf.cards[1]);
                cf.rootView.addView(cf.cards[0]);

                if (cf.activity instanceof MainActivity) {
                    SwipeListener listener = new SwipeListener(cf.cards[0], ((MainActivity) cf.activity).getViewPager(), cf);
                    cf.cards[0].setOnTouchListener(listener);
                }

                cf.clearCard(2);
                //blockAccess = false; -> done in SwipeListener
            }
        },
        /**
         * <p>there are more than 2 cards being shown</p>
         * <p>the index is inside the current card set and all the card shown are also inside the set</p>
         */
        INSET {
            public void updateCardStack(CardFragment cf) {
                cf.putCardOnStack(2, cf.currentSet.data.get(cf.currentSetCardIndex + 2));
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>there are more than 2 cards being shown</p>
         * <p>last card from last card is already on the 3 card shown</p>
         */
        LAST {
            public void updateCardStack(CardFragment cf) {
                cf.createMessageCard(2, cf.getString(R.string.pile_end_title), cf.getString(R.string.pile_end_msg));//TODO missing button
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>there are more than 2 cards being shown</p>
         * <p>already have the next page information</p>
         */
        LOADED {
            public void updateCardStack(CardFragment cf) {
                //negative when there are still cards from the previous set on the pile
                cf.currentSetCardIndex = cf.currentSetCardIndex - cf.currentSet.data.size();
                Log.d("updateCardStack", "currentSetCardIndex: " + Integer.toString(cf.currentSetCardIndex));
                cf.previousSet = cf.currentSet;
                cf.currentSet = cf.nextSet;
                cf.nextSet = null;
                cf.putCardOnStack(2, cf.currentSet.data.get(cf.currentSetCardIndex + 2));
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>there are more than 2 cards being shown</p>
         * <p>missing next page card set<p/>
         */
        MISSING {
            public void updateCardStack(CardFragment cf) {
                cf.createNoConnectionMessageCard(2, RequestType.nextSet);
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * this state indicates an occurrence that was not expected
         */
        INVALID {
            public void updateCardStack(CardFragment cf) {
                Log.e("ERROR", "INVALID.updateCardStack - Unexpected state");
                //TODO add exception msg here later maybe ???;
            }
        };

        public abstract void updateCardStack(CardFragment cf);
    }

    public void CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate() {
        //update cards display
        setCardMargin(0);
        setCardMargin(1);
        setCardMargin(2);
        rootView.addView(cards[2]);
        rootView.addView(cards[1]);
        rootView.addView(cards[0]);

        //add listener to top card
        if (activity instanceof MainActivity
                && cards_professional_data[0] != null //TODO listener only added in profile cards, for now
                ) {
            SwipeListener listener = new SwipeListener(cards[0], ((MainActivity) activity).getViewPager(), this);
            cards[0].setOnTouchListener(listener);
        }

        //prepare next set if needed
        if (nextSet == null && currentSetCardIndex + AppDefinitions.MIN_NUMBER_OFCARDS_2LOAD >= currentSet.data.size()) {
            previousSet = null;
            System.gc();
            prepareNextPage();
        }
    }

    public CardStackStateOnDiscard getCardStackStateOnDiscard() {

        //no more cards below top card
        if (cards[1] == null) return CardStackStateOnDiscard.ZERO;

        //only one card left on pile card (below top card)
        //TODO not sure about this line, there may be a prettier way to to it
        if (cards[2] != null && cards[2].getTag() != null && cards[2].getTag().equals("msg"))
            return CardStackStateOnDiscard.ONE;

        //in case something unexpected happened and current set is not available anymore -> the cards can't be loaded!
        if (currentSet == null) return CardStackStateOnDiscard.INVALID;

        //if there are more than 1 card below top card

        if (currentSetCardIndex + 2 < currentSet.data.size()) return CardStackStateOnDiscard.INSET;

        if (currentSet.meta.pagination.getLinks() != null && currentSet.meta.pagination.getLinks().getNext() != null) {
            if (nextSet != null) {
                return CardStackStateOnDiscard.INSET.LOADED; //already have the next page information
            } else {
                return CardStackStateOnDiscard.MISSING; //content failed to get next page/(card set) on time
            }
        }
        //else
        //there are no more pages to show
        return CardStackStateOnDiscard.LAST;
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

        CardStackStateOnDiscard state = getCardStackStateOnDiscard();
        Log.d("discardTopCard", "STATE = " + state.toString());
        state.updateCardStack(this);
        //blockAccess = false; -> done in SwipeListener
    }

    /**
     * Recovers the previous discarded card
     * <also> reponsable for requesting the loading of the previous page and updating the currentSet and nextSet
     */
    public void restorePreviousCard() {
        if (blockAccess) {
            return; //don't make anything while animation plays
        }

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
                if (previousSet == null) {
                    preparePreviousPage();
                }
            }
        } else if (currentSetCardIndex == -3) {//transfer previousSet to currentSet if more than 2 cards already taken from it
            currentSetCardIndex = previousSet.data.size() - 3;
            nextSet = currentSet;
            currentSet = previousSet;
            previousSet = null;
            System.gc();// no need to keep 3 sets stored
            putCardOnStack(0, currentSet.data.get(currentSetCardIndex));
        } else {
            if (currentSetCardIndex < 0) {//needs to get card from previous set immediately.
                nextSet = null;
                System.gc();
                if (previousSet == null) {//if previous set was not yet loaded
                    switch (preparePreviousPageI()) { //TODO->NOT TESTED i think???
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
                        case error: //did not receive answer
                            createNoConnectionMessageCard(0, RequestType.retry_prevSet);
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
        setCardMargin(0);
        rootView.addView(cards[0]);

        if (activity instanceof MainActivity
                && cards_professional_data[0] != null //TODO listener only added in profile cards, for now
                ) {
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

    /**  */
    public void clearCard(int card_index) {
        cards[card_index] = null;
        cards_professional_data[card_index] = null;
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
        //description.setMovementMethod(new ScrollingMovementMethod());

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

    public RelativeLayout createMessageCard(int cardIndex, String title, String message) {
        RelativeLayout card = (RelativeLayout) inflater.inflate(R.layout.message_card, rootView, false);
        ((TextView) card.findViewById(R.id.title)).setText(Html.fromHtml(title));
        ((TextView) card.findViewById(R.id.message)).setText(Html.fromHtml(message));
        cards[cardIndex] = card;
        cards_professional_data[cardIndex] = null;
        return card;
    }

    public RelativeLayout createNoConnectionMessageCard(int cardIndex, final RequestType retryType) {
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        //TODO WORKING NOW
        RelativeLayout card = createMessageCard(cardIndex, getString(R.string.no_conection_title), getString(R.string.no_conection_msg));
        loadingLL = (LinearLayout) card.findViewById(R.id.loadingMessage);
        Button retryButton = (Button) card.findViewById(R.id.messagecard_retry_button);
        retryButton.setText(R.string.retry);

        //TODO REMOVED BUTTON... will only be added when completed
        //retryButton.setVisibility(View.VISIBLE);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryRequestType = retryType;
                switch (retryType) {
                    case retry_newSet:
                        prepareNewStack(true);
                        break;
                    case retry_nextSet:
                        prepareNextPage(true);
                        break;
                    case retry_prevSet:
                        break;
                    default:
                        return;
                }
                loadingWidget.startLoading(loadingLL, cards[0]);
            }
        });
        return card;
    }

    @VisibleForTesting
    protected RelativeLayout professionalCard(Professional p) {
        RelativeLayout card = createProfessionalCard(p.title, p.location, p.description, p.rate, p.currency, p.type, p.avatar_url);
        return card;
    }

    //endregion

    //region api RELATED

    /**
     * send a new search query
     * <br>will wait for task to end or timeout/error (blocking)
     *
     * @return true if received query result on time
     */
    public QueryResult prepareNewSearchQuery(boolean retry) {
        requestType = retry ? RequestType.retry_newSet : RequestType.newSet;//not needed, unlike nextSet, should remain here anyways because it might be useful for debugging later
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, null,
                queryProfilesURL, searchQuery, geoLocation.getLatitude(), geoLocation.getLongitude());

        SearchQueryResult result;
        try {
            result = (SearchQueryResult) request.execute().get(AppDefinitions.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.e("prepareNewSearchQuery", "793:" + e.toString());
            return QueryResult.error;
        }
        if (request.gotError()) {
            return QueryResult.error;
        }
        if (result != null && result.data != null && result.data.size() > 0) {
            this.currentSet = result;
            return QueryResult.successful;
        }
        return QueryResult.emptySet;
    }

    public void prepareNextPage() {
        prepareNextPage(false);
    }

    /**
     * will try to load next page on background via AsyncTask (nonblocking)
     *
     * @param retry indicates that the page requested should have already been loaded, this will inform the HttpRequest implementation that there is only one card visible (others should be added)
     */
    public void prepareNextPage(boolean retry) {
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null) ;
        requestType = retry ? RequestType.retry_nextSet : RequestType.nextSet;
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
     * try to load previous page immediately! will wait for task to end or error (blocking)
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
        Log.d("LOAD PREV IMMIDIATE", "STARTED");
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, null, link);

        SearchQueryResult result;
        try {
            result = (SearchQueryResult) request.execute().get(AppDefinitions.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Log.e("preparePreviousPageI", e.toString());
            return QueryResult.error;
        }
        if (request.gotError()) return QueryResult.error;
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
        //else if (requestType == RequestType.newSet) { //not used right now
        //    nextSet = null;
        //    currentSet = (SearchQueryResult) answer;
        //}
        if (requestType == RequestType.retry_prevSet
                || requestType == RequestType.retry_nextSet
                || requestType == RequestType.retry_newSet
                ) {
            loadingWidget.endLoading(loadingLL, null);
            //if(queryResult==)
            //TODO REMOVE LOADING animation & ADD VIEW REFRESH
            //TODO REMOVE LOADING animation & ADD VIEW REFRESH
            //TODO REMOVE LOADING animation & ADD VIEW REFRESH
            //TODO REMOVE LOADING animation & ADD VIEW REFRESH
            //TODO REMOVE LOADING animation & ADD VIEW REFRESH
        }
    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData) {

        if (requestType == RequestType.retry_prevSet
                || requestType == RequestType.retry_nextSet
                || requestType == RequestType.retry_newSet
                ) {
            loadingWidget.endLoading(loadingLL, cards[0]);
            //TODO REMOVE LOADING animation
            //TODO REMOVE LOADING animation
            //TODO REMOVE LOADING animation
            //TODO REMOVE LOADING animation
            //TODO REMOVE LOADING animation
        }

    }

    //endregion


    //region CARD ACTIONS

    void callProfessional() {
        Professional p = cards_professional_data[0];
        Answers.getInstance().logCustom(new CustomEvent("Telefonema").putCustomAttribute("string_id", p.string_id));
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", p.phone, null)));
    }

    //endregion

    //region PERMISSIONS

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Realizado dependendo do tipo de permissao
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callProfessional();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion

}
