"use server";

import { redirect } from "next/navigation";

const allowedSortBy = new Set(["createdAt", "price", "year", "make", "model"]);
const allowedStatus = new Set(["AVAILABLE", "RESERVED", "SOLD", "INACTIVE"]);

function getStringValue(formData: FormData, key: string) {
  const value = formData.get(key);
  return typeof value === "string" ? value : "";
}

export async function applyVehiclesFiltersAction(formData: FormData) {
  const activeRaw = getStringValue(formData, "active");
  const statusRaw = getStringValue(formData, "status");
  const sortByRaw = getStringValue(formData, "sortBy");
  const sortDirRaw = getStringValue(formData, "sortDir");
  const sizeRaw = Number(getStringValue(formData, "size"));
  const pageRaw = Number(getStringValue(formData, "page"));

  const active = activeRaw === "true" || activeRaw === "false" ? activeRaw : "";
  const status = allowedStatus.has(statusRaw) ? statusRaw : "";
  const sortBy = allowedSortBy.has(sortByRaw) ? sortByRaw : "createdAt";
  const sortDir = sortDirRaw === "asc" ? "asc" : "desc";
  const size = Number.isFinite(sizeRaw) ? Math.min(100, Math.max(1, sizeRaw)) : 10;
  const page = Number.isFinite(pageRaw) ? Math.max(0, pageRaw) : 0;

  const search = new URLSearchParams();
  search.set("page", String(page));
  search.set("size", String(size));
  search.set("sortBy", sortBy);
  search.set("sortDir", sortDir);

  if (active !== "") {
    search.set("active", active);
  }
  if (status !== "") {
    search.set("status", status);
  }

  redirect(`/vehicles?${search.toString()}`);
}
