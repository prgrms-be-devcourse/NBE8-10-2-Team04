// app/api/auth/logout/route.ts
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function POST() {
  // 백엔드 로그아웃 호출
  // 프론트 쿠키(accessToken)를 Authorization으로 실어 보내는 방식(백엔드가 Bearer 지원할 때만 의미있음)
  const c = await cookies();

  const accessToken = c.get("accessToken")?.value;
  const apiKey = c.get("apiKey")?.value;

  try {
    if (accessToken && apiKey) {
      const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
      await fetch(`${API_BASE}/api/v1/user/logout`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          apiKey: apiKey, // ✅ 백엔드가 apiKey 요구하는 구조라면 필수
          accept: "application/json",
        },
        cache: "no-store",
      });
    }
  } catch (e) {
    // 백 호출 실패해도 프론트 쿠키 삭제는 진행
    console.log("backend logout failed:", e);
  }

  // 프론트(Next) 쿠키 삭제
  c.delete("accessToken");
  c.delete("apiKey");

  redirect("/login");
}
