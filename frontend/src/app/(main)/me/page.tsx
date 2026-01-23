"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowLeft, User, Mail, Save, Trash2, Lock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog';
import { validateUpdateForm } from '@/lib/validation';


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

  // 사용자 정보 조회
  useEffect(() => {
    const fetchUser = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const res = await fetch("/api/v1/user/me", {
          method: "GET",
          credentials: "include",
          cache: "no-store",
        });

        if (res.status === 401) {
          router.replace("/login");
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
        let errorMsg = "사용자 정보를 불러오지 못했습니다.";
        
        // 네트워크 에러 처리
        if (err?.message?.includes("Failed to fetch") || err?.name === "TypeError") {
          errorMsg = "서버에 연결할 수 없습니다. 백엔드 서버가 실행 중인지 확인해주세요.";
        } else if (err?.message) {
          errorMsg = err.message;
        }
        
        setError(errorMsg);
        console.error("사용자 정보 조회 오류:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, [router]);

  // 사용자 정보 수정
  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // 현재 비밀번호 확인
    if (!currentPassword) {
      setError("현재 비밀번호를 입력해주세요.");
      return;
    }

    // 새 비밀번호 입력 확인
    if (!newPassword) {
      setError("새 비밀번호를 입력해주세요.");
      return;
    }

    // 새 비밀번호 유효성 검사
    const passwordValidation = validateUpdateForm(email, newPassword);
    if (!passwordValidation.isValid) {
      setError(passwordValidation.error || "입력 정보를 확인해주세요.");
      return;
    }

    try {
      setIsSaving(true);
      setError(null);

      const res = await fetch("/api/v1/user/me", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          email: email.trim(),
          password: newPassword, // 백엔드는 password만 받으므로 새 비밀번호를 password로 전송
        }),
      });

      if (res.status === 401) {
        router.replace("/login");
        return;
      }

      if (!res.ok) {
        let errorMessage = `HTTP ${res.status}`;
        try {
          const errorData = await res.json() as RsData<any>;
          // 백엔드에서 반환하는 에러 메시지 처리
          if (errorData.msg) {
            // 백엔드 validation 에러 형식: "field-code-message" 또는 여러 줄
            // 예: "email-Email-올바른 이메일 형식이 아닙니다\npassword-NotBlank-비밀번호는 필수입니다"
            const messages = errorData.msg.split('\n');
            if (messages.length > 0) {
              // 마지막 부분만 추출 (더 읽기 쉬운 메시지)
              const lastMessage = messages[messages.length - 1];
              const parts = lastMessage.split('-');
              if (parts.length >= 3) {
                // 필드명과 메시지 추출
                const fieldName = parts[0];
                const message = parts.slice(2).join('-');
                
                // 필드명을 한글로 변환
                const fieldMap: Record<string, string> = {
                  'email': '이메일',
                  'password': '비밀번호',
                };
                const fieldLabel = fieldMap[fieldName] || fieldName;
                errorMessage = `${fieldLabel}: ${message}`;
              } else {
                errorMessage = lastMessage;
              }
            } else {
              errorMessage = errorData.msg;
            }
          } else if (errorData.resultCode) {
            errorMessage = "요청 처리 중 오류가 발생했습니다.";
          }
        } catch (parseError) {
          // JSON 파싱 실패 시 기본 메시지 사용
          const text = await res.text().catch(() => "");
          if (text) {
            errorMessage = text;
          }
        }
        throw new Error(errorMessage);
      }

      const body = (await res.json()) as RsData<any>;
      
      alert(body.msg || "회원정보가 수정되었습니다.");
      
      // 수정 후 다시 사용자 정보 조회
      const userRes = await fetch("/api/v1/user/me", {
        method: "GET",
        credentials: "include",
        cache: "no-store",
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
    } catch (err: any) {
      let errorMsg = "회원정보 수정에 실패했습니다.";
      
      // 네트워크 에러 처리
      if (err?.message?.includes("Failed to fetch") || err?.name === "TypeError") {
        errorMsg = "서버에 연결할 수 없습니다. 백엔드 서버가 실행 중인지 확인해주세요.";
      } else if (err?.message) {
        errorMsg = err.message;
      }
      
      setError(errorMsg);
      console.error("회원정보 수정 오류:", err);
    } finally {
      setIsSaving(false);
    }
  };

  // 회원 탈퇴
  const handleDelete = async () => {
    if (!confirm("정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
      return;
    }

    try {
      setIsDeleting(true);
      setError(null);

      const res = await fetch("/api/v1/user/me", {
        method: "DELETE",
        credentials: "include",
      });

      if (res.status === 401) {
        router.replace("/login");
        return;
      }

      if (!res.ok) {
        const errorData = await res.json().catch(() => ({}));
        throw new Error(errorData.msg || `HTTP ${res.status}`);
      }

      const body = (await res.json()) as RsData<any>;
      alert(body.msg || "회원탈퇴가 완료되었습니다.");
      
      // 탈퇴 후 로그인 페이지로 이동
      router.replace("/login");
    } catch (err: any) {
      setError(err?.message ?? "회원탈퇴에 실패했습니다.");
      alert(err?.message ?? "회원탈퇴에 실패했습니다.");
    } finally {
      setIsDeleting(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (user) {
      setLoginId(user.loginId);
      setEmail(user.email);
    }
    setCurrentPassword('');
    setNewPassword('');
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
      <div className="bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={() => router.back()}
              variant="outline"
              className="bg-blue-600 hover:bg-blue-700 text-white border-blue-400"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              돌아가기
            </Button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-pink-500 mb-2">
            레인저 프로필
          </h2>
          <p className="text-gray-400">당신의 정보를 관리하세요</p>
        </div>

        {error && (
          <div className="mb-4 p-4 bg-red-900/50 border border-red-500 rounded-lg text-red-200">
            {error}
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
            <form onSubmit={handleUpdate} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="loginId" className="text-white">아이디</Label>
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
                <Label htmlFor="email" className="text-white">이메일</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                    disabled={!isEditing}
                    className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500 disabled:opacity-60"
                    required
                  />
                </div>
              </div>

              {isEditing && (
                <>
                  <div className="space-y-2">
                    <Label htmlFor="currentPassword" className="text-white">현재 비밀번호</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="currentPassword"
                        type="password"
                        value={currentPassword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          setCurrentPassword(e.target.value);
                          setError(null); // 입력 시 에러 메시지 초기화
                        }}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        placeholder="현재 비밀번호를 입력하세요"
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="newPassword" className="text-white">새 비밀번호</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="newPassword"
                        type="password"
                        value={newPassword}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          setNewPassword(e.target.value);
                          setError(null); // 입력 시 에러 메시지 초기화
                        }}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        placeholder="새 비밀번호를 입력하세요 (2-20자)"
                        required
                        minLength={2}
                        maxLength={20}
                      />
                    </div>
                    <p className="text-xs text-gray-500">비밀번호는 2자 이상 20자 이하여야 합니다.</p>
                  </div>
                </>
              )}

              <div className="flex gap-4">
                {!isEditing ? (
                  <Button
                    type="button"
                    onClick={() => setIsEditing(true)}
                    className="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white border-2 border-blue-400"
                  >
                    정보 수정
                  </Button>
                ) : (
                  <>
                    <Button
                      type="submit"
                      disabled={isSaving}
                      className="flex-1 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white border-2 border-green-400 disabled:opacity-50"
                    >
                      <Save className="h-4 w-4 mr-2" />
                      {isSaving ? "저장 중..." : "저장"}
                    </Button>
                    <Button
                      type="button"
                      onClick={handleCancel}
                      disabled={isSaving}
                      variant="outline"
                      className="flex-1 bg-gray-700 hover:bg-gray-600 text-white border-gray-500 disabled:opacity-50"
                    >
                      취소
                    </Button>
                  </>
                )}
              </div>
            </form>

            <div className="mt-8 pt-8 border-t border-gray-700">
              <AlertDialog>
                <AlertDialogTrigger asChild>
                  <Button
                    variant="destructive"
                    disabled={isDeleting}
                    className="w-full bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 border-2 border-red-400 disabled:opacity-50"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    {isDeleting ? "탈퇴 중..." : "회원 탈퇴"}
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
                    <AlertDialogCancel className="bg-gray-700 hover:bg-gray-600 text-white border-gray-500">취소</AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleDelete}
                      disabled={isDeleting}
                      className="bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 text-white disabled:opacity-50"
                    >
                      {isDeleting ? "탈퇴 중..." : "탈퇴하기"}
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
