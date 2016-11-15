package pt.aodispor.aodispor_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfessionalProfileActivity extends AppCompatActivity {
    public ProfessionalProfileActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professional_card);
        /*
        TextView name = (TextView) card.findViewById(R.id.name);
        name.setText(Html.fromHtml(n));
        */

        Bundle extras = getIntent().getExtras();

        TextView profession = (TextView) findViewById(R.id.profession);
        String _profession = extras.getString("profession");
        profession.setText(Html.fromHtml(_profession));
        profession.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        TextView location = (TextView) findViewById(R.id.location);
        String _location = extras.getString("location");
        location.setText(Html.fromHtml(_location));
        location.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        location.setClickable(true);
        location.setOnClickListener(new LocalizationOnClickListener(this,location));

        TextView description = (TextView) findViewById(R.id.description);
        String _description = extras.getString("description");
        description.setText(Html.fromHtml(_description));
        description.setMovementMethod(new ScrollingMovementMethod());

        TextView price = (TextView) findViewById(R.id.price);
        String _price = extras.getString("price");
        price.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        price.setText(Html.fromHtml(_price));

        String _type = extras.getString("type");
        String _currency = extras.getString("currency");

        switch(_type) {
            case "H":
                price.setText(Html.fromHtml(_price + " " + _currency + "/h"));
                price.setTextColor(getResources().getColor(R.color.by_hour));
                break;
            case "S":
                price.setText(Html.fromHtml(_price + " " + _currency));
                price.setTextColor(getResources().getColor(R.color.by_service));
                break;
            case "D":
                price.setText(Html.fromHtml(_price + " por dia"));
        }

        String _avatar = extras.getString("avatar_url");
        ImageView avatar = (ImageView) findViewById(R.id.profile_image);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(_avatar, avatar);
    }
}
