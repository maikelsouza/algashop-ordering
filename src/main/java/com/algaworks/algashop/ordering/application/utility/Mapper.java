package com.algaworks.algashop.ordering.application.utility;

public interface Mapper {

    <T> T covert(Object object, Class<T> destinationType);
}
