package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import com.zweigbergk.speedswede.util.collection.MapExtension;

public abstract class Commitment<E> {

    ListExtension<Executable> mExecutables;
    ListExtension<Client<E>> mClients;
    MapExtension<Executable, Executable.Interest<E>> mInterestExecutables;

    E mCompletedProduct;

    protected abstract boolean isFulfilled();

    protected abstract void addClient(Client<E> client);

    protected void addExecutable(Executable executable, Executable.Interest<E> interest) {
        if (interest.caresFor(mCompletedProduct)) {
            executable.run();
        }
    }

    protected abstract boolean hasProduct();

    E getProduct() {
        return mCompletedProduct;
    }


    public void then(Client<E> client) {
        addClient(client);
    }


}
