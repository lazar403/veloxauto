package rs.lazar403.veloxauto.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.repository.CustomerRepository;
import rs.lazar403.veloxauto.service.CustomerService;

import java.util.List;

/*
    No need for @Transactional on methods because of a single repo and jpa (it has default readOnly)
    you dont need @Transactional if you have:
                                                - 1 repo call
                                                - no lazy access
                                                - methods are simple
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
//    @Transactional no need because of a single repo (, if multiple repos
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
