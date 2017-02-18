package pt.aodispor.android.view.base;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.CardStack;
import pt.aodispor.android.R;


public class CardStackTestClass extends CardStack{

    @Override
    protected RelativeLayout createProfessionalCard(String profession_text,
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
                price.setTextColor(fragment.getResources().getColor(R.color.by_hour));
                break;
            case "S":
                price.setText(Html.fromHtml(price_value + " " + currency_type));
                price.setTextColor(fragment.getResources().getColor(R.color.by_service));
                break;
            case "D":
                price.setText(Html.fromHtml(price_value + " por dia"));
        }

        return card;
    }

}
