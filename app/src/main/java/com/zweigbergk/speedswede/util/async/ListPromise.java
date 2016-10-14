package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import com.zweigbergk.speedswede.util.collection.List;

public class ListPromise<E> extends Promise<List<E>> {

    private Client<E> iteratorClient;

    public ListPromise(Result<List<E>> blueprint, PromiseNeed... needs) {
        super(blueprint, needs);
    }

    public static <Product> ListPromise<Product> empty() {
        return new ListPromise<>(null, PromiseNeed.LIST);
    }

    @Override
    protected void notifyListeners() {
        super.notifyListeners();

        supplyIteratorClient();
    }

    private void supplyIteratorClient() {
        Lists.forEach(mCompletedProduct, iteratorClient::supply);
    }

    public void forEach(Client<E> client) {
        if (!hasProduct()) {
            iteratorClient = client;
        } else {
            if (!mBuildFailed) {
                supplyIteratorClient();
            } else {
                Log.d(TAG, "Build failed");
            }
        }
    }
}
