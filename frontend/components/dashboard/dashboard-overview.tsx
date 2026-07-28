"use client";

import { useEffect, useMemo, useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { apiFetch } from "@/lib/api/client";

type DashboardData = {
  metrics: {
    activeListings: number;
    newLeads: number;
    monthlyRevenue: number;
    coinBalance: number;
  };
  recentVehicles: Array<{
    id: string;
    title: string;
    year: number;
    status: "AVAILABLE" | "RESERVED" | "SOLD";
    priceEur: number;
  }>;
  coinOffers: Array<{
    id: string;
    title: string;
    coins: number;
    costEur: number;
    effect: string;
  }>;
};

type ViewState = "success" | "loading" | "empty" | "error";

function formatEur(value: number) {
  return new Intl.NumberFormat("en-IE", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0,
  }).format(value);
}

export function DashboardOverview() {
  const [viewState, setViewState] = useState<ViewState>("success");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<DashboardData | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    async function load() {
      try {
        setIsLoading(true);
        setError(null);
        const response = await apiFetch<DashboardData>(`/api/mock/dashboard?state=${viewState}`, {
          signal: controller.signal,
          suppressErrorToast: true,
        });
        setData(response);
      } catch (e) {
        if (!controller.signal.aborted) {
          setError(e instanceof Error ? e.message : "Failed to load dashboard.");
          setData(null);
        }
      } finally {
        if (!controller.signal.aborted) {
          setIsLoading(false);
        }
      }
    }

    load();
    return () => controller.abort();
  }, [viewState]);

  const statusBadge = useMemo(() => {
    if (isLoading) return <Badge variant="secondary">Loading</Badge>;
    if (error) return <Badge variant="outline">Error state</Badge>;
    if ((data?.recentVehicles.length ?? 0) === 0) return <Badge variant="secondary">Empty state</Badge>;
    return <Badge>Live mock data</Badge>;
  }, [data?.recentVehicles.length, error, isLoading]);

  return (
    <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 px-4 py-6 md:px-6">
      <section className="grid gap-4 rounded-xl border bg-card p-6 md:grid-cols-[1fr_auto] md:items-center">
        <div className="space-y-2">
          <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">Dealer Dashboard</h1>
          <p className="text-sm text-muted-foreground">Operational overview of inventory, leads, and promotions.</p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          {statusBadge}
          <Button variant={viewState === "success" ? "default" : "outline"} size="sm" onClick={() => setViewState("success")}>
            Success
          </Button>
          <Button variant={viewState === "loading" ? "default" : "outline"} size="sm" onClick={() => setViewState("loading")}>
            Loading
          </Button>
          <Button variant={viewState === "empty" ? "default" : "outline"} size="sm" onClick={() => setViewState("empty")}>
            Empty
          </Button>
          <Button variant={viewState === "error" ? "default" : "outline"} size="sm" onClick={() => setViewState("error")}>
            Error
          </Button>
        </div>
      </section>

      {error ? (
        <Card>
          <CardHeader>
            <CardTitle>Dashboard unavailable</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => setViewState("success")}>Try normal state</Button>
          </CardContent>
        </Card>
      ) : (
        <>
          <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {isLoading ? (
              Array.from({ length: 4 }).map((_, idx) => (
                <Card key={idx}>
                  <CardHeader>
                    <Skeleton className="h-4 w-24" />
                  </CardHeader>
                  <CardContent>
                    <Skeleton className="h-8 w-20" />
                  </CardContent>
                </Card>
              ))
            ) : (
              <>
                <Card>
                  <CardHeader>
                    <CardDescription>Active listings</CardDescription>
                    <CardTitle>{data?.metrics.activeListings ?? 0}</CardTitle>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader>
                    <CardDescription>New leads</CardDescription>
                    <CardTitle>{data?.metrics.newLeads ?? 0}</CardTitle>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader>
                    <CardDescription>This month revenue</CardDescription>
                    <CardTitle>{formatEur(data?.metrics.monthlyRevenue ?? 0)}</CardTitle>
                  </CardHeader>
                </Card>
                <Card>
                  <CardHeader>
                    <CardDescription>Promotion coins</CardDescription>
                    <CardTitle>{data?.metrics.coinBalance ?? 0}</CardTitle>
                  </CardHeader>
                </Card>
              </>
            )}
          </section>

          <section className="grid gap-4 xl:grid-cols-[2fr_1fr]">
            <Card>
              <CardHeader>
                <CardTitle>Recent inventory</CardTitle>
                <CardDescription>Latest added vehicles and sale status.</CardDescription>
              </CardHeader>
              <CardContent>
                {isLoading ? (
                  <div className="space-y-2">
                    <Skeleton className="h-10 w-full" />
                    <Skeleton className="h-10 w-full" />
                    <Skeleton className="h-10 w-full" />
                  </div>
                ) : (data?.recentVehicles.length ?? 0) === 0 ? (
                  <p className="text-sm text-muted-foreground">No vehicles found for the selected state.</p>
                ) : (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Vehicle</TableHead>
                        <TableHead>Year</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead className="text-right">Price</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {data?.recentVehicles.map((vehicle) => (
                        <TableRow key={vehicle.id}>
                          <TableCell className="font-medium">{vehicle.title}</TableCell>
                          <TableCell>{vehicle.year}</TableCell>
                          <TableCell>
                            <Badge variant={vehicle.status === "AVAILABLE" ? "default" : "secondary"}>{vehicle.status}</Badge>
                          </TableCell>
                          <TableCell className="text-right">{formatEur(vehicle.priceEur)}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Coin offers</CardTitle>
                <CardDescription>Boost listing visibility in search results.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-3">
                {isLoading ? (
                  <>
                    <Skeleton className="h-20 w-full" />
                    <Skeleton className="h-20 w-full" />
                  </>
                ) : (
                  data?.coinOffers.map((offer) => (
                    <div key={offer.id} className="rounded-lg border p-3">
                      <div className="mb-1 flex items-center justify-between">
                        <p className="font-medium">{offer.title}</p>
                        <Badge variant="secondary">{offer.coins} coins</Badge>
                      </div>
                      <p className="text-xs text-muted-foreground">{offer.effect}</p>
                      <div className="mt-3 flex items-center justify-between">
                        <span className="text-sm font-semibold">{formatEur(offer.costEur)}</span>
                        <Button size="sm" variant="outline">
                          Buy
                        </Button>
                      </div>
                    </div>
                  ))
                )}
              </CardContent>
            </Card>
          </section>
        </>
      )}
    </main>
  );
}
