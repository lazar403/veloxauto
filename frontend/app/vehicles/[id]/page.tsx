import Link from "next/link";
import { notFound } from "next/navigation";
import { cookies } from "next/headers";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { type VehicleSummary } from "@/lib/api/types";
import { VehicleImageCarousel } from "@/components/vehicles/vehicle-image-carousel";

const DEFAULT_BASE_URL = "http://localhost:8081";

type UserRole = "ADMIN" | "SALES" | "CUSTOMER";

function formatCurrency(value: string | number) {
  const numeric = typeof value === "string" ? Number(value) : value;
  return new Intl.NumberFormat("en-IE", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0,
  }).format(Number.isNaN(numeric) ? 0 : numeric);
}

function readRoleCookie(value: string | undefined): UserRole | null {
  if (value === "ADMIN" || value === "SALES" || value === "CUSTOMER") {
    return value;
  }
  return null;
}

function getVehicleImages(vehicle: VehicleSummary) {
  const images = [vehicle.primaryImageUrl, ...(vehicle.imageUrls ?? [])].filter((url): url is string => !!url && url.trim().length > 0);
  return [...new Set(images)];
}

async function getVehicle(id: string): Promise<VehicleSummary | null> {
  const baseUrl = process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_BASE_URL;
  const targetUrl = `${baseUrl}/api/vehicles/${id}`;

  try {
    const response = await fetch(targetUrl, {
      cache: "no-store",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      return null;
    }

    return (await response.json()) as VehicleSummary;
  } catch {
    return null;
  }
}

export default async function VehicleDetailsPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const vehicle = await getVehicle(id);

  if (!vehicle) {
    notFound();
  }

  const cookieStore = await cookies();
  const role = readRoleCookie(cookieStore.get("velox_role")?.value);
  const userId = Number(cookieStore.get("velox_user_id")?.value);
  const hasUserId = Number.isFinite(userId);
  const isAdmin = role === "ADMIN";
  const isOwnerSeller = role === "SALES" && hasUserId && userId === vehicle.createdById;
  const canEdit = isAdmin || isOwnerSeller;
  const images = getVehicleImages(vehicle);
  const coverImage = images[0] ?? `https://placehold.co/1200x800/png?text=${encodeURIComponent(`${vehicle.make} ${vehicle.model}`)}`;

  return (
    <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 px-4 py-6 md:px-6">
      <div className="flex items-center justify-between gap-3">
        <div>
          <p className="text-xs text-muted-foreground">Vehicle details</p>
          <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">
            {vehicle.make} {vehicle.model}
          </h1>
        </div>
        <div className="flex items-center gap-2">
          <Badge variant={vehicle.status === "AVAILABLE" ? "default" : "secondary"}>{vehicle.status}</Badge>
          {canEdit ? (
            <Button asChild>
              <Link href={`/vehicles/${vehicle.id}/edit`}>Edit listing</Link>
            </Button>
          ) : null}
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
        <Card className="overflow-hidden">
          <VehicleImageCarousel className="aspect-[16/10] w-full" alt={`${vehicle.make} ${vehicle.model}`} images={images.length > 0 ? images : [coverImage]} />
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>{formatCurrency(vehicle.price)}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3 text-sm">
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">Year</span>
              <span>{vehicle.year}</span>
            </div>
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">Mileage</span>
              <span>{vehicle.mileage ?? "N/A"} km</span>
            </div>
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">Transmission</span>
              <span>{vehicle.transmission ?? "N/A"}</span>
            </div>
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">Fuel</span>
              <span>{vehicle.fuelType ?? "N/A"}</span>
            </div>
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">Engine</span>
              <span>{vehicle.engineCapacity ?? "N/A"} cc</span>
            </div>
            <div className="flex justify-between gap-4">
              <span className="text-muted-foreground">VIN</span>
              <span className="text-right">{vehicle.vin}</span>
            </div>
            <div className="border-t pt-3">
              <p className="text-xs text-muted-foreground">Offer author</p>
              <Link href={`/customers/${vehicle.createdById}`} className="mt-1 block text-sm font-medium underline-offset-2 hover:underline">
                {vehicle.createdByName ?? `User #${vehicle.createdById}`}
              </Link>
              {vehicle.createdByRole ? <p className="text-xs text-muted-foreground">Role: {vehicle.createdByRole}</p> : null}
              {vehicle.createdByPhoneNumber ? <p className="text-xs text-muted-foreground">Phone: {vehicle.createdByPhoneNumber}</p> : null}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Description</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">{vehicle.description?.trim() ? vehicle.description : "No description provided for this listing."}</p>
        </CardContent>
      </Card>

      <div>
        <Button variant="outline" asChild>
          <Link href="/vehicles">Back to inventory</Link>
        </Button>
      </div>
    </main>
  );
}
