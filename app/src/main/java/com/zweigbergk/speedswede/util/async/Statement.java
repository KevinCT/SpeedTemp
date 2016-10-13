package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class Statement {

    private Promise<Boolean> builder;

    private boolean inverted;

    public Statement() {
        builder = new Promise<>(assertionResult);
        builder.requires(PromiseNeed.ASSERTION);

        inverted = false;
    }

    public void then(Executable executable) {
        onTrue(executable);
    }

    public static Statement invert(Statement builder) {
        return builder.invert();
    }

    public void setReturnValue(boolean value) {
        builder.addItem(PromiseNeed.ASSERTION, value);
    }

    public void onTrue(Executable executable) {
        builder.addExecutable(executable, this::onTrue);
    }

    public void onFalse(Executable executable) {
        builder.addExecutable(executable, this::onFalse);
    }

    public Statement invert() {
        inverted = true;
        return this;
    }

    public static Statement not(Statement statement) {
        return statement.invert();
    }

    public void setBuildFailed(boolean value) {
        builder.setPromiseFailed(value);
    }

    private boolean determineInterest(boolean value) {
        return (inverted || value) && !(inverted && value);
    }

    private static Promise.Result<Boolean> assertionResult =
            items -> items.getBoolean(PromiseNeed.ASSERTION);

    private boolean onTrue(boolean value) {
        return determineInterest(value);
    }

    private boolean onFalse(boolean value) {
        return determineInterest(!value);
    }

    public void then(Client<Boolean> client) {
        builder.addClient(client);
    }
}
