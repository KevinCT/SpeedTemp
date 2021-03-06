package com.zweigbergk.speedswede.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;

import java.util.Locale;


public class ParcelHelper {

    private static final int FLAGS_NORMAL = 0;
    private static final String TAG = ParcelHelper.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    public static <E extends Parcelable> void writeParcelableList(Parcel parcel, ListExtension<E> list) {
        parcel.writeInt(list.size());
        list.foreach(e -> parcel.writeParcelable(e, FLAGS_NORMAL));
    }

    public static <E extends Parcelable> ListExtension<E> readParcelableList(Parcel parcel, Class<E> eClass) {
        int size = parcel.readInt();
        ListExtension<E> list = new ArrayListExtension<>();
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
            Parcel parcel, MapExtension<K, V > map) {
        parcel.writeInt(map.size());
        for(MapExtension.Entry<K, V> e : map.entrySet()){
            parcel.writeParcelable(e.getKey(), FLAGS_NORMAL);
            parcel.writeParcelable(e.getValue(), FLAGS_NORMAL);
        }
    }

    /**
     * From http://stackoverflow.com/questions/8254654/how-write-java-util-map-into-parcel-in-a-smart-way
     * 10/10/2016
     */
    // For reading from a Parcel
    public static <K extends Parcelable,V extends Parcelable> MapExtension<K,V> readParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass) {
        int size = parcel.readInt();
        MapExtension<K, V> map = new HashMapExtension<>();
        for(int i = 0; i < size; i++){
            map.put(kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    public static <E extends Parcelable> void saveParcelableList(Bundle bundle, ListExtension<E> list, String tag) {
        bundle.putInt(tag, list.size());
        for (int i = 0; i < list.size(); i++) {
            if (bundle.getParcelable(tag + i) != null) {
                Log.w(TAG, String.format("Overwriting data in bundle %s at tag %s with index %d",
                        bundle.toString(), tag, i));
            }
            Log.d(TAG, "Saving item: " + list.get(i));
            bundle.putParcelable(tag + i, list.get(i));
        }
    }

    public static <E extends Parcelable> ListExtension<E> retrieveParcelableList(Bundle bundle, String tag) {
        ListExtension<E> list = new ArrayListExtension<>();
        int size = bundle.getInt(tag);
        for (int i = 0; i < size; i++) {
            Log.d(TAG, "retrieveParcelableList(): Adding item!");
            list.add(bundle.getParcelable(tag + i));
        }
        return list;
    }
}
