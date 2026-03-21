package rs.lazar403.veloxauto.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerUpdateRequest;
import rs.lazar403.veloxauto.enums.CustomerRole;
import rs.lazar403.veloxauto.mapper.CustomerMapper;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pure unit test — no Spring context, just Mockito mocks for repo and mapper
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // [======== CREATE ========]

    @Test
    void createCustomer_shouldSaveAndReturnResponse() {
        // arrange: mock the full create flow — email check, mapping, save, response mapping
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setEmail("user@test.com");

        Customer customer = new Customer();
        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId(1L);

        when(customerRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        // act
        CustomerResponse result = customerService.createCustomer(request);

        // assert: verify biz defaults were set and save was called
        assertThat(result.getId()).isEqualTo(1L);
        verify(customerRepository).save(customer);
        assertThat(customer.getRole()).isEqualTo(CustomerRole.CUSTOMER);
        assertThat(customer.isActive()).isTrue();
    }

    @Test
    void createCustomer_withDuplicateEmail_shouldThrow() {
        // arrange: email already exists in DB
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setEmail("taken@test.com");

        when(customerRepository.existsByEmail("taken@test.com")).thenReturn(true);

        // assert: should throw before save is ever called
        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists.");

        verify(customerRepository, never()).save(any());
    }

    // [======== GET BY ID ========]

    @Test
    void getCustomerById_shouldReturnResponse() {
        // arrange: customer exists
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        // assert
        CustomerResponse result = customerService.getCustomerById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCustomerById_whenNotFound_shouldThrow() {
        // arrange: empty Optional simulates missing customer
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // assert
        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");
    }

    // [======== GET ALL ========]

    @Test
    void getAllCustomers_shouldReturnMappedList() {
        // arrange: repo returns entities, mapper converts to response list
        List<Customer> customers = List.of(new Customer(), new Customer());
        List<CustomerResponse> expectedResponses = List.of(new CustomerResponse(), new CustomerResponse());

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.toResponseList(customers)).thenReturn(expectedResponses);

        // Act & Assert
        List<CustomerResponse> result = customerService.getAllCustomers();
        assertThat(result).hasSize(2);
    }

    // [======== UPDATE ========]

    @Test
    void updateCustomer_shouldApplyChangesAndSave() {
        // arrange: existing customer, update with same email (no uniqueness check needed)
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("user@test.com");

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setFirstName("User Updated");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(updatedCustomer);
        when(customerMapper.toResponse(updatedCustomer)).thenReturn(expectedResponse);

        // act
        CustomerResponse result = customerService.updateCustomer(1L, request);

        // assert: mapper.updateEntity was called to apply partial changes
        assertThat(result.getId()).isEqualTo(1L);
        verify(customerMapper).updateEntity(request, customer);
    }

    @Test
    void updateCustomer_withNewDuplicateEmail_shouldThrow() {
        // arrange: customer changes email to one that's already taken
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("old@test.com");

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setEmail("taken@test.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("taken@test.com")).thenReturn(true);

        // act & Assert: should throw, save should never be called
        assertThatThrownBy(() -> customerService.updateCustomer(1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists.");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_whenNotFound_shouldThrow() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(999L, new CustomerUpdateRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");
    }

    // [======== DEACTIVATE ========]

    @Test
    void deactivateCustomer_shouldSetInactiveAndSave() {
        // arrange: active customer
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // act
        customerService.deactivateCustomer(1L);

        // assert: isActive flipped to false and save was called
        assertThat(customer.isActive()).isFalse();
        verify(customerRepository).save(customer);
    }

    @Test
    void deactivateCustomer_whenNotFound_shouldThrow() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deactivateCustomer(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");
    }
}
