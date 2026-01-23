import { cookies } from "next/headers";
import { redirect } from "next/navigation";


type JwtPayload = {
  id?: number;
  loginId?: string;
  email?: string;
  sub?: string;
  exp?: number;
  iat?: number;
};

function parseJwt(token: string): JwtPayload | null {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;

    const base64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const json = Buffer.from(base64, "base64").toString("utf8");
    return JSON.parse(json);
  } catch {
    return null;
  }
}


export default async function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;

  // 로그인 안 했으면 (main) 접근 불가
  if (!accessToken) redirect("/login");

  const payload = parseJwt(accessToken);

  // 토큰이 깨졌거나 만료되었으면 로그인으로
  const nowSec = Math.floor(Date.now() / 1000);
  if (!payload || (payload.exp && payload.exp <= nowSec)) {
    redirect("/login");
  }

  const loginId = payload.loginId ?? payload.sub ?? "사용자";
  const email = payload.email; // 토큰에 없으면 표시 안 함

  return (
    <div>
      <div className="bg-gradient-to-r from-red-500 via-blue-500 to-yellow-400 p-[2px]">
        <header className="bg-[#0a0d14]">
          <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-3">
            <div>
              <div className="text-sm font-semibold tracking-[0.2em] text-white">
                POWER RANGERS
              </div>
              <div className="text-[10px] text-white/60">관리 시스템</div>
            </div>

            <div className="flex items-center gap-3 text-xs text-white/70">
              <span className="hidden sm:inline">
                {loginId} 레인저
              </span>
              
              {email ? (
                <span className="rounded-full bg-white/10 px-3 py-1 ring-1 ring-white/15">
                  {email}
                </span>
              ) : null}
              <form action="/api/auth/logout" method="POST">
                <button className="rounded-full bg-red-500 px-3 py-1 text-xs font-semibold text-white">
                  로그아웃
                </button>
              </form>
            </div>
          </div>
        </header>
      </div>

      {children}
    </div>
  );
}
