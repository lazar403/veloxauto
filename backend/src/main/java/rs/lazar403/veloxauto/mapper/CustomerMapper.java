package rs.lazar403.veloxauto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import rs.lazar403.veloxauto.dto.customer.CustomerCreateRequest;
import rs.lazar403.veloxauto.dto.customer.CustomerResponse;
import rs.lazar403.veloxauto.model.Customer;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE // ignore role, isActive, audit in service layer
)
public interface CustomerMapper {

    Customer toEntity(CustomerCreateRequest request);

    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);
}
