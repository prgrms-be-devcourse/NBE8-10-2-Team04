// app/api/categories/route.ts
import { NextResponse } from "next/server";
import { headers } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function GET() {
  try {
    const h = await headers();
    const cookie = h.get("cookie") ?? "";

    const res = await fetch(`${BASE_URL}/api/v1/categories`, {
      method: "GET",
      cache: "no-store",
      headers: {
        "Content-Type": "application/json",
        ...(cookie ? { Cookie: cookie } : {}),
      },
    });

    const data = await res.json();
    return NextResponse.json(data, { status: res.status });
  } catch (error: unknown) {
    return NextResponse.json(
      { resultCode: "500-1", msg: error instanceof Error ? error.message : "서버 오류", data: null },
      { status: 500 },
    );
  }
}