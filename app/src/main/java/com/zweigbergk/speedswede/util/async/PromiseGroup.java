package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.Tuple;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

// TODO Use Tag() instead of Tuples...
class PromiseGroup<E> extends Promise<E> {

    private List<Tuple<PromiseNeed, Commitment<?>>> mPromiseTuples;

    PromiseGroup(List<Tuple<PromiseNeed, Commitment<?>>> tuples) {
        super(null);

        mPromiseTuples = new ArrayList<>();

        //We have to add the next requirement before we add our item, since otherwise the promise
        //will be marked as finished as soon as a Guarantee is passed.
        for (int i = 0; i < tuples.size(); ++i) {
            Tuple<PromiseNeed, Commitment<?>> tuple = tuples.get(i);
            if (i + 1 < tuples.size()) {
                this.requires(tuples.get(i + 1).getKey());
            } else {
                //Finally, add requirement for the first tuple
                this.requires(tuples.get(0).getKey());
            }

            includePromiseTuple(tuple);
        }
    }

    private static final Result<List<?>> listBlueprint = itemMap -> {
        List<Object> result = new ArrayList<>();
        Lists.forEach(itemMap.getItems(), entry -> result.add(entry.getValue()));
        return result;
    };

    static PromiseGroup<List<?>> normal(List<Tuple<PromiseNeed, Commitment<?>>> tuples) {
        return (PromiseGroup<List<?>>) new PromiseGroup<List<?>>(tuples).setResultForm(listBlueprint);
    }

    private void includePromiseTuple(Tuple<PromiseNeed, Commitment<?>> tuple) {
        mPromiseTuples.add(tuple);
        Log.d(TAG, "Adding tuple yeah!");

        Commitment<?> commitment = tuple.getValue();
        PromiseNeed need = tuple.getKey();

        commitment.addClient(item -> {
            if (item != null) {
                Log.d(TAG, Stringify.curlyFormat("Received item: {item} at need: {need}",
                        item.toString(), need));
                this.addItem(need, commitment);
            }
        });
    }

    @Override
    protected void complete() throws PromiseException {
        Log.d(TAG, "In complete()");
        ItemMap items = new ItemMap();
        Lists.forEach(mPromiseTuples, tuple -> {
            if (tuple.getKey() == null) {
                throw new PromiseException("Every Tuple must have a key. Look at the tuples added" +
                        "to the PromiseGroup and make sure none of them have a null key.");
            } else {
                Log.d(TAG, Stringify.curlyFormat("complete(): Adding tuple: {tuple}", tuple));
                items.put(tuple.getKey(), tuple.getValue().getProduct());
            }
        });

        mCompletedProduct = mResultForm.makeFromItems(items);

        super.notifyListeners();
    }

   /* @Override
    protected boolean isFulfilled() {
        return Lists.reject(mPromiseTuples, tuple -> {
            Log.d(TAG, Stringify.curlyFormat("Tuple with key: {key} is fulfilled? {bool}",
                    tuple.getKey(), tuple.toString().isFulfilled()));
            return tuple.toString().isFulfilled();
        }).size() == 0;
    }*/

    private static class PromiseException extends RuntimeException {
        PromiseException(String message) {
            super(message);
        }
    }
}