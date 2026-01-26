// hooks/useItems.ts
import { useState, useEffect, useCallback } from 'react';
import type { ItemSummary } from '@/components/items/ItemCard';

const API_BASE = 'http://localhost:8080';

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
      const res = await fetch(`${API_BASE}/api/v1/items${qs}`, {
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
    const res = await fetch(`${API_BASE}/api/v1/items/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (res.ok) {
      setItems((prev) => prev.filter((item) => item.id !== id));
      return true;
    }
    return false;
  }, []);

  const toggleItemActive = useCallback((id: number, isActive: boolean) => {
    setItems((prev) => prev.map((item) => (item.id === id ? { ...item, isActive } : item)));
  }, []);

  const refetch = useCallback(() => {
    fetchItems(categoryId);
  }, [categoryId, fetchItems]);

  return {
    items,
    loading,
    error,
    deleteItem,
    toggleItemActive,
    refetch,
  };
}
