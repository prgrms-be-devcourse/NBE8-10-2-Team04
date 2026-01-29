/**
 * 카테고리별 평균 사용 기간 통계를 조회하는 커스텀 훅
 */
import { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8080';

export type CategoryStatistics = {
  categoryId: number;
  categoryName: string;
  averageUsageDays: number;
};

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

export function useCategoryStatistics() {
  const [statistics, setStatistics] = useState<CategoryStatistics[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchStatistics = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await fetch(`${API_BASE_URL}/api/v1/items/statistics/category-average`, {
          method: 'GET',
          credentials: 'include',
          cache: 'no-store',
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const body = (await res.json()) as RsData<CategoryStatistics[]>;
        setStatistics(body.data ?? []);
      } catch {
        setError('통계를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchStatistics();
  }, []);

  return { statistics, loading, error };
}
