import { NextRequest, NextResponse } from "next/server";

const DEFAULT_BASE_URL = "http://localhost:8081";

function getBackendBaseUrl() {
  return process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_BASE_URL;
}

export async function GET(request: NextRequest) {
  const query = request.nextUrl.search;
  const targetUrl = `${getBackendBaseUrl()}/api/vehicles${query}`;

  try {
    const backendResponse = await fetch(targetUrl, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      cache: "no-store",
    });

    const contentType = backendResponse.headers.get("content-type") ?? "application/json";
    const bodyText = await backendResponse.text();

    return new NextResponse(bodyText, {
      status: backendResponse.status,
      headers: {
        "content-type": contentType,
      },
    });
  } catch {
    return NextResponse.json(
      {
        message: "Backend service is unavailable. Make sure the backend API is running.",
      },
      { status: 503 }
    );
  }
}
