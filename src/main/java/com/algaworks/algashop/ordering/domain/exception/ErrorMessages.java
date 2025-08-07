package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.Phone;

public class ErrorMessages {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email is invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "BirthDate must be a past date";
    public static final String VALIDATION_ERROR_FULLNAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FULLNAME_FIRST_NAME_IS_BLANK = "First cannot be blank";
    public static final String VALIDATION_ERROR_FULLNAME_LAST_NAME_IS_BLANK = "Last cannot be blank";
    public static final String VALIDATION_ERROR_DOCUMENT_IS_NULL = "Document cannot be null";
    public static final String VALIDATION_ERROR_DOCUMENT_IS_BLANK = "Document cannot be blank";
    public static final String VALIDATION_ERROR_PHONE_IS_NULL = "Phone cannot be null";
    public static final String VALIDATION_ERROR_PHONE_IS_BLANK = "Phone cannot be blank";
    public static final String ERROR_CUSTOMER_ARCHIVED =  "Customer is archived cannot be changed";
}
