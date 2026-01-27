import { NextRequest, NextResponse } from "next/server";
import { headers } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

// GET /api/v1/items/[id] - 아이템 상세 조회
export async function GET(request: NextRequest, { params }: { params: Promise<{ id: string }> }) {
  try {
    const { id } = await params;
    const h = await headers();
    const cookie = h.get("cookie") ?? "";

    const res = await fetch(`${BASE_URL}/api/v1/items/${id}`, {
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

// DELETE /api/v1/items/[id] - 아이템 삭제
export async function DELETE(request: NextRequest, { params }: { params: Promise<{ id: string }> }) {
  try {
    const { id } = await params;
    const h = await headers();
    const cookie = h.get("cookie") ?? "";

    const res = await fetch(`${BASE_URL}/api/v1/items/${id}`, {
      method: "DELETE",
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
