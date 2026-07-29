import { z } from "zod";

export const vehicleStatusSchema = z.enum([
  "AVAILABLE",
  "RESERVED",
  "SOLD",
  "INACTIVE",
]);

export const vehicleFilterSchema = z.object({
  active: z.coerce.boolean().optional(),
  status: vehicleStatusSchema.optional(),
});

export type VehicleFilterInput = z.infer<typeof vehicleFilterSchema>;
