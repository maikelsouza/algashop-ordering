package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.VALIDATION_ERROR_DOCUMENT_IS_BLANK;

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
        String value = "000-00-0000";
        Document document = new Document(value);

        Assertions.assertThat(document.value()).isEqualTo(value);
    }

    @Test
    void shouldReturnNameDocument(){
        String value = "000-00-0000";
        Document document = new Document(value);

        Assertions.assertThat(document.toString()).isEqualTo(value);
    }


}