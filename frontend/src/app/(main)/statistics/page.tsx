/**
 * 통계 페이지
 */
'use client';

import { BarChart3, ArrowLeft } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useCategoryStatistics } from '@/hooks/useCategoryStatistics';
import { getCategoryStyle } from '@/lib/category-styles';

export default function StatisticsPage() {
  const router = useRouter();
  const { statistics, loading, error } = useCategoryStatistics();

  if (loading) {
    return (
      <div className="min-h-screen bg-[#070a12] text-white flex items-center justify-center">
        <div className="text-white/60">통계 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#070a12] text-white">
      {/* Header */}
      <div className="mx-auto max-w-6xl px-6 py-4">
        <Button
          onClick={() => router.push('/')}
          variant="outline"
          className="bg-purple-600 hover:bg-purple-700 text-white border-purple-400"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          돌아가기
        </Button>
      </div>

      {/* Main Content */}
      <main className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-purple-500 to-pink-500 mb-2">
            카테고리별 통계
          </h2>
          <p className="text-white/60 text-sm">카테고리별 평균 사용 기간을 확인하세요</p>
        </div>

        {error && (
          <div className="rounded-xl bg-red-500/10 p-4 text-sm text-red-200 ring-1 ring-red-500/30 mb-6">{error}</div>
        )}

        {!error && statistics.length === 0 && (
          <div className="rounded-xl bg-white/5 p-8 text-center text-white/60 ring-1 ring-white/15">
            통계 데이터가 없습니다. 아이템을 교체한 후 확인해보세요.
          </div>
        )}

        {!error && statistics.length > 0 && (
          <div className="grid gap-6 lg:grid-cols-2">
            {/* 카드 형태로 표시 */}
            {statistics.map((stat) => {
              const style = getCategoryStyle(stat.categoryName);
              const CategoryIcon = style.icon;

              return (
                <Card
                  key={stat.categoryId}
                  className="bg-black/60 border-2 ring-1 ring-white/10 backdrop-blur"
                  style={{ borderColor: `${style.color}40` }}
                >
                  <CardHeader>
                    <div className="flex items-center gap-3">
                      <div
                        className="flex h-12 w-12 items-center justify-center rounded-full"
                        style={{
                          backgroundColor: style.color,
                          boxShadow: `0 0 15px ${style.color}55`,
                        }}
                      >
                        <CategoryIcon className="h-6 w-6 text-white" />
                      </div>
                      <CardTitle className="text-white">{stat.categoryName}</CardTitle>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="text-center">
                      <div className="text-4xl font-black text-white">
                        {stat.averageUsageDays.toFixed(1)}
                        <span className="text-xl ml-2 text-white/70">일</span>
                      </div>
                      <div className="mt-2 text-sm text-white/60">평균 사용 기간</div>
                    </div>

                    {/* 프로그레스 바 (선택사항) */}
                    <div className="mt-4">
                      <div className="h-2 rounded-full bg-white/10 overflow-hidden">
                        <div
                          className="h-full rounded-full transition-all"
                          style={{
                            width: `${Math.min((stat.averageUsageDays / 365) * 100, 100)}%`,
                            backgroundColor: style.color,
                          }}
                        />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}

        {/* 요약 통계 */}
        {statistics.length > 0 && (
          <Card className="mt-6 bg-black/60 border-2 border-purple-500/40 ring-1 ring-white/10 backdrop-blur">
            <CardHeader>
              <div className="flex items-center gap-3">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-purple-600">
                  <BarChart3 className="h-6 w-6 text-white" />
                </div>
                <CardTitle className="text-white">전체 요약</CardTitle>
              </div>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-3 gap-4 text-center">
                <div>
                  <div className="text-2xl font-bold text-white">{statistics.length}</div>
                  <div className="text-xs text-white/60 mt-1">카테고리 수</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-white">
                    {(statistics.reduce((sum, s) => sum + s.averageUsageDays, 0) / statistics.length).toFixed(1)}
                  </div>
                  <div className="text-xs text-white/60 mt-1">전체 평균 (일)</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-white">
                    {Math.max(...statistics.map((s) => s.averageUsageDays)).toFixed(1)}
                  </div>
                  <div className="text-xs text-white/60 mt-1">최대값 (일)</div>
                </div>
              </div>
            </CardContent>
          </Card>
        )}
      </main>
    </div>
  );
}
