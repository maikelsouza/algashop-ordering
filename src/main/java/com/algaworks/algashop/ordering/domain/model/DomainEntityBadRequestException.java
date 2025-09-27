package com.algaworks.algashop.ordering.domain.model;

public class DomainEntityBadRequestException extends RuntimeException{


    public DomainEntityBadRequestException(){}

    public DomainEntityBadRequestException(String message) {
        super(message);
    }

    public DomainEntityBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
