package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Tuple;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;
import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Promise<E> extends Commitment<E> {
    public static final String TAG = Promise.class.getSimpleName().toUpperCase();

    Result<E> mResultForm;

    private PromiseState promiseState;

    private Map<PromiseNeed, List<Promise<?>>> chainedPromises;

    //Error flag. If this is set, the builder will return null to all its listeners.
    boolean mBuildFailed;

    public static <E> Promise<E> create() {
        return new Promise<>(null);
    }

    /** @param needs The requires that are required to be non-null for the builder to call complete() */
    public Promise(Result<E> blueprint, PromiseNeed... needs) {
        mResultForm = blueprint;
        mBuildFailed = false;

        mClients = new ArrayList<>();
        mExecutables = new ArrayList<>();
        mInterestExecutables = new HashMap<>();
        chainedPromises = new HashMap<>();

        promiseState = new PromiseState();
        Lists.forEach(Arrays.asList(needs), promiseState::addNeed);
    }

    public Promise<E> setResultForm(Result<E> result) {
        if (mResultForm != null) {
            Log.w(TAG, "WARNING! Replacing an existing result. At: ");
            new Exception().printStackTrace();
        }

        mResultForm = result;

        return this;
    }

    public static Promise<List<?>> all(List<Tuple<PromiseNeed, Commitment<?>>> tuples) {
        return PromiseGroup.normal(tuples);
    }

    /*public static <E> Promise<E> group(Result<E> resultForm, Tuple<PromiseNeed, Commitment<?>>... promises) {
        PromiseGroup<E> group = new PromiseGroup<>(promises);
        group.setResultForm(resultForm);
        return group;
    }*/


    public static <E> Promise<E> group(Result<E> resultForm, List<Tuple<PromiseNeed, Commitment<?>>> promises) {
        PromiseGroup<E> group = new PromiseGroup<>(promises);
        group.setResultForm(resultForm);
        return group;
    }

    public <NewType> Promise<NewType> thenPromise(PromiseNeed need, Promise<NewType> promise) {
        if (!chainedPromises.containsKey(need)) {
            chainedPromises.put(need, new ArrayList<>());
        }

        chainedPromises.get(need).add(promise);
        return promise;
    }

    public void requires(PromiseNeed... locks) {
        Lists.forEach(Arrays.asList(locks), promiseState::addNeed);
    }

    protected void complete() {
        mCompletedProduct = mResultForm.makeFromItems(promiseState.getItems());

        notifyListeners();

        forwardToChainedPromises();
    }

    @Override
    protected void addClient(Client<E> client) {
        if (!hasProduct()) {
            Log.d(TAG, "No product. Adding client...");
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

    @Override
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

    /**
     * This forwards the completed product to every Promise that has called thenPromise()
     * on this Promise.
     */
    private void forwardToChainedPromises() {
        List<Tuple<PromiseNeed, Promise<?>>> promises = new ArrayList<>();

        Lists.forEach(chainedPromises, entry -> {
            List<Promise<?>> list = entry.getValue();
            Lists.forEach(list, promise -> promises.add(new Tuple<>(entry.getKey(), promise)));
        });

        Lists.forEach(promises, tuple -> {
            tuple.getValue().addItem(tuple.getKey(), mCompletedProduct);
        });
    }

    protected void notifyListeners() {
        Lists.forEach(mClients.iterator(), client -> {
            client.supply(mCompletedProduct);
        });

        mClients.clear();

        Lists.forEach(mExecutables.iterator(), Executable::run);

        mExecutables.clear();

        for (Map.Entry<Executable, Executable.Interest<E>> entry :
                mInterestExecutables.entrySet()) {
            Executable.Interest<E> interest = entry.getValue();
            Executable executable = entry.getKey();
            if (interest.caresFor(mCompletedProduct)) {
                executable.run();
            }
        }

        mInterestExecutables.clear();

    }

    public void requireState(PromiseNeed need, StateRequirement requirement) {
        promiseState.requireState(need, requirement);
    }

    public void addItem(PromiseNeed need, Object data) {
        promiseState.put(need, data);

        if (isFulfilled()) {
            Log.d(TAG, "All requires have been met. Completing...");
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
            Log.d(TAG, "All requires have been met. Completing...");
            complete();
        }
    }



    @Override
    protected boolean isFulfilled() {
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

    @Override
    protected boolean hasProduct() {
        return mCompletedProduct != null || mBuildFailed;
    }

    public static class ItemMap {

        Map<PromiseNeed, Object> items;

        ItemMap() {
            items = new HashMap<>();
        }

        public void put(PromiseNeed key, Object item) {
            items.put(key, item);
        }

        public static <E> List<E> asSimilarList(ItemMap map) {
            List<E> result = new ArrayList<>();
            Lists.forEach(map.getItems(), item -> result.add( (E) item.getValue()));
            return result;
        }

        public Object get(PromiseNeed need) {
            return items.get(need);
        }

        public List getList(PromiseNeed need) {
            return (List) items.get(need);
        }

        Map<PromiseNeed, Object> getItems() {
            return items;
        }

        public String getString(PromiseNeed need) {
            return (String) items.get(need);
        }

        public int getInt(PromiseNeed need) {
            return (int) items.get(need);
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
    }

    public interface Result<E> {
        E makeFromItems(ItemMap map);
    }
}
