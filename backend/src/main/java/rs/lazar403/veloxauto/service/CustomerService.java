package rs.lazar403.veloxauto.service;

import rs.lazar403.veloxauto.dto.common.PagedResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;
import rs.lazar403.veloxauto.dto.customer.CustomerUpdateRequest;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    CustomerResponse getCustomerById(Long id);

    PagedResponse<CustomerResponse> getCustomers(Boolean active, int page, int size, String sortBy, String sortDir);

    CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request);

    void deactivateCustomer(Long id);
}
