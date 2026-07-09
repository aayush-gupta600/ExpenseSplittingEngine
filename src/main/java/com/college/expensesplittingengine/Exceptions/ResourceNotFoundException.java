package com.college.expensesplittingengine.Exceptions;
import lombok.Getter;
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final String field;
    private final Long fieldId;

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     String field) {
        super(String.format("%s not found with %s = %s",
                resourceName, fieldName, field));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
        this.fieldId = null;
    }

    public ResourceNotFoundException(String resourceName,
                                     String fieldName,
                                     Long fieldId) {
        super(String.format("%s not found with %s = %d",
                resourceName, fieldName, fieldId));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = null;
        this.fieldId = fieldId;
    }
}