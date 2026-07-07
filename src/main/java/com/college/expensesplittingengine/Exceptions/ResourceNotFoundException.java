package com.college.expensesplittingengine.Exceptions;
public class ResourceNotFoundException extends RuntimeException {
    String ResourceName;
    String fieldName;
    String field;
    Long fieldId;
    public ResourceNotFoundException(String ResourceName,String fieldName,String field) {
        super(String.format("%s Not Found with %s %s", ResourceName, fieldName, field));
        this.ResourceName = ResourceName;
        this.fieldName = fieldName;
        this.field = field;
    }
    public ResourceNotFoundException(String ResourceName,String field,Long fieldId) {
        super(String.format("%s Not Found with %s %s", ResourceName, field, fieldId));
        this.ResourceName = ResourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
    public ResourceNotFoundException() {

    }
}
