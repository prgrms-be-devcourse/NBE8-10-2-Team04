import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export default async function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;

  // 로그인 안 했으면 (main) 접근 불가
  if (!accessToken) {
    redirect("/login");
  }

  return (
    <div>
      <div className="bg-gradient-to-r from-red-500 via-blue-500 to-yellow-400 p-[1px]">
        <header className="bg-[#0a0d14]">
          <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-3">
            <div>
              <div className="text-sm font-semibold tracking-[0.2em] text-white">
                POWER RANGERS
              </div>
              <div className="text-[10px] text-white/60">관리 시스템</div>
            </div>

            <div className="flex items-center gap-3 text-xs text-white/70">
              <span className="hidden sm:inline">레드 레인저</span>
              <span className="rounded-full bg-white/10 px-3 py-1 ring-1 ring-white/15">
                dbase129@gmail.com
              </span>
              <button className="rounded-full bg-red-500 px-3 py-1 text-xs font-semibold text-white">
                로그아웃
              </button>
            </div>
          </div>
        </header>
      </div>

      {children}
    </div>
  );
}
