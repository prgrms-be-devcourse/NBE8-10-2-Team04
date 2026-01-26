import { NextRequest, NextResponse } from "next/server";
import { cookies } from "next/headers";
import { applyCookiesToResponse } from "@/lib/cookie-utils";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

// PATCH /api/v1/user/me/password - 비밀번호 변경
export async function PATCH(request: NextRequest) {
  try {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;
    const apiKey = cookieStore.get("apiKey")?.value;

    const body = await request.json();

    const res = await fetch(`${BASE_URL}/api/v1/user/me/password`, {
      method: "PATCH",
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
    const setCookieHeader = res.headers.get("set-cookie");
    const response = NextResponse.json(data, { status: res.status });

    // 401 에러 시 쿠키 삭제 후 재인증 유도
    if (res.status === 401) {
      response.cookies.delete("accessToken");
      response.cookies.delete("apiKey");
      return response;
    }

    // 백엔드에서 쿠키를 설정한 경우, 프론트엔드 쿠키에도 반영
    if (setCookieHeader) {
      applyCookiesToResponse(response, setCookieHeader);
    } else if (res.ok && res.status === 200) {
      // 성공했는데 토큰 갱신이 없는 경우 경고 (개발 환경에서만)
      if (process.env.NODE_ENV === "development") {
        console.warn("백엔드에서 새 토큰을 설정하지 않았습니다. 비밀번호 변경 시 토큰 갱신이 필요할 수 있습니다.");
      }
    }

    if (!res.ok) {
      return response;
    }

    return response;
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
