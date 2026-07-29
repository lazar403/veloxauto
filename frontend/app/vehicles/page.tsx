import Link from "next/link";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { type PaginatedResponse, type VehicleStatus, type VehicleSummary } from "@/lib/api/types";
import { VehiclesFiltersForm } from "@/components/vehicles/vehicles-filters-form";
import { VehicleCard } from "@/components/vehicles/vehicle-card";

const DEFAULT_BASE_URL = "http://localhost:8081";
type SortBy = "createdAt" | "price" | "year" | "make" | "model";
type SortDir = "asc" | "desc";
const DEFAULT_STATE = {
  active: "true",
  status: "",
  sortBy: "createdAt",
  sortDir: "desc",
  size: 10,
  page: 0,
} as const;

const allowedSortBy = new Set<SortBy>(["createdAt", "price", "year", "make", "model"]);
const allowedStatus = new Set(["AVAILABLE", "RESERVED", "SOLD", "INACTIVE"]);

type SearchParams = Record<string, string | string[] | undefined>;
type VehiclesState = {
  active: "" | "true" | "false";
  status: "" | VehicleStatus;
  sortBy: SortBy;
  sortDir: SortDir;
  size: number;
  page: number;
};

type VehiclesResult = {
  data: PaginatedResponse<VehicleSummary>;
  error: string | null;
};

function getSingleParam(value: string | string[] | undefined) {
  return Array.isArray(value) ? value[0] : value;
}

function parseState(searchParams: SearchParams): VehiclesState {
  const activeParam = getSingleParam(searchParams.active);
  const statusParam = getSingleParam(searchParams.status);
  const sortByParam = getSingleParam(searchParams.sortBy);
  const sortDirParam = getSingleParam(searchParams.sortDir);
  const sizeParam = Number(getSingleParam(searchParams.size));
  const pageParam = Number(getSingleParam(searchParams.page));

  const active = activeParam === "true" || activeParam === "false" ? activeParam : "";
  const status = statusParam && allowedStatus.has(statusParam) ? (statusParam as VehicleStatus) : "";
  const sortBy = sortByParam && allowedSortBy.has(sortByParam as SortBy) ? (sortByParam as SortBy) : DEFAULT_STATE.sortBy;
  const sortDir: SortDir = sortDirParam === "asc" ? "asc" : "desc";
  const size = Number.isFinite(sizeParam) ? Math.min(100, Math.max(1, sizeParam)) : DEFAULT_STATE.size;
  const page = Number.isFinite(pageParam) ? Math.max(0, pageParam) : DEFAULT_STATE.page;

  return {
    active,
    status,
    sortBy,
    sortDir,
    size,
    page,
  };
}

function normalizePaginatedVehicles(input: unknown): PaginatedResponse<VehicleSummary> {
  if (Array.isArray(input)) {
    return {
      content: input as VehicleSummary[],
      page: 0,
      size: input.length,
      totalElements: input.length,
      totalPages: input.length > 0 ? 1 : 0,
      first: true,
      last: true,
    };
  }
  if (!input || typeof input !== "object") {
    return {
      content: [],
      page: 0,
      size: DEFAULT_STATE.size,
      totalElements: 0,
      totalPages: 0,
      first: true,
      last: true,
    };
  }

  const value = input as Partial<PaginatedResponse<VehicleSummary>>;
  const content = Array.isArray(value.content) ? value.content : [];
  const page = typeof value.page === "number" ? value.page : 0;
  const size = typeof value.size === "number" ? value.size : DEFAULT_STATE.size;
  const totalElements = typeof value.totalElements === "number" ? value.totalElements : content.length;
  const totalPages = typeof value.totalPages === "number" ? value.totalPages : 0;
  const first = typeof value.first === "boolean" ? value.first : page <= 0;
  const last = typeof value.last === "boolean" ? value.last : page >= Math.max(totalPages - 1, 0);

  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    first,
    last,
  };
}

async function getVehicles(state: VehiclesState): Promise<VehiclesResult> {
  const search = new URLSearchParams();
  search.set("page", String(state.page));
  search.set("size", String(state.size));
  search.set("sortBy", state.sortBy);
  search.set("sortDir", state.sortDir);

  if (state.active !== "") {
    search.set("active", state.active);
  }
  if (state.status) {
    search.set("status", state.status);
  }

  const baseUrl = process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_BASE_URL;
  const targetUrl = `${baseUrl}/api/vehicles?${search.toString()}`;

  try {
    const response = await fetch(targetUrl, {
      cache: "no-store",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      const data = (await response.json().catch(() => null)) as { message?: string } | null;
      return {
        data: normalizePaginatedVehicles(null),
        error: data?.message ?? `Request failed with status ${response.status}`,
      };
    }

    const payload = (await response.json().catch(() => null)) as unknown;
    return {
      data: normalizePaginatedVehicles(payload),
      error: null,
    };
  } catch {
    return {
      data: normalizePaginatedVehicles(null),
      error: "Backend service is unavailable. Make sure the backend API is running.",
    };
  }
}

function formatCurrency(value: string | number) {
  const numeric = typeof value === "string" ? Number(value) : value;
  return new Intl.NumberFormat("en-IE", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0,
  }).format(Number.isNaN(numeric) ? 0 : numeric);
}


export default async function InventoryPage({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}) {
  const parsedParams = await searchParams;
  const state = parseState(parsedParams);
  const { data, error } = await getVehicles(state);
  const currentPage = data.page;
  const totalPages = Math.max(data.totalPages, 1);
  const pageInfo = `Page ${currentPage + 1} of ${totalPages} · ${data.totalElements} vehicles`;

  return (
    <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 px-4 py-6 md:px-6">
      <section className="grid gap-4 rounded-xl border bg-card p-6 md:grid-cols-[1fr_auto] md:items-center">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">Inventory</h1>
          <p className="text-sm text-muted-foreground">Browse and manage available vehicles with server-side filtering and pagination.</p>
        </div>
        <Badge variant="secondary">{pageInfo}</Badge>
      </section>

      <Card>
        <CardHeader>
          <CardTitle>Filters</CardTitle>
          <CardDescription>Refine inventory by activity, status, sorting, and page size.</CardDescription>
        </CardHeader>
        <CardContent>
          <VehiclesFiltersForm
            initialValues={{
              active: state.active,
              status: state.status,
              sortBy: state.sortBy,
              sortDir: state.sortDir,
              size: state.size,
            }}
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Vehicles</CardTitle>
          <CardDescription>Data sourced from backend `/api/vehicles` endpoint.</CardDescription>
        </CardHeader>
        <CardContent>
          {error ? (
            <div className="space-y-3">
              <p className="text-sm text-red-500">{error}</p>
            </div>
          ) : data.content.length === 0 ? (
            <p className="text-sm text-muted-foreground">No vehicles found for the selected filters.</p>
          ) : (
            <>
              <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                {data.content.map((vehicle) => (
                  <VehicleCard key={vehicle.id} vehicle={vehicle} priceLabel={formatCurrency(vehicle.price)} />
                ))}
              </div>

              <div className="mt-4 flex items-center justify-between">
                <p className="text-xs text-muted-foreground">{pageInfo}</p>
                <div className="flex gap-2">
                  {data.first ? (
                    <Button variant="outline" disabled>
                      Previous
                    </Button>
                  ) : (
                    <Button variant="outline" asChild>
                      <Link
                        href={{
                          pathname: "/vehicles",
                          query: {
                            active: state.active || undefined,
                            status: state.status || undefined,
                            sortBy: state.sortBy,
                            sortDir: state.sortDir,
                            size: String(state.size),
                            page: String(Math.max(0, currentPage - 1)),
                          },
                        }}
                      >
                        Previous
                      </Link>
                    </Button>
                  )}

                  {data.last ? (
                    <Button variant="outline" disabled>
                      Next
                    </Button>
                  ) : (
                    <Button variant="outline" asChild>
                      <Link
                        href={{
                          pathname: "/vehicles",
                          query: {
                            active: state.active || undefined,
                            status: state.status || undefined,
                            sortBy: state.sortBy,
                            sortDir: state.sortDir,
                            size: String(state.size),
                            page: String(currentPage + 1),
                          },
                        }}
                      >
                        Next
                      </Link>
                    </Button>
                  )}
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </main>
  );
}
