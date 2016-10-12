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

public class Promise<E> {
    public static final String TAG = Promise.class.getSimpleName().toUpperCase();

    private Blueprint<E> mBlueprint;

    private List<Client<E>> mClients;

    private Map<Executable, Executable.Interest<E>> mInterestExecutables;

    private List<Executable> mExecutables;

    private PromiseState promiseState;

    protected E mCompletedProduct;

    //Error flag. If this is set, the builder will return null to all its listeners.
    boolean mBuildFailed;

    public static <E> Promise<E> create() {
        return new Promise<>(null);
    }

    /** @param needs The needs that are required to be non-null for the builder to call complete() */
    public Promise(Blueprint<E> blueprint, PromiseNeed... needs) {

        mBlueprint = blueprint;
        mBuildFailed = false;

        mClients = new ArrayList<>();
        mExecutables = new ArrayList<>();
        mInterestExecutables = new HashMap<>();

        promiseState = new PromiseState();
        Lists.forEach(Arrays.asList(needs), promiseState::addLock);
    }

    public void setBlueprint(Blueprint<E> blueprint) {
        if (mBlueprint != null) {
            Log.w(TAG, "WARNING! Replacing an existing blueprint. At: ");
            new Exception().printStackTrace();
        }

        mBlueprint = blueprint;
    }

    public static Promise all(Promise... promises) {
        return new PromiseGroup(promises);
    }

    public void needs(PromiseNeed... locks) {
        Lists.forEach(Arrays.asList(locks), promiseState::addLock);
    }

    protected void complete() {
        mCompletedProduct = mBlueprint.makeFromItems(promiseState.getItems());

        notifyListeners();

        Log.d(TAG, "Completing!");
    }

    protected void notifyListeners() {
        Lists.forEach(mClients.iterator(), client -> {
            client.supply(mCompletedProduct);
            mClients.remove(client);
        });

        Lists.forEach(mExecutables.iterator(), executable -> {
            executable.run();
            mExecutables.remove(executable);
        });

        for (Map.Entry<Executable, Executable.Interest<E>> entry :
                mInterestExecutables.entrySet()) {
            Executable.Interest<E> interest = entry.getValue();
            Executable executable = entry.getKey();
            if (interest.caresFor(mCompletedProduct)) {
                executable.run();
            }

            mInterestExecutables.remove(executable);
        }
    }

    public void requireState(PromiseNeed need, StateRequirement requirement) {
        promiseState.requireState(need, requirement);
    }

    public void addItem(PromiseNeed need, Object data) {
        promiseState.put(need, data);

        if (isFulfilled()) {
            Log.d(TAG, "All needs have been met. Completing...");
            complete();
        }
    }

    /**
     * Tells the promise to update its state.
     * This MUST be called if StateRequirements are used, since the Promise will not know
     * that a requirement has been met unless it is explicitly asked to check.
     * That check is done using through method.
     */
    public void remind() {
        promiseState.updateState();

        if (isFulfilled()) {
            Log.d(TAG, "All needs have been met. Completing...");
            complete();
        }
    }

    protected void addClient(Client<E> client) {
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

    protected boolean isFulfilled() {
        return promiseState.isFulfilled();
    }

    protected void addExecutable(Executable executable, Executable.Interest<E> interest) {
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

    protected void addExecutable(Executable executable) {
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
    public void setPromiseFailed(boolean value) {
        mBuildFailed = value;
        if (mBuildFailed) {
            notifyListeners();
        }
    }

    /**
     *
     * @return The completed product, or null if the product is not completed.
     */
    protected E getProduct() {
        return hasProduct() ? mCompletedProduct : null;
    }

    protected boolean hasProduct() {
        return mCompletedProduct != null || mBuildFailed;
    }

    public interface Blueprint<E> {
        E makeFromItems(ItemMap map);
    }

    public void thenNotify(Client<E> client) {
        addClient(client);
    }

    public void thenPassTo(Client<E> client) {
        addClient(client);
    }

    public void then(Client<E> client) {
        addClient(client);
    }

    public void thenNotify(Executable executable) {
        addExecutable(executable);
    }

    public void whenFinished(Executable executable) {
        addExecutable(executable);
    }

    public static class ItemMap {

        private Map<PromiseNeed, Object> items;

        ItemMap() {
            items = new HashMap<>();
        }

        public Object get(PromiseNeed lock) {
            return items.get(lock);
        }

        public User getUser(PromiseNeed need) {
            return (User) items.get(need);
        }

        public List getList(PromiseNeed need) {
            return (List) items.get(need);
        }

        public String getString(PromiseNeed lock) {
            return (String) items.get(lock);
        }

        public int getInt(PromiseNeed lock) {
            return (int) items.get(lock);
        }

        public Long getLong(PromiseNeed need) {
            Object item = items.get(need);
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

        public Boolean getBoolean(PromiseNeed need) {
            Object item = items.get(need);

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

        public void put(PromiseNeed need, Object value) {
            items.put(need, value);
        }
    }
}
