package rs.lazar403.veloxauto.service;


import rs.lazar403.veloxauto.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer addCustomer(Customer customer);
    List<Customer> getAllCustomers();
}
