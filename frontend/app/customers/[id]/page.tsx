import Link from "next/link";
import { notFound } from "next/navigation";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { type CustomerSummary } from "@/lib/api/types";

const DEFAULT_BASE_URL = "http://localhost:8081";

async function getCustomer(id: string): Promise<CustomerSummary | null> {
  const baseUrl = process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_BASE_URL;
  const targetUrl = `${baseUrl}/api/customers/${id}`;

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

    return (await response.json()) as CustomerSummary;
  } catch {
    return null;
  }
}

function displayName(customer: CustomerSummary) {
  const fullName = `${customer.firstName ?? ""} ${customer.lastName ?? ""}`.trim();
  return fullName.length > 0 ? fullName : `User #${customer.id}`;
}

export default async function CustomerProfilePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const customer = await getCustomer(id);

  if (!customer) {
    notFound();
  }

  return (
    <main className="mx-auto flex w-full max-w-4xl flex-1 flex-col gap-6 px-4 py-6 md:px-6">
      <div className="flex items-center justify-between gap-3">
        <div>
          <p className="text-xs text-muted-foreground">Seller profile</p>
          <h1 className="text-2xl font-semibold tracking-tight md:text-3xl">{displayName(customer)}</h1>
        </div>
        <Badge variant={customer.isActive ? "default" : "secondary"}>{customer.role}</Badge>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Contact information</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3 text-sm">
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground">Email</span>
            <span className="text-right">{customer.email}</span>
          </div>
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground">Phone</span>
            <span>{customer.phoneNumber || "Not provided"}</span>
          </div>
          <div className="flex justify-between gap-4">
            <span className="text-muted-foreground">Status</span>
            <span>{customer.isActive ? "Active" : "Inactive"}</span>
          </div>
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
