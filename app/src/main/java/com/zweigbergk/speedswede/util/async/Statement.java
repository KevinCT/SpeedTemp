package com.zweigbergk.speedswede.util.async;

import com.zweigbergk.speedswede.util.Tuple;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;

public class Statement extends Promise<Boolean> {
    private boolean inverted;

    Statement(boolean needsAssertion) {
        super(null);

        if (needsAssertion) {
            requires(PromiseNeed.ASSERTION);
            setResultForm(items -> items.getBoolean(PromiseNeed.ASSERTION));
        }

        inverted = false;
    }

    public Statement() {
        this(true);
    }

    /**
     * Only runs if the Statement returns true.
     */
    public void then(Executable executable) {
        onTrue(executable);
    }

    @Override
    public void addItem(PromiseNeed need, Object data) throws StatementException {
        throw new StatementException("addItem() may not be called from within a Statement. Use a Promise instead.");
    }

    private StatementGroup combine(Result<Boolean> resultForm, ListExtension<Tuple<PromiseNeed, Statement>> statements) {
        return new StatementGroup(resultForm, statements);
    }

    @SuppressWarnings("unused")
    public Statement and(Statement statement) {
        ListExtension<Tuple<PromiseNeed, Statement>> combined = new ArrayListExtension<>();
        combined.add(new Tuple<>(PromiseNeed.FIRST_ASSERTION, this));
        combined.add(new Tuple<>(PromiseNeed.SECOND_ASSERTION, statement));

        Result<Boolean> result = items ->
                items.getBoolean(PromiseNeed.FIRST_ASSERTION)
                        && items.getBoolean(PromiseNeed.SECOND_ASSERTION);

        return combine(result, combined);
    }

    public Statement or(Statement statement) {
        ListExtension<Tuple<PromiseNeed, Statement>> combined = new ArrayListExtension<>();
        combined.add(new Tuple<>(PromiseNeed.FIRST_ASSERTION, this));
        combined.add(new Tuple<>(PromiseNeed.SECOND_ASSERTION, statement));

        Result<Boolean> result = items ->
                items.getBoolean(PromiseNeed.FIRST_ASSERTION)
                        || items.getBoolean(PromiseNeed.SECOND_ASSERTION);

        return combine(result, combined);
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


    /**
     *  Should never be used in a Statement. Only call from StatementGroup.
     */
    void addItemFromSubClass(PromiseNeed need, Boolean item) {
        super.addItem(need, item);
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
