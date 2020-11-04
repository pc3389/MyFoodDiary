package bo.young.myfoodrecords.fragments;

import android.example.myfoodrecords.R;
import bo.young.myfoodrecords.utils.RealmHelper;
import bo.young.myfoodrecords.adapter.ItemViewAdapter;
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
    private RealmChangeListener<Realm> realmChangeListener;

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
        ItemViewAdapter itemViewAdapter = new ItemViewAdapter(helper.retrieveAllFoodFromSelectedDb(), getActivity(), getActivity());
        recyclerView.setAdapter(itemViewAdapter);
    }

    /**
     * Add a realm change listener for automatic UI updates
     */
    private void refresh() {
        realmChangeListener = new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(recyclerView != null) {
                            ItemViewAdapter adapter = new ItemViewAdapter(helper.retrieveAllFoodFromSelectedDb(), getActivity(), getActivity());
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && realmChangeListener != null) {
            realm.removeChangeListener(realmChangeListener);
            realm.close();
        }
    }
}
