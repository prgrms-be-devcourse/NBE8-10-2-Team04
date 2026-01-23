import { NextRequest, NextResponse } from "next/server";
import { cookies } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

// GET /api/v1/user/me - 사용자 정보 조회
export async function GET() {
  try {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;
    const apiKey = cookieStore.get("apiKey")?.value;

    const res = await fetch(`${BASE_URL}/api/v1/user/me`, {
      method: "GET",
      headers: {
        Cookie: [
          accessToken ? `accessToken=${accessToken}` : "",
          apiKey ? `apiKey=${apiKey}` : "",
        ]
          .filter(Boolean)
          .join("; "),
      },
      cache: "no-store",
    });

    const data = await res.json();

    if (!res.ok) {
      return NextResponse.json(data, { status: res.status });
    }

    return NextResponse.json(data, { status: 200 });
  } catch (error: any) {
    return NextResponse.json(
      {
        resultCode: "500-1",
        msg: error?.message || "서버 오류가 발생했습니다.",
        data: null,
      } as RsData<null>,
      { status: 500 }
    );
  }
}

// PUT /api/v1/user/modify - 사용자 정보 수정
export async function PUT(request: NextRequest) {
  try {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;
    const apiKey = cookieStore.get("apiKey")?.value;

    const body = await request.json();

    const res = await fetch(`${BASE_URL}/api/v1/user/modify`, {
      method: "PUT",
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

    // 백엔드에서 쿠키를 설정한 경우, 프론트엔드 쿠키에도 반영
    const setCookieHeader = res.headers.get("set-cookie");
    if (setCookieHeader) {
      const response = NextResponse.json(data, { status: res.status });
      // 쿠키 파싱 및 설정
      setCookieHeader.split(",").forEach((cookie) => {
        const [nameValue] = cookie.split(";");
        const [name, value] = nameValue.split("=");
        if (name && value) {
          response.cookies.set(name.trim(), value.trim(), {
            httpOnly: true,
            secure: process.env.NODE_ENV === "production",
            sameSite: "lax",
            path: "/",
          });
        }
      });
      return response;
    }

    if (!res.ok) {
      return NextResponse.json(data, { status: res.status });
    }

    return NextResponse.json(data, { status: 200 });
  } catch (error: any) {
    return NextResponse.json(
      {
        resultCode: "500-1",
        msg: error?.message || "서버 오류가 발생했습니다.",
        data: null,
      } as RsData<null>,
      { status: 500 }
    );
  }
}

// DELETE /api/v1/user/me - 회원 탈퇴
export async function DELETE() {
  try {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;
    const apiKey = cookieStore.get("apiKey")?.value;

    const res = await fetch(`${BASE_URL}/api/v1/user/me`, {
      method: "DELETE",
      headers: {
        Cookie: [
          accessToken ? `accessToken=${accessToken}` : "",
          apiKey ? `apiKey=${apiKey}` : "",
        ]
          .filter(Boolean)
          .join("; "),
      },
      cache: "no-store",
    });

    const data = await res.json();

    // 백엔드에서 쿠키를 삭제한 경우, 프론트엔드 쿠키도 삭제
    const setCookieHeader = res.headers.get("set-cookie");
    if (setCookieHeader) {
      const response = NextResponse.json(data, { status: res.status });
      // 쿠키 삭제
      response.cookies.delete("accessToken");
      response.cookies.delete("apiKey");
      return response;
    }

    if (!res.ok) {
      return NextResponse.json(data, { status: res.status });
    }

    return NextResponse.json(data, { status: 200 });
  } catch (error: any) {
    return NextResponse.json(
      {
        resultCode: "500-1",
        msg: error?.message || "서버 오류가 발생했습니다.",
        data: null,
      } as RsData<null>,
      { status: 500 }
    );
  }
}
