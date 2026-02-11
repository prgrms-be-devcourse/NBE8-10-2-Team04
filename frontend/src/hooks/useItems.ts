/**
 * 아이템 목록을 조회하고 관리하는 커스텀 훅
 */
import { useState, useEffect, useCallback } from 'react';
import type { ItemSummary } from '@/components/items/ItemCard';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

export function useItems(categoryId: number | null) {
  const [items, setItems] = useState<ItemSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchItems = useCallback(async (categoryId: number | null) => {
    try {
      setLoading(true);
      setError(null);

      const qs = categoryId ? `?categoryId=${categoryId}` : '';
      const res = await fetch(`${API_BASE_URL}/api/v1/items${qs}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store',
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);

      const body = (await res.json()) as RsData<ItemSummary[]>;
      setItems(body.data ?? []);
    } catch {
      setError('아이템 목록을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchItems(categoryId);
  }, [categoryId, fetchItems]);

  const deleteItem = useCallback(async (id: number) => {
    const res = await fetch(`${API_BASE_URL}/api/v1/items/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (res.ok) {
      setItems((prev) => prev.filter((item) => item.id !== id));
      return true;
    }
    return false;
  }, []);

  const toggleItemActive = useCallback(async (id: number, isActive: boolean) => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/items/${id}/toggle-active`, {
        method: 'PUT',
        credentials: 'include',
      });

      if (res.ok) {
        // 로컬 상태 업데이트
        setItems((prev) => prev.map((item) => (item.id === id ? { ...item, isActive } : item)));
        return true;
      }
      return false;
    } catch (error) {
      console.error('Toggle active failed:', error);
      return false;
    }
  }, []);

  const refetch = useCallback(() => {
    fetchItems(categoryId);
  }, [categoryId, fetchItems]);

  // 아이템을 교체하는 함수
  const replaceItem = useCallback(
    async (id: number) => {
      const res = await fetch(`${API_BASE_URL}/api/v1/items/${id}/replace`, {
        method: 'PUT',
        credentials: 'include',
      });

      // 교체 성공 시 목록 새로고침
      if (res.ok) {
        await fetchItems(categoryId);
        return true;
      }
      return false;
    },
    [categoryId, fetchItems],
  );

  return {
    items,
    loading,
    error,
    deleteItem,
    toggleItemActive,
    replaceItem,
    refetch,
  };
}
