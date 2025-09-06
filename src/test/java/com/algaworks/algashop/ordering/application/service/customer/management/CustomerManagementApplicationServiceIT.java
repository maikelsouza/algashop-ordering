package com.algaworks.algashop.ordering.application.service.customer.management;

import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.management.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_ARCHIVED;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;


    @Test
    public void shouldRegister(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(customerId,
                        "John",
                        "Doe",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7,5));

    }

    @Test
    public void shouldUpdate(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerUpdateInput customerUpdateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();
        UUID customerId = customerManagementApplicationService.create(input);
        customerManagementApplicationService.update(customerId, customerUpdateInput );

        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(customerId,
                        "Matt",
                        "Damon",
                        "johndoe@email.com",
                        LocalDate.of(1991, 7,5));
    }

    @Test
    public void archive(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        CustomerOutput customerOutput = customerManagementApplicationService.findById(customerId);

        Assertions.assertThat(customerOutput.getArchived()).isNotNull();
        Assertions.assertThat(customerOutput.getArchived()).isTrue();
        Assertions.assertThat(customerOutput.getArchivedAt()).isNotNull();
        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getPhone,
                        CustomerOutput::getDocument,
                        CustomerOutput::getBirthDate,
                        CustomerOutput::getPromotionNotificationsAllowed
                ).containsExactly(customerId,
                        "Anonymous",
                        "Anonymous",
                        "000-000-0000",
                        "000-00-0000",
                        null,
                        false);

        Assertions.assertThat(customerOutput.getEmail()).endsWith("@anonymous.com");
        Assertions.assertThat(customerOutput.getAddress()).isNotNull();
        Assertions.assertThat(customerOutput.getAddress().getNumber()).isNotNull().isEqualTo("Anonymized");
        Assertions.assertThat(customerOutput.getAddress().getComplement()).isNull();

    }

    @Test
    void givenNotExistingCustomer_whenArchive_shouldGenerationException() {

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value()))
                .withMessage(String.format(ERROR_CUSTOMER_NOT_FOUND,CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value()));
    }

    @Test
    void givenCustomer_whenArchiveClientThatHasAlreadyBeenArchived_shouldGenerationException2() {

        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(customerId))
                .withMessage(ERROR_CUSTOMER_ARCHIVED);
    }

}