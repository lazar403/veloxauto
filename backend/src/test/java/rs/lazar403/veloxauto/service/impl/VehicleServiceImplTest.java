package rs.lazar403.veloxauto.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import rs.lazar403.veloxauto.dto.common.PagedResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.exception.ConflictException;
import rs.lazar403.veloxauto.exception.NotFoundException;
import rs.lazar403.veloxauto.mapper.VehicleMapper;
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.model.Vehicle;
import rs.lazar403.veloxauto.repository.CustomerRepository;
import rs.lazar403.veloxauto.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pure unit test — no Spring context, Mockito mocks for both repos and mapper
@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    // [======== CREATE ========]

    @Test
    void createVehicle_shouldLookUpCreatorSaveAndReturn() {
        // arrange: VIN is unique, creator exists — full happy path
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setVin("1HGCM82633A004352");
        request.setCreatedById(1L);

        Customer creator = new Customer();
        creator.setId(1L);

        Vehicle vehicle = new Vehicle();
        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(1L);

        VehicleResponse expectedResponse = new VehicleResponse();
        expectedResponse.setId(1L);

        when(vehicleRepository.existsByVin("1HGCM82633A004352")).thenReturn(false);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(vehicleMapper.toEntity(request)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(savedVehicle);
        when(vehicleMapper.toResponse(savedVehicle)).thenReturn(expectedResponse);

        // act
        VehicleResponse result = vehicleService.createVehicle(request);

        // assert: creator was set on the entity and isActive defaulted to true
        assertThat(result.getId()).isEqualTo(1L);
        verify(vehicleRepository).save(vehicle);
        assertThat(vehicle.getCreatedBy()).isEqualTo(creator);
        assertThat(vehicle.getIsActive()).isTrue();
    }

    @Test
    void createVehicle_withDuplicateVin_shouldThrow() {
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setVin("DUPLICATE_VIN");

        when(vehicleRepository.existsByVin("DUPLICATE_VIN")).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("VIN already exists.");

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void createVehicle_withInvalidCreator_shouldThrow() {
        VehicleCreateRequest request = new VehicleCreateRequest();
        request.setVin("1HGCM82633A004352");
        request.setCreatedById(999L);

        when(vehicleRepository.existsByVin("1HGCM82633A004352")).thenReturn(false);
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // assert
        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Creator not found");

        verify(vehicleRepository, never()).save(any());
    }

    // [======== GET BY ID ========]

    @Test
    void getVehicleById_shouldReturnResponse() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        VehicleResponse expectedResponse = new VehicleResponse();
        expectedResponse.setId(1L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toResponse(vehicle)).thenReturn(expectedResponse);

        VehicleResponse result = vehicleService.getVehicleById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getVehicleById_whenNotFound_shouldThrow() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }
    // [======== GET VEHICLES ========]
    // [======== GET ALL ========]

    @Test
    void getVehicles_shouldReturnMappedPage() {
        Vehicle v1 = new Vehicle();
        Vehicle v2 = new Vehicle();
        Page<Vehicle> vehicles = new PageImpl<>(List.of(v1, v2));

        VehicleResponse r1 = new VehicleResponse();
        VehicleResponse r2 = new VehicleResponse();

        when(vehicleRepository.findAll(any())).thenReturn(vehicles);
        when(vehicleMapper.toResponse(v1)).thenReturn(r1);
        when(vehicleMapper.toResponse(v2)).thenReturn(r2);

        PagedResponse<VehicleResponse> result = vehicleService.getVehicles(null, null, 0, 20, "createdAt", "desc");
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    // [======== UPDATE ========]

    @Test
    void updateVehicle_shouldApplyChangesAndSave() {
        // arrange: vehicle exists, partial update applied via mapper
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        VehicleUpdateRequest request = new VehicleUpdateRequest();
        request.setColor("Red");

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(1L);

        VehicleResponse expectedResponse = new VehicleResponse();
        expectedResponse.setId(1L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(updatedVehicle);
        when(vehicleMapper.toResponse(updatedVehicle)).thenReturn(expectedResponse);

        // act
        VehicleResponse result = vehicleService.updateVehicle(1L, request);

        // assert: mapper.updateEntity was called to apply partial changes
        assertThat(result.getId()).isEqualTo(1L);
        verify(vehicleMapper).updateEntity(request, vehicle);
    }

    @Test
    void updateVehicle_whenNotFound_shouldThrow() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.updateVehicle(999L, new VehicleUpdateRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }

    // [======== DEACTIVATE ========]

    @Test
    void deactivateVehicle_shouldSetInactiveAndSave() {
        // arrange: active vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setIsActive(true);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        // act
        vehicleService.deactivateVehicle(1L);

        // assert: isActive flipped to false
        assertThat(vehicle.getIsActive()).isFalse();
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void deactivateVehicle_whenNotFound_shouldThrow() {
        when(vehicleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.deactivateVehicle(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }
}
