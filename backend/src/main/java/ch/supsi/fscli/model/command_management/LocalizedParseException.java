package ch.supsi.fscli.model.command_management;

import java.text.ParseException;

public class LocalizedParseException extends ParseException {
    private final Object[] args;

    public LocalizedParseException(String messageKey, Object... args) {
        super(messageKey, 0);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}