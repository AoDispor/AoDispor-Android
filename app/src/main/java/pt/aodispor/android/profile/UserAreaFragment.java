package pt.aodispor.android.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.dialogs.NewPriceDialog;

public class UserAreaFragment extends Fragment implements Notification {
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private static final String URL_UPLOAD_IMAGE = "https://api.aodispor.pt/users/me/profile/avatar";
    private static final int SELECT_PICTURE = 0;
    private ListView listView;
    private LinearLayout loadingMessage;
    private CustomAdapter arrayAdapter;
    private Button saveButton;
    private boolean[] updatedItems;
    private View root;

    public static UserAreaFragment newInstance() {
        return new UserAreaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.new_profile, container, false);
        listView = (ListView) root.findViewById(R.id.list);
        loadingMessage = (LinearLayout) root.findViewById(R.id.loadingMessage);
        saveButton = (Button) root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setEnabled(false);
                startLoading();
                updateListItems();
            }
        });

        List<ListItem> list = new ArrayList<>();
        ListItem profile = new Profile(getContext(), getActivity());
        profile.setNotification(this);
        list.add(profile);
        updatedItems = new boolean[list.size()];
        arrayAdapter = new CustomAdapter(getContext(), R.layout.profile, list);
        listView.setAdapter(arrayAdapter);

        startListItems();

        startLoading();

        return root;
    }

    private void startListItems() {
        for(int i = 0; i < arrayAdapter.getCount(); i++){
            ListItem item = arrayAdapter.getItem(i);
            if(item != null) {
                updatedItems[i] = item.onStart();
            }
        }
        if(checkAllUpdated()) {
            endLoading();
        }
    }

    private void updateListItems() {
        for(int i = 0; i < arrayAdapter.getCount(); i++){
            ListItem item = arrayAdapter.getItem(i);
            if(item != null) {
                updatedItems[i] = item.onUpdate();
            }
        }
        if(checkAllUpdated()) {
           endLoading();
        }
    }

    private boolean checkAllUpdated() {
        boolean allUpdated = true;
        for (int i = 0; i < updatedItems.length; i++) {
            if(!updatedItems[i]) {
                allUpdated = false;
            }
        }
        return allUpdated;
    }

    private void startLoading() {
        listView.setVisibility(View.INVISIBLE);
        loadingMessage.setVisibility(View.VISIBLE);
    }

    private void endLoading() {
        updatedItems = new boolean[arrayAdapter.getCount()];
        loadingMessage.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        listView.scrollTo(0, 0);
        saveButton.setEnabled(true);
    }

    @Override
    public void notify(ListItem item, boolean ok, String message) {
        updatedItems[arrayAdapter.getPosition(item)] = ok;
        if(ok) {
            if(checkAllUpdated()) {
                endLoading();
            }
        } else {
            errorHandler(message);
        }
    }

    private void errorHandler(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        endLoading();
    }

    /**
     * Custom adapter that is responsible for creating the views according to each item's type.
     */
    public class CustomAdapter extends ArrayAdapter<ListItem> {
        private List<ListItem> listItems;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects) {
            super(context, resource, objects);
            listItems = objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ListItem item = listItems.get(position);
            return item.getView();
        }
    }

}
