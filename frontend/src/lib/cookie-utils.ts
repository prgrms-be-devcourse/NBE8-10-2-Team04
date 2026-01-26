import { NextResponse } from "next/server";

export type CookieUpdate = {
  accessToken?: string;
  apiKey?: string;
};

/**
 * Set-Cookie 헤더를 파싱하여 쿠키 정보를 추출합니다.
 * 복잡한 쿠키 값(쉼표 포함 등)도 올바르게 처리합니다.
 */
export function parseSetCookie(setCookieHeader: string | null): CookieUpdate {
  if (!setCookieHeader) {
    return {};
  }

  const cookies: CookieUpdate = {};
  
  // Set-Cookie 헤더는 각각 별도로 오거나, 쉼표로 구분될 수 있음
  // 정규식으로 올바르게 분리 (쿠키 이름으로 시작하는 패턴)
  const cookieStrings = setCookieHeader.split(/,(?=\s*\w+=)/);
  
  cookieStrings.forEach((cookie) => {
    const trimmed = cookie.trim();
    if (!trimmed) return;
    
    // 세미콜론 이전의 name=value 부분만 추출
    const [nameValue] = trimmed.split(";");
    const equalIndex = nameValue.indexOf("=");
    
    if (equalIndex > 0) {
      const name = nameValue.substring(0, equalIndex).trim();
      const value = nameValue.substring(equalIndex + 1).trim();
      
      if (name && value) {
        if (name === "accessToken") {
          cookies.accessToken = value;
        } else if (name === "apiKey") {
          cookies.apiKey = value;
        }
      }
    }
  });
  
  return cookies;
}

/**
 * 백엔드 응답의 쿠키를 프론트엔드 쿠키에 반영합니다.
 */
export function applyCookiesToResponse(
  response: NextResponse,
  setCookieHeader: string | null
): void {
  if (!setCookieHeader) {
    return;
  }

  const cookieUpdate = parseSetCookie(setCookieHeader);
  
  Object.entries(cookieUpdate).forEach(([name, value]) => {
    if (value) {
      response.cookies.set(name, value, {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        sameSite: "lax",
        path: "/",
      });
    }
  });
}
