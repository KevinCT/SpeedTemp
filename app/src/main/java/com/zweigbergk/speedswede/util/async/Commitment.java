package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.collection.Map;

public abstract class Commitment<E> {

    List<Executable> mExecutables;
    List<Client<E>> mClients;
    Map<Executable, Executable.Interest<E>> mInterestExecutables;

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
