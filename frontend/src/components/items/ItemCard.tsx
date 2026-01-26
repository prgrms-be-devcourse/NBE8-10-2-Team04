'use client';

import { useRouter } from 'next/navigation';
import { Box, Pencil, RefreshCw, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { getCategoryStyle } from '@/lib/category-styles';

export type ItemSummary = {
  id: number;
  name: string;
  categoryName: string | null;
  nextReplacementDate: string | null; // LocalDate -> 보통 "YYYY-MM-DD"로 옴
  imgUrl: string | null;
  dDay: number;
  isActive: boolean;
};

function dDayLabel(dDay: number) {
  if (dDay === 0) return 'D-0';
  if (dDay > 0) return `D-${dDay}`;
  return `D+${Math.abs(dDay)}`;
}

function dDayBadgeClass(isActive: boolean) {
  // 활성은 초록, 비활성은 흐린 회색
  if (!isActive) return 'bg-white/15 text-white/70';
  return 'bg-green-500/90 text-white';
}

export function ItemCard({
  item,
  onToggleActive,
  onReplace,
  onDelete,
  onEdit,
}: {
  item: ItemSummary;
  onToggleActive: (id: number, next: boolean) => void;
  onReplace: (id: number) => void;
  onDelete: (id: number) => void;
  onEdit: (id: number) => void;
}) {
  const router = useRouter();
  const disabled = !item.isActive;

  const goDetail = () => router.push(`/items/${item.id}`);

  // 카테고리 스타일 가져오기
  const categoryStyle = item.categoryName ? getCategoryStyle(item.categoryName) : null;
  const CategoryIcon = categoryStyle?.icon;
  const categoryColor = categoryStyle?.color;

  return (
    <div
      role="button"
      tabIndex={0}
      onClick={goDetail}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') goDetail();
      }}
      className={`cursor-pointer rounded-2xl border border-white/10 bg-white/5 p-5 shadow-[0_0_0_1px_rgba(255,255,255,0.03)] ${
        disabled ? 'opacity-70' : ''
      }`}
    >
      {/* 상단 */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div
            className={`flex h-9 w-9 items-center justify-center rounded-full ${
              item.isActive ? 'bg-red-600' : 'bg-white/20'
            }`}
          >
            <Box className="h-5 w-5 text-white" />
          </div>

          <div className="text-base font-semibold">{item.name}</div>
        </div>

        <span className={`rounded-md px-2 py-1 text-xs font-semibold ${dDayBadgeClass(item.isActive)}`}>
          {dDayLabel(item.dDay)}
        </span>
      </div>

      {/* 카테고리 - 색상과 아이콘 적용 */}
      <div className="mt-4">
        {item.categoryName ? (
          <span
            className="inline-flex items-center gap-1.5 rounded-md px-2.5 py-1 text-xs font-medium"
            style={{
              backgroundColor: `${categoryColor}20`, // 20% opacity
              color: categoryColor,
              border: `1px solid ${categoryColor}40`,
            }}
          >
            {CategoryIcon && <CategoryIcon size={14} />}
            {item.categoryName}
          </span>
        ) : (
          <span className="inline-flex rounded-md bg-white/10 px-2 py-1 text-xs text-white/80">미분류</span>
        )}
      </div>

      {/* 날짜 */}
      <div className="mt-3 space-y-1 text-xs text-white/60">
        <div>
          최근 교체일: <span className="text-white/80">-</span>
        </div>
        <div>
          다음 교체일: <span className="text-white/80">{item.nextReplacementDate ?? '-'}</span>
        </div>
      </div>

      {/* 토글 + 수정/삭제 */}
      <div className="mt-4 flex items-center justify-between">
        <div
          className="flex items-center gap-2"
          onClick={(e) => e.stopPropagation()}
          onKeyDown={(e) => e.stopPropagation()}
        >
          <span className="text-xs text-white/70">활성화</span>
          <Switch checked={item.isActive} onCheckedChange={(v: boolean) => onToggleActive(item.id, v)} />
        </div>

        <div
          className="flex items-center gap-3 text-xs text-white/70"
          onClick={(e) => e.stopPropagation()}
          onKeyDown={(e) => e.stopPropagation()}
        >
          <button
            onClick={() => onEdit(item.id)}
            className="inline-flex items-center gap-1 hover:text-white"
            type="button"
          >
            <Pencil className="h-4 w-4" />
            수정
          </button>

          <button
            onClick={() => onDelete(item.id)}
            className="inline-flex items-center gap-1 hover:text-white"
            type="button"
          >
            <Trash2 className="h-4 w-4" />
            삭제
          </button>
        </div>
      </div>

      {/* 교체 버튼 */}
      <div className="mt-4" onClick={(e) => e.stopPropagation()}>
        <Button
          className={`h-9 w-full rounded-md font-semibold ${
            disabled ? 'bg-white/15 text-white/70 hover:bg-white/15' : 'bg-purple-600 hover:bg-purple-700'
          }`}
          onClick={() => onReplace(item.id)}
          disabled={disabled}
        >
          <RefreshCw className="mr-2 h-4 w-4" />
          교체
        </Button>
      </div>
    </div>
  );
}
