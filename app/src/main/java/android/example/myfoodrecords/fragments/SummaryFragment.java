package android.example.myfoodrecords.fragments;

import android.example.myfoodrecords.R;
import android.example.myfoodrecords.RealmHelper;
import android.example.myfoodrecords.SummaryAdapter;
import android.example.myfoodrecords.model.Food;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class SummaryFragment extends Fragment {

    private Realm realm;
    private RealmHelper helper;
    private RealmChangeListener realmChangeListener;

    private View rootView;
    private List<Food> foodList = new ArrayList<>();

    private RecyclerView recyclerView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        setupUi();

        return rootView;
    }

    private void setupUi() {



        helper = new RealmHelper(realm);
        helper.selectFromDb();

        recyclerView = rootView.findViewById(R.id.summary_rc);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        SummaryAdapter summaryAdapter = new SummaryAdapter(helper.retireve(), getActivity());
        recyclerView.setAdapter(summaryAdapter);
        refresh();
    }

    private void refresh() {
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                SummaryAdapter adapter = new SummaryAdapter(helper.retireve(), getActivity());
                recyclerView.setAdapter(adapter);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }


}
