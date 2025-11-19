package com.algaworks.algashop.ordering.core.domain.model;

public class ErrorMessages {

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email is invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "Birth Date must be a past date";
    public static final String VALIDATION_ERROR_FULLNAME_FIRST_NAME_IS_NULL = "First Name cannot be null";
    public static final String VALIDATION_ERROR_FULLNAME_LAST_NAME_IS_NULL = "Last Name cannot be null";
    public static final String VALIDATION_ERROR_FULLNAME_FIRST_NAME_IS_BLANK = "First Name cannot be blank";
    public static final String VALIDATION_ERROR_FULLNAME_LAST_NAME_IS_BLANK = "Last Name cannot be blank";
    public static final String VALIDATION_ERROR_DOCUMENT_IS_NULL = "Document cannot be null";
    public static final String VALIDATION_ERROR_DOCUMENT_IS_BLANK = "Document cannot be blank";
    public static final String VALIDATION_ERROR_PHONE_IS_NULL = "Phone cannot be null";
    public static final String VALIDATION_ERROR_PHONE_IS_BLANK = "Phone cannot be blank";
    public static final String VALIDATION_ERROR_PRODUCT_NAME_IS_NULL = "Product Name cannot be null";
    public static final String VALIDATION_ERROR_PRODUCT_NAME_IS_BLANK = "Product Name cannot be blank";
    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived cannot be changed";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer %s was not found";
    public static final String ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART = "Customer %s already haves shopping cart";
    public static final String ERROR_CUSTOMER_EMAIL_IS_IN_USE = "Customer email is in use";
    public static final String VALIDATION_ERROR_MONEY_IS_NEGATIVE =  "Money cannot be negative";
    public static final String VALIDATION_ERROR_MONEY_IS_NULL = "Money cannot be null";
    public static final String VALIDATION_ERROR_MONEY_IS_DIVIDED_BY_ZERO = "Money cannot be divided by zero";
    public static final String VALIDATION_ERROR_QUANTITY_IS_NULL = "Quantity cannot be null";
    public static final String VALIDATION_ERROR_QUANTITY_IS_NEGATIVE =  "Quantity cannot be negative";
    public static final String ERROR_ORDER_STATUS_CANNOT_BE_CHANGED = "Cannot change order %s status from %s to %s";
    public static final String ERROR_ORDER_DELIVERY_DATE_CANNOT_BE_IN_THE_PAST =
            "Order %s expected delivery date cannot be in the past";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS =
            "Order %s cannot be closed, it has no items";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO =
            "Order %s cannot be placed, it has no shipping info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO
            = "Order %s cannot be placed, it has no billing info";
    public static final String ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD
            = "Order %s cannot be placed, it has no payment method";
    public static final String ERROR_ORDER_DOES_NOT_CONTAIN_ITEM = "Order %s does not contain item %s";
    public static final String ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY = "Order %s cannot add loyalty points order is not ready";
    public static final String ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER = "Order %s not belongs to customer %s";
    public static final String ERROR_ORDER_CANNOT_BE_EDITED = "Order %s with status %s cannot be edited";
    public static final String ERROR_ORDER_NOT_FOUND = "Order %s was not found";
    public static final String ERROR_PRODUCT_IS_OUT_OF_STOCK = "Product %s is out of stock";
    public static final String ERROR_PRODUCT_NOT_FOUND = "Product %s was not found";
    public static final String ERROR_SHOPPING_CARD_DOES_NOT_CONTAIN_ITEM = "Shopping Card %s does not contain item %s";
    public static final String ERROR_SHOPPING_CARD_DOES_NOT_CONTAIN_PRODUCT = "Shopping Card %s does not contain product %s";
    public static final String ERROR_SHOPPING_CARD_ITEM_INCOMPATIBLE_PRODUCT = "Shopping Card Item %s incompatible product %s";
    public static final String ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT = "Shopping Card %s cannot proceed to checkout";
    public static final String ERROR_SHOPPING_CARD_NOT_FOUND = "Shopping Card %s was not found";
    public static final String ERROR_SHOPPING_CARD_NOT_FOUND_FOR_CUSTOMER = "Shopping Card was not found for by customer %s";

}
