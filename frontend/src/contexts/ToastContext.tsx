'use client';

import React, { createContext, useContext, useState, useCallback } from 'react';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { AlertCircle, CheckCircle2, Info, X } from 'lucide-react';

type ToastType = 'success' | 'error' | 'info';

interface Toast {
  id: number;
  type: ToastType;
  title?: string;
  message: string;
}

interface ToastContextType {
  showToast: (type: ToastType, message: string, title?: string) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);

  const showToast = useCallback((type: ToastType, message: string, title?: string) => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, type, message, title }]);

    // 4초 후 자동으로 토스트 제거
    setTimeout(() => {
      setToasts((prev) => prev.filter((toast) => toast.id !== id));
    }, 4000);
  }, []);

  const removeToast = (id: number) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}

      {/* Toast Container */}
      <div className="fixed top-4 right-4 z-[9999] flex flex-col items-end gap-2 pointer-events-none">
        {toasts.map((toast) => (
          <div key={toast.id} className="pointer-events-auto animate-in slide-in-from-right-full duration-300 w-fit">
            {/* relative 클래스를 추가하여 내부 버튼의 기준점을 잡습니다. */}
            <Alert
              variant={toast.type === 'error' ? 'destructive' : 'default'}
              className={`relative min-w-[320px] max-w-md shadow-lg break-keep pr-10 ${
                // 우측 패딩(pr-10) 추가
                toast.type === 'success'
                  ? 'border-green-500 bg-green-500/10'
                  : toast.type === 'info'
                    ? 'border-blue-500 bg-blue-500/10'
                    : ''
              }`}
            >
              <div className="flex items-start gap-3 w-full">
                {toast.type === 'success' && <CheckCircle2 className="h-5 w-5 text-green-500 shrink-0" />}
                {toast.type === 'error' && <AlertCircle className="h-5 w-5 shrink-0" />}
                {toast.type === 'info' && <Info className="h-5 w-5 text-blue-500 shrink-0" />}

                <div className="flex-1 min-w-0">
                  {toast.title && (
                    <AlertTitle
                      className={
                        toast.type === 'success' ? 'text-green-500' : toast.type === 'info' ? 'text-blue-500' : ''
                      }
                    >
                      {toast.title}
                    </AlertTitle>
                  )}
                  <AlertDescription
                    className={`${toast.type === 'success' ? 'text-green-400' : toast.type === 'info' ? 'text-blue-400' : ''} whitespace-normal`}
                  >
                    {toast.message}
                  </AlertDescription>
                </div>
              </div>

              {/* X 버튼을 absolute로 설정하여 오른쪽 상단으로 강제 이동 */}
              <button
                onClick={() => removeToast(toast.id)}
                className={`absolute top-3 right-3 shrink-0 hover:opacity-70 transition-opacity p-1 ${
                  toast.type === 'success' ? 'text-green-500' : toast.type === 'info' ? 'text-blue-500' : 'text-red-500'
                }`}
              >
                <X className="h-4 w-4" />
              </button>
            </Alert>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}
