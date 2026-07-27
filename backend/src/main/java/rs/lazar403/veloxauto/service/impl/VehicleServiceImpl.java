package rs.lazar403.veloxauto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.lazar403.veloxauto.dto.common.PagedResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.enums.VehicleStatus;
import rs.lazar403.veloxauto.exception.ConflictException;
import rs.lazar403.veloxauto.exception.NotFoundException;
import rs.lazar403.veloxauto.mapper.VehicleMapper;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.model.Vehicle;
import rs.lazar403.veloxauto.repository.CustomerRepository;
import rs.lazar403.veloxauto.repository.VehicleRepository;
import rs.lazar403.veloxauto.service.VehicleService;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "createdAt",
            "updatedAt",
            "price",
            "year",
            "mileage",
            "status",
            "make",
            "model"
    );

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public VehicleResponse createVehicle(VehicleCreateRequest request) {
        if (vehicleRepository.existsByVin(request.getVin())) {
            throw new ConflictException("VIN already exists.");
        }

        Customer creator = customerRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new NotFoundException("Creator not found with id: " + request.getCreatedById()));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setCreatedBy(creator);
        vehicle.setIsActive(true);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> getVehicles(
            Boolean active,
            VehicleStatus status,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        String safeSortBy = resolveSortField(sortBy);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC, safeSortBy)
        );

        Page<Vehicle> vehicles;
        if (active != null && status != null) {
            vehicles = vehicleRepository.findByIsActiveAndStatus(active, status, pageable);
        } else if (active != null) {
            vehicles = vehicleRepository.findByIsActive(active, pageable);
        } else if (status != null) {
            vehicles = vehicleRepository.findByStatus(status, pageable);
        } else {
            vehicles = vehicleRepository.findAll(pageable);
        }

        return PagedResponse.from(vehicles.map(vehicleMapper::toResponse));
    }

    @Override
    public VehicleResponse updateVehicle(Long id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));

        vehicleMapper.updateEntity(request, vehicle);
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    public void deactivateVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        return ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    }
}
