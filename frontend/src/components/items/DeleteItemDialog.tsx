/**
 * 아이템 삭제 확인 다이얼로그 컴포넌트
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

interface DeleteItemDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: () => void;
}

export function DeleteItemDialog({ open, onOpenChange, onConfirm }: DeleteItemDialogProps) {
  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent className="bg-[#1a1f2e] border border-red-500/50 text-white max-w-[400px]">
        <AlertDialogHeader>
          <AlertDialogTitle className="text-xl font-bold">아이템 삭제</AlertDialogTitle>
          <AlertDialogDescription className="text-gray-300 pt-2 leading-relaxed">
            정말로 이 아이템을 삭제하시겠습니까? <br />
            아이템 삭제 시, 모든 교체 이력도 함께 삭제됩니다.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter className="mt-6 flex gap-3">
          <AlertDialogCancel className="bg-gray-200 text-black hover:bg-gray-300 border-none px-6">
            취소
          </AlertDialogCancel>
          <AlertDialogAction onClick={onConfirm} className="bg-red-600 text-white hover:bg-red-700 border-none px-6">
            삭제
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
