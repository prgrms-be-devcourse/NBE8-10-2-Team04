'use client';

import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-react';

// 토스트 자동 제거 시간(ms)
const TOAST_DURATION = 4000;

// 토스트 한 개의 고정 너비(px)
const TOAST_WIDTH = 400;

// 다른 UI 위에 항상 표시되도록 높은 z-index
const TOAST_Z_INDEX = 9999;

// 토스트 종류
type ToastType = 'success' | 'error' | 'info';

// 개별 토스트 객체
interface Toast {
  id: number; // 토스트 식별자
  type: ToastType; // 토스트 타입
  title?: string; // 선택적 제목
  message: string; // 본문 메시지
}

// Context에서 외부로 노출할 API
interface ToastContextType {
  showToast: (type: ToastType, message: string, title?: string) => void;
}

// 타입별 스타일 설정 구조
interface ToastStyleConfig {
  alert: string; // Alert 컴포넌트 클래스
  icon: React.ComponentType<{ className?: string }>; // 아이콘 컴포넌트
  iconColor: string; // 아이콘 색상
  titleColor: string; // 제목 텍스트 색상
  descriptionColor: string; // 설명 텍스트 색상
}

// 토스트 타입별 스타일 매핑
const TOAST_STYLES: Record<ToastType, ToastStyleConfig> = {
  success: {
    alert: 'border-green-500 bg-green-950/95 text-green-50',
    icon: CheckCircle2,
    iconColor: 'text-green-400',
    titleColor: 'text-green-400',
    descriptionColor: 'text-green-100',
  },
  info: {
    alert: 'border-blue-500 bg-blue-950/95 text-blue-50',
    icon: Info,
    iconColor: 'text-blue-400',
    titleColor: 'text-blue-400',
    descriptionColor: 'text-blue-100',
  },
  error: {
    alert: 'border-red-600 bg-red-950/95 text-red-50',
    icon: AlertCircle,
    iconColor: 'text-red-400',
    titleColor: 'text-red-400',
    descriptionColor: 'text-red-100',
  },
};

/**
 * Toast Context 생성
 * showToast 함수만 외부로 노출
 */
const ToastContext = createContext<ToastContextType | undefined>(undefined);

// 전역 토스트 상태 관리
export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]); // 현재 화면에 표시 중인 토스트 목록
  const [scrollY, setScrollY] = useState(0);

  const [hasHeader, setHasHeader] = useState(false);

  // 헤더 존재 여부 감지 (스크롤 감지 useEffect 위에 추가)
  useEffect(() => {
    const checkHeader = () => {
      const header = document.querySelector('header');
      setHasHeader(!!header);
    };

    // 초기 체크
    checkHeader();

    // DOM 변경 감지 (페이지 전환 시)
    const observer = new MutationObserver(checkHeader);
    observer.observe(document.body, { childList: true, subtree: true });

    return () => observer.disconnect();
  }, []);

  // 스크롤 감지 로직
  useEffect(() => {
    const handleScroll = () => {
      // 0부터 64px 사이에서만 반응하도록 제한
      setScrollY(Math.min(window.scrollY, 64));
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  /**
   * 특정 토스트 제거
   * 닫기 버튼 클릭 또는 자동 제거 시 호출
   */
  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  }, []);

  // 토스트 표시
  const showToast = useCallback(
    (type: ToastType, message: string, title?: string) => {
      const id = Date.now(); // 간단한 고유 ID 생성

      // 토스트 추가
      setToasts((prev) => [...prev, { id, type, message, title }]);

      // 지정된 시간 후 자동 제거
      setTimeout(() => {
        removeToast(id);
      }, TOAST_DURATION);
    },
    [removeToast],
  );

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}

      <div
        className="fixed right-4 flex flex-col items-end gap-2 pointer-events-none transition-transform duration-75 ease-out"
        style={{
          zIndex: TOAST_Z_INDEX,
          // 스크롤된 만큼 위로 올리되, 최대 64px만 올림
          transform: `translateY(-${scrollY}px)`,
          top: '80px', // 초기 위치 (top-20)
        }}
      >
        {toasts.map((toast) => {
          const style = TOAST_STYLES[toast.type];
          const IconComponent = style.icon;

          return (
            <div key={toast.id} className="pointer-events-auto animate-in slide-in-from-right-full duration-300 w-fit">
              <Alert
                variant={toast.type === 'error' ? 'destructive' : 'default'}
                className={`relative shadow-2xl break-keep pr-10 border-2 ${style.alert}`}
                style={{ width: TOAST_WIDTH }}
                role="alert"
              >
                <div className="flex items-start gap-3 w-full">
                  {/* 타입별 아이콘 */}
                  <IconComponent className={`h-5 w-5 shrink-0 mt-0.5 ${style.iconColor}`} />

                  {/* 제목 + 메시지 영역 */}
                  <div className="flex-1 min-w-0">
                    {toast.title && (
                      <AlertTitle className={`font-bold mb-1 whitespace-nowrap ${style.titleColor}`}>
                        {toast.title}
                      </AlertTitle>
                    )}
                    <AlertDescription className={`font-medium leading-normal ${style.descriptionColor}`}>
                      <span className="block min-w-[300px]">{toast.message}</span>
                    </AlertDescription>
                  </div>
                </div>

                {/* 수동 닫기 버튼 */}
                <button
                  onClick={() => removeToast(toast.id)}
                  className={`absolute top-3 right-3 shrink-0 hover:opacity-70 transition-opacity p-1 ${style.iconColor}`}
                  aria-label="알림 닫기"
                  type="button"
                >
                  <X className="h-4 w-4" />
                </button>
              </Alert>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
}

/**
 * useToast 훅
 * - ToastProvider 내부에서만 사용 가능
 */
export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}
