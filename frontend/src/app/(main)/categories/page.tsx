"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import type { LucideIcon } from "lucide-react";
import {
  ArrowLeft,
  Grid,
  Package,
  Home,
  Bath,
  Utensils,
  Sparkles,
  PawPrint,
  Car,
  Laptop,
  Briefcase,
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

type CategoryApi = {
  id: number | string;
  name: string;
};

type CategoryUI = {
  id: string;
  name: string;
  description: string;
  count: number;
  color: string;
  icon: LucideIcon;
};

function getIndexById(id: number | string, length: number) {
  const n = Number(id);
  if (Number.isNaN(n)) return 0;
  return (n - 1) % length; 
}

const COLOR_PALETTE = [
  "from-red-600 to-red-700",       // 0 → 빨강
  "from-yellow-500 to-yellow-600", // 1 → 노랑
  "from-green-600 to-green-700",   // 2 → 초록
  "from-blue-600 to-blue-700",     // 3 → 파랑
  "from-pink-600 to-pink-700",     // 4 → 분홍
] as const;

function getCategoryColor(id: number | string) {
  return COLOR_PALETTE[getIndexById(id, COLOR_PALETTE.length)];
}

const ICONS = [
  Home,
  Bath,
  Utensils,
  Sparkles,
  PawPrint,
  Car,
  Laptop,
  Briefcase,
] as const;

function getCategoryIcon(id: number | string) {
  return ICONS[getIndexById(id, ICONS.length)];
}


export default function CategoriesPage() {
  const router = useRouter();

  const [categories, setCategories] = useState<CategoryUI[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    const run = async () => {
      try {
        setLoading(true);
        setErrorMsg(null);

        const res = await fetch("/api/categories", { cache: "no-store" });
        const body: RsData<CategoryApi[]> = await res.json();

        if (!res.ok) {
          throw new Error(body?.message ?? "카테고리 조회 실패");
        }

        const ui: CategoryUI[] = (body.data ?? []).map((c) => ({
          id: String(c.id),
          name: c.name,
          description: "카테고리 설명",
          count: 0,
          icon: getCategoryIcon(c.id),
          color: getCategoryColor(c.id),
        }));


        setCategories(ui);
      } catch (e: any) {
        setErrorMsg(e?.message ?? "카테고리 조회 중 오류가 발생했어요.");
      } finally {
        setLoading(false);
      }
    };

    run();
  }, []);

  const totalCount = useMemo(
    () => categories.reduce((sum, cat) => sum + (cat.count ?? 0), 0),
    [categories]
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-pink-600 via-purple-600 to-blue-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={() => router.back()}
              variant="outline"
              className="bg-pink-600 hover:bg-pink-700 text-white border-pink-400"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              돌아가기
            </Button>
          </div>
        </div>
      </div>

      {/* Main */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-pink-500 to-purple-500 mb-2">
            카테고리 관리
          </h2>
          <p className="text-gray-400">아이템 카테고리를 확인하세요</p>
        </div>

        {loading && (
          <div className="text-center text-gray-300 py-10">불러오는 중...</div>
        )}

        {errorMsg && (
          <div className="text-center text-red-300 py-10">
            {errorMsg}
            <div className="mt-4">
              <Button onClick={() => location.reload()} className="bg-white/10 hover:bg-white/20">
                새로고침
              </Button>
            </div>
          </div>
        )}

        {!loading && !errorMsg && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {categories.map((category) => {
                const Icon = category.icon;
                return (
                  <Card
                    key={category.id}
                    className="bg-black/80 backdrop-blur-sm border-4 border-white/20 hover:border-white/40 hover:scale-105 transition-all shadow-2xl"
                  >
                    <CardHeader>
                      <div
                        className={`w-20 h-20 rounded-full bg-gradient-to-br ${category.color} flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg`}
                      >
                        <Icon className="h-10 w-10 text-white" />
                      </div>
                      <CardTitle className="text-white text-center text-2xl">
                        {category.name}
                      </CardTitle>
                      <CardDescription className="text-gray-400 text-center">
                        {category.description}
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="text-center">
                        <div
                          className={`inline-flex items-center justify-center w-full py-3 rounded-lg bg-gradient-to-r ${category.color} border-2 border-white/30`}
                        >
                          <Package className="h-5 w-5 text-white mr-2" />
                          <span className="text-white text-xl">
                            {category.count}개 아이템
                          </span>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>

            <Card className="mt-8 bg-black/80 backdrop-blur-sm border-4 border-gradient shadow-2xl">
              <CardHeader>
                <div className="w-16 h-16 rounded-full bg-gradient-to-br from-purple-600 to-pink-600 flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg">
                  <Grid className="h-8 w-8 text-white" />
                </div>
                <CardTitle className="text-white text-center">전체 통계</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                  {categories.map((category) => (
                    <div
                      key={category.id}
                      className="text-center p-4 bg-gray-900/50 rounded-lg border border-gray-700"
                    >
                      <p className="text-gray-400 text-sm mb-1">{category.name}</p>
                      <p className="text-2xl text-white">{category.count}</p>
                    </div>
                  ))}
                </div>

                <div className="mt-6 text-center">
                  <p className="text-gray-400 mb-2">총 아이템 수</p>
                  <p className="text-5xl font-black text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-yellow-500 to-pink-500">
                    {totalCount}
                  </p>
                </div>
              </CardContent>
            </Card>
          </>
        )}
      </div>
    </div>
  );
}
