package pt.aodispor.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NewProfileFragment extends Fragment{
    private ListView listView;

    public static NewProfileFragment newInstance() {
        return new NewProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.new_profile, container, false);
        listView = (ListView) root.findViewById(R.id.list);
        ListItem[] list = {new ListItemField(getContext(), "loool"), new ListItemField(getContext(), "loool"), new ListItemField(getContext(), "loool")};
        listView.setAdapter(new CustomAdapter(getContext(), R.layout.profile_field, list));
        return root;
    }

    public class CustomAdapter extends ArrayAdapter {
        private ListItem[] listItems;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ListItem[] objects) {
            super(context, resource, objects);
            listItems = objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ListItem item = listItems[position];
            return item.getView();
        }
    }

}
