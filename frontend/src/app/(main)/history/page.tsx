'use client';

import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { History as HistoryIcon, Calendar, Package } from 'lucide-react';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { PageHeader } from '@/components/common/PageHeader';

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type HistoryItem = {
  id: number | string;
  itemName: string;
  timestamp: string;
};

//실제값 사용하려면 주석 변경하면됨
//const API_BASE = "";
const API_BASE = "http://localhost:8080";

export default function Page() {
  const router = useRouter();

  const [items, setItems] = useState<HistoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    const run = async () => {
      try {
        setLoading(true);
        setErrorMsg(null);

        const res = await fetch(`${API_BASE}/api/v1/items/histories`, {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        });

        if (res.status === 401) {
          router.replace("/login");
          return;
        }

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const body = (await res.json()) as RsData<HistoryItem[]>;
        setItems(body.data ?? []);
      } catch {
        setErrorMsg("이력 목록을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    };

    run();
  }, [router]);

  const historyItems = useMemo(() => {
    return [...items].sort((a, b) => {
      const ta = new Date(a.startDate.replace(" ", "T")).getTime();
      const tb = new Date(b.startDate.replace(" ", "T")).getTime();
      return tb - ta;
    });
  }, [items]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="mx-auto max-w-7xl px-4 py-4">
        <Button
          onClick={() => router.back()}
          variant="outline"
          className="border-yellow-400 bg-yellow-600 text-white hover:bg-yellow-700">
          <ArrowLeft className="mr-2 h-4 w-4" />
          돌아가기
        </Button>
      </div>

      {/* Main */}
      <div className="mx-auto max-w-5xl px-4 py-8">
        <div className="mb-8 text-center">
          <h2 className="mb-2 bg-gradient-to-r from-yellow-500 to-orange-500 bg-clip-text text-4xl font-black text-transparent">
            전체 이력 조회
          </h2>
          <p className="text-gray-400">모든 아이템의 기록을 확인하세요</p>
        </div>

        <Card className="border-4 border-yellow-400 bg-black/80 shadow-2xl backdrop-blur-sm">
          <CardHeader>
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full border-4 border-white/30 bg-gradient-to-br from-yellow-600 to-yellow-700 shadow-lg">
              <HistoryIcon className="h-8 w-8 text-white" />
            </div>
            <CardTitle className="text-center text-white">전체 이력 조회</CardTitle>
            <CardDescription className="text-center text-gray-400">
              모든 아이템의 교체 이력을 확인하실 수 있습니다.
            </CardDescription>
          </CardHeader>

          <CardContent>
            <div className="space-y-4">
              {loading && (
                <div className="rounded-xl bg-white/5 p-4 text-sm text-white/60 ring-1 ring-white/15">
                  불러오는 중...
                </div>
              )}

              {!loading && errorMsg && (
                <div className="rounded-xl bg-red-500/10 p-4 text-sm text-red-200 ring-1 ring-red-500/30">
                  {errorMsg}
                </div>
              )}

              {!loading && !errorMsg && historyItems.length === 0 && (
                <div className="rounded-xl bg-white/5 p-4 text-sm text-white/60 ring-1 ring-white/15">
                  이력이 없습니다.
                </div>
              )}

              {!loading &&
                !errorMsg &&
                historyItems.map((item) => (
                  <div
                    key={String(item.id)}
                    className="rounded-lg border border-gray-700 bg-gray-900/50 p-4 transition-colors hover:border-yellow-500/50"
                  >
                    <div className="flex items-start gap-4">
                      <div className="flex h-10 w-10 flex-shrink-0 items-center justify-center 
                     rounded-full bg-green-600 ">
                        <Package className="h-6 w-6 text-white" />
                      </div>

                      {/* 텍스트 영역 */}
                      <div className="flex-1">
                        {/* 아이템명 */}
                        <div className="text-sm font-semibold text-white">
                          {item.itemName}
                        </div>

                        {/* 교체일 */}
                        <div className="mt-1 flex items-center gap-1 text-xs text-white/60">
                          <Calendar className="h-3.5 w-3.5" />
                          교체일: <span className="text-yellow-400">{item.timestamp.split(' ')[0]}</span>
                        </div>

                        {/* 사용기간 */}
                        <div className="mt-1 flex items-center gap-1 text-xs text-white/60">
                          <Clock className="h-3.5 w-3.5" />
                          사용기간:{" "}
                          {item.usedDays === null ? (
                            <span className="text-green-400">사용 중</span>
                          ) : (
                            <span className="text-gray-400">
                              {item.usedDays}일
                            </span>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
