package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.Tuple;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;

import java.util.Locale;
import java.util.Map;

class PromiseGroup<E> extends Promise<E> {
    private static final String TAG = PromiseGroup.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    private ListExtension<Tuple<PromiseNeed, Commitment<?>>> mPromiseTuples;

    PromiseGroup(ListExtension<Tuple<PromiseNeed, Commitment<?>>> tuples) {
        super(null);

        mPromiseTuples = new ArrayListExtension<>();

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

    @SuppressWarnings("unused")
    static PromiseGroup<ListExtension<?>> normal(ListExtension<Tuple<PromiseNeed, Commitment<?>>> tuples) {
        return (PromiseGroup<ListExtension<?>>)
                new PromiseGroup<ListExtension<?>>(tuples).setResultForm(itemMap ->
                        itemMap.getItems().transform(Map.Entry::getValue));
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

        mPromiseTuples.foreach(tuple -> {
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

    private static class PromiseException extends RuntimeException {
        PromiseException(String message) {
            super(message);
        }
    }
}