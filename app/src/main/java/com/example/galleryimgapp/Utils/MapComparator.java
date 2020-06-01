package com.example.galleryimgapp.Utils;

import java.util.Comparator;
import java.util.HashMap;

public class MapComparator implements Comparator<HashMap<String, String>> {

    private final String key;
    private final String order;

    public MapComparator(String key, String order) {
        this.key = key;
        this.order = order;
    }

    @Override
    public int compare(HashMap<String, String> o1,
                       HashMap<String, String> o2) {

        String o1Value = o1.get(key);
        String o2Value = o2.get(key);
        if (this.order.toLowerCase().contentEquals("asc")){
            return o1Value.compareTo(o2Value);
        }
        else {
            return o2Value.compareTo(o1Value);
        }
    }
}
