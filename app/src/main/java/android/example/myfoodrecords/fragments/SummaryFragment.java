package android.example.myfoodrecords.fragments;

import android.example.myfoodrecords.adapter.ItemViewAdapter;
import android.example.myfoodrecords.R;
import android.example.myfoodrecords.adapter.SummaryAdapter;
import android.example.myfoodrecords.model.Food;
import android.example.myfoodrecords.model.SummaryItem;
import android.example.myfoodrecords.utils.RealmHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private RecyclerView recyclerView;
    private Button foodButton;
    private Button placeButton;

    private List<SummaryItem> summaryItemList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupRealm();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        setupUi();
        return rootView;
    }

    private void setupRealm() {
        realm = Realm.getDefaultInstance();
        helper = new RealmHelper(realm);
    }

    private void setupUi() {
        setFoodSummaryItemList();
        recyclerView = rootView.findViewById(R.id.summary_rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        SummaryAdapter summaryAdapter = new SummaryAdapter(summaryItemList, getActivity());
        recyclerView.setAdapter(summaryAdapter);

        foodButton = rootView.findViewById(R.id.summary_food_button);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFoodSummaryItemList();
                SummaryAdapter summaryAdapter = new SummaryAdapter(summaryItemList, getActivity());
                recyclerView.setAdapter(summaryAdapter);
            }
        });
        placeButton = rootView.findViewById(R.id.summary_place_button);
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceSummaryItemList();
                SummaryAdapter summaryAdapter = new SummaryAdapter(summaryItemList, getActivity());
                recyclerView.setAdapter(summaryAdapter);
            }
        });
    }

    private void setFoodSummaryItemList() {
        summaryItemList = new ArrayList<>();
        List<Food> sortedFoodList= helper.retireveFoodWithNameSorted();
        int count = 0;
        float rating = 0;

        for(int i = 0; i < sortedFoodList.size() - 1; i++) {
            rating += sortedFoodList.get(i).getRating();
            count++;
            if(!sortedFoodList.get(i).getName().equals(sortedFoodList.get(i + 1).getName())) {

                String name = sortedFoodList.get(i).getName();
                addItemToSummayItemList(name, count, rating);
                count = 0;
                rating = 0;
            }
        }

        int lastIndex = sortedFoodList.size() - 1;
        String lastItemName = sortedFoodList.get(lastIndex).getName();
        String secondLastName = sortedFoodList.get(lastIndex - 1).getName();
        float lastItemRating = sortedFoodList.get(lastIndex).getRating();

        if(!lastItemName.equals(secondLastName)) {
            addItemToSummayItemList(lastItemName, 1, lastItemRating);
        } else {
            addItemToSummayItemList(lastItemName, count + 1, rating + lastItemRating);
        }
    }

    //TODO summaryList Place. need to fix
    private void setPlaceSummaryItemList() {
        summaryItemList = new ArrayList<>();
        List<Food> sortedFoodList= helper.retrieveFoodWithPlaceNameSorted();
        int count = 0;
        float rating = 0;

        for(int i = 0; i < sortedFoodList.size() - 1; i++) {
            rating += sortedFoodList.get(i).getRating();
            count++;
            if(sortedFoodList.get(i).getPlaceModel().getPlaceName() != null && sortedFoodList.get(i + 1).getPlaceModel().getPlaceName() != null) {
                if (!sortedFoodList.get(i).getName().equals(sortedFoodList.get(i + 1).getName())) {

                    String name = sortedFoodList.get(i).getPlaceModel().getPlaceName();
                    addItemToSummayItemList(name, count, rating);
                    count = 0;
                    rating = 0;
                }
            }
        }

        int lastIndex = sortedFoodList.size() - 1;
        String lastItemName = sortedFoodList.get(lastIndex).getPlaceModel().getPlaceName();
        String secondLastName = sortedFoodList.get(lastIndex - 1).getPlaceModel().getPlaceName();
        float lastItemRating = sortedFoodList.get(lastIndex).getRating();

        if(!lastItemName.equals(secondLastName)) {
            addItemToSummayItemList(lastItemName, 1, lastItemRating);
        } else {
            addItemToSummayItemList(lastItemName, count + 1, rating + lastItemRating);
        }
    }

    private void addItemToSummayItemList(String name, int count, float rating) {
        SummaryItem summaryItem = new SummaryItem();
        summaryItem.setName(name);
        summaryItem.setCount(count);
        summaryItem.setRating(rating / count);
        summaryItemList.add(summaryItem);
    }
}
