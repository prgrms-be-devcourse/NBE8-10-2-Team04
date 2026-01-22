// src/app/components/icons/MissionIcons.tsx
import React from "react";

export type MissionKey = "items" | "me" | "history" | "categories";

export function IconBox(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 2 3 7l9 5 9-5-9-5Z" />
      <path d="M3 7v10l9 5 9-5V7" />
      <path d="M12 12v10" />
    </svg>
  );
}

export function IconUser(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M20 21a8 8 0 1 0-16 0" />
      <path d="M12 13a4 4 0 1 0-4-4 4 4 0 0 0 4 4Z" />
    </svg>
  );
}

export function IconClock(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 22a10 10 0 1 0-10-10 10 10 0 0 0 10 10Z" />
      <path d="M12 6v6l4 2" />
    </svg>
  );
}

export function IconLayers(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" {...props}>
      <path d="M12 2 2 7l10 5 10-5-10-5Z" />
      <path d="M2 12l10 5 10-5" />
      <path d="M2 17l10 5 10-5" />
    </svg>
  );
}

export function MissionIcon({
  kind,
  className,
}: {
  kind: MissionKey;
  className?: string;
}) {
  const common = `h-6 w-6 ${className ?? ""}`;

  if (kind === "items") return <IconBox className={common} />;
  if (kind === "me") return <IconUser className={common} />;
  if (kind === "history") return <IconClock className={common} />;
  return <IconLayers className={common} />;
}

export function MissionBadgeIcon() {
  return (
    <span className="relative inline-flex h-12 w-12 items-center justify-center rounded-full bg-emerald-700 shadow-[0_0_0_2px_rgba(16,185,129,0.25),0_0_8px_rgba(16,185,129,0.18)]">
      <span className="absolute inset-0 rounded-full bg-gradient-to-b from-white/15 to-transparent" />
      <svg
        viewBox="0 0 24 24"
        className="relative h-6 w-6 text-emerald-100"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
      >
        <path d="M12 2 3 7l9 5 9-5-9-5Z" />
        <path d="M3 7v10l9 5 9-5V7" />
        <path d="M12 12v10" />
      </svg>
    </span>
  );
}
