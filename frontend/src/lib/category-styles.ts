import { Home, Utensils, Droplets, Sparkles, PawPrint, Car, Smartphone, Briefcase, LucideIcon } from 'lucide-react';

export type CategoryStyle = {
  icon: LucideIcon;
  color: string; // Hex color
};

export const categoryStyles: Record<string, CategoryStyle> = {
  '집/생활': {
    icon: Home,
    color: '#fa4c4c', // red
  },
  욕실: {
    icon: Droplets,
    color: '#22d3ee', // Cyan
  },
  주방: {
    icon: Utensils,
    color: '#fb923c', // Orange
  },
  뷰티: {
    icon: Sparkles,
    color: '#f472b6', // Pink
  },
  반려동물: {
    icon: PawPrint,
    color: '#facc15', // Yellow
  },
  자동차: {
    icon: Car,
    color: '#94a3b8', // Slate
  },
  전자기기: {
    icon: Smartphone,
    color: '#60ff6d', // green
  },
  업무: {
    icon: Briefcase,
    color: '#3b82f6', // Blue
  },
};

// 기본 스타일 (매칭되지 않을 경우)
export const defaultCategoryStyle: CategoryStyle = {
  icon: Home,
  color: '#9ca3af', // Gray
};

// 카테고리 이름으로 스타일 가져오기
export function getCategoryStyle(categoryName: string): CategoryStyle {
  return categoryStyles[categoryName] || defaultCategoryStyle;
}

// Hex color를 Tailwind 색상 클래스로 변환하는 헬퍼 함수들
export function getBgColorClass(categoryName: string): string {
  const colorMap: Record<string, string> = {
    '집/생활': 'bg-red-500/10',
    욕실: 'bg-cyan-500/10',
    주방: 'bg-orange-500/10',
    뷰티: 'bg-pink-500/10',
    반려동물: 'bg-yellow-500/10',
    자동차: 'bg-slate-500/10',
    전자기기: 'bg-green-500/10',
    업무: 'bg-blue-500/10',
  };
  return colorMap[categoryName] || 'bg-gray-500/10';
}

export function getTextColorClass(categoryName: string): string {
  const colorMap: Record<string, string> = {
    '집/생활': 'text-red-400',
    욕실: 'text-cyan-400',
    주방: 'text-orange-400',
    뷰티: 'text-pink-400',
    반려동물: 'text-yellow-400',
    자동차: 'text-slate-400',
    전자기기: 'text-green-400',
    업무: 'text-blue-400',
  };
  return colorMap[categoryName] || 'text-gray-400';
}

export function getIconBgClass(categoryName: string): string {
  const colorMap: Record<string, string> = {
    '집/생활': 'bg-red-500',
    욕실: 'bg-cyan-500',
    주방: 'bg-orange-500',
    뷰티: 'bg-pink-500',
    반려동물: 'bg-yellow-500',
    자동차: 'bg-slate-500',
    전자기기: 'bg-green-500',
    업무: 'bg-blue-500',
  };
  return colorMap[categoryName] || 'bg-gray-500';
}
