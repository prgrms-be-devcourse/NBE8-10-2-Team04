'use client';

import { useEffect, useMemo, useState } from 'react';
import { Grid, Package, Star } from 'lucide-react';
import { getCategoryStyle, getIconBgClass } from '@/lib/category-styles';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { PageHeader } from '@/components/common/PageHeader';
import { useRouter } from 'next/navigation';

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

type CategoryApi = {
  id: number | string;
  name: string;
  itemCount: number;
};

type CategoryUI = {
  id: string;
  name: string;
  count: number;
};

// 가장 자주 교체한 아이템 타입 추가
type MostReplacedItem = {
  itemId: number;
  itemName: string;
  categoryName: string;
  replacementCount: number;
  imgUrl: string;
};

export default function CategoriesPage() {
  const router = useRouter();
  const [categories, setCategories] = useState<CategoryUI[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // 가장 자주 교체한 아이템 상태 추가
  const [mostReplacedItems, setMostReplacedItems] = useState<MostReplacedItem[]>([]);
  const [rankingLoading, setRankingLoading] = useState(true);
  const [rankingError, setRankingError] = useState<string | null>(null);

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
          count: c.itemCount ?? 0,
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

  // 가장 자주 교체한 아이템 조회
  useEffect(() => {
    const fetchMostReplacedItems = async () => {
      try {
        setRankingLoading(true);
        setRankingError(null);

        const res = await fetch('/api/v1/items/statistics/most-replaced?limit=5', {
          method: 'GET',
          credentials: 'include',
          cache: 'no-store',
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const body = (await res.json()) as RsData<MostReplacedItem[]>;
        setMostReplacedItems(body.data ?? []);
      } catch (e: any) {
        setRankingError('순위를 불러오지 못했습니다.');
      } finally {
        setRankingLoading(false);
      }
    };

    fetchMostReplacedItems();
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

                const iconBg = getIconBgClass(category.name); // 아이콘 배경 (진한색)

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
                <div className="grid grid-cols-3 md:grid-cols-5 gap-4">
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

            {/* 가장 자주 교체하는 아이템 TOP 5 카드 추가 */}
            <Card className="mt-8 bg-black/80 backdrop-blur-sm border-4 border-gradient shadow-2xl">
              <CardHeader>
                <div className="w-16 h-16 rounded-full bg-gradient-to-br from-yellow-500 to-red-600 flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg">
                  <Star className="h-8 w-8 text-white" />
                </div>
                <CardTitle className="text-white text-center">가장 자주 교체하는 아이템 TOP 5</CardTitle>
                <CardDescription className="text-gray-400 text-center">교체 횟수가 많은 순서대로 정렬</CardDescription>
              </CardHeader>
              <CardContent>
                {rankingLoading ? (
                  <p className="text-center text-gray-400">로딩 중...</p>
                ) : rankingError ? (
                  <p className="text-center text-red-400">{rankingError}</p>
                ) : mostReplacedItems.length === 0 ? (
                  <p className="text-center text-gray-400">아직 교체 이력이 없습니다.</p>
                ) : (
                  <div className="space-y-3">
                    {mostReplacedItems.map((item, index) => (
                      <div
                        key={item.itemId}
                        className="flex items-center justify-between p-4 bg-gray-900/50 rounded-lg border border-gray-700 hover:border-yellow-500/50 transition-colors"
                      >
                        <div className="flex items-center gap-4">
                          <div
                            className={`
                            w-10 h-10 rounded-full flex items-center justify-center font-black text-xl
                            ${index === 0 ? 'bg-gradient-to-br from-yellow-400 to-yellow-600 text-white' : ''}
                            ${index === 1 ? 'bg-gradient-to-br from-gray-300 to-gray-500 text-white' : ''}
                            ${index === 2 ? 'bg-gradient-to-br from-orange-400 to-orange-600 text-white' : ''}
                            ${index >= 3 ? 'bg-gray-700 text-gray-300' : ''}
                          `}
                          >
                            {index + 1}
                          </div>
                          {item.imgUrl && (
                            <img
                              src={item.imgUrl}
                              alt={item.itemName}
                              className="w-12 h-12 rounded-lg object-cover border-2 border-white/20"
                            />
                          )}
                          <div>
                            <p className="text-white font-semibold">{item.itemName}</p>
                            <p className="text-sm text-gray-400">{item.categoryName}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="text-2xl font-black text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-red-500">
                            {item.replacementCount}
                          </p>
                          <p className="text-xs text-gray-400">교체</p>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </>
        )}
      </div>
    </div>
  );
}
