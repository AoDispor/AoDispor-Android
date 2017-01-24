package pt.aodispor.android.view.base;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.mockito.Mock;

import java.util.ArrayList;

import pt.aodispor.android.api.Links;
import pt.aodispor.android.api.Meta;
import pt.aodispor.android.api.Pagination;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.CardFragment;
import pt.aodispor.android.R;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardFragmentTestClass extends CardFragment {

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
                ((TextView) cards[0].findViewById(R.id.location)).getText().toString()
                        + ((TextView) cards[0].findViewById(R.id.profession)).getText().toString();
    }

    @Mock
    Links links;

    public enum Test {forward, backward, mix}

    public void unblockAccess() {
        blockAccess = false;
    }

    public void setTestData(ArrayList<Professional> test_dataset, Test test) {
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

        cards = new RelativeLayout[3];
        cards_professional_data = new Professional[3];

        switch (test) {
            case forward:
                currentSetCardIndex = 0;
                cards[0] = professionalCard(test_dataset.get(0));
                cards[1] = professionalCard(test_dataset.get(1));
                cards[2] = professionalCard(test_dataset.get(2));
                cards_professional_data[0] = test_dataset.get(0);
                cards_professional_data[1] = test_dataset.get(1);
                cards_professional_data[2] = test_dataset.get(2);
                break;
            case backward:
                currentSetCardIndex = 85 - 64;
                cards[0] = professionalCard(test_dataset.get(85));
                cards[1] = professionalCard(test_dataset.get(86));
                cards[2] = professionalCard(test_dataset.get(87));
                cards_professional_data[0] = test_dataset.get(85);
                cards_professional_data[1] = test_dataset.get(86);
                cards_professional_data[2] = test_dataset.get(87);
                break;
            case mix:
                currentSetCardIndex = 50;
                cards[0] = professionalCard(test_dataset.get(50));
                cards[1] = professionalCard(test_dataset.get(51));
                cards[2] = professionalCard(test_dataset.get(52));
                cards_professional_data[0] = test_dataset.get(51);
                cards_professional_data[1] = test_dataset.get(52);
                cards_professional_data[2] = test_dataset.get(53);
                break;
            default:
                break;
        }
    }

    @Override
    public RelativeLayout createProfessionalCard(String profession_text,
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

        return card;
    }

}
