package com.zweigbergk.speedswede.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.collection.Map;


public class ParcelHelper {

    public static final String TAG = ParcelHelper.class.getSimpleName().toUpperCase();

    public static <E extends Parcelable> void writeParcelableList(Parcel parcel, int flags, List<E> list) {
        parcel.writeInt(list.size());
        Lists.forEach(list, e -> parcel.writeParcelable(e, flags));
    }

    public static <E extends Parcelable> List<E> readParcelableList(Parcel parcel, Class<E> eClass) {
        int size = parcel.readInt();
        List<E> list = new ArrayList<>();
        while (size-- > 0) {
            list.add(eClass.cast(parcel.readParcelable(eClass.getClassLoader())));
        }

        return list;
    }

    /**
     * From http://stackoverflow.com/questions/8254654/how-write-java-util-map-into-parcel-in-a-smart-way
     * 10/10/2016
     */
    // For writing to a Parcel
    public static <K extends Parcelable,V extends Parcelable> void writeParcelableMap(
            Parcel parcel, int flags, Map<K, V > map) {
        parcel.writeInt(map.size());
        for(Map.Entry<K, V> e : map.entrySet()){
            parcel.writeParcelable(e.getKey(), flags);
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    /**
     * From http://stackoverflow.com/questions/8254654/how-write-java-util-map-into-parcel-in-a-smart-way
     * 10/10/2016
     */
    // For reading from a Parcel
    public static <K extends Parcelable,V extends Parcelable> Map<K,V> readParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass) {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<>();
        for(int i = 0; i < size; i++){
            map.put(kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    public static <E extends Parcelable> void saveParcableList(Bundle bundle, List<E> list, String tag) {
        bundle.putInt(tag, list.size());
        for (int i = 0; i < list.size(); i++) {
            if (bundle.getParcelable(tag + i) != null) {
                Log.w(TAG, String.format("Overwriting data in bundle %s at tag %s with index %d",
                        bundle.toString(), tag, i));
            }
            bundle.putParcelable(tag + i, list.get(i));
        }
    }

    public static <E extends Parcelable> List<E> retrieveParcableList(Bundle bundle, String tag) {
        List<E> list = new ArrayList<>();
        int size = bundle.getInt(tag);
        for (int i = 0; i < size; i++) {
            list.add(bundle.getParcelable(tag + i));
        }
        return list;
    }
}
