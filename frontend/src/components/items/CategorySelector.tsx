/**
 * 카테고리 선택 셀렉트 박스 컴포넌트
 */

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { getCategoryStyle } from '@/lib/category-styles';

type Category = {
  id: number;
  name: string;
};

interface CategorySelectorProps {
  categories: Category[];
  selectedId: number | null;
  onSelect: (id: number | null) => void;
  disabled?: boolean;
}

export function CategorySelector({ categories, selectedId, onSelect, disabled }: CategorySelectorProps) {
  const selectedCategory = categories.find((c) => c.id === selectedId);
  const SelectedIcon = selectedCategory ? getCategoryStyle(selectedCategory.name).icon : null;

  return (
    <div className="w-52">
      <Select
        value={selectedId === null ? 'all' : String(selectedId)}
        onValueChange={(v: string) => {
          onSelect(v === 'all' ? null : Number(v));
        }}
        disabled={disabled}
      >
        <SelectTrigger className="h-9 bg-white/5 text-white border-white/10">
          <SelectValue placeholder="카테고리 선택">
            <div className="flex items-center gap-2">
              {SelectedIcon && <SelectedIcon size={16} />}
              <span>{selectedCategory?.name || '전체'}</span>
            </div>
          </SelectValue>
        </SelectTrigger>
        <SelectContent className="bg-[#0b1224] text-white border-white/10">
          <SelectItem value="all">
            <div className="flex items-center gap-2">
              <span>전체</span>
            </div>
          </SelectItem>
          {categories.map((c) => {
            const { icon: Icon, color } = getCategoryStyle(c.name);
            return (
              <SelectItem key={c.id} value={String(c.id)}>
                <div className="flex items-center gap-2">
                  <Icon size={16} style={{ color }} />
                  <span>{c.name}</span>
                </div>
              </SelectItem>
            );
          })}
        </SelectContent>
      </Select>
    </div>
  );
}
