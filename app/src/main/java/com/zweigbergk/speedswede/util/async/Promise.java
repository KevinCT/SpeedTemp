package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Tuple;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import java.util.Locale;

import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;

public class Promise<E> extends Commitment<E> {
    private static final String TAG = Promise.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    Result<E> mResultForm;

    PromiseState promiseState;

    private MapExtension<PromiseNeed, ListExtension<Promise<?>>> chainedPromises;

    //Error flag. If this is set, the builder will return null to all its listeners.
    private boolean mBuildFailed;

    public static <E> Promise<E> create() {
        return new Promise<>(null);
    }

    /** @param needs The requires that are required to be non-null for the builder to call complete() */
    public Promise(Result<E> blueprint, PromiseNeed... needs) {
        mResultForm = blueprint;
        mBuildFailed = false;

        mClients = new ArrayListExtension<>();
        mExecutables = new ArrayListExtension<>();
        mInterestExecutables = new HashMapExtension<>();
        chainedPromises = new HashMapExtension<>();

        promiseState = new PromiseState();
        Arrays.asList(needs).foreach(promiseState::addNeed);
    }

    public Promise<E> setResultForm(Result<E> result) {
        if (mResultForm != null) {
            Log.w(TAG, "WARNING! Replacing an existing result. At: ");
            new Exception().printStackTrace();
        }

        mResultForm = result;

        return this;
    }

    public static <E> Promise<E> group(Result<E> resultForm, ListExtension<Tuple<PromiseNeed, Commitment<?>>> promises) {
        PromiseGroup<E> group = new PromiseGroup<>(promises);
        group.setResultForm(resultForm);
        return group;
    }

    public <NewType> Promise<NewType> thenPromise(PromiseNeed need, Promise<NewType> promise) {
        if (!chainedPromises.containsKey(need)) {
            chainedPromises.put(need, new ArrayListExtension<>());
        }

        chainedPromises.get(need).add(promise);
        return promise;
    }

    public void requires(PromiseNeed... locks) {
        Arrays.asList(locks).foreach(promiseState::addNeed);
    }

    void complete() {
        mCompletedProduct = mResultForm.makeFromItems(promiseState.getItems());

        notifyListeners();

        forwardToChainedPromises();
    }

    @Override
    protected void addClient(Client<E> client) {
        if (!hasProduct()) {
            Log.d(TAG, "No product, adding client");
            mClients.add(client);
        } else {
            Log.d(TAG, "Product found!");
            if (!mBuildFailed) {
                Log.d(TAG, "Build did not fail. supplying with product...");
                client.supply(mCompletedProduct);
            } else {
                client.supply(null);
            }
        }
    }

    void addExecutable(Executable executable, Executable.Interest<E> interest) {
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

    /**
     * This forwards the completed product to every Promise that has called thenPromise()
     * on this Promise.
     */
    private void forwardToChainedPromises() {
        chainedPromises.foreach(promiseList ->
                promiseList.getValue().foreach(promise ->
                        promise.addItem(promiseList.getKey(), mCompletedProduct)));
    }

    void notifyListeners() {
        mClients.foreach(client -> client.supply(mCompletedProduct));
        mClients.clear();

        mExecutables.foreach(Executable::run);
        mExecutables.clear();

        mInterestExecutables.foreach(entry -> {
                    Executable.Interest<E> interest = entry.getValue();
                    Executable executable = entry.getKey();
                    if (interest.caresFor(mCompletedProduct)) {
                        executable.run();
                    }
                });

        mInterestExecutables.clear();
    }

    public void addItem(PromiseNeed need, Object data) {
        promiseState.put(need, data);

        if (isFulfilled()) {
            Log.d(TAG, "All requires have been met. Completing...");
            complete();
        }
    }

    private boolean isFulfilled() {
        return promiseState.isFulfilled();
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
    @Override
    protected E getProduct() {
        return hasProduct() ? mCompletedProduct : null;
    }

    private boolean hasProduct() {
        return mCompletedProduct != null || mBuildFailed;
    }

    public static class ItemMap {

        MapExtension<PromiseNeed, Object> items;

        ItemMap() {
            items = new HashMapExtension<>();
        }

        public void put(PromiseNeed key, Object item) {
            items.put(key, item);
        }

        public Object get(PromiseNeed need) {
            return items.get(need);
        }

        public DataSnapshot getSnapshot(PromiseNeed need) {
            return (DataSnapshot) items.get(need);
        }


        public User getUser(PromiseNeed need) {
            return (User) items.get(need);
        }

        MapExtension<PromiseNeed, Object> getItems() {
            return items;
        }

        Boolean getBoolean(PromiseNeed need) {
            Object item = items.get(need);

            if (item == null) {
                return false;
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
    }

    public interface Result<E> {
        E makeFromItems(ItemMap map);
    }
}
