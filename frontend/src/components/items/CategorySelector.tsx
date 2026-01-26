// components/items/CategorySelector.tsx
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

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
          <SelectValue placeholder="카테고리 선택" />
        </SelectTrigger>
        <SelectContent className="bg-[#0b1224] text-white border-white/10">
          <SelectItem value="all">전체</SelectItem>
          {categories.map((c) => (
            <SelectItem key={c.id} value={String(c.id)}>
              {c.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
