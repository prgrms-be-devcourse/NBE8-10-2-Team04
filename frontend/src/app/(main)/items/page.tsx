'use client';

import { useState } from 'react';
import { ItemCard } from '@/components/items/ItemCard';
import { CategorySelector } from '@/components/items/CategorySelector';
import { DeleteItemDialog } from '@/components/items/DeleteItemDialog';
import { useCategories } from '@/hooks/useCategories';
import { useItems } from '@/hooks/useItems';
import ItemModifyModal from '@/components/ItemModifyModal';

export default function ItemsPage() {
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
  const [selectedItemId, setSelectedItemId] = useState<number | null>(null); // 수정하고자 하는 itemId
  const [isModifyModalOpen, setIsModifyModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  const { categories, loading: loadingCategories, error: categoriesError } = useCategories();
  const {
    items,
    loading: loadingItems,
    error: itemsError,
    deleteItem,
    toggleItemActive,
    refetch
  } = useItems(selectedCategoryId);

  const handleDeleteClick = (id: number) => {
    setDeleteId(id);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (deleteId === null) return;

    try {
      const success = await deleteItem(deleteId);
      if (!success) {
        alert('삭제 실패');
      }
    } catch (err) {
      alert('삭제 실패');
    } finally {
      setIsDeleteDialogOpen(false);
      setDeleteId(null);
    }
  };

  const handleReplace = async (id: number) => {
    // TODO: 교체 로직
  };

  const handleEdit = (id: number) => {
    // 수정하고자 하는 itemId를 상태에 저장
    setSelectedItemId(id);
    // 모달 열기
    setIsModifyModalOpen(true);
  };

  const isLoading = loadingCategories || loadingItems;
  const errorMsg = categoriesError || itemsError;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0b0f1a] via-[#0a1020] to-[#070b14] text-white">
      <div className="mx-auto max-w-6xl px-6 py-10">
        {/* 제목 */}
        <header className="text-center">
          <h1 className="text-3xl font-extrabold tracking-tight text-red-500">아이템 관리</h1>
          <p className="mt-2 text-sm text-white/60">레인저 장비를 관리하세요</p>
        </header>

        {/* 카테고리 필터 */}
        <div className="mt-8 flex justify-start">
          <CategorySelector
            categories={categories}
            selectedId={selectedCategoryId}
            onSelect={setSelectedCategoryId}
            disabled={loadingCategories}
          />
        </div>

        {/* 로딩/에러 상태 */}
        {isLoading && <div className="mt-10 text-center text-white/70">불러오는 중...</div>}
        {errorMsg && <div className="mt-10 text-center text-red-300">{errorMsg}</div>}

        {/* 아이템 그리드 */}
        {!isLoading && !errorMsg && (
          <div className="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {items.map((item) => (
              <ItemCard
                key={item.id}
                item={item}
                onToggleActive={toggleItemActive}
                onReplace={handleReplace}
                onDelete={handleDeleteClick}
                onEdit={handleEdit}
              />
            ))}
          </div>
        )}
      </div>

      {/* 삭제 확인 모달 */}
      <DeleteItemDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen} onConfirm={confirmDelete} />

      {/* 수정 모달 */}
      {(selectedItemId !== null && isModifyModalOpen) && (
        <ItemModifyModal
          itemId={selectedItemId}
          onClose={() => {
            setIsModifyModalOpen(false);
            setSelectedItemId(null);
          }}
          onUpdate={() => refetch()} // 수정 후 최신 데이터를 다시 불러옴
        />
      )}
    </div>
  );
}
