package com.mobilonix.voices.delegates;

public interface Callback<T> {
    boolean onExecuted(T data);
}
