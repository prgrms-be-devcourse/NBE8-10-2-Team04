"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Box, User, Clock, Layers } from "lucide-react";
import { getCategoryStyle } from "@/lib/category-styles";

type MissionKey = "items" | "me" | "history" | "categories";

type MissionCard = {
  key: MissionKey;
  title: string;
  subtitle: string;
  action: string;
  color: "red" | "blue" | "yellow" | "pink";
};

type TodayMission = {
  id: string;
  title: string;
  category: string;
  desc: string;
  dueText: string;
  dDay: number;
};

// 백엔드(/api/v1/items) 목록 응답(ItemSummaryResponse) 형태
type ItemSummaryApi = {
  id: number;
  name: string;
  categoryName: string | null;
  nextReplacementDate: string | null; // LocalDate -> "YYYY-MM-DD"
  imgUrl: string | null;
  dDay: number;
  isActive: boolean;
};

// RsData 래퍼 형태
type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

const API_BASE = "http://localhost:8080";

const COLOR = {
  red: {
    ring: "ring-red-500",
    glow: "shadow-[0_0_0_1px_rgba(239,68,68,0.3),0_0_18px_rgba(239,68,68,0.25)]",
    btn: "bg-red-500 hover:bg-red-400 hover:scale-105 active:scale-95",
    icon: "text-red-50",
    iconBg: "bg-red-500",
  },
  blue: {
    ring: "ring-blue-500",
    glow: "shadow-[0_0_0_1px_rgba(59,130,246,0.3),0_0_18px_rgba(59,130,246,0.25)]",
    btn: "bg-blue-500 hover:bg-blue-400 hover:scale-105 active:scale-95",
    icon: "text-blue-50",
    iconBg: "bg-blue-500",
  },
  yellow: {
    ring: "ring-yellow-500",
    glow: "shadow-[0_0_0_1px_rgba(234,179,8,0.3),0_0_18px_rgba(234,179,8,0.25)]",
    btn: "bg-yellow-500 hover:bg-yellow-400 hover:scale-105 active:scale-95",
    icon: "text-yellow-50",
    iconBg: "bg-yellow-500",
  },
  pink: {
    ring: "ring-pink-500",
    glow: "shadow-[0_0_0_1px_rgba(236,72,153,0.3),0_0_18px_rgba(236,72,153,0.25)]",
    btn: "bg-pink-500 hover:bg-pink-400 hover:scale-105 active:scale-95",
    icon: "text-pink-50",
    iconBg: "bg-pink-500",
  },
} as const;

function MissionIcon({
  kind,
  className,
}: {
  kind: MissionKey;
  className?: string;
}) {
  const cn = `h-6 w-6 ${className ?? ""}`;

  if (kind === "items") return <Box className={cn} />;
  if (kind === "me") return <User className={cn} />;
  if (kind === "history") return <Clock className={cn} />;
  return <Layers className={cn} />;
}

function MissionBadgeIcon() {
  return (
    <span className="relative inline-flex h-12 w-12 items-center justify-center rounded-full bg-emerald-700 shadow-[0_0_0_2px_rgba(16,185,129,0.25),0_0_8px_rgba(16,185,129,0.18)]">
      <span className="absolute inset-0 rounded-full bg-gradient-to-b from-white/15 to-transparent" />
      <Box className="relative h-6 w-6 text-emerald-100" />
    </span>
  );
}

function getMissionCategoryStyle(categoryName: string) {
  const name = (categoryName);
  const style = getCategoryStyle(name);
  return { Icon: style.icon, color: style.color };
}

export default function Page() {
  const router = useRouter();

  // 왼쪽 카드(고정 UI)
  const cards: MissionCard[] = [
    { key: "items", title: "아이템 관리", subtitle: "아이템을 등록, 조회, 수정, 삭제하세요", action: "미션 시작", color: "red" },
    { key: "me", title: "내 정보", subtitle: "내 정보를 조회하고 수정하세요", action: "미션 시작", color: "blue" },
    { key: "history", title: "이력 조회", subtitle: "이력을 확인하세요", action: "미션 시작", color: "yellow" },
    { key: "categories", title: "카테고리", subtitle: "카테고리를 관리하세요", action: "미션 시작", color: "pink" },
  ];

  // 오른쪽 오늘의 미션(서버에서 받아옴)
  const [today, setToday] = useState<TodayMission[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  // 카드 클릭 -> 해당 페이지 이동
  const onCardClick = (key: MissionKey) => {
    if (key === "items") router.push("/items");
    if (key === "me") router.push("/me");
    if (key === "history") router.push("/history");
    if (key === "categories") router.push("/categories");
  };

  // "로그인 유저의 아이템 목록 조회 -> D-7 이하만 today로 매핑
  useEffect(() => {
    (async () => {
      try {
        setIsLoading(true);
        setLoadError(null);

        const res = await fetch(`${API_BASE}/api/v1/items`, {
          method: "GET",
          credentials: "include",
        });

        if (res.status === 401) {
          router.replace("/login");
          return;
        }

        if (!res.ok) throw new Error(`items load failed: ${res.status}`);

        const json = (await res.json()) as RsData<ItemSummaryApi[]>;
        const items = json?.data ?? [];

        const missions: TodayMission[] = items
          .filter((it) => it.isActive !== false)
          .filter((it) => it.dDay <= 7)
          .map((it) => ({
            id: String(it.id),
            title: it.name,
            category: it.categoryName ?? "",
            desc: it.dDay >= 0 ? `교체까지 ${it.dDay}일 남음` : `교체일 ${Math.abs(it.dDay)}일 지남`,
            dueText: it.nextReplacementDate ?? "-",
            dDay: it.dDay,
          }));

        setToday(missions);
      } catch (e: any) {
        setLoadError(e?.message ?? "load error");
      } finally {
        setIsLoading(false);
      }
    })();
  }, [router]);

  return (
    <div className="min-h-screen bg-[#070a12] text-white">

      <main className="mx-auto w-full max-w-6xl px-6 pb-14 pt-8">
        <div className="mb-8 text-center">
            <h2
              className="inline-block text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-green-500 to-blue-500 mb-2">
              레인저 미션 센터
            </h2>
          <div className="mt-1 text-xs text-white/70">원하는 미션을 선택하세요</div>
        </div>

        <div className="grid gap-6 lg:grid-cols-2">
          {/* 좌측 블럭 */}
          <section className="grid gap-5">
            {cards.map((c) => (
              <div key={c.key} className={`rounded-2xl bg-black/60 p-6 ring-3 ${COLOR[c.color].ring} ${COLOR[c.color].glow} backdrop-blur`}>
                <div className="flex justify-start">
                  <span className={`inline-flex h-11 w-11 items-center justify-center rounded-full ring-2 ring-white/10 ${COLOR[c.color].iconBg}`}>
                    <MissionIcon kind={c.key} className={`${COLOR[c.color].icon} drop-shadow`} />
                  </span>
                </div>

                <div className="mt-4">
                  <div className="text-sm font-semibold">{c.title}</div>
                  <div className="mt-2 text-xs leading-relaxed text-white/60">{c.subtitle}</div>
                </div>

                <button
                  onClick={() => onCardClick(c.key)}
                  className={`mt-6 w-full rounded-lg px-4 py-2 text-xs font-semibold text-white transition ${COLOR[c.color].btn}`}
                >
                  {c.action}
                </button>
              </div>
            ))}
          </section>

          {/* 우측 블럭 */}
          <section className="rounded-2xl bg-black/60 p-6 ring-3 ring-emerald-500/60 shadow-[0_0_0_1px_rgba(16,185,129,0.25),0_0_18px_rgba(16,185,129,0.2)] backdrop-blur">
            <div className="flex flex-col items-start">
              <MissionBadgeIcon />
              <div className="mt-3 text-sm font-semibold">오늘의 미션</div>
              <div className="mt-1 text-xs text-white/60">교체해야 할 미션 리스트</div>
              <div className="mt-3 text-xs text-white/60">
                총 <span className="font-semibold text-white/90">{today.length}</span>개
              </div>
            </div>

            <div className="mt-5 space-y-3">
              {/* 로딩/에러/빈 상태 */}
              {isLoading && (
                <div className="rounded-xl bg-white/5 p-4 text-sm text-white/60 ring-1 ring-white/15">
                  불러오는 중...
                </div>
              )}

              {loadError && (
                <div className="rounded-xl bg-red-500/10 p-4 text-sm text-red-200 ring-1 ring-red-500/30">
                  불러오기 실패: {loadError}
                </div>
              )}

              {!isLoading && !loadError && today.length === 0 && (
                <div className="rounded-xl bg-white/5 p-4 text-sm text-white/60 ring-1 ring-white/15">
                  D-7 이하 아이템이 없어요.
                </div>
              )}

              {/* 리스트 */}
              {!isLoading &&
                !loadError &&
                today.map((m) => (
                  <div key={m.id} className="flex items-center justify-between rounded-xl bg-white/5 p-4 ring-1 ring-white/15">
                    <div className="flex items-center gap-3">
                      {(() => {
                        const { Icon, color } = getMissionCategoryStyle(m.category);

                        return (
                          <span
                            className="inline-flex h-9 w-9 items-center justify-center rounded-full ring-1"
                            style={{
                              backgroundColor: color,
                              border: `1px solid ${color}`,
                              boxShadow: `0 0 10px ${color}55`,
                            }}
                          >
                            <Icon className="h-5 w-5 text-white" />
                          </span>
                        );
                      })()}

                      <div>
                        <div className="text-sm font-semibold">{m.title}</div>

                        <div className="mt-1 text-xs text-white/50">
                          {m.category && (
                            <>
                              <span className="text-emerald-100/90">{m.category}</span>
                              <span className="mx-2 text-white/25">|</span>
                            </>
                          )}
                          <span>{m.dueText}</span>
                        </div>
                      </div>
                    </div>

                    {/* D-day 표시 뱃지 */}
                    {m.dDay !== undefined && (
                      <span
                        className={`rounded-full px-3 py-1.5 text-xs font-semibold text-white ring-1 ${
                          m.dDay >= 0
                            ? "bg-[#22C55E] ring-[#22C55E]/40"   // 남음(초록) dDay포함
                            : "bg-[#DC2626] ring-[#DC2626]/40"   // 지남(빨강)
                        }`}
                      >
                        {m.dDay === 0 ? "D-Day" : m.dDay > 0 ? `D-${m.dDay}` : `D+${Math.abs(m.dDay)}`}
                      </span>
                    )}
                  </div>
                ))
              }
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
