import { NextResponse } from "next/server";

const successData = {
  metrics: {
    activeListings: 128,
    newLeads: 24,
    monthlyRevenue: 214700,
    coinBalance: 840,
  },
  recentVehicles: [
    { id: "v1", title: "BMW M340i", year: 2022, status: "AVAILABLE", priceEur: 47900 },
    { id: "v2", title: "Audi A5", year: 2021, status: "RESERVED", priceEur: 36200 },
    { id: "v3", title: "Mercedes C220d", year: 2020, status: "SOLD", priceEur: 31500 },
  ],
  coinOffers: [
    { id: "c1", title: "Top 10 Spotlight", coins: 120, costEur: 19, effect: "Prioritized placement for 24 hours." },
    { id: "c2", title: "Featured Card", coins: 280, costEur: 39, effect: "Highlighted card in search for 3 days." },
    { id: "c3", title: "Premium Boost", coins: 520, costEur: 69, effect: "Top-tier ranking + badge for 7 days." },
  ],
} as const;

export async function GET(request: Request) {
  const url = new URL(request.url);
  const state = url.searchParams.get("state") ?? "success";

  if (state === "loading") {
    await new Promise((resolve) => setTimeout(resolve, 1400));
    return NextResponse.json(successData);
  }

  if (state === "empty") {
    return NextResponse.json({
      ...successData,
      recentVehicles: [],
      metrics: {
        ...successData.metrics,
        activeListings: 0,
      },
    });
  }

  if (state === "error") {
    return NextResponse.json(
      {
        timestamp: new Date().toISOString(),
        status: 503,
        error: "Service Unavailable",
        message: "Mock provider temporarily unavailable.",
        path: "/api/mock/dashboard",
        validationErrors: null,
      },
      { status: 503 }
    );
  }

  return NextResponse.json(successData);
}
