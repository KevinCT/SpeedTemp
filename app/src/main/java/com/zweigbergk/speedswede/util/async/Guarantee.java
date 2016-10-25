package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public class Guarantee<E> extends Commitment<E> {

    private E mCompletedProduct;

    public Guarantee(E product) {
        mCompletedProduct = product;
    }

    protected void addClient(Client<E> client) {
        client.supply(mCompletedProduct);
    }

    protected E getProduct() {
        return mCompletedProduct;
    }
}
