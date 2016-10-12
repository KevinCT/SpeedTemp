package com.zweigbergk.speedswede.util;

import java.util.ArrayList;
import java.util.List;

class PromiseGroup extends Promise<List> {

    private List<Promise> mPromises;

    public PromiseGroup(Promise... promises) {
        super(null);

        mPromises = new ArrayList<>();

        Lists.forEach(promises, this::includePromise);
    }

    private void includePromise(Promise promise) {
        mPromises.add(promise);
    }

    @Override
    protected boolean hasProduct() {
        return Lists.reject(mPromises, Promise::isFulfilled).size() == 0;
    }

    @Override
    protected void complete() {
        mCompletedProduct = Lists.map(mPromises, Promise::getProduct);

        notifyListeners();
    }
}