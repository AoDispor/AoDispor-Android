package pt.aodispor.aodispor_android.view.base;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.mockito.Mock;

import java.util.ArrayList;

import pt.aodispor.aodispor_android.API.Links;
import pt.aodispor.aodispor_android.API.Meta;
import pt.aodispor.aodispor_android.API.Pagination;
import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;
import pt.aodispor.aodispor_android.AppDefinitions;
import pt.aodispor.aodispor_android.CardFragment;
import pt.aodispor.aodispor_android.R;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CardFragmentTestClass extends CardFragment {

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle savedInstanceState) {
        currentSetCardIndex=0;
        inflater = i;
        container = c;
        rootView = (RelativeLayout) i.inflate(R.layout.card_zone, container, false);
        activity = null;

        Button button = (Button) rootView.findViewById(R.id.prevCardButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                restorePreviousCard();
            }
        });

        //initializes moqup test data instead of the real deal
        //setTestData();

        return rootView;
    }

    public SearchQueryResult getCurrentSet() {
        return currentSet;
    }

    public String getCurrentShownCardProfessionalLocationPlusProfession() {
        return
                ((TextView)cards[0].findViewById(R.id.location)).getText().toString()
                        +       ((TextView)cards[0].findViewById(R.id.profession)).getText().toString();
    }

    @Mock
    Links links;

    public void setTestData(ArrayList<Professional> test_dataset)
    {
        links = mock(Links.class);

        currentSet = new SearchQueryResult();
        currentSet.data = new ArrayList<Professional>();
        currentSet.meta = new Meta();
        currentSet.meta.pagination = mock(Pagination.class);
        when(currentSet.meta.pagination.getLinks()).thenReturn(links);
        when(links.getNext()).thenReturn("some link");

        nextSet = new SearchQueryResult();
        nextSet.data = new ArrayList<Professional>();
        currentSet.data.addAll(test_dataset.subList(0,64));
        nextSet.data.addAll(test_dataset.subList(64,128));

        /*for(int i = 0; i<64 ; ++i)
            currentSet.data.add(ProfessionalTestClass.testProfessional("locC"+Integer.toString(i),"titC"+Integer.toString(i)));
        nextSet = new SearchQueryResult();
        nextSet.data = new ArrayList<Professional>();
        for(int i = 0; i<64 ; ++i)
            nextSet.data.add(ProfessionalTestClass.testProfessional("locN"+Integer.toString(i),"titN"+Integer.toString(i)));*/
        cards = new RelativeLayout[3];
        cards[0] = professionalCard(currentSet.data.get(0));
        cards[1] = professionalCard(currentSet.data.get(1));
        cards[2] = professionalCard(currentSet.data.get(2));
        cards_professional_data = new Professional[3];
        cards_professional_data[0] = currentSet.data.get(0);
        cards_professional_data[1] = currentSet.data.get(1);
        cards_professional_data[2] = currentSet.data.get(2);
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

        switch(payment_type) {
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
