package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {

    @Autowired
    private CustomerManagementApplicationService customerManagementApplicationService;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoSpyBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;

    @Autowired
    private CustomerQueryService queryService;

    @Test
    public void shouldRegister(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = queryService.findById(customerId);
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

        Mockito.verify(customerEventListener)
                .listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(customerEventListener,
                Mockito.never()).listen(Mockito.any(CustomerArchivedEvent.class));

        Mockito.verify(customerNotificationApplicationService)
                .notifyNewRegistration(Mockito.any(NotifyNewRegistrationInput.class));

    }

    @Test
    public void shouldUpdate(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerUpdateInput customerUpdateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdate().build();
        UUID customerId = customerManagementApplicationService.create(input);
        customerManagementApplicationService.update(customerId, customerUpdateInput );

        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = queryService.findById(customerId);
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

        CustomerOutput customerOutput = queryService.findById(customerId);

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
    void givenCustomer_whenArchiveClientThatHasAlreadyBeenArchived_shouldGenerationException() {

        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.archive(customerId))
                .withMessage(ERROR_CUSTOMER_ARCHIVED);
    }

    @Test
    void changeEmail(){
        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);
        String newEmail = "johndoe2@email.com";

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.changeEmail(customerId,newEmail);

        CustomerOutput customerOutput = queryService.findById(customerId);

        Assertions.assertThat(customerOutput.getEmail()).isNotNull();
        Assertions.assertThat(customerOutput.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void givenNotExistingCustomer_whenChangeEmail_shouldGenerationException() {

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value(), "johndoe2@email.com"))
                .withMessage(String.format(ERROR_CUSTOMER_NOT_FOUND,CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value()));
    }

    @Test
    void givenCustomer_whenChangeEmailClientThatHasAlreadyBeenArchived_shouldGenerationException() {

        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();

        customerManagementApplicationService.archive(customerId);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId,"johndoe2@email.com" ))
                .withMessage(ERROR_CUSTOMER_ARCHIVED);
    }

    @Test
    void givenCustomer_whenTryChangeEmailWithInvalidFormat_shouldGenerationException() {

        CustomerInput input = CustomerInputTestDataBuilder.aCustomer().build();
        UUID customerId = customerManagementApplicationService.create(input);

        Assertions.assertThat(customerId).isNotNull();


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId,"johndoeemail.com" ))
                .withMessage(VALIDATION_ERROR_EMAIL_IS_INVALID);
    }

    @Test
    void givenTwoCustomers_whenTryChangeEmailWithSecondCustomer_shouldGenerationException() {

        CustomerInput input1 = CustomerInputTestDataBuilder.aCustomer().build();
        CustomerInput input2 = CustomerInputTestDataBuilder.aCustomer().email("johndoe2@email.com").build();
        UUID customerId1 = customerManagementApplicationService.create(input1);
        UUID customerId2 = customerManagementApplicationService.create(input2);

        Assertions.assertThat(customerId1).isNotNull();
        Assertions.assertThat(customerId2).isNotNull();


        assertThatExceptionOfType(CustomerEmailIsInUseException.class)
                .isThrownBy(() -> customerManagementApplicationService.changeEmail(customerId1,input2.getEmail()))
                .withMessage(String.format(ERROR_CUSTOMER_EMAIL_IS_IN_USE,customerId1));
    }


}