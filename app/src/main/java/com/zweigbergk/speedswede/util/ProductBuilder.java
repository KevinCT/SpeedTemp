package com.zweigbergk.speedswede.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductBuilder<Product> {
    public static final String TAG = ProductBuilder.class.getSimpleName().toUpperCase();

    private Map<BuilderKey, Object> mObjects;

    private Set<BuilderKey> mRequiredKeys;
    private Blueprint<Product> mBlueprint;

    private List<Client<Product>> mClients;

    /** @param keys The keys that are required to be non-null for the builder to call complete() */
    public ProductBuilder(Blueprint<Product> blueprint, BuilderKey... keys) {
        mObjects = new HashMap<>();
        mBlueprint = blueprint;
        mRequiredKeys = new HashSet<>();
        require(keys);

        mClients = new ArrayList<>();
    }

    public void require(BuilderKey... keys) {
        Lists.forEach(Arrays.asList(keys), key -> mRequiredKeys.add(key));
    }

    private void complete() {
        Product product = mBlueprint.realize(mObjects);
        Log.d(TAG, "We required : " + mRequiredKeys);
        Lists.forEach(mClients, listener -> listener.supply(product));
    }

    public void append(BuilderKey key, Object data) {
        mObjects.put(key, data);

        Log.d(TAG, "Appending: " + data.toString());

        if (hasMetRequirements()) {
            Log.d(TAG, "We have all keys. Completing...");
            complete();
        }
    }

    private boolean hasMetRequirements() {
        for (BuilderKey key : mRequiredKeys) {
            if (mObjects.get(key) == null) {
                return false;
            }
        }

        return true;
    }

    public void addClient(Client<Product> client) {
        mClients.add(client);
    }

    public void removeClient(Client<Product> client) {
        mClients.remove(client);
    }

    public interface Blueprint<Product> {
        Product realize(Map<BuilderKey, Object> map);
    }
}
