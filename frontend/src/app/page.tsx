"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

type MissionKey = "items" | "members" | "history" | "categories";

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
  done: boolean;
};

const COLOR = {
  red: {
    ring: "ring-red-500",
    glow: "shadow-[0_0_0_1px_rgba(239,68,68,0.3),0_0_18px_rgba(239,68,68,0.25)]",
    btn: "bg-red-500 hover:bg-red-500",
    icon: "text-red-50",
    iconBg: "bg-red-500",
  },
  blue: {
    ring: "ring-blue-500",
    glow: "shadow-[0_0_0_1px_rgba(59,130,246,0.3),0_0_18px_rgba(59,130,246,0.25)]",
    btn: "bg-blue-500 hover:bg-blue-500",
    icon: "text-blue-50",
    iconBg: "bg-blue-500",
  },
  yellow: {
    ring: "ring-yellow-500",
    glow: "shadow-[0_0_0_1px_rgba(234,179,8,0.3),0_0_18px_rgba(234,179,8,0.25)]",
    btn: "bg-yellow-500 hover:bg-yellow-500",
    icon: "text-yellow-50",
    iconBg: "bg-yellow-500",
  },
  pink: {
    ring: "ring-pink-500",
    glow: "shadow-[0_0_0_1px_rgba(236,72,153,0.3),0_0_18px_rgba(236,72,153,0.25)]",
    btn: "bg-pink-500 hover:bg-pink-500",
    icon: "text-pink-50",
    iconBg: "bg-pink-500",
  },
} as const;

function DotIcon({ tone }: { tone: "red" | "blue" | "yellow" | "pink" | "green" }) {
  const cls =
    tone === "green"
      ? "text-emerald-300"
      : tone === "red"
      ? "text-red-300"
      : tone === "blue"
      ? "text-blue-300"
      : tone === "yellow"
      ? "text-yellow-300"
      : "text-pink-300";

  return (
    <span className={`inline-flex h-8 w-8 items-center justify-center rounded-full bg-white/5 ring-1 ring-white/10 ${cls}`}>
      <span className="h-3 w-3 rounded-full bg-current shadow-[0_0_16px_rgba(255,255,255,0.15)]" />
    </span>
  );
}

function IconBox(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 2 3 7l9 5 9-5-9-5Z" />
      <path d="M3 7v10l9 5 9-5V7" />
      <path d="M12 12v10" />
    </svg>
  );
}

function IconUser(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M20 21a8 8 0 1 0-16 0" />
      <path d="M12 13a4 4 0 1 0-4-4 4 4 0 0 0 4 4Z" />
    </svg>
  );
}

function IconClock(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 22a10 10 0 1 0-10-10 10 10 0 0 0 10 10Z" />
      <path d="M12 6v6l4 2" />
    </svg>
  );
}

function IconLayers(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 2 2 7l10 5 10-5-10-5Z" />
      <path d="M2 12l10 5 10-5" />
      <path d="M2 17l10 5 10-5" />
    </svg>
  );
}

function MissionIcon({ kind, className }: { kind: MissionKey; className?: string }) {
  const common = `h-6 w-6 ${className ?? ""}`;
  if (kind === "items") return <IconBox className={common} />;
  if (kind === "members") return <IconUser className={common} />;
  if (kind === "history") return <IconClock className={common} />;
  return <IconLayers className={common} />;
}

function MissionBadgeIcon() {
  return (
    <span className="relative inline-flex h-12 w-12 items-center justify-center rounded-full bg-emerald-700 shadow-[0_0_0_2px_rgba(16,185,129,0.25),0_0_8px_rgba(16,185,129,0.18)]">
      <span className="absolute inset-0 rounded-full bg-gradient-to-b from-white/15 to-transparent" />
      <svg viewBox="0 0 24 24" className="relative h-6 w-6 text-emerald-100" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M12 2 3 7l9 5 9-5-9-5Z" />
        <path d="M3 7v10l9 5 9-5V7" />
        <path d="M12 12v10" />
      </svg>
    </span>
  );
}

export default function Page() {
  const router = useRouter();

  const cards: MissionCard[] = [
    {
      key: "items",
      title: "아이템 관리",
      subtitle: "아이템을 등록, 조회, 수정, 삭제하세요",
      action: "미션 시작",
      color: "red",
    },
    {
      key: "members",
      title: "멤버 관리",
      subtitle: "멤버를 추가하고 조회하세요",
      action: "미션 시작",
      color: "blue",
    },
    {
      key: "history",
      title: "이력 조회",
      subtitle: "이력을 확인하세요",
      action: "미션 시작",
      color: "yellow",
    },
    {
      key: "categories",
      title: "카테고리",
      subtitle: "카테고리를 관리하세요",
      action: "미션 시작",
      color: "pink",
    },
  ];

  const [today, setToday] = useState<TodayMission[]>([
    { id: "1", title: "칫솔", category: "욕실", desc: "카테고리: 욕실 소모", dueText: "2026-01-16", done: false },
    { id: "2", title: "칫솔", category: "욕실", desc: "카테고리: 욕실 소모", dueText: "2026-01-18", done: false },
    { id: "3", title: "칫솔", category: "욕실", desc: "카테고리: 욕실 소모", dueText: "2026-01-20", done: false },
    { id: "4", title: "칫솔", category: "욕실", desc: "카테고리: 욕실 소모", dueText: "2026-01-22", done: false },
    { id: "5", title: "칫솔", category: "욕실", desc: "카테고리: 욕실 소모", dueText: "2026-01-24", done: false },
  ]);

  const toggleDone = (id: string) => {
    setToday((prev) => prev.map((m) => (m.id === id ? { ...m, done: !m.done } : m)));
  };

  const onCardClick = (key: MissionKey) => {
    if (key === "items") router.push("/items");
    if (key === "members") router.push("/members");
    if (key === "history") router.push("/history");
    if (key === "categories") router.push("/categories");
  };

  return (
    <div className="min-h-screen bg-[#070a12] text-white">
      <div className="h-1 w-full bg-gradient-to-r from-red-500 via-blue-500 via-yellow-400 to-pink-500" />

      <div className="pointer-events-none fixed inset-0 -z-10">
        <div className="absolute left-1/2 top-16 h-[520px] w-[880px] -translate-x-1/2 rounded-full bg-gradient-to-r from-red-500/12 via-blue-500/12 to-pink-500/12 blur-3xl" />
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(255,255,255,0.08),transparent_60%)]" />
      </div>

      <main className="mx-auto w-full max-w-6xl px-6 pb-14 pt-8">
        <div className="mb-8 text-center">
          <div className="text-3xl font-extrabold tracking-tight text-red-300">레인저 미션 센터</div>
          <div className="mt-1 text-xs text-white/70">원하는 미션을 선택하세요</div>
        </div>

        <div className="grid gap-6 lg:grid-cols-2">
          <section className="grid gap-5">
            {/* 좌측 블럭 */}
            {cards.map((c) => (
              <div
                key={c.key}
                className={`rounded-2xl bg-black/60 p-6 ring-3 ${COLOR[c.color].ring} ${COLOR[c.color].glow} backdrop-blur`}
              >
                <div className="flex justify-start">
                  <span
                    className={`inline-flex h-11 w-11 items-center justify-center rounded-full ring-2 ring-white/10 ${COLOR[c.color].iconBg}`}
                  >
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
          <section className="rounded-2xl bg-black/60 p-6 ring-2 ring-emerald-500/60 shadow-[0_0_0_1px_rgba(16,185,129,0.25),0_0_18px_rgba(16,185,129,0.2)] backdrop-blur">
            <div className="flex flex-col items-start">
              <MissionBadgeIcon />
              <div className="mt-3 text-sm font-semibold">오늘의 미션</div>
              <div className="mt-1 text-xs text-white/60">교체해야 할 미션 리스트</div>
              <div className="mt-3 text-xs text-white/60">
                총 <span className="font-semibold text-white/90">{today.length}</span>개
              </div>
            </div>

            <div className="mt-5 space-y-3">
              {today.map((m) => (
                <div key={m.id} className="flex items-center justify-between rounded-xl bg-white/5 p-4 ring-1 ring-white/15">
                  <div className="flex items-center gap-3">
                    <span className="inline-flex h-9 w-9 items-center justify-center rounded-full bg-emerald-500/20 ring-1 ring-emerald-400/50">
                      <span className="h-3 w-3 rounded-full bg-emerald-200 shadow-[0_0_6px_rgba(16,185,129,0.4)]" />
                    </span>

                    <div>
                      <div className="text-sm font-semibold">{m.title}</div>
                      <div className="mt-1 text-xs text-white/50">
                        <span className="text-emerald-100/90">{m.desc}</span>
                        <span className="mx-2 text-white/25">|</span>
                        <span>{m.dueText}</span>
                      </div>
                    </div>
                  </div>

                  <button
                    onClick={() => toggleDone(m.id)}
                    className={`rounded-full px-3 py-1.5 text-xs font-semibold ring-1 transition ${
                      m.done
                        ? "bg-emerald-500/25 text-emerald-50 ring-emerald-500/40"
                        : "bg-emerald-500 text-white ring-emerald-400/80"
                    }`}
                  >
                    {m.done ? "완료" : "D-7"}
                  </button>
                </div>
              ))}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}
