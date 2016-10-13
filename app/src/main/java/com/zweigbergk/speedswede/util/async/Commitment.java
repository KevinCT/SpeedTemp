package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import java.util.List;
import java.util.Map;

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

    public void thenNotify(Client<E> client) {
        addClient(client);
    }

    public void thenPassTo(Client<E> client) {
        addClient(client);
    }

    public void then(Client<E> client) {
        addClient(client);
    }

    public void thenNotify(Executable executable) {
        addExecutable(executable);
    }

    public void whenFinished(Executable executable) {
        addExecutable(executable);
    }
}
