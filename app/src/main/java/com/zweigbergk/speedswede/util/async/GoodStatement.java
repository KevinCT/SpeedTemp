package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class GoodStatement extends Promise<Boolean> {
    private boolean inverted;

    public GoodStatement() {
        super(null);

        requires(PromiseNeed.ASSERTION);
        setResultForm(items -> items.getBoolean(PromiseNeed.ASSERTION));
        inverted = false;
    }

    public void then(Executable executable) {
        onTrue(executable);
    }

    public static Statement invert(Statement builder) {
        return builder.invert();
    }

    @Override
    public void addItem(PromiseNeed need, Object data) throws StatementException {
        throw new StatementException("addItem() may not be called from within a Statement. Use a Promise instead.");
    }

    public void setReturnValue(boolean value) {
        super.addItem(PromiseNeed.ASSERTION, value);
    }

    public void onTrue(Executable executable) {
        addExecutable(executable, this::onTrue);
    }

    public void onFalse(Executable executable) {
        addExecutable(executable, this::onFalse);
    }

    public GoodStatement invert() {
        inverted = true;
        return this;
    }

    public static Statement not(Statement statement) {
        return statement.invert();
    }

    private boolean determineInterest(boolean value) {
        return (inverted || value) && !(inverted && value);
    }

    private boolean onTrue(boolean value) {
        return determineInterest(value);
    }

    private boolean onFalse(boolean value) {
        return determineInterest(!value);
    }

    private static class StatementException extends RuntimeException {
        StatementException(String message) {
            super(message);
        }
    }
}
