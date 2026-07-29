"use client";

import { useTransition } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { applyVehiclesFiltersAction } from "@/app/vehicles/vehicles-actions";
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";

const vehiclesFilterSchema = z.object({
  active: z.enum(["", "true", "false"]),
  status: z.enum(["", "AVAILABLE", "RESERVED", "SOLD", "INACTIVE"]),
  sortBy: z.enum(["createdAt", "price", "year", "make", "model"]),
  sortDir: z.enum(["asc", "desc"]),
  size: z.coerce.number().int().min(1).max(100),
});

type VehiclesFilterValues = z.infer<typeof vehiclesFilterSchema>;

type VehiclesFiltersFormProps = {
  initialValues: VehiclesFilterValues;
};

const defaultValues: VehiclesFilterValues = {
  active: "true",
  status: "",
  sortBy: "createdAt",
  sortDir: "desc",
  size: 10,
};

export function VehiclesFiltersForm({ initialValues }: VehiclesFiltersFormProps) {
  const [isPending, startTransition] = useTransition();
  const form = useForm<VehiclesFilterValues>({
    defaultValues: initialValues,
  });

  function onSubmit(values: VehiclesFilterValues) {
    const parsed = vehiclesFilterSchema.safeParse(values);
    if (!parsed.success) {
      return;
    }
    const formData = new FormData();
    formData.set("active", parsed.data.active);
    formData.set("status", parsed.data.status);
    formData.set("sortBy", parsed.data.sortBy);
    formData.set("sortDir", parsed.data.sortDir);
    formData.set("size", String(parsed.data.size));
    formData.set("page", "0");

    startTransition(() => {
      void applyVehiclesFiltersAction(formData);
    });
  }

  return (
    <Form {...form}>
      <form className="grid gap-3 md:grid-cols-6" onSubmit={form.handleSubmit(onSubmit)}>
        <FormField
          control={form.control}
          name="active"
          render={({ field }) => (
            <FormItem className="md:col-span-1">
              <FormLabel>Active</FormLabel>
              <FormControl>
                <select className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm" {...field}>
                  <option value="">All</option>
                  <option value="true">Active</option>
                  <option value="false">Inactive</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem className="md:col-span-1">
              <FormLabel>Status</FormLabel>
              <FormControl>
                <select className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm" {...field}>
                  <option value="">All</option>
                  <option value="AVAILABLE">AVAILABLE</option>
                  <option value="RESERVED">RESERVED</option>
                  <option value="SOLD">SOLD</option>
                  <option value="INACTIVE">INACTIVE</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="sortBy"
          render={({ field }) => (
            <FormItem className="md:col-span-1">
              <FormLabel>Sort by</FormLabel>
              <FormControl>
                <select className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm" {...field}>
                  <option value="createdAt">Newest first</option>
                  <option value="price">Price</option>
                  <option value="year">Year</option>
                  <option value="make">Make</option>
                  <option value="model">Model</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="sortDir"
          render={({ field }) => (
            <FormItem className="md:col-span-1">
              <FormLabel>Direction</FormLabel>
              <FormControl>
                <select className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm" {...field}>
                  <option value="desc">DESC</option>
                  <option value="asc">ASC</option>
                </select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="size"
          render={({ field }) => (
            <FormItem className="md:col-span-1">
              <FormLabel>Page size</FormLabel>
              <FormControl>
                <Input
                  type="number"
                  min={1}
                  max={100}
                  value={field.value}
                  onChange={(event) => {
                    field.onChange(Number(event.target.value));
                  }}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex items-end gap-2 md:col-span-1">
          <Button type="submit" disabled={isPending}>
            Apply
          </Button>
          <Button
            type="button"
            variant="outline"
            disabled={isPending}
            onClick={() => {
              form.reset(defaultValues);
            }}
          >
            Reset
          </Button>
        </div>
      </form>
    </Form>
  );
}
