/**
 * 카테고리 목록을 조회하는 커스텀 훅
 */
import { useState, useEffect } from 'react';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

type Category = {
  id: number;
  name: string;
};

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await fetch(`${API_BASE}/api/v1/categories`, {
          method: 'GET',
          credentials: 'include',
          cache: 'no-store',
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);

        const body = (await res.json()) as RsData<Category[]>;
        setCategories(body.data ?? []);
      } catch {
        setError('카테고리를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, []);

  return { categories, loading, error };
}
