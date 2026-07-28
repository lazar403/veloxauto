import { toast } from "react-hot-toast";
import { ApiErrorResponse } from "@/lib/api/types";

const DEFAULT_BASE_URL = "http://localhost:8080";

function getBaseUrl() {
  return process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_BASE_URL;
}

function resolveUrl(path: string) {
  if (/^https?:\/\//i.test(path)) {
    return path;
  }

  if (path.startsWith("/api/")) {
    return path;
  }

  return `${getBaseUrl()}${path}`;
}

export async function apiFetch<T>(
  path: string,
  init?: RequestInit & { suppressErrorToast?: boolean }
): Promise<T> {
  const response = await fetch(resolveUrl(path), {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
  });

  if (!response.ok) {
    const data = (await response.json().catch(() => null)) as ApiErrorResponse | null;
    const message = data?.message ?? `Request failed with status ${response.status}`;

    if (!init?.suppressErrorToast) {
      toast.error(message);
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return null as T;
  }

  return response.json() as Promise<T>;
}
