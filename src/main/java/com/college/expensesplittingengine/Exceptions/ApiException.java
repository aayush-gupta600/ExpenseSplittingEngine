package com.college.expensesplittingengine.Exceptions;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}