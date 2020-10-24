package com.github.gilz688.rccarclient.util;

public class Event<T> {
    private final T content;

    public Event(T content) {
        this.content = content;
    }

    boolean hasBeenHandled = false;

    public void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }

    /**
     * Returns the content and prevents its use again.
     */
    public T getContentIfNotHandledOrReturnNull() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return content;
    }
}
