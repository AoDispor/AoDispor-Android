package pt.aodispor.android.features.cardstack;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.github.karthyks.runtimepermissions.PermissionActivity;

import java.util.concurrent.TimeUnit;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.features.shared.LoadingWidget;
import pt.aodispor.android.features.main.MainActivity;
import pt.aodispor.android.R;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;
import pt.aodispor.android.data.models.aodispor.BasicCardFields;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.models.aodispor.Links;
import pt.aodispor.android.data.models.aodispor.UserRequest;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.utils.Permission;

import static pt.aodispor.android.AppDefinitions.RESTORE_ANIMATION_MILLISECONDS;

/**
 * Class representing a card stack fragment.
 * <p>
 * This class controls all the behaviours of the card stack such as the discarding of a card.
 * This class initializes the stack of cards in an array of RelativeLayout and iterates them.
 * </p>
 */
public class CardFragment extends Fragment {

    //region DEV_ONLY TESTING
    static private final boolean DEV_force2ndPage = false;
    static private final boolean DEV_injectPedidoMockup = false;
    static private final String injectedMockupDate = "2017-08-20 20:35:56";
    //endregion DEV_ONLY TESTING

    /**
     * indicates if CardFragment was started at least once
     */
    static private boolean started = false;

    //MediaPlayer cardShuffleSound;

    public void setSearchQuery(String query) {
        searchQuery = query;
    }

    //private LocationManager locationManager;
    public static final int REQUEST_CODE = 111;//TODO document what request_code is...

    //private static final String queryProfilesURL = "https://api.aodispor.pt/profiles/?query={query}&lat={lat}&lon={lon}";

    //RequestType retryRequestType;

    /**
     * used to know if the query was successful or not. <br><br>emptySet indicates that an answer was received but no results were found
     */
    @VisibleForTesting
    protected enum QueryResult {
        error, emptySet, successful, none
    }

    QueryResult queryResult;

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
    protected CardStackContainer cardStackContainer;
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

    private String searchQuery = "";

    ViewsRefresher viewsRefresher;

    public LinearLayout getLoadingLayout() {
        loadingLL = (LinearLayout) rootView.findViewById(R.id.loadingWidgetLayout);
        return loadingLL;
    }


    //region - fixes for interactions with cards when using a drawer

    /**
     * used to check in BLOCKING and UNBLOCKING is done properly
     */
    private boolean NUMB = false;

    public void BLOCK_INTERACTIONS() {
        if (NUMB) throw new RuntimeException("NUMB=TRUE NOT EXPECTED");
        CardStack cardStack = cardStackContainer.cardStack;
        if (cardStack != null && cardStack.cards != null && cardStack.cards[CardStack.TOP] != null)
            cardStack.cards[CardStack.TOP].dispatchTouchEvent(MotionEvent.obtain(
                    0, 0,
                    MotionEvent.ACTION_UP,
                    0, 0, 0
            ));
        blockAccess = true;
        NUMB = true;
    }

    public void UNBLOCK_INTERACTIONS() {
        if (!NUMB) throw new RuntimeException("NUMB=FALSE NOT EXPECTED");
        blockAccess = false;
        NUMB = false;
    }

    //endregion


    /**
     * Default constructor for CardFragment class.
     */
    public CardFragment() {
        blockAccess = false;
        loadingWidget = new LoadingWidget();
        cardStackContainer = new CardStackContainer();
        viewsRefresher = new ViewsRefresher(cardStackContainer);
        //cardStack = new CardStack();
    }

    /**
     * Factory method to create a new instance of CardFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     *
     * @return the CardFragment object created.
     */
    public static CardFragment newInstance() {
        return new CardFragment();
    }

    /**
     * This method creates the View of this card stack fragment.
     *
     * @param i                  the LayoutInflater object to inflate cardstack__card_zone.xmlrd_zone.xml and <card>.xml.
     * @param c                  the root ViewGroup.
     * @param savedInstanceState object with saved states of previously created fragment.
     * @return returns the root view of the fragment. Not to be confused with the root ViewGroup of
     * this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        final CardStack cardStack = cardStackContainer.cardStack;
        currentSetCardIndex = 0;
        inflater = i;
        container = c;
        rootView = (RelativeLayout) i.inflate(R.layout.cardstack__card_zone, container, false);
        activity = getActivity();

        cardStack.setBasicVariables(this, i, rootView);

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
                if (cardStack.getCardInfoAt(CardStack.TOP) == null) return;

               /* int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    Permission.requestPermission(getActivity(), AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
                    return;
                }*/

                Permission.checkPermission(getActivity(), Permission.PERMISSIONS_CALL_PHONE,
                        new Runnable() {
                            @Override
                            public void run() {
                                phoneCallTopCard();
                            }
                        }, null);


            }
        });

        ImageButton smsButton = (ImageButton) rootView.findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardStack.getCardInfoAt(CardStack.TOP) == null) return;
                BasicCardFields p = cardStack.getCardInfoAt(CardStack.TOP);

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
                if (cardStack.getCardInfoAt(CardStack.TOP) == null) return;
                BasicCardFields p = cardStack.getCardInfoAt(CardStack.TOP);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.aodispor.pt/" + p.string_id);
                sendIntent.setType("text/plain");
                Answers.getInstance().logShare(new ShareEvent().putCustomAttribute("string_id", p.string_id));
                startActivity(sendIntent);
            }
        });

        Permission.checkPermission(getActivity(), Permission.PERMISSIONS_REQUEST_GPS,
                new Runnable() {
                    @Override
                    public void run() {
                        GeoLocation.getInstance().updateLatLon(CardFragment.this.getContext());
                        prepareNewSearchQuery(false);
                    }
                }, null);

        //prepareNewStack();

        /*if (!started) {
            started = true;
            Permission.requestPermission(getActivity(), AppDefinitions.PERMISSIONS_REQUEST_GPS);
        }*/
        return rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case PermissionActivity.PERMISSION_GRANTED:
                    //Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                    GeoLocation.getInstance().updateLatLon(this.getContext());//updateGeoLocation();//setupLocationManager();
                    prepareNewStack();
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

    //region NAVIGATION/PAGINATION

    /**
     * to be called when doing a new search. call before setupNewStack().
     */
    public void prepareNewStack() {
        prepareNewStack(false);
    }

    /**
     * to be called when doing a new search or retrying to load the same search
     */
    public void prepareNewStack(boolean retry) {
        CardStack cardStack = cardStackContainer.cardStack;
        currentSetCardIndex = 0;
        nextSet = null;
        previousSet = null;

        loadingWidget.startLoading(getLoadingLayout(), cardStack.getCardAt(CardStack.TOP));

        prepareNewSearchQuery(retry);
    }

    /**
     * to be called after receiving an answer (or not) from API
     */
    public void setupNewStack(QueryResult queryResult) {
        CardStack cardStack = cardStackContainer.cardStack;
        this.queryResult = queryResult;

        if (cardStack.areCardViewsInitialized()) cardStack.removeAllCardViews();
        else cardStack.initNewStack();

        switch (queryResult) {
            case successful://received answer and it has professionals
                cardStack.addCard(0, currentSet.data.get(0));
                if (currentSet.data.size() > 1) {
                    cardStack.addCard(1, currentSet.data.get(1));
                    if (currentSet.data.size() > 2) {
                        cardStack.addCard(2, currentSet.data.get(2));
                    } else {
                        cardStack.clearCard(2);
                        cardStack.addMessageCard(2, getString(R.string.pile_end_title), getString(R.string.pile_end_msg));
                    }
                } else {
                    cardStack.addMessageCard(1, getString(R.string.pile_end_title), getString(R.string.pile_end_msg));
                    cardStack.clearCard(2);
                }

                if (activity instanceof MainActivity) {
                    SwipeListener listener = new SwipeListener(cardStack.getCardAt(0),/* ((MainActivity) activity).getViewPager(), */this);
                    cardStack.getCardAt(0).setOnTouchListener(listener);
                }

                if (currentSet.data.size() >= 2) {
                    rootView.addView(cardStack.getCardAt(2));
                }

                if (currentSet.data.size() >= 1) {
                    rootView.addView(cardStack.getCardAt(1));
                }
                break;
            case emptySet: //received answer but there aren't any professionals
                cardStack.addMessageCard(0, getString(R.string.no_results_title), getString(R.string.no_results_msg) + "<b>" +
                        (searchQuery.length() > 25 ? (searchQuery.substring(0, 25) + "...") : searchQuery) + "<\\b>");
                cardStack.clearCards(1, 2);
                break;
            case error: //did not receive answer
                cardStack.addNoConnectionCard(0,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //requestType = RequestType.retry_newSet;
                                //loadingWidget.startLoading(getLoadingLayout(), cardStack.getCardAt(CardStack.TOP));
                                CardFragment.this.prepareNewStack(true);
                            }
                        }
                );
                cardStack.clearCards(1, 2);
                break;
            default:
                cardStack.addMessageCard(0, "ERRO", "");//TODO replace with xml defined strings
                cardStack.clearCards(1, 2);
                break;
        }
        cardStack.updateAllCardsMargins();
        rootView.addView(cardStack.getCardAt(0));
        viewsRefresher.startTask();
    }


    /**
     * <p>evaluate state and update card stack accordingly</p>
     * <p>first layer indicates how many cards are loaded/visible to the user</p>
     * <p><b>inset</b> indicates that the index is inside the currentSet and all the card shown are also from that set</p>
     * <p><b>loaded</b> indicates that the next set has already been loaded and <b>missing</b> that hasn't been loaded yet </p>
     * +----------------+----+
     * |                |    |
     * +--+--+          +++  +++
     * |>1   |          |1|  |0| (number of cards below the top card)
     * +-----+------+   +-+  +-+
     * |            |
     * |            |
     * +--------+   +--+------+
     * |INSET   |      |OUTSET| (all shown cards inside currentSet?)
     * +--------+---+  +---+--+
     * |            |
     * +------+     +-----+
     * |      |     |LAST |    | (current page is the last page?)
     * |+PAGES|     |SET  |
     * +------+--+  +-----+
     * |         |
     * +------+  +-------+
     * |LOADED|  |MISSING|  (is the nextSet loaded?)
     * +------+  +-------+
     */
    private enum CardStackStateOnDiscard {
        /**
         * <p>Only a card (the top one) left in the stack. </p>
         * <p>There may be more sets left to explore in case connection was lost</p>
         */
        ZERO {
            public void updateCardStack(final CardFragment cf) {
                CardStack cardStack = cf.cardStackContainer.cardStack;
                cf.rootView.addView(cardStack.getCardAt(CardStack.TOP));
                //blockAccess = false; -> done in SwipeListener
            }

        }
        /**<p>Only two cards left in the stack. </p>
         * <p>There may be more sets left to explore in case connection was lost</p>*/
        , ONE {
            public void updateCardStack(final CardFragment cf) {
                //cf.cardStack.setCardMargin(1);
                CardStack cardStack = cf.cardStackContainer.cardStack;
                cf.rootView.addView(cardStack.getCardAt(1));
                cf.rootView.addView(cardStack.getCardAt(CardStack.TOP));

                if (cf.activity instanceof MainActivity) {
                    SwipeListener listener = new SwipeListener(cardStack.getCardAt(CardStack.TOP), /*((MainActivity) cf.activity).getViewPager(),*/ cf);
                    cardStack.getCardAt(CardStack.TOP).setOnTouchListener(listener);
                }

                cardStack.clearCard(2);
                //blockAccess = false; -> done in SwipeListener
            }
        },
        /**
         * <p>the index is inside the current card set and all the card shown are also inside the set</p>
         */
        INSET {
            public void updateCardStack(final CardFragment cf) {
                CardStack cardStack = cf.cardStackContainer.cardStack;
                cardStack.addCard(2, cf.currentSet.data.get(cf.currentSetCardIndex + 2));
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>last card from last card is already on the 3 card shown</p>
         */
        LAST {
            public void updateCardStack(final CardFragment cf) {
                CardStack cardStack = cf.cardStackContainer.cardStack;
                cardStack.addMessageCard(2, cf.getString(R.string.pile_end_title), cf.getString(R.string.pile_end_msg));
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>already have the next page information</p>
         */
        LOADED {
            public void updateCardStack(final CardFragment cf) {
                CardStack cardStack = cf.cardStackContainer.cardStack;
                //negative when there are still cards from the previous set on the pile
                cf.currentSetCardIndex = cf.currentSetCardIndex - cf.currentSet.data.size();
                Log.d("updateCardStack", "currentSetCardIndex: " + Integer.toString(cf.currentSetCardIndex));
                cf.previousSet = cf.currentSet;
                cf.currentSet = cf.nextSet;
                cf.nextSet = null;
                cardStack.addCard(2, cf.currentSet.data.get(cf.currentSetCardIndex + 2));
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * <p>missing next page card set<p/>
         */
        MISSING {
            public void updateCardStack(final CardFragment cf) {
                CardStack cardStack = cf.cardStackContainer.cardStack;
                cardStack.addNoConnectionCard(2, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //cf.requestType = RequestType.retry_nextSet;
                        //cf.loadingWidget.startLoading(cf.getLoadingLayout(), cf.cardStack.getCardAt(CardStack.TOP));
                        cf.prepareNextPage(true);
                    }
                });
                cf.CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate();
            }
        },
        /**
         * this state indicates an occurrence that was not expected
         */
        INVALID {
            public void updateCardStack(final CardFragment cf) {
                Log.e("ERROR", "INVALID.updateCardStack - Unexpected state");
                //TODO add exception msg here later maybe ???;
            }
        };

        public abstract void updateCardStack(final CardFragment cf);
    }

    public void CardStackOnDiscard_MoreThanTwoCardsVisibleUpdate() {
        //update cards display
        CardStack cardStack = cardStackContainer.cardStack;
        RelativeLayout topCard = cardStack.getCardAt(CardStack.TOP);
        rootView.addView(cardStack.getCardAt(2));
        rootView.addView(cardStack.getCardAt(1));
        rootView.addView(topCard);

        //add listener to top card
        if (activity instanceof MainActivity
                && cardStack.getCardInfoAt(CardStack.TOP) != null //TODO listener only added in profile cards, for now
                ) {
            SwipeListener listener = new SwipeListener(topCard, /*((MainActivity) activity).getViewPager(),*/ this);
            topCard.setOnTouchListener(listener);
        }

        //prepare next set if needed
        if (nextSet == null && currentSetCardIndex + AppDefinitions.MIN_NUMBER_OFCARDS_2LOAD >= currentSet.data.size()) {
            previousSet = null;
            System.gc();
            prepareNextPage();
        }
    }

    public CardStackStateOnDiscard getCardStackStateOnDiscard() {
        CardStack cardStack = cardStackContainer.cardStack;
        //no more cards below top card
        if (cardStack.getCardAt(1) == null) return CardStackStateOnDiscard.ZERO;

        //only one card left on pile card (below top card)
        //TODO not sure about this line, there may be a prettier way to to it
        if (cardStack.getCardAt(2) != null && cardStack.getCardAt(2).getTag() != null && cardStack.getCardAt(2).getTag().equals("msg"))
            return CardStackStateOnDiscard.ONE;

        //in case something unexpected happened and current set is not available anymore -> the cards can't be loaded!
        if (currentSet == null) return CardStackStateOnDiscard.INVALID;

        //if there are more than 1 card below top card

        if (currentSetCardIndex + 2 < currentSet.data.size()) return CardStackStateOnDiscard.INSET;

        if (currentSet.meta.pagination.getLinks() != null && currentSet.meta.pagination.getLinks().getNext() != null) {
            if (nextSet != null) {
                return CardStackStateOnDiscard.LOADED; //already have the next page information
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
        CardStack cardStack = cardStackContainer.cardStack;
        currentSetCardIndex++;
        cardStack.removeAllCardViews();
        cardStack.swapCardsOnStack(1, 0);
        cardStack.swapCardsOnStack(2, 1);

        CardStackStateOnDiscard state = getCardStackStateOnDiscard();
        Log.d("discardTopCard", "STATE = " + state.toString());
        state.updateCardStack(this);
        cardStack.updateAllCardsMargins();
        viewsRefresher.startTask();
        //blockAccess = false; -> done in SwipeListener
    }

    /**
     * Recovers the previous discarded card
     * <also> reponsable for requesting the loading of the previous page and updating the currentSet and nextSet
     */
    public void restorePreviousCard() {
        if (blockAccess || queryResult != QueryResult.successful ||
                !cardStackContainer.cardStack.canIterateBackwards()
                ) {
            return; //don't make anything while animation plays
        }

        if (currentSetCardIndex < -2) {
            Log.d("ERROR 003", "Unexpected state");
        }//TODO not expected throw exception or development warning

        blockAccess = true;

        CardStack originalCardStack = new CardStack(cardStackContainer.cardStack);

        int originalIndex = currentSetCardIndex;
        cardStackContainer.cardStack.swapCardsOnStack(1, 2);
        cardStackContainer.cardStack.swapCardsOnStack(0, 1);

        cardStackContainer.cardStack.getCardAt(CardStack.TOP).setOnTouchListener(null);

        currentSetCardIndex--;
        if (currentSetCardIndex >= 0) { //can get previous card from currentSet
            if (currentSet == null) {
                currentSetCardIndex = originalIndex;
                blockAccess = false;
                return;
            }
            cardStackContainer.cardStack.addCard(0, currentSet.data.get(currentSetCardIndex));
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
            cardStackContainer.cardStack.addCard(0, currentSet.data.get(currentSetCardIndex));
        } else {
            if (currentSetCardIndex < 0) {//needs to get card from previous set immediately.
                nextSet = null;
                System.gc();
                if (previousSet == null) {//if previous set was not yet loaded
                    //TODO preparePreviousPageI should be done ideally via Callback
                    switch (preparePreviousPageI()) { //TODO->NOT TESTED i think???
                        case successful:
                            break;
                        case emptySet:
                            break;
                        case none: //when reached pile start do not change pile state
                            cardStackContainer.cardStack = originalCardStack;
                            currentSetCardIndex = originalIndex;
                            if (activity instanceof MainActivity) {
                                RelativeLayout topCard = cardStackContainer.cardStack.getCardAt(CardStack.TOP);
                                SwipeListener listener = new SwipeListener(topCard, /*((MainActivity) activity).getViewPager(),*/ this);
                                topCard.setOnTouchListener(listener);
                            }

                            blockAccess = false;
                            return;
                        case error: //did not receive answer
                            cardStackContainer.cardStack.addNoConnectionCard(0, true,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //requestType = RequestType.retry_prevSet;
                                            //TODO missing try to reload prev page
                                            //TODO loadingWidget.startLoading(loadingLL, cardStack.getCardAt(CardStack.TOP));
                                        }
                                    }
                            );
                            break;
                        default:
                            break;
                    }
                }
                if (previousSet != null) {//if set was received or was already loaded
                    cardStackContainer.cardStack.addCard(0, previousSet.data.get(previousSet.data.size() + currentSetCardIndex));
                }
            }
        }
        originalCardStack.removeAllCardViews();
        RelativeLayout topCard = cardStackContainer.cardStack.getCardAt(CardStack.TOP);
        if (cardStackContainer.cardStack.getCardAt(2) != null) {
            rootView.addView(cardStackContainer.cardStack.getCardAt(2));
        }
        rootView.addView(cardStackContainer.cardStack.getCardAt(1));
        rootView.addView(topCard);

        // if (activity instanceof MainActivity
        //       && cardStack.getCardInfoAt(CardStack.TOP) != null //TODO listener only added in profile cards, for now
        //     ) {
        SwipeListener listener = new SwipeListener(topCard, /*((MainActivity) activity).getViewPager(),*/ this);
        topCard.setOnTouchListener(listener);
        //}

        cardStackContainer.cardStack.updateAllCardsMargins();
        viewsRefresher.startTask();

        topCard.setX(800 * -1);
        topCard.setY(700 * -1);
        topCard.setRotation(40);
        topCard.animate().rotation(0).translationX(0).translationY(0).
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


    //region api RELATED

    public void prepareNewSearchQuery(boolean retry) {

        final GeoLocation geoLocation = GeoLocation.getInstance();

        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildCardStackRequest(searchQuery, geoLocation);

        request.addOnSuccessHandlers(onNewQuery);
        request.addOnSuccessHandlers(closeLoading);
        if (retry) {
            request.addOnFailHandlers(closeLoadingResetVisibility);
        } else {
            request.addOnFailHandlers(setupErrorStack);
            request.addOnFailHandlers(closeLoading);
        }
        request.execute();
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
        //if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null) ;
        //requestType.val = retry ? RequestType.retry_nextSet : RequestType.nextSet;
        Links links = null;
        try {
            links = currentSet.meta.pagination.getLinks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (links == null)
            return;
        String link = links.getNext();
        if (link == null)
            return;
        Log.d("LOAD NEXT BACKGROUND", "STARTED");
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.GET(SearchQueryResult.class, link);
        request.addOnSuccessHandlers(retry ? onPrevPageRetry : onNextPage);
        if (retry) {
            request.addOnSuccessHandlers(closeLoading);
            request.addOnFailHandlers(closeLoadingResetVisibility);
        }
        request.execute();
    }

    /**
     * will try to load previous page on background via AsyncTask (nonblocking)
     */
    public void preparePreviousPage() {
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null)
            return;
        //requestType.val = RequestType.prevSet;
        Links links = currentSet.meta.pagination.getLinks();
        if (links == null)
            return;
        String link = links.getPrevious();
        if (link == null)
            return;
        Log.d("LOAD PREV BACKGROUND", "STARTED");
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.GET(SearchQueryResult.class, link);
        request.addOnSuccessHandlers(onPrevPage);
        //TODO implement retry later
        request.execute();
    }

    /**
     * try to load previous page immediately! will wait for task to end or error (blocking)
     */
    public QueryResult preparePreviousPageI() {
        //TODO should not be blocking, not used yet so no problem -> REDO THIS METHOD!
        if (currentSet == null || currentSet.meta == null || currentSet.meta.pagination == null)
            return QueryResult.none;
        //requestType.val = RequestType.prevSet;
        Links links = currentSet.meta.pagination.getLinks();
        if (links == null)
            return QueryResult.none;
        String link = links.getPrevious();
        if (link == null)
            return QueryResult.none;
        Log.d("LOAD PREV IMMIDIATE", "STARTED");
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = HttpRequestTask.GET(SearchQueryResult.class, link);

        SearchQueryResult result;
        try {
            result = (SearchQueryResult) request.execute().get(20000, TimeUnit.MILLISECONDS);
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


    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onPrevPage =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    previousSet = (SearchQueryResult) answer;
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onPrevPageRetry =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    throw new RuntimeException("Not Implemented");
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onNextPage =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    nextSet = (SearchQueryResult) answer;
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onNextPageRetry =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    //TODO not yet tested
                    previousSet = currentSet;
                    currentSet = (SearchQueryResult) answer;
                    nextSet = null;
                    cardStackContainer.cardStack.replaceTopCard(currentSet.data.get(0));
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onNewQuery =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    //TODO review this later
                    if (answer != null) {
                        SearchQueryResult result = (SearchQueryResult) answer;
                        if (result.data != null && result.data.size() > 0) {
                            CardFragment.this.currentSet = result;
                            //TODO ADD LATER -> MediaPlayer.create(CardFragment.this.getContext(), R.raw.se_cardstackshuffle).start();
                            //cardShuffleSound.start();

                            if (DEV_injectPedidoMockup) {
                                UserRequest p = new UserRequest();
                                p.full_name = "test name";
                                p.title = "test title";
                                p.location = "some place";
                                p.description = "blablabla";
                                p.rate = "15";
                                p.data_expiracao = injectedMockupDate;//new java.util.Date();
                                CardFragment.this.currentSet.data.add(0, p);
                                //CardFragment.this.currentSet.data.add(1, p);
                            }

                            setupNewStack(QueryResult.successful);
                        } else {
                            setupNewStack(QueryResult.emptySet);
                        }
                    }
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> setupErrorStack =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI data) {
                    setupNewStack(QueryResult.error);
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> closeLoadingResetVisibility =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI data) {
                    loadingWidget.endLoading(true);
                }
            };

    private final HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> closeLoading =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI dta) {
                    loadingWidget.endLoading(false);
                }
            };


    //endregion


    //region CARD ACTIONS

    void phoneCallTopCard() {
        BasicCardFields p = cardStackContainer.cardStack.getCardInfoAt(CardStack.TOP);
        Answers.getInstance().logCustom(new CustomEvent("Telefonema").putCustomAttribute("string_id", p.string_id));
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", p.phone, null)));
    }

//endregion


    @Override
    public void onStart() {
        super.onStart();
        viewsRefresher.startTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewsRefresher.startTask();
    }

    @Override
    public void onPause() {
        viewsRefresher.stopTask();
        super.onPause();
    }

    @Override
    public void onStop() {
        viewsRefresher.stopTask();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        viewsRefresher.stopTask();
        super.onDestroy();
    }

}
