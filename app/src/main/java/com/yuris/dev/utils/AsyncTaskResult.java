package com.yuris.dev.utils;

/**
 * Created by yuris on 2016-07-13.
 */
public class AsyncTaskResult<T> {
    private T result;
    private Exception error;

    public boolean hasError() {
        return error != null;
    }

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public AsyncTaskResult(T result) {
        super();
        this.result = result;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}
