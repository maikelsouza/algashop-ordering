package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Import({CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class, CustomerPersistenceEntityDisassembler.class})
class CustomersIT {

    private Customers customers;


    @Autowired
    public CustomersIT(Customers customers) {
        this.customers = customers;
    }

    @Test
    public void shouldPersistAndFind() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        Optional<Customer> optionalCustomer = customers.ofId(customerId);

        Assertions.assertThat(optionalCustomer.isPresent());
        Customer savedCustomer = optionalCustomer.get();

        Assertions.assertThat(savedCustomer).satisfies(
                entity -> Assertions.assertThat(entity.id()).isEqualTo(customerId),
                entity -> Assertions.assertThat(entity.fullName().firstName()).isEqualTo(customer.fullName().firstName()),
                entity -> Assertions.assertThat(entity.fullName().lastName()).isEqualTo(customer.fullName().lastName()),
                entity -> Assertions.assertThat(entity.birthDate().value()).isEqualTo(customer.birthDate().value()),
                entity -> Assertions.assertThat(entity.email().value()).isEqualTo(customer.email().value()),
                entity -> Assertions.assertThat(entity.phone().value()).isEqualTo(customer.phone().value()),
                entity -> Assertions.assertThat(entity.document().value()).isEqualTo(customer.document().value()),
                entity -> Assertions.assertThat(entity.isPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
                entity -> Assertions.assertThat(entity.isArchived()).isEqualTo(customer.isArchived()),
                entity -> Assertions.assertThat(entity.registeredAt()).isEqualTo(customer.registeredAt()),
                entity -> Assertions.assertThat(entity.address().complement()).isEqualTo(customer.address().complement()),
                entity -> Assertions.assertThat(entity.address().zipCode().value()).isEqualTo(customer.address().zipCode().value()),
                entity -> Assertions.assertThat(entity.address().number()).isEqualTo(customer.address().number()),
                entity -> Assertions.assertThat(entity.address().neighborhood()).isEqualTo(customer.address().neighborhood()),
                entity -> Assertions.assertThat(entity.address().state()).isEqualTo(customer.address().state()),
                entity -> Assertions.assertThat(entity.address().street()).isEqualTo(customer.address().street()),
                entity -> Assertions.assertThat(entity.address().toString()).isEqualTo(customer.address().toString())
        );
    }

    @Test
    public void shouldUpdateExistingCustomer() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);

        customer = customers.ofId(customer.id()).orElseThrow();

        FullName fullName = new FullName("James", "Smith");
        customer.changeName(fullName);

        customers.add(customer);

        customer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customer.fullName().firstName()).isEqualTo(fullName.firstName());
        Assertions.assertThat(customer.fullName().lastName()).isEqualTo(fullName.lastName());
    }

    @Test
    public void shouldNotAllowStalesUpdates() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);

        Customer customer1 = customers.ofId(customer.id()).orElseThrow();
        Customer customer2 = customers.ofId(customer.id()).orElseThrow();

        FullName JamesSmith = new FullName("James", "Smith");
        customer1.changeName(JamesSmith);
        customers.add(customer1);

        FullName WilliamJohnson = new FullName("William", " Johnson");
        customer2.changeName(WilliamJohnson);


        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> customers.add(customer2));

        Customer savedCustomer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(savedCustomer.fullName().firstName()).isEqualTo(JamesSmith.firstName());
        Assertions.assertThat(savedCustomer.fullName().lastName()).isEqualTo(JamesSmith.lastName());
    }

    @Test
    public void shouldCountExistingCustomer() {

        Assertions.assertThat(customers.count()).isZero();

        Customer customer1 = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer1);

        Customer customer2 = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer2);

        Assertions.assertThat(customers.count()).isEqualTo(2);
    }

    @Test
    public void shouldReturnIfCustomerExists() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Assertions.assertThat(customers.exists(customer.id())).isTrue();
        Assertions.assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    public void shouldFindByEmail() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Optional<Customer> customerOptional = customers.ofEmail(customer.email());

        Assertions.assertThat(customerOptional).isPresent();
    }

    @Test
    public void shouldNotFindEmailIfNoExistsWithEmail() {
        Optional<Customer> customerOptional = customers.ofEmail(new Email(UUID.randomUUID().toString() + "@email.com"));

        Assertions.assertThat(customerOptional).isNotPresent();
    }

    @Test
    public void shouldReturnIfEmailInUse() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        Assertions.assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
        Assertions.assertThat(customers.isEmailUnique(customer.email(), new CustomerId())).isFalse();
        Assertions.assertThat(customers.isEmailUnique(new Email("maikel.souza@gmail.com"), new CustomerId())).isTrue();
    }

}