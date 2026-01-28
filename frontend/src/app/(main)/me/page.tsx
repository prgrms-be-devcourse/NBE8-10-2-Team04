'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { User, Mail, Save, Trash2, Lock, CheckCircle2, X, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { PageHeader } from '@/components/common/PageHeader';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { validateEmail, validatePasswordForUpdate } from '@/lib/validation';
import { useToast } from '@/contexts/ToastContext';

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type UserDto = {
  id: number;
  loginId: string;
  email: string;
};

export default function MePage() {
  const router = useRouter();
  const { showToast } = useToast();
  const [user, setUser] = useState<UserDto | null>(null);
  const [loginId, setLoginId] = useState('');
  const [email, setEmail] = useState('');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<{ email?: boolean; password?: boolean } | null>(null);

  // 비밀번호 확인 다이얼로그 상태
  const [isPasswordDialogOpen, setIsPasswordDialogOpen] = useState(false);
  const [verifyPassword, setVerifyPassword] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [verifyError, setVerifyError] = useState<string | null>(null);

  // 사용자 정보 조회
  useEffect(() => {
    const fetchUser = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const res = await fetch('/api/v1/user/me', {
          method: 'GET',
          credentials: 'include',
          cache: 'no-store',
        });

        if (res.status === 401) {
          router.replace('/login');
          return;
        }

        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }

        const body = (await res.json()) as RsData<UserDto>;
        const userData = body.data;

        if (userData) {
          setUser(userData);
          setLoginId(userData.loginId);
          setEmail(userData.email);
        }
      } catch (err: any) {
        let errorMsg = '사용자 정보를 불러오지 못했습니다.';

        // 네트워크 에러 처리
        if (err?.message?.includes('Failed to fetch') || err?.name === 'TypeError') {
          errorMsg = '서버에 연결할 수 없습니다. 백엔드 서버가 실행 중인지 확인해주세요.';
        } else if (err?.message) {
          errorMsg = err.message;
        }

        setError(errorMsg);
        console.error('사용자 정보 조회 오류:', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, [router]);

  // 정보 수정 (이메일 + 비밀번호) - 각각 별도 API 호출
  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    e.stopPropagation();

    const emailValidation = validateEmail(email);
    if (!emailValidation.isValid) {
      setError(emailValidation.error ?? '이메일을 확인해주세요.');
      return;
    }

    // 비밀번호를 변경하려는 경우 검증
    const isChangingPassword = newPassword.length > 0;
    if (isChangingPassword) {
      if (!currentPassword) {
        setError('비밀번호를 변경하려면 현재 비밀번호를 입력해주세요.');
        return;
      }
      const pwValidation = validatePasswordForUpdate(newPassword);
      if (!pwValidation.isValid) {
        setError(pwValidation.error ?? '새 비밀번호를 확인해주세요.');
        return;
      }
    }

    try {
      setIsSaving(true);
      setError(null);

      // 1. 이메일 수정 (변경된 경우만)
      const emailChanged = user && email.trim() !== user.email;
      if (emailChanged) {
        const emailRes = await fetch('/api/v1/user/me', {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ email: email.trim() }),
        });

        if (emailRes.status === 401) {
          router.replace('/login');
          return;
        }

        if (!emailRes.ok) {
          const errorData = (await emailRes.json()) as RsData<unknown>;
          throw new Error((errorData as { msg?: string }).msg ?? `이메일 수정 실패: HTTP ${emailRes.status}`);
        }
      }

      // 2. 비밀번호 변경 (입력된 경우만)
      if (isChangingPassword) {
        const passwordRes = await fetch('/api/v1/user/me/password', {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ currentPassword, newPassword }),
        });

        if (passwordRes.status === 401) {
          router.replace('/login');
          return;
        }

        if (!passwordRes.ok) {
          const errorData = (await passwordRes.json()) as RsData<unknown>;
          throw new Error((errorData as { msg?: string }).msg ?? `비밀번호 변경 실패: HTTP ${passwordRes.status}`);
        }
      }

      // 3. 성공 메시지 설정
      const updatedItems: { email?: boolean; password?: boolean } = {};
      if (emailChanged) updatedItems.email = true;
      if (isChangingPassword) updatedItems.password = true;

      setSuccessMessage(updatedItems);

      // 4초 후 성공 메시지 자동 제거
      setTimeout(() => {
        setSuccessMessage(null);
      }, 4000);

      // 사용자 정보 다시 조회
      const userRes = await fetch('/api/v1/user/me', {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store',
      });

      if (userRes.ok) {
        const userBody = (await userRes.json()) as RsData<UserDto>;
        const userData = userBody.data;
        if (userData) {
          setUser(userData);
          setLoginId(userData.loginId);
          setEmail(userData.email);
        }
      }

      setIsEditing(false);
      setCurrentPassword('');
      setNewPassword('');
      setError(null);

      // 성공 메시지는 위에서 이미 설정됨
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : '정보 수정에 실패했습니다.';
      setError(
        message.includes('Failed to fetch') ? '서버에 연결할 수 없습니다. 백엔드가 실행 중인지 확인해주세요.' : message,
      );
    } finally {
      setIsSaving(false);
    }
  };

  // 회원 탈퇴
  const handleDelete = async () => {
    if (!confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      return;
    }

    try {
      setIsDeleting(true);
      setError(null);

      const res = await fetch('/api/v1/user/me', {
        method: 'DELETE',
        credentials: 'include',
      });

      if (res.status === 401) {
        router.replace('/login');
        return;
      }

      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.msg || `HTTP ${res.status}`);
      }

      const body = (await res.json()) as RsData<any>;
      showToast('success', body.msg || '회원탈퇴가 완료되었습니다.');

      router.replace('/login');
    } catch (err: any) {
      const errorMessage = err?.message ?? '회원탈퇴에 실패했습니다.';
      setError(errorMessage);
      showToast('error', errorMessage);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (user) {
      setEmail(user.email);
    }
    setCurrentPassword('');
    setNewPassword('');
  };

  // 비밀번호 확인 후 수정 모드 진입
  const handleVerifyPassword = async () => {
    if (!verifyPassword.trim()) {
      setVerifyError('비밀번호를 입력해주세요.');
      return;
    }

    try {
      setIsVerifying(true);
      setVerifyError(null);

      const res = await fetch('/api/v1/user/me/verify-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ password: verifyPassword }),
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.msg || '비밀번호가 일치하지 않습니다.');
      }

      // 비밀번호 확인 성공 - 수정 모드로 전환
      setIsPasswordDialogOpen(false);
      setVerifyPassword('');
      setVerifyError(null);
      setIsEditing(true);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : '비밀번호 확인에 실패했습니다.';
      setVerifyError(message);
    } finally {
      setIsVerifying(false);
    }
  };

  // 다이얼로그 닫기
  const handlePasswordDialogClose = () => {
    setIsPasswordDialogOpen(false);
    setVerifyPassword('');
    setVerifyError(null);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900 flex items-center justify-center">
        <div className="text-white text-xl">로딩 중...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <PageHeader variant="blue" />

      {/* Main Content */}
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-pink-500 mb-2">
            레인저 프로필
          </h2>
          <p className="text-gray-400">당신의 정보를 관리하세요</p>
        </div>

        {error && (
          <Alert variant="destructive" className="mb-4 bg-red-900/50 border-red-500">
            <AlertCircle className="h-4 w-4" />
            <AlertTitle>오류</AlertTitle>
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {successMessage && (
          <div className="mb-4 p-4 bg-green-900/50 border-2 border-green-500 rounded-lg text-green-200 animate-in slide-in-from-top-2 duration-300">
            <div className="flex items-start justify-between">
              <div className="flex items-start gap-3 flex-1">
                <CheckCircle2 className="h-5 w-5 text-green-400 mt-0.5 flex-shrink-0" />
                <div className="flex-1">
                  <div className="font-semibold mb-2">정보가 성공적으로 수정되었습니다!</div>
                  <div className="space-y-1.5 text-sm">
                    {successMessage.email && (
                      <div className="flex items-center gap-2">
                        <Mail className="h-4 w-4 text-green-300" />
                        <span>이메일이 수정되었습니다.</span>
                      </div>
                    )}
                    {successMessage.password && (
                      <div className="flex items-center gap-2">
                        <Lock className="h-4 w-4 text-green-300" />
                        <span>비밀번호가 변경되었습니다.</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
              <button
                onClick={() => setSuccessMessage(null)}
                className="ml-4 text-green-300 hover:text-green-100 transition-colors flex-shrink-0"
                aria-label="닫기"
              >
                <X className="h-5 w-5" />
              </button>
            </div>
          </div>
        )}

        <Card className="bg-black/80 backdrop-blur-sm border-4 border-blue-400 shadow-2xl">
          <CardHeader>
            <div className="w-20 h-20 rounded-full bg-gradient-to-br from-blue-600 to-blue-700 flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg">
              <User className="h-10 w-10 text-white" />
            </div>
            <CardTitle className="text-white text-center">내 정보</CardTitle>
            <CardDescription className="text-gray-400 text-center">
              {isEditing ? '정보를 수정하세요' : '정보를 조회하고 수정할 수 있습니다'}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="loginId" className="text-white">
                  아이디
                </Label>
                <div className="relative">
                  <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    id="loginId"
                    type="text"
                    value={loginId}
                    disabled
                    className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500 disabled:opacity-60"
                  />
                </div>
                <p className="text-xs text-gray-500">아이디는 변경할 수 없습니다.</p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="email" className="text-white">
                  이메일
                </Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                      setEmail(e.target.value);
                      setError(null);
                    }}
                    disabled={!isEditing}
                    className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500 disabled:opacity-60"
                    required
                  />
                </div>
              </div>

              {isEditing && (
                <form onSubmit={handleUpdate} className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="currentPassword" className="text-white">
                      현재 비밀번호 <span className="text-xs text-gray-500">(비밀번호 변경 시에만 필요)</span>
                    </Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="currentPassword"
                        type="password"
                        value={currentPassword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          setCurrentPassword(e.target.value);
                          setError(null);
                        }}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        placeholder="비밀번호 변경 시에만 입력하세요"
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="newPassword" className="text-white">
                      새 비밀번호
                    </Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="newPassword"
                        type="password"
                        value={newPassword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          setNewPassword(e.target.value);
                          setError(null);
                        }}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        placeholder="비밀번호 변경 시에만 입력하세요 (2-20자)"
                        minLength={2}
                        maxLength={20}
                      />
                    </div>
                    <p className="text-xs text-gray-500">비밀번호를 변경하지 않으려면 비워두세요.</p>
                  </div>

                  <div className="flex gap-4">
                    <Button
                      type="submit"
                      disabled={isSaving}
                      className="flex-1 bg-green-500 hover:bg-green-400 hover:scale-105 active:scale-95 transition text-white border-2 border-green-400 disabled:opacity-50"
                    >
                      <Save className="h-4 w-4 mr-2" />
                      {isSaving ? '저장 중...' : '저장'}
                    </Button>
                    <Button
                      type="button"
                      onClick={handleCancel}
                      disabled={isSaving}
                      variant="outline"
                      className="flex-1 bg-gray-600 hover:bg-gray-500 hover:scale-105 active:scale-95 transition text-white border-gray-500 disabled:opacity-50"
                    >
                      취소
                    </Button>
                  </div>
                </form>
              )}

              {!isEditing && (
                <div className="flex gap-4">
                  <Button
                    type="button"
                    onClick={() => setIsPasswordDialogOpen(true)}
                    className="flex-1 bg-blue-500 hover:bg-blue-400 hover:scale-105 active:scale-95 transition text-white border-2 border-blue-400"
                  >
                    정보 수정
                  </Button>
                </div>
              )}

              {/* 비밀번호 확인 다이얼로그 */}
              <AlertDialog open={isPasswordDialogOpen} onOpenChange={(open) => !open && handlePasswordDialogClose()}>
                <AlertDialogContent className="bg-black border-2 border-blue-400">
                  <AlertDialogHeader>
                    <AlertDialogTitle className="text-white">비밀번호 확인</AlertDialogTitle>
                    <AlertDialogDescription className="text-gray-400">
                      정보 수정을 위해 현재 비밀번호를 입력해주세요.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <div className="py-4">
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        type="password"
                        value={verifyPassword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          setVerifyPassword(e.target.value);
                          setVerifyError(null);
                        }}
                        onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
                          if (e.key === 'Enter') {
                            e.preventDefault();
                            handleVerifyPassword();
                          }
                        }}
                        placeholder="비밀번호를 입력하세요"
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        disabled={isVerifying}
                      />
                    </div>
                    {verifyError && (
                      <p className="mt-2 text-sm text-red-400">{verifyError}</p>
                    )}
                  </div>
                  <AlertDialogFooter>
                    <AlertDialogCancel
                      onClick={handlePasswordDialogClose}
                      className="bg-gray-600 hover:bg-gray-500 hover:scale-105 active:scale-95 transition text-white border-gray-500"
                      disabled={isVerifying}
                    >
                      취소
                    </AlertDialogCancel>
                    <Button
                      onClick={handleVerifyPassword}
                      disabled={isVerifying}
                      className="bg-blue-500 hover:bg-blue-400 hover:scale-105 active:scale-95 transition text-white disabled:opacity-50"
                    >
                      {isVerifying ? '확인 중...' : '확인'}
                    </Button>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>

            <div className="mt-8 pt-8 border-t border-gray-700">
              <AlertDialog>
                <AlertDialogTrigger asChild>
                  <Button
                    variant="destructive"
                    disabled={isDeleting}
                    className="w-full bg-red-500 hover:bg-red-400 hover:scale-105 active:scale-95 transition border-2 border-red-400 disabled:opacity-50"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    {isDeleting ? '탈퇴 중...' : '회원 탈퇴'}
                  </Button>
                </AlertDialogTrigger>
                <AlertDialogContent className="bg-black border-2 border-red-400">
                  <AlertDialogHeader>
                    <AlertDialogTitle className="text-white">정말 탈퇴하시겠습니까?</AlertDialogTitle>
                    <AlertDialogDescription className="text-gray-400">
                      이 작업은 되돌릴 수 없습니다. 모든 데이터가 영구적으로 삭제됩니다.
                    </AlertDialogDescription>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel className="bg-gray-600 hover:bg-gray-500 hover:scale-105 active:scale-95 transition text-white border-gray-500">
                      취소
                    </AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleDelete}
                      disabled={isDeleting}
                      className="bg-red-500 hover:bg-red-400 hover:scale-105 active:scale-95 transition text-white disabled:opacity-50"
                    >
                      {isDeleting ? '탈퇴 중...' : '탈퇴하기'}
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
