package android.example.myfoodrecords.fragments;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.activities.MainActivity;
import android.example.myfoodrecords.utils.RealmHelper;
import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class ItemViewFragment extends Fragment {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private View rootView;

    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRealm();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_item_view, container, false);

        setupUi();

        return rootView;
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
        helper.selectAllFoodsFromDb();
        refresh();
    }

    private void setupUi() {
        recyclerView = rootView.findViewById(R.id.item_view_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        ItemViewAdapter itemViewAdapter = new ItemViewAdapter(helper.retrieveAllFoodFromSelectedDb(), getActivity());
        recyclerView.setAdapter(itemViewAdapter);
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                if(recyclerView == null) {
                    recyclerView = rootView.findViewById(R.id.item_view_rc);
                }
                ItemViewAdapter adapter = new ItemViewAdapter(helper.retrieveAllFoodFromSelectedDb(), getActivity());
                recyclerView.setAdapter(adapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }
}
