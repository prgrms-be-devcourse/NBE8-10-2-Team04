'use client';

import { useEffect, useMemo, useState } from 'react';
import { Grid, Package} from 'lucide-react';
import {
  getCategoryStyle,
  getIconBgClass,
  getBgColorClass,
  getTextColorClass,
} from "@/lib/category-styles";


import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { PageHeader } from '@/components/common/PageHeader';

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
  count: number;
};

export default function CategoriesPage() {
  const [categories, setCategories] = useState<CategoryUI[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    const run = async () => {
      try {
        setLoading(true);
        setErrorMsg(null);

        const res = await fetch('/api/categories', { cache: 'no-store' });
        const body: RsData<CategoryApi[]> = await res.json();

        if (!res.ok) {
          throw new Error(body?.message ?? '카테고리 조회 실패');
        }

        const ui: CategoryUI[] = (body.data ?? []).map((c) => ({
          id: String(c.id),
          name: c.name,
          count: 0,
        }));

        setCategories(ui);
      } catch (e: any) {
        setErrorMsg(e?.message ?? '카테고리 조회 중 오류가 발생했어요.');
      } finally {
        setLoading(false);
      }
    };

    run();
  }, []);

  const totalCount = useMemo(() => categories.reduce((sum, cat) => sum + (cat.count ?? 0), 0), [categories]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <PageHeader variant="pink" />

      {/* Main */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-pink-500 to-purple-500 mb-2">
            카테고리 관리
          </h2>
          <p className="text-gray-400">아이템 카테고리를 확인하세요</p>
        </div>

        {loading && <div className="text-center text-gray-300 py-10">불러오는 중...</div>}

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
                const { icon: Icon } = getCategoryStyle(category.name);

                const iconBg = getIconBgClass(category.name);   // 아이콘 배경 (진한색)
                
                return (
                  <Card
                    key={category.id}
                    className="bg-black/80 backdrop-blur-sm border-4 border-white/20 hover:border-white/40 hover:scale-105 transition-all shadow-2xl"
                  >
                    <CardHeader>
                      <div
                        className={`w-20 h-20 rounded-full ${iconBg} flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg`}
                      >
                        <Icon className="h-10 w-10 text-white" />
                      </div>

                      <CardTitle className="text-white text-center text-2xl">{category.name}</CardTitle>
                    </CardHeader>

                    <CardContent>
                      <div className="text-center">
                        <div
                          className={`inline-flex items-center justify-center w-full py-3 rounded-lg ${iconBg} border-2 border-white/20`}
                        >
                          <Package className="h-5 w-5 text-white mr-2" />
                          <span className="text-white text-xl">{category.count}개 아이템</span>
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
                    <div key={category.id} className="text-center p-4 bg-gray-900/50 rounded-lg border border-gray-700">
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
