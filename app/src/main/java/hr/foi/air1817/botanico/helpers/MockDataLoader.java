package hr.foi.air1817.botanico.helpers;

import java.util.ArrayList;
import java.util.List;

import hr.foi.air1817.botanico.entities.Plant;

public class MockDataLoader {
    public static List<Plant> getDemoData(){
        List<Plant> mPlants = new ArrayList<Plant>();
        mPlants.add(new Plant(1,"Tulips",34,3,4,""));
        mPlants.add(new Plant(2,"Salad",34,20,500,""));
        return mPlants;
    }
}