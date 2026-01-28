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
              // 1. w-[400px]로 고정 너비를 주어 텍스트 공간을 강제로 확보합니다.
              className={`relative w-[400px] shadow-2xl break-keep pr-10 border-2 ${
                toast.type === 'success'
                  ? 'border-green-500 bg-green-950/95 text-green-50'
                  : toast.type === 'info'
                    ? 'border-blue-500 bg-blue-950/95 text-blue-50'
                    : 'border-red-600 bg-red-950/95 text-red-50'
              }`}
            >
              <div className="flex items-start gap-3 w-full">
                {toast.type === 'success' && <CheckCircle2 className="h-5 w-5 text-green-400 shrink-0 mt-0.5" />}
                {toast.type === 'error' && <AlertCircle className="h-5 w-5 text-red-400 shrink-0 mt-0.5" />}
                {toast.type === 'info' && <Info className="h-5 w-5 text-blue-400 shrink-0 mt-0.5" />}

                <div className="flex-1">
                  {toast.title && (
                    <AlertTitle
                      // 2. whitespace-nowrap으로 제목이 한 줄에 나오도록 고정
                      className={`font-bold mb-1 whitespace-nowrap ${
                        toast.type === 'success'
                          ? 'text-green-400'
                          : toast.type === 'info'
                            ? 'text-blue-400'
                            : 'text-red-400'
                      }`}
                    >
                      {toast.title}
                    </AlertTitle>
                  )}
                  <AlertDescription
                    // 3. 텍스트가 잘리지 않고 옆으로 흐르도록 넉넉한 공간 보장
                    className={`font-medium leading-normal ${
                      toast.type === 'success'
                        ? 'text-green-100'
                        : toast.type === 'info'
                          ? 'text-blue-100'
                          : 'text-red-100'
                    }`}
                  >
                    {/* 4. 문구가 너무 길면 자연스럽게 다음 줄로 넘어가되, 공간이 있으면 한 줄로 출력 */}
                    <span className="block min-w-[300px]">{toast.message}</span>
                  </AlertDescription>
                </div>
              </div>

              <button
                onClick={() => removeToast(toast.id)}
                className={`absolute top-3 right-3 shrink-0 hover:opacity-70 transition-opacity p-1 ${
                  toast.type === 'success' ? 'text-green-400' : toast.type === 'info' ? 'text-blue-400' : 'text-red-400'
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
