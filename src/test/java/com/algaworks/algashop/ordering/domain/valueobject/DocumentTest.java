package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.VALIDATION_ERROR_DOCUMENT_IS_BLANK;

class DocumentTest {


    @Test
    void shouldNotCreateDocumentNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Document(null));
    }

    @Test
    void shouldNotCreateDocumentBlank(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Document(""))
                .withMessage(VALIDATION_ERROR_DOCUMENT_IS_BLANK);
    }

    @Test
    void shouldCreateDocumentNotNullAndNotBlank(){
        String valueDocument = "nameDocument";
        Document document = new Document(valueDocument);

        Assertions.assertThat(document.value()).isEqualTo(valueDocument);

    }


}