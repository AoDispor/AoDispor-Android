package pt.aodispor.aodispor_android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;

public class ProfessionalProfileActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 0;
    private String selectedImagePath;
    private ImageView _image;
    private ImageLoader imageLoader;

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

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(_avatar, avatar);

        avatar.setClickable(true);
        _image = avatar;
        avatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openGallery(SELECT_PICTURE);
            }
        });

    }
    public void openGallery(int req_code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",200);
        intent.putExtra("outputY",200);
        intent.putExtra("return-data",true);
        startActivityForResult(intent, req_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Log.d("D", "b4uri");
            Bundle bundle = data.getExtras();
            Bitmap image = bundle.getParcelable("data");
            _image.setImageBitmap(image);
        }
    }
}
