package rs.lazar403.veloxauto.service;


import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    List<CustomerResponse> getAllCustomers();
}
