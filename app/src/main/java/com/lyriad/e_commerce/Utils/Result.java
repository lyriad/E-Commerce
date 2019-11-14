package com.lyriad.e_commerce.Utils;

public class Result<T> {

    private T result;
    private Exception error;

    public T getResult() {
        return result;
    }

    public Exception getError() {
        return error;
    }

    public Result(T result) {
        super();
        this.result = result;
    }

    public Result(Exception error) {
        super();
        this.error = error;
    }
}
