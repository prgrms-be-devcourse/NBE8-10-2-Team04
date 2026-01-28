import { NextRequest, NextResponse } from "next/server";
import { headers } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

// GET /api/v1/items/[id]/histories - 아이템 교체 이력 조회
export async function GET(request: NextRequest, { params }: { params: Promise<{ id: string }> }) {
  try {
    const { id } = await params;
    const h = await headers();
    const cookie = h.get("cookie") ?? "";

    const res = await fetch(`${BASE_URL}/api/v1/items/${id}/histories`, {
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
