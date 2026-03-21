package rs.lazar403.veloxauto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import rs.lazar403.veloxauto.dto.vehicle.VehicleCreateRequest;
import rs.lazar403.veloxauto.dto.vehicle.VehicleResponse;
import rs.lazar403.veloxauto.dto.vehicle.VehicleUpdateRequest;
import rs.lazar403.veloxauto.model.Vehicle;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface VehicleMapper {

    @Mapping(target = "createdBy", ignore = true) // set in service via Customer lookup
    Vehicle toEntity(VehicleCreateRequest request);

    // partial update — only non-null fields from request overwrite the entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(VehicleUpdateRequest request, @MappingTarget Vehicle vehicle);

    @Mapping(source = "createdBy.id", target = "createdById")
    VehicleResponse toResponse(Vehicle vehicle);

    List<VehicleResponse> toResponseList(List<Vehicle> vehicles);
}
