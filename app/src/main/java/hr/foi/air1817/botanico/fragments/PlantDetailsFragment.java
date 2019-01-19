package hr.foi.air1817.botanico.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watering.GardenSettings;
import com.example.watering.MoistureLevelGraphFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hr.foi.air1817.botanico.NavigationManager;
import hr.foi.air1817.botanico.PlantViewModel;
import hr.foi.air1817.botanico.helpers.CurrentPlant;
import hr.foi.air1817.botanico.R;
import hr.foi.air1817.botanico.entities.Plant;

public class PlantDetailsFragment extends Fragment {
    int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plant_details_fragment, container, false);
        return v;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.nav_plant_details);

        final ImageView plantImage = getView().findViewById(R.id.plantImage);
        final TextView temp = getView().findViewById(R.id.temperature_data);
        final TextView hum = getView().findViewById(R.id.humidity_data);
        final TextView lux = getView().findViewById(R.id.light_data);

        final Bundle data = getArguments();
        id = (int) data.get("id");
        CurrentPlant.path = Integer.toString(id);

        final PlantViewModel viewModel = ViewModelProviders.of((AppCompatActivity)getActivity()).get(PlantViewModel.class);
        LiveData<Plant> liveData = viewModel.getPlantLiveData();


        liveData.observe((AppCompatActivity) getActivity(), new Observer<Plant>() {
            @Override
            public void onChanged(@Nullable Plant plant) {
                //TODO bind
                hum.setText(Float.toString(plant.getHumidity()));
                lux.setText(Float.toString(plant.getLight()));
                temp.setText(Float.toString(plant.getTemp()));
            }
        });

        StorageReference loadImage = FirebaseStorage.getInstance().getReference( id + "/avatar_image/avatar.jpg");

        loadImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).into(plantImage);
            }
        });

        final ImageButton gardenSettingsButton = getView().findViewById(R.id.garden_settings_icon);
        gardenSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment gsf = new GardenSettings();
                NavigationManager.getInstance().switchFragment(gsf, "details");
                }
            });

        final ImageButton humidtyButton = getActivity().findViewById(R.id.humidity_img);
        humidtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> dateList = new ArrayList<>();
                final ArrayList<String> moistureList = new ArrayList<>();
                DatabaseReference loadData = FirebaseDatabase.getInstance().getReference( "/"+ id +"/history");
                loadData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            dateList.add(String.valueOf( dsp.child("date").getValue()).substring(0, 2));
                            moistureList.add(String.valueOf(dsp.child("moisture").getValue()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Bundle args = new Bundle();
                args.putStringArrayList("moisture", moistureList);
                args.putStringArrayList("date", dateList);
                Fragment mlg = new MoistureLevelGraphFragment();
                mlg.setArguments(args);
                NavigationManager.getInstance().switchFragment(mlg, "mositure_graph");
            }
        });
    }


}
