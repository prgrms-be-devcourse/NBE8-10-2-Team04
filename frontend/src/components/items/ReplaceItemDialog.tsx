/**
 * 아이템 교체 확인 다이얼로그 컴포넌트
 */

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

interface ReplaceItemDialogProps {
  /** 다이얼로그 표시 여부 */
  open: boolean;

  /** 다이얼로그 열림/닫힘 상태 변경 핸들러 */
  onOpenChange: (open: boolean) => void;

  /** '교체' 버튼 클릭 시 실행될 콜백 함수 */
  onConfirm: () => void;
}

export function ReplaceItemDialog({ open, onOpenChange, onConfirm }: ReplaceItemDialogProps) {
  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent className="bg-[#1a1f2e] border border-purple-500/50 text-white max-w-[400px]">
        {/* 다이얼로그 헤더 */}
        <AlertDialogHeader>
          <AlertDialogTitle className="text-xl font-bold">아이템 교체</AlertDialogTitle>
          <AlertDialogDescription className="text-gray-300 pt-2 leading-relaxed">
            정말로 이 아이템을 교체하시겠습니까? <br />
            아이템 교체 시, 교체 이력이 저장됩니다.
          </AlertDialogDescription>
        </AlertDialogHeader>

        {/* 다이얼로그 푸터 (버튼 영역) */}
        <AlertDialogFooter className="mt-6 flex gap-3">
          {/* 취소 버튼 - 클릭 시 다이얼로그 닫힘 */}
          <AlertDialogCancel className="bg-gray-200 text-black hover:bg-gray-300 border-none px-6">
            취소
          </AlertDialogCancel>

          {/* 교체 버튼 - 클릭 시 onConfirm 콜백 실행 */}
          <AlertDialogAction
            onClick={onConfirm}
            className="bg-purple-600 text-white hover:bg-purple-700 border-none px-6"
          >
            교체
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
