// app/api/categories/route.ts
import { NextResponse } from "next/server";

export async function GET() {
  const base = process.env.NEXT_PUBLIC_BACKEND_URL ?? "http://localhost:8080";

  const res = await fetch(`${base}/api/v1/categories`, {
    method: "GET",
    cache: "no-store",
    headers: { "Content-Type": "application/json" },
  });

  const data = await res.json();

  return NextResponse.json(data, { status: res.status });
}
