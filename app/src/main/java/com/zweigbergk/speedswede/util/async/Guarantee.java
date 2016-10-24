package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class Guarantee<E> extends Commitment<E> {

    private E mCompletedProduct;

    public Guarantee(E product) {
        mCompletedProduct = product;
    }

    protected boolean isFulfilled() {
        return true;
    }

    protected void addClient(Client<E> client) {
        client.supply(mCompletedProduct);
    }

    protected void addExecutable(Executable executable, Executable.Interest<E> interest) {
        if (interest.caresFor(mCompletedProduct)) {
            executable.run();
        }
    }

    protected boolean hasProduct() {
        return mCompletedProduct != null;
    }

    protected void addExecutable(Executable executable) {
        if (!hasProduct()) {
            mExecutables.add(executable);
        } else {
            executable.run();
        }
    }

    protected E getProduct() {
        return mCompletedProduct;
    }
}
