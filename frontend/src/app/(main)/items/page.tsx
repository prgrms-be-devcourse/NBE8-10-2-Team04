'use client';

import { useState } from 'react';
import { ItemCard } from '@/components/items/ItemCard';
import { CategorySelector } from '@/components/items/CategorySelector';
import { DeleteItemDialog } from '@/components/items/DeleteItemDialog';
import { PageHeader } from '@/components/common/PageHeader';
import { ReplaceItemDialog } from '@/components/items/ReplaceItemDialog';
import { useCategories } from '@/hooks/useCategories';
import { useItems } from '@/hooks/useItems';
import ItemModifyForm from '@/components/ItemModifyModal';

export default function ItemsPage() {
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
  const [selectedItemId, setSelectedItemId] = useState<number | null>(null); // 수정하고자 하는 itemId
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  // 교체 대기 중인 아이템 ID
  const [replaceId, setReplaceId] = useState<number | null>(null);

  // 교체 확인 다이얼로그 표시 여부
  const [isReplaceDialogOpen, setIsReplaceDialogOpen] = useState(false);

  const { categories, loading: loadingCategories, error: categoriesError } = useCategories();
  const {
    items,
    loading: loadingItems,
    error: itemsError,
    deleteItem,
    toggleItemActive,
    refetch
    replaceItem,
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

  /**
   * 교체 버튼 클릭 핸들러
   * - 바로 교체하지 않고 확인 다이얼로그를 표시
   */
  const handleReplaceClick = (id: number) => {
    setReplaceId(id);
    setIsReplaceDialogOpen(true);
  };

  /**
   * 교체 확인 핸들러
   * - 다이얼로그에서 '교체' 버튼을 눌렀을 때 실행
   */
  const confirmReplace = async () => {
    if (replaceId === null) return;

    try {
      const success = await replaceItem(replaceId);

      if (!success) {
        alert('교체 실패');
      } else {
        alert('교체되었습니다');
      }
    } catch (err) {
      alert('교체 실패');
    } finally {
      setIsReplaceDialogOpen(false);
      setReplaceId(null);
    }
  };

  const handleEdit = (id: number) => {
    // 수정하고자 하는 itemId를 상태에 저장
    setSelectedItemId(id);
    // 모달 열기
    setIsModalOpen(true);
  };

  const isLoading = loadingCategories || loadingItems;
  const errorMsg = categoriesError || itemsError;

  return (
    <div className="min-h-screen bg-gradient-to-b from-[#0b0f1a] via-[#0a1020] to-[#070b14] text-white">
      {/* Header */}
      <PageHeader variant="red" />

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
                onReplace={handleReplaceClick}
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
      {(selectedItemId !== null && isModalOpen) && (
        <ItemModifyForm
          itemId={selectedItemId}
          onClose={() => {
            setIsModalOpen(false);
            setSelectedItemId(null);
          }}
          onUpdate={() => refetch()} // 수정 후 최신 데이터를 다시 불러옴
        />
      )}
      {/* 교체 확인 다이얼로그 */}
      <ReplaceItemDialog open={isReplaceDialogOpen} onOpenChange={setIsReplaceDialogOpen} onConfirm={confirmReplace} />
    </div>
  );
}
