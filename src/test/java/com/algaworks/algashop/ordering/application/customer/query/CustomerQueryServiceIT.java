package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
//@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
//@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class CustomerQueryServiceIT {

    @Autowired
    private CustomerQueryService queryService;

    @Autowired
    private Customers customers;


    @Test
    public void shouldFilterByFirstName(){
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(4, 0);

        filter.setFirstName("James".toUpperCase());
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("James");
    }

    @Test
    public void shouldFilterByFirstNameNotExisting(){
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(4, 0);

        filter.setFirstName("James2".toUpperCase());
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByEmail() {
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("user1@test.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("test2@algashop.com")).build());
        customers.add(CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).email(new Email("user3@test.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setEmail("test");

        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent())
                .extracting(CustomerSummaryOutput::getEmail)
                .containsExactlyInAnyOrder("user1@test.com", "test2@algashop.com", "user3@test.com");
    }

    @Test
    public void shouldFilterByEmailNotExisting(){
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).
                email(new Email("michael.Johnson@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).
                email(new Email("emily.davis@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).
                email(new Email("james.miller@gmail.com")).build());
        CustomerFilter filter = new CustomerFilter(4, 0);

        filter.setEmail("john2.doe@gmail.com".toUpperCase());
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByFirstNameAndEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).
                email(new Email("michael.Johnson@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).
                email(new Email("emily.davis@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).
                email(new Email("james.miller@gmail.com")).build());

        CustomerFilter filter = new CustomerFilter(4, 0);

        filter.setFirstName("James".toUpperCase());
        filter.setEmail("james.miller".toUpperCase());
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("James");
        Assertions.assertThat(page.getContent().getFirst().getEmail()).isEqualTo("james.miller@gmail.com");
    }

    @Test
    public void shouldSuccessPagination() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);


        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void shouldSuccessAscOrdernationFirstName() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);


        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Emily");
    }

    @Test
    public void shouldSuccessDescOrdernationFirstName() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);


        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent().getFirst().getFirstName()).isEqualTo("Michael");
    }


    @Test
    public void shouldSuccessAscOrdernationEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).
                email(new Email("michael.Johnson@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).
                email(new Email("emily.davis@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).
                email(new Email("james.miller@gmail.com")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);
        filter.setSortByProperty(CustomerFilter.SortType.EMAIL);
        filter.setSortDirection(Sort.Direction.ASC);


        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent().getFirst().getEmail()).isEqualTo("emily.davis@gmail.com");
    }

    @Test
    public void shouldSuccessDescOrdernationEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).
                email(new Email("michael.Johnson@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).
                email(new Email("emily.davis@gmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).
                email(new Email("james.miller@gmail.com")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);
        filter.setSortByProperty(CustomerFilter.SortType.EMAIL);
        filter.setSortDirection(Sort.Direction.DESC);


        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
        Assertions.assertThat(page.getContent().getFirst().getEmail()).isEqualTo("michael.Johnson@gmail.com");
    }

    @Test
    public void shouldEmptyResult() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Michael", "Johnson")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Emily", "Davis")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("James", "Miller")).build());

        CustomerFilter filter = new CustomerFilter(3, 0);

        filter.setFirstName("James2".toUpperCase());
        Page<CustomerSummaryOutput> page = queryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }


}