package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.List;

public class ListBuilder<E> extends ProductBuilder<List<E>> {

    private Client<E> iteratorClient;

    public ListBuilder(Blueprint<List<E>> blueprint, ProductLock... locks) {
        super(blueprint, locks);
    }

    public static <Product> ListBuilder<Product> empty() {
        return new ListBuilder<>(null, ProductLock.LIST);
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
