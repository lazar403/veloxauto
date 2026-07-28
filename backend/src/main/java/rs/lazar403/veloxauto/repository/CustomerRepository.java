package rs.lazar403.veloxauto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.lazar403.veloxauto.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Page<Customer> findByIsActive(boolean active, Pageable pageable);
}
