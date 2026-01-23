// app/api/auth/logout/route.ts
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function POST() {
  // (선택) 백엔드 로그아웃 호출
  // 프론트 쿠키(accessToken)를 Authorization으로 실어 보내는 방식(백엔드가 Bearer 지원할 때만 의미있음)
  const c = await cookies();

  console.log("logout before", {
    access: c.get("accessToken")?.value?.length,
    apiKey: c.get("apiKey")?.value?.length,
  });

  // 프론트(Next) 쿠키 삭제
  c.delete("accessToken");
  c.delete("apiKey");

  console.log("logout after", {
    access: c.get("accessToken")?.value?.length,
    apiKey: c.get("apiKey")?.value?.length,
  });

  redirect("/login");
}
