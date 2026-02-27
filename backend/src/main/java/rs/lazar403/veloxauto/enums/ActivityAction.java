package rs.lazar403.veloxauto.enums;

public enum ActivityAction {
    // Customer actions
    CUSTOMER_REGISTERED,
    CUSTOMER_UPDATED,
    CUSTOMER_DEACTIVATED,
    
    // Vehicle actions
    VEHICLE_CREATED,
    VEHICLE_UPDATED,
    VEHICLE_DEACTIVATED,
    VEHICLE_PRICE_CHANGED,
    
    // Reservation actions
    RESERVATION_CREATED,
    RESERVATION_CANCELED,
    RESERVATION_EXPIRED,
    
    // Sale actions
    SALE_INITIATED,
    SALE_COMPLETED,
    SALE_CANCELED,
    
    // Favorite actions
    FAVORITE_ADDED,
    FAVORITE_REMOVED
}
