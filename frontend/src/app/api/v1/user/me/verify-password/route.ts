import { NextRequest, NextResponse } from "next/server";
import { cookies } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

// POST /api/v1/user/me/verify-password - 비밀번호 확인
export async function POST(request: NextRequest) {
  try {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;
    const apiKey = cookieStore.get("apiKey")?.value;

    const body = await request.json();

    const res = await fetch(`${BASE_URL}/api/v1/user/me/verify-password`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Cookie: [
          accessToken ? `accessToken=${accessToken}` : "",
          apiKey ? `apiKey=${apiKey}` : "",
        ]
          .filter(Boolean)
          .join("; "),
      },
      body: JSON.stringify(body),
      cache: "no-store",
    });

    const data = await res.json();

    if (!res.ok) {
      return NextResponse.json(data, { status: res.status });
    }

    return NextResponse.json(data, { status: 200 });
  } catch (error: unknown) {
    return NextResponse.json(
      {
        resultCode: "500-1",
        msg: error instanceof Error ? error.message : "서버 오류가 발생했습니다.",
        data: null,
      } as RsData<null>,
      { status: 500 }
    );
  }
}
