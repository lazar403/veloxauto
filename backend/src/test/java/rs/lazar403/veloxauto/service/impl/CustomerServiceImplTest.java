package rs.lazar403.veloxauto.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.lazar403.veloxauto.dto.common.PagedResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerUpdateRequest;
import rs.lazar403.veloxauto.enums.CustomerRole;
import rs.lazar403.veloxauto.exception.ConflictException;
import rs.lazar403.veloxauto.exception.NotFoundException;
import rs.lazar403.veloxauto.mapper.CustomerMapper;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void createCustomer_shouldSaveAndReturnResponse() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        Customer customer = new Customer();
        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId(1L);

        when(customerRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(customerMapper.toEntity(request)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

        CustomerResponse result = customerService.createCustomer(request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(customerRepository).save(customer);
        verify(passwordEncoder).encode("password123");
        assertThat(customer.getPassword()).isEqualTo("hashed-password");
        assertThat(customer.getRole()).isEqualTo(CustomerRole.CUSTOMER);
        assertThat(customer.isActive()).isTrue();
    }

    @Test
    void createCustomer_withDuplicateEmail_shouldThrow() {
        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setEmail("taken@test.com");

        when(customerRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already exists.");

        verify(customerRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void getCustomerById_shouldReturnResponse() {
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(expectedResponse);

        CustomerResponse result = customerService.getCustomerById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCustomerById_whenNotFound_shouldThrow() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void getCustomers_shouldReturnMappedPage() {
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        Page<Customer> page = new PageImpl<>(List.of(c1, c2));

        CustomerResponse r1 = new CustomerResponse();
        CustomerResponse r2 = new CustomerResponse();

        when(customerRepository.findAll(any())).thenReturn(page);
        when(customerMapper.toResponse(c1)).thenReturn(r1);
        when(customerMapper.toResponse(c2)).thenReturn(r2);

        PagedResponse<CustomerResponse> result = customerService.getCustomers(null, 0, 20, "createdAt", "desc");
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    void updateCustomer_shouldApplyChangesAndSave() {
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

        CustomerResponse result = customerService.updateCustomer(1L, request);

        assertThat(result.getId()).isEqualTo(1L);
        verify(customerMapper).updateEntity(request, customer);
    }

    @Test
    void updateCustomer_withNewDuplicateEmail_shouldThrow() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("old@test.com");

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setEmail("taken@test.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.updateCustomer(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already exists.");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_whenNotFound_shouldThrow() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(999L, new CustomerUpdateRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void deactivateCustomer_shouldSetInactiveAndSave() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deactivateCustomer(1L);

        assertThat(customer.isActive()).isFalse();
        verify(customerRepository).save(customer);
    }

    @Test
    void deactivateCustomer_whenNotFound_shouldThrow() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deactivateCustomer(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found");
    }
}
