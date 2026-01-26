"use client";

import { useEffect, useMemo, useState } from "react";
import { ItemCard, type ItemSummary } from "@/components/items/ItemCard";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type Category = {
  id: number;
  name: string;
};

const API_BASE = "http://localhost:8080";

export default function Page() {
  // 카테고리
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);

  // 아이템
  const [items, setItems] = useState<ItemSummary[]>([]);

  // 로딩/에러
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [loadingItems, setLoadingItems] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // 카테고리 불러오기
  useEffect(() => {
    const run = async () => {
      try {
        setLoadingCategories(true);
        setErrorMsg(null);

        const res = await fetch(`${API_BASE}/api/v1/categories`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const body = (await res.json()) as RsData<Category[]>;
        setCategories(body.data ?? []);
      } catch {
        setErrorMsg("카테고리를 불러오지 못했습니다.");
      } finally {
        setLoadingCategories(false);
      }
    };

    run();
  }, []);

  // 아이템 불러오기(categoryId 따라 분기)
  const fetchItems = async (categoryId: number | null) => {
    const qs = categoryId ? `?categoryId=${categoryId}` : "";
    const res = await fetch(`${API_BASE}/api/v1/items${qs}`, {
      method: "GET",
      credentials: "include",
      cache: "no-store",
    });

    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    const body = (await res.json()) as RsData<ItemSummary[]>;
    return body.data ?? [];
  };

  // category 변경될 때마다 items 재조회
  useEffect(() => {
    const run = async () => {
      try {
        setLoadingItems(true);
        setErrorMsg(null);

        const data = await fetchItems(selectedCategoryId);
        setItems(data);
      } catch {
        setErrorMsg("아이템 목록을 불러오지 못했습니다.");
      } finally {
        setLoadingItems(false);
      }
    };

    run();
  }, [selectedCategoryId]);

  // 교체: PUT /api/v1/items/{id}/replace → 성공하면 목록 다시 불러오기
  const handleReplace = async (id: number) => {
    // TODO : 교체
  };

  // 삭제: DELETE /api/v1/items/{id}
  const handleDelete = async (id: number) => {
    if (!confirm("삭제할까요?")) return;

    // TODO : 삭제
  };

  // 수정 (일단 알림)
  const handleEdit = (id: number) => {
    alert(`수정 클릭: ${id}`);
    // TODO: 수정 모달
  };

  // isActive 토글은 지금 백에 API가 없으니 UI만 반영(원하면 버튼 숨겨도 됨)
  const handleToggleActive = (id: number, next: boolean) => {
    setItems((prev) => prev.map((i) => (i.id === id ? { ...i, isActive: next } : i)));
  };

  const isLoading = loadingCategories || loadingItems;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0b0f1a] via-[#0a1020] to-[#070b14] text-white">
      <div className="mx-auto max-w-6xl px-6 py-10">
        {/* 제목 */}
        <div className="text-center">
          <h1 className="text-3xl font-extrabold tracking-tight text-red-500">
            아이템 관리
          </h1>
          <p className="mt-2 text-sm text-white/60">레인저 장비를 관리하세요</p>
        </div>

        {/* 카테고리 필터 */}
        <div className="mt-8 flex justify-start">
          <div className="w-52">
            <Select
              value={selectedCategoryId === null ? "all" : String(selectedCategoryId)}
              onValueChange={(v: string) => {
                if (v === "all") setSelectedCategoryId(null);
                else setSelectedCategoryId(Number(v));
              }}
              disabled={loadingCategories}
            >
              <SelectTrigger className="h-9 bg-white/5 text-white border-white/10">
                <SelectValue placeholder="카테고리 선택" />
              </SelectTrigger>

              <SelectContent className="bg-[#0b1224] text-white border-white/10">
                <SelectItem value="all">전체</SelectItem>
                {categories.map((c) => (
                  <SelectItem key={c.id} value={String(c.id)}>
                    {c.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* 로딩/에러 */}
        {isLoading && <div className="mt-10 text-center text-white/70">불러오는 중...</div>}
        {errorMsg && <div className="mt-10 text-center text-red-300">{errorMsg}</div>}

        {/* 그리드 */}
        {!isLoading && !errorMsg && (
          <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {items.map((item) => (
              <ItemCard
                key={item.id}
                item={item}
                onToggleActive={handleToggleActive}
                onReplace={handleReplace}
                onDelete={handleDelete}
                onEdit={handleEdit}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
