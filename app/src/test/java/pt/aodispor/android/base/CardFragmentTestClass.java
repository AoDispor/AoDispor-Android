package pt.aodispor.android.base;

import pt.aodispor.android.features.cardstack.CardFragment;

import static org.mockito.Mockito.mock;

public class CardFragmentTestClass extends CardFragment {
/*
    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        currentSetCardIndex = 0;
        inflater = i;
        container = c;
        rootView = (RelativeLayout) i.inflate(R.layout.card_zone, container, false);
        activity = null;

        return rootView;
    }

    public SearchQueryResult getCurrentSet() {
        return currentSet;
    }

    public String getCurrentShownCardProfessionalLocationPlusProfession() {
        return
                ((TextView) cardStack.getCardAt(CardStack.TOP).findViewById(R.id.location)).getText().toString()
                        + ((TextView) cardStack.getCardAt(CardStack.TOP).findViewById(R.id.profession)).getText().toString();
    }

    @Mock
    Links links;

    public enum Test {forward, backward, mix}

    public void unblockAccess() {
        blockAccess = false;
    }

    public void setTestData(ArrayList<Professional> test_dataset, Test test) {

        cardStack = new CardStackTestClass();
        cardStack.initNewStack();
        cardStack.setBasicVariables(this,inflater,rootView);

        links = mock(Links.class);
        when(links.getPrevious()).thenReturn("some prev link");
        when(links.getNext()).thenReturn("some next link");

        currentSet = new SearchQueryResult();
        currentSet.data = new ArrayList<Professional>();
        currentSet.meta = new Meta();
        currentSet.meta.pagination = mock(Pagination.class);

        if (test == Test.backward) {
            previousSet = new SearchQueryResult();
            previousSet.data = new ArrayList<Professional>();
            previousSet.data.addAll(test_dataset.subList(0, 64));
            currentSet.data.addAll(test_dataset.subList(64, 128));
        } else {
            nextSet = new SearchQueryResult();
            nextSet.data = new ArrayList<Professional>();
            currentSet.data.addAll(test_dataset.subList(0, 64));
            nextSet.data.addAll(test_dataset.subList(64, 128));
        }
        when(currentSet.meta.pagination.getLinks()).thenReturn(links);

        cardStack.initNewStack();

        switch (test) {
            case forward:
                currentSetCardIndex = 0;
                cardStack.addCard(0,test_dataset.get(0));
                cardStack.addCard(1,test_dataset.get(1));
                cardStack.addCard(2,test_dataset.get(2));
                break;
            case backward:
                currentSetCardIndex = 85 - 64;
                cardStack.addCard(0,test_dataset.get(85));
                cardStack.addCard(1,test_dataset.get(86));
                cardStack.addCard(2,test_dataset.get(87));
                break;
            case mix:
                currentSetCardIndex = 50;
                cardStack.addCard(0,test_dataset.get(50));
                cardStack.addCard(1,test_dataset.get(51));
                cardStack.addCard(2,test_dataset.get(52));
                break;
            default:
                break;
        }
    }


*/
}
