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
import rs.lazar403.veloxauto.model.Customer;
import rs.lazar403.veloxauto.model.Vehicle;
import rs.lazar403.veloxauto.model.VehicleImage;
import java.util.Comparator;

import java.util.List;
import java.util.Objects;

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
    @Mapping(target = "createdByName", expression = "java(extractCreatorName(vehicle.getCreatedBy()))")
    @Mapping(source = "createdBy.phoneNumber", target = "createdByPhoneNumber")
    @Mapping(source = "createdBy.role", target = "createdByRole")
    @Mapping(target = "primaryImageUrl", expression = "java(extractPrimaryImageUrl(vehicle))")
    @Mapping(target = "imageUrls", expression = "java(extractImageUrls(vehicle))")
    VehicleResponse toResponse(Vehicle vehicle);

    List<VehicleResponse> toResponseList(List<Vehicle> vehicles);

    default String extractPrimaryImageUrl(Vehicle vehicle) {
        if (vehicle.getImages() == null || vehicle.getImages().isEmpty()) {
            return null;
        }

        VehicleImage primary = vehicle.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
                .findFirst()
                .orElseGet(() -> vehicle.getImages().stream()
                        .sorted(Comparator.comparing(
                                image -> image.getDisplayOrder() == null ? Integer.MAX_VALUE : image.getDisplayOrder()
                        ))
                        .findFirst()
                        .orElse(null));

        return primary != null ? primary.getImageUrl() : null;
    }

    default List<String> extractImageUrls(Vehicle vehicle) {
        if (vehicle.getImages() == null || vehicle.getImages().isEmpty()) {
            return List.of();
        }

        return vehicle.getImages().stream()
                .sorted(Comparator
                        .comparing((VehicleImage image) -> !Boolean.TRUE.equals(image.getIsPrimary()))
                        .thenComparing(image -> image.getDisplayOrder() == null ? Integer.MAX_VALUE : image.getDisplayOrder()))
                .map(VehicleImage::getImageUrl)
                .filter(Objects::nonNull)
                .toList();
    }

    default String extractCreatorName(Customer customer) {
        if (customer == null) {
            return null;
        }

        String firstName = customer.getFirstName() == null ? "" : customer.getFirstName().trim();
        String lastName = customer.getLastName() == null ? "" : customer.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();

        return fullName.isEmpty() ? null : fullName;
    }
}
