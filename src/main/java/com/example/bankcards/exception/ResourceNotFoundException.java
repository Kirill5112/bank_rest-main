package com.example.bankcards.exception;


public class ResourceNotFoundException extends AppBusinessException{
    public ResourceNotFoundException(String entityName, String primaryKey) {
        super(entityName + " with primary key: " + primaryKey + " not found");
    }
}