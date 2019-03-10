package com.yakin.fastselector;

public interface ISelectionHandler<T> {

    void onSelectionResult(int resultCode, T t);
}
