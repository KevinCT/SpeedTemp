package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class Statement extends Promise<Boolean> {
    private boolean inverted;

    public Statement() {
        super(null);

        requires(PromiseNeed.ASSERTION);
        setResultForm(items -> items.getBoolean(PromiseNeed.ASSERTION));
        inverted = false;
    }

    /**
     * Only runs if the Statement returns true.
     * @param executable
     */
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

    public Statement invert() {
        inverted = true;
        return this;
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
