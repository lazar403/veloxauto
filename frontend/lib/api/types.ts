export type ApiErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string> | null;
};

export type PaginatedResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type VehicleStatus = "AVAILABLE" | "RESERVED" | "SOLD" | "INACTIVE";
export type CustomerRole = "CUSTOMER" | "ADMIN" | "SALES";

export type CustomerSummary = {
  id: number;
  firstName: string;
  lastName: string | null;
  email: string;
  phoneNumber: string | null;
  isActive: boolean;
  role: CustomerRole;
  createdAt: string;
};

export type VehicleSummary = {
  id: number;
  createdById: number;
  createdByName?: string | null;
  createdByPhoneNumber?: string | null;
  createdByRole?: CustomerRole | null;
  make: string;
  model: string;
  year: number;
  vin: string;
  price: string;
  mileage: number | null;
  color: string | null;
  transmission?: string | null;
  fuelType?: string | null;
  engineCapacity?: number | null;
  description?: string | null;
  status: VehicleStatus;
  isActive: boolean;
  exchange: boolean;
  primaryImageUrl?: string | null;
  imageUrls?: string[];
  createdAt: string;
};
