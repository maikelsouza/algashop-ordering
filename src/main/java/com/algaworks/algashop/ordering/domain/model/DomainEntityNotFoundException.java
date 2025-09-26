package com.algaworks.algashop.ordering.domain.model;

public class DomainEntityNotFoundException extends RuntimeException{


    public DomainEntityNotFoundException(){}

    public DomainEntityNotFoundException(String message) {
        super(message);
    }

    public DomainEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
