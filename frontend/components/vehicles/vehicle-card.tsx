import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { type VehicleSummary } from "@/lib/api/types";
import { VehicleImageCarousel } from "@/components/vehicles/vehicle-image-carousel";

type VehicleCardProps = {
  vehicle: VehicleSummary;
  priceLabel: string;
};

function getVehicleImages(vehicle: VehicleSummary) {
  const images = [vehicle.primaryImageUrl, ...(vehicle.imageUrls ?? [])].filter((url): url is string => !!url && url.trim().length > 0);
  if (images.length > 0) {
    return images;
  }
  return [`https://placehold.co/960x640/png?text=${encodeURIComponent(`${vehicle.make} ${vehicle.model}`)}`];
}

export function VehicleCard({ vehicle, priceLabel }: VehicleCardProps) {
  return (
    <Card className="h-full overflow-hidden transition-shadow hover:shadow-md">
      <VehicleImageCarousel className="aspect-[16/10] w-full" alt={`${vehicle.make} ${vehicle.model}`} images={getVehicleImages(vehicle)} />
      <CardHeader className="space-y-1 p-4">
        <div className="flex items-start justify-between gap-3">
          <h3 className="line-clamp-1 text-base font-semibold">
            {vehicle.make} {vehicle.model}
          </h3>
          <Badge variant={vehicle.status === "AVAILABLE" ? "default" : "secondary"}>{vehicle.status}</Badge>
        </div>
        <p className="text-xs text-muted-foreground">
          {vehicle.year} • {vehicle.mileage ?? "N/A"} km
        </p>
        {vehicle.createdByName ? <p className="text-xs text-muted-foreground">Seller: {vehicle.createdByName}</p> : null}
      </CardHeader>
      <CardContent className="flex items-center justify-between gap-3 p-4 pt-0">
        <p className="text-lg font-semibold">{priceLabel}</p>
        <Button variant="outline" size="sm" asChild>
          <Link href={`/vehicles/${vehicle.id}`}>View details</Link>
        </Button>
      </CardContent>
    </Card>
  );
}
