package com.zweigbergk.speedswede.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductBuilder<Product> {
    public static final String TAG = ProductBuilder.class.getSimpleName().toUpperCase();

    private Blueprint<Product> mBlueprint;

    private List<Client<Product>> mClients;

    private TreasureChest mTreasureChest;

    /** @param locks The locks that are required to be non-null for the builder to call complete() */
    public ProductBuilder(Blueprint<Product> blueprint, ProductLock... locks) {

        mBlueprint = blueprint;

        mClients = new ArrayList<>();

        mTreasureChest = new TreasureChest();
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    public void attachLocks(ProductLock... locks) {
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    private void complete() {
        Product product = mBlueprint.makeFromItems(mTreasureChest.getItems());
        Log.d(TAG, product.toString());

        if (mClients.size() == 0) {
            Log.e(TAG, String.format("WARNING! [NO_CLIENT_ATTACHED] Productbuilder with object:" +
                    "[%s] has no attached clients.", product));
        } else {
            Lists.forEach(mClients, listener -> listener.supply(product));
            Log.d(TAG, "Completing!");
        }
    }

    public void requireState(ProductLock key, StateRequirement requirement) {
        mTreasureChest.requireState(key, requirement);
    }

    public void addItem(ProductLock lock, Object data) {
        mTreasureChest.put(lock, data);

        Log.d(TAG, "Appending: " + data.toString());

        if (mTreasureChest.isOpened()) {
            Log.d(TAG, "All locks have been opened. Completing...");
            complete();
        }
    }

    public void addClient(Client<Product> client) {
        mClients.add(client);
    }

    public void removeClient(Client<Product> client) {
        mClients.remove(client);
    }

    public interface Blueprint<Product> {
        Product makeFromItems(Map<ProductLock, Object> map);
    }
}
