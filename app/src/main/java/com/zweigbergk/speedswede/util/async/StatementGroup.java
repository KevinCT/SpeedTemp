package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.Tuple;

import com.zweigbergk.speedswede.util.collection.List;

// TODO Use Tag() instead of Tuples...
class StatementGroup extends Statement {

    StatementGroup(Result<Boolean> resultForm, List<Tuple<PromiseNeed, Statement>> tuples) {
        super(false);
        //Since default constructor in Statement always sets a result form, don't use default method here.
        mResultForm = resultForm;

        Lists.forEach(tuples, tuple -> requires(tuple.getKey()));
        Lists.forEach(tuples, this::includePromiseTuple);
    }

    private void includePromiseTuple(Tuple<PromiseNeed, Statement> tuple) {
        Statement statement = tuple.getValue();
        PromiseNeed need = tuple.getKey();

        statement.addClient(result -> {
            Log.d(TAG, Stringify.curlyFormat("Received statement result: {item} at need: {need}",
                    result, need));
            super.addItemFromSubClass(need, result);
        });
    }

    @Override
    protected void complete() throws PromiseException {
        Log.d(TAG, "Finished! Result: " + mResultForm.makeFromItems(promiseState.getItems()));
        super.complete();
    }

    private static class PromiseException extends RuntimeException {
        PromiseException(String message) {
            super(message);
        }
    }
}