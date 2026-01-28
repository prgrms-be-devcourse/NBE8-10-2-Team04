'use client';

import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';

type ButtonVariant = 'blue' | 'red' | 'yellow' | 'pink' | 'purple';

interface PageHeaderProps {
  showBackButton?: boolean;
  onBack?: () => void;
  className?: string;
  variant?: ButtonVariant;
}

const variantStyles: Record<ButtonVariant, string> = {
  blue: 'bg-blue-600 hover:bg-blue-700 text-white border-blue-400',
  red: 'bg-red-600 hover:bg-red-700 text-white border-red-400',
  yellow: 'bg-yellow-600 hover:bg-yellow-700 text-white border-yellow-400',
  pink: 'bg-pink-600 hover:bg-pink-700 text-white border-pink-400',
  purple: 'bg-purple-600 hover:bg-purple-700 text-white border-purple-400',
};

export function PageHeader({ showBackButton = true, onBack, className = '', variant = 'blue' }: PageHeaderProps) {
  const router = useRouter();

  const handleBack = () => {
    if (onBack) {
      onBack(); // onBack props가 주어졌으면 해당 함수를 따륾
    } else {
      router.push("/"); // 기본적으로는 홈 화면으로 이동
    }
  };

  if (!showBackButton) return null;

  return (
    <div className={`mx-auto max-w-7xl px-4 py-4 ${className}`}>
      <Button onClick={handleBack} variant="outline" className={variantStyles[variant]}>
        <ArrowLeft className="mr-2 h-4 w-4" />
        돌아가기
      </Button>
    </div>
  );
}
