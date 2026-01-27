'use client';

import { useParams, useRouter } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { PageHeader } from '@/components/common/PageHeader';

import { Activity, Tag, Package, Calendar, History } from 'lucide-react';
import ItemModifyForm from '@/components/ItemModifyModal';
import { DeleteItemDialog } from '@/components/items/DeleteItemDialog';

type ItemHistory = {
  id: number;
  startDate: string;
};

type ItemDetail = {
  id: number;
  name: string;
  categoryName: string | null;
  cycleDays: string | null;
  startDate: string | null;
  nextReplacementDate: string | null;
  dDay: number;
  isActive: boolean;
  imgUrl: string | null;
};

export default function ItemPage() {
  const router = useRouter();
  const { id: idStr } = useParams<{ id: string }>();
  const id = parseInt(idStr);

  const [item, setItem] = useState<ItemDetail | null>(null);
  const [itemHistories, setItemHistories] = useState<ItemHistory[]>([]);
  const [isItemLoading, setIsItemLoading] = useState(true);
  const [isItemHistoryLoading, setIsItemHistoryLoading] = useState(true);

  // 수정 모달
  const [isModalOpen, setIsModalOpen] = useState(false);

  // 삭제 다이얼로그
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  const disabled = !item?.isActive;

  // 아이템 삭제
  const handleDelete = async () => {
    try {
      const response = await fetch(`/api/v1/items/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        router.push('/items');
      } else {
        console.error('삭제 실패');
      }
    } catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  // 아이템 상세 정보 불러오기
  const fetchItem = async () => {
    try {
      const response = await fetch(`/api/v1/items/${id}`);
      if (response.ok) {
        const msg = await response.json();
        setItem(msg.data);
      }
    } catch (error) {
      console.error('Error fetching item:', error);
    } finally {
      setIsItemLoading(false);
    }
  };

  // 아이템 이력 불러오기
  const fetchItemHistories = async () => {
    try {
      const response = await fetch(`/api/v1/items/${id}/histories`);
      if (response.ok) {
        const msg = await response.json();
        setItemHistories(msg);
      }
    } catch (error) {
      console.error('Error fetching itemHistories:', error);
    } finally {
      setIsItemHistoryLoading(false);
    }
  };

  // cycleDays 변환
  const formatCycle = (cycle: string | null) => {
    if (!cycle) return '-';

    const unit = cycle.slice(-1).toLowerCase(); // 마지막 글자 (m, y, d)
    const value = cycle.slice(0, -1); // 숫자 부분

    switch (unit) {
      case 'm':
        return `${value}개월`;
      case 'y':
        return `${value}년`;
      case 'd':
        return `${value}일`;
      default:
        // 만약 단위 없이 숫자만 들어온 경우(예: "30")를 대비한 예외 처리
        return isNaN(Number(cycle)) ? cycle : `${cycle}일`;
    }
  };

  // 날짜 차이(일) 계산 함수
  const getDiffDays = (start: string, end: Date | string) => {
    const startDate = new Date(start);
    const endDate = new Date(end);

    // 시간 정보를 제거하고 날짜만 비교하기 위해 UTC 기준으로 계산
    const diffTime = endDate.getTime() - startDate.getTime();
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    return diffDays < 0 ? 0 : diffDays;
  };

  // 계산된 이력 데이터 생성
  const calculatedHistories = useMemo(() => {
    const today = new Date();

    return itemHistories.map((history, index) => {
      let usageDays = 0;

      if (index === 0) {
        // 가장 최신 데이터: 오늘과의 차이
        usageDays = getDiffDays(history.startDate, today);
      } else {
        // 이전 데이터: 바로 위(최근) 데이터의 시작일과의 차이
        usageDays = getDiffDays(history.startDate, itemHistories[index - 1].startDate);
      }

      return {
        ...history,
        displayUsageDays: usageDays,
      };
    });
  }, [itemHistories]);

  // 페이지 접속 시 데이터 fetch
  useEffect(() => {
    fetchItem();
    fetchItemHistories();
  }, [idStr]);

  if (isItemLoading)
    return <div className="min-h-screen bg-[#020617] text-white flex items-center justify-center">로딩 중...</div>;
  if (!item)
    return (
      <div className="min-h-screen bg-[#020617] text-white flex items-center justify-center">
        아이템을 찾을 수 없습니다
      </div>
    );

  return (
    <div className="min-h-screen bg-[#020617] font-sans pb-20">
      {/* Header */}
      <PageHeader variant="purple" />

      {/* Main Titles */}
      <div className="text-center mb-10">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-400 via-pink-400 to-purple-400 bg-clip-text text-transparent mb-2">
          아이템 상세 정보
        </h1>
        <p className="text-gray-400 text-sm">아이템의 세부 정보를 확인하세요</p>
      </div>

      {/* Detail Card */}
      <div className="max-w-[800px] mx-auto px-4">
        <div className="relative bg-[#0B0E14] border-2 border-[#A855F7]/50 rounded-[2rem] p-8 md:p-12 shadow-2xl">
          {/* Avatar & Name */}
          <div className="flex flex-col items-center mb-12">
            <div className="w-24 h-24 rounded-full bg-blue-600 flex items-center justify-center mb-4 shadow-lg shadow-blue-600/20">
              <div className="w-20 h-20 rounded-full bg-[#2563EB] flex items-center justify-center">
                {item.imgUrl ? (
                  <img src={item.imgUrl} alt={item.name} className="w-full h-full rounded-full object-cover" />
                ) : (
                  <div className="w-12 h-12 bg-[#3B82F6] rounded-full" />
                )}
              </div>
            </div>
            <h2 className="text-2xl font-bold text-white tracking-tight">{item.name}</h2>
          </div>
          Status Section
          <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6 mb-4">
            <div className="flex items-center gap-2 text-[#22C55E] mb-4">
              <Activity className="h-5 w-5" />
              <span className="font-semibold">상태</span>
            </div>
            <div className="flex gap-3">
              <button
                className={`px-6 py-2 rounded-lg text-sm font-medium transition-all ${
                  item.isActive ? 'bg-[#22C55E] text-white' : 'bg-[#1E293B] text-gray-500'
                }`}
              >
                {item.isActive ? '활성' : '비활성'}
              </button>
            </div>
          </div>
          {/* 2-Column Grid Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            {/* Category */}
            <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6">
              <div className="flex items-center gap-2 text-blue-400 mb-3">
                <Tag className="h-5 w-5" />
                <span className="font-semibold">카테고리</span>
              </div>
              <div className="bg-[#1E293B] inline-block px-3 py-1 rounded-md text-sm text-gray-300">
                {item.categoryName || '-'}
              </div>
            </div>

            {/* Cycle */}
            <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6">
              <div className="flex items-center gap-2 text-purple-400 mb-3">
                <Package className="h-5 w-5" />
                <span className="font-semibold">교체 주기</span>
              </div>
              <div className="text-white text-sm">{formatCycle(item.cycleDays)}</div>
            </div>

            {/* Start Date */}
            <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6">
              <div className="flex items-center gap-2 text-yellow-500 mb-3">
                <Calendar className="h-5 w-5" />
                <span className="font-semibold">시작일</span>
              </div>
              <div className="text-white text-sm">{item.startDate || '-'}</div>
            </div>

            {/* Next Replacement */}
            <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6">
              <div className="flex items-center gap-2 text-yellow-500 mb-3">
                <Calendar className="h-5 w-5" />
                <span className="font-semibold">다음 교체일</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-white text-sm">{item.nextReplacementDate || '-'}</span>
                {item.dDay !== undefined && (
                  <div
                    className={`${item.dDay > 0 ? 'bg-[#22C55E]' : 'bg-[#DC2626]'} px-3 py-1 rounded-md text-xs font-bold text-white`}
                  >
                    {item.dDay === 0 ? 'D-Day' : `D${item.dDay > 0 ? '-' : '+'}${Math.abs(item.dDay)}`}
                  </div>
                )}
              </div>
            </div>
          </div>
          {/* History Section */}
          <div className="bg-[#0F172A]/50 border border-white/5 rounded-2xl p-6 mb-12">
            <div className="flex items-center gap-2 text-pink-500 mb-4">
              <History className="h-5 w-5" />
              <span className="font-semibold">교체 이력</span>
            </div>
            <div className="space-y-3">
              {/* 로딩 중 */}
              {isItemHistoryLoading && (
                <div className="rounded-xl bg-white/5 p-4 text-sm text-white/60 ring-1 ring-white/15">
                  불러오는 중...
                </div>
              )}

              {/* 로딩 완료 */}
              {calculatedHistories.length > 0 ? (
                calculatedHistories.map((history) => (
                  <div key={history.id} className="bg-[#0B0E14] border border-white/5 rounded-xl p-5">
                    <div className="text-white text-xl font-bold mb-2 tracking-tight">{history.startDate}</div>
                    <div className="text-gray-500 text-base">실제 사용일: {history.displayUsageDays}일</div>
                  </div>
                ))
              ) : (
                <div className="text-gray-500 text-center py-4">이력이 없습니다.</div>
              )}
            </div>
          </div>
          {/* Action Buttons */}
          <div className="flex gap-4">
            <button
              onClick={() => setIsModalOpen(true)}
              className="flex-1 bg-[#2563EB] hover:bg-[#1D4ED8] text-white py-3 rounded-xl font-bold transition-colors cursor-pointer"
            >
              수정
            </button>
            <button
              onClick={() => setIsDeleteDialogOpen(true)}
              className="flex-1 bg-[#DC2626] hover:bg-[#B91C1C] text-white py-3 rounded-xl font-bold transition-colors cursor-pointer"
            >
              삭제
            </button>
          </div>
          {/* 수정 모달 */}
          {isModalOpen && (
            <ItemModifyForm
              itemId={id}
              onClose={() => setIsModalOpen(false)}
              onUpdate={() => fetchItem()} // 수정 후 최신 데이터를 다시 불러옴
            />
          )}
          {/* 삭제 다이얼로그 */}
          <DeleteItemDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen} onConfirm={handleDelete} />
        </div>
      </div>
    </div>
  );
}
