package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.zweigbergk.speedswede.core.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductBuilder<Product> {
    public static final String TAG = ProductBuilder.class.getSimpleName().toUpperCase();

    private Blueprint<Product> mBlueprint;

    private List<Client<Product>> mClients;

    private List<Executable> mExecutables;

    private TreasureChest mTreasureChest;

    private Product mCompletedProduct;

    //Error flag. If this is set, the builder will return null to all its listeners.
    private boolean mBuildFailed;

    public static <Product> ProductBuilder<Product> shell() {
        return new ProductBuilder<>(null);
    }

    /** @param locks The locks that are required to be non-null for the builder to call complete() */
    public ProductBuilder(Blueprint<Product> blueprint, ProductLock... locks) {

        mBlueprint = blueprint;
        mBuildFailed = false;

        mClients = new ArrayList<>();
        mExecutables = new ArrayList<>();

        mTreasureChest = new TreasureChest();
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    public void setBlueprint(Blueprint<Product> blueprint) {
        if (mBlueprint != null) {
            Log.e(TAG, "WARNING! Replacing an existing blueprint. At: " + Thread.currentThread().getStackTrace().toString());
        }

        mBlueprint = blueprint;
    }

    public void attachLocks(ProductLock... locks) {
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    private void complete() {
        mCompletedProduct = mBlueprint.makeFromItems(mTreasureChest.getItems());
        Log.d(TAG, mCompletedProduct.toString());

        notifyListeners();

        Log.d(TAG, "Completing!");
    }

    private void notifyListeners() {
        Lists.forEach(mClients, client -> {
            client.supply(mCompletedProduct);
            mClients.remove(client);
        });

        Lists.forEach(mExecutables, executable -> {
            executable.run();
            mExecutables.remove(executable);
        });
    }

    public void requireState(ProductLock key, StateRequirement requirement) {
        mTreasureChest.requireState(key, requirement);
    }

    public void addItem(ProductLock lock, Object data) {
        mTreasureChest.put(lock, data);

        if (mTreasureChest.isOpened()) {
            Log.d(TAG, "All locks have been opened. Completing...");
            complete();
        }
    }

    public void updateState() {
        mTreasureChest.updateState();

        if (mTreasureChest.isOpened()) {
            Log.d(TAG, "All locks have been opened. Completing...");
            complete();
        }
    }

    public void addClient(Client<Product> client) {
        if (!hasProduct()) {
            mClients.add(client);
        } else {
            if (!mBuildFailed) {
                client.supply(mCompletedProduct);
            } else {
                client.supply(null);
            }
        }
    }

    public void addExecutable(Executable executable) {
        if (!hasProduct()) {
            mExecutables.add(executable);
        } else {
                executable.run();
        }
    }

    /**
     * CAREFUL! Sets the interrupted error flag. This will make the builder return null to all
     * its listeners.
     * */
    public void setBuildFailed(boolean value) {
        mBuildFailed = value;
        if (mBuildFailed) {
            notifyListeners();
        }
    }

    public boolean isFinished() {
        return mTreasureChest.isOpened();
    }

    private boolean hasProduct() {
        return mCompletedProduct != null || mBuildFailed;
    }

    public interface Blueprint<Product> {
        Product makeFromItems(ItemMap map);
    }

    public void thenNotify(Client<Product> client) {
        addClient(client);
    }

    public void thenPassTo(Client<Product> client) {
        addClient(client);
    }

    public void then(Client<Product> client) {
        addClient(client);
    }

    public void thenNotify(Executable executable) {
        addExecutable(executable);
    }

    public void thenPassTo(Executable executable) {
        addExecutable(executable);
    }

    public void then(Executable executable) {
        addExecutable(executable);
    }

    public static class ItemMap {

        private Map<ProductLock, Object> items;

        public ItemMap() {
            items = new HashMap<>();
        }

        public Object get(ProductLock lock) {
            return items.get(lock);
        }

        public String getString(ProductLock lock) {
            return (String) items.get(lock);
        }

        public int getInt(ProductLock lock) {
            return (int) items.get(lock);
        }

        public Long getLong(ProductLock lock) {
            Object item = items.get(lock);
            return item != null ? (long) item : null;
        }

        public Boolean getBoolean(ProductLock lock) {
            Object item = items.get(lock);
            return item != null ? (boolean) item : null;
        }

        public User getUser(ProductLock lock) {
            return (User) items.get(lock);
        }

        public List getList(ProductLock lock) {
            return (List) items.get(lock);
        }

        public void put(ProductLock key, Object value) {
            items.put(key, value);
        }
    }
}
