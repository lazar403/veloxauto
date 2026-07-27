package rs.lazar403.veloxauto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import rs.lazar403.veloxauto.service.CustomerService;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "firstName",
            "lastName",
            "email",
            "createdAt",
            "updatedAt"
    );

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        request.setEmail(normalizedEmail);

        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email already exists.");
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setRole(CustomerRole.CUSTOMER);
        customer.setActive(true);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CustomerResponse> getCustomers(Boolean active, int page, int size, String sortBy, String sortDir) {
        String safeSortBy = resolveSortField(sortBy);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC, safeSortBy)
        );

        Page<Customer> customers = active == null
                ? customerRepository.findAll(pageable)
                : customerRepository.findByIsActive(active, pageable);

        return PagedResponse.from(customers.map(customerMapper::toResponse));
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        if (request.getEmail() != null) {
            String normalizedEmail = normalizeEmail(request.getEmail());
            if (!normalizedEmail.equals(customer.getEmail()) && customerRepository.existsByEmail(normalizedEmail)) {
                throw new ConflictException("Email already exists.");
            }
            request.setEmail(normalizedEmail);
        }

        customerMapper.updateEntity(request, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deactivateCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        customer.setActive(false);
        customerRepository.save(customer);
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        return ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
