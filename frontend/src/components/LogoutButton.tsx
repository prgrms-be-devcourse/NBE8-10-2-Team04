"use client";

import { useRouter } from "next/navigation";

export default function LogoutButton() {
  const router = useRouter();

  const onLogout = async () => {
    const ok = window.confirm("로그아웃 하시겠습니까?");
    if (!ok) return;

    await fetch("/api/auth/logout", { method: "POST" });

    // route.ts에서 redirect("/login") 해도 되지만
    // fetch로 호출하면 페이지 이동은 클라에서 하는 게 제일 안전함
    router.push("/login");
    router.refresh();
  };

  return (
    <button
      type="button"
      onClick={onLogout}
      className="rounded-full bg-red-500 px-3 py-1 text-xs font-semibold text-white"
    >
      로그아웃
    </button>
  );
}
