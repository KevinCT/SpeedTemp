package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;
import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductBuilder<Product> {
    public static final String TAG = ProductBuilder.class.getSimpleName().toUpperCase();

    private Blueprint<Product> mBlueprint;

    private List<Client<Product>> mClients;

    private Map<Executable, Executable.Interest<Product>> mInterestExecutables;

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
        mInterestExecutables = new HashMap<>();

        mTreasureChest = new TreasureChest();
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    public void setBlueprint(Blueprint<Product> blueprint) {
        if (mBlueprint != null) {
            Log.w(TAG, "WARNING! Replacing an existing blueprint. At: ");
            new Exception().printStackTrace();
        }

        mBlueprint = blueprint;
    }

    public void attachLocks(ProductLock... locks) {
        Lists.forEach(Arrays.asList(locks), mTreasureChest::addLock);
    }

    private synchronized void complete() {
        mCompletedProduct = mBlueprint.makeFromItems(mTreasureChest.getItems());

        notifyListeners();

        Log.d(TAG, "Completing!");
    }

    private void notifyListeners() {
        Lists.forEach(mClients.iterator(), client -> {
            client.supply(mCompletedProduct);
            mClients.remove(client);
        });

        Lists.forEach(mExecutables.iterator(), executable -> {
            executable.run();
            mExecutables.remove(executable);
        });

        for (Map.Entry<Executable, Executable.Interest<Product>> entry :
                mInterestExecutables.entrySet()) {
            Executable.Interest<Product> interest = entry.getValue();
            Executable executable = entry.getKey();
            if (interest.caresFor(mCompletedProduct)) {
                executable.run();
            }

            mInterestExecutables.remove(executable);
        }
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

    public void addExecutable(Executable executable, Executable.Interest<Product> interest) {
        if (!hasProduct()) {
            mInterestExecutables.put(executable, interest);
        } else {
            if (!mBuildFailed) {
                if (interest.caresFor(mCompletedProduct)) {
                    executable.run();
                }
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
            Long value = -1L;

            if (item == null) {
                return null;
            }

            if (item.getClass() == Long.class) {
                value = (Long) item;
            } else {
                try {
                    value = Long.parseLong(item.toString());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "In getLong(): Tried to format " + item.toString() + " as long but was unsuccessful. Returning -1.");
                }
            }

            return value;
        }

        public Boolean getBoolean(ProductLock lock) {
            Object item = items.get(lock);

            if (item == null) {
                return null;
            }

            Boolean value = false;

            if (item.getClass() == Boolean.class) {
                value = (Boolean) item;
            } else {
                try {
                    value = Boolean.parseBoolean(item.toString());
                } catch (NumberFormatException e) {
                    Log.e(TAG, "In getBoolean(): Tried to format " + item.toString() + " as boolean but was unsuccessful. Returning false.");
                }
            }

            return value;
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
