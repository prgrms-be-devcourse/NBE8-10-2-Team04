import { useState } from 'react';
import { ArrowLeft, User, Mail, Save, Trash2 } from 'lucide-react';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/app/components/ui/card';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/app/components/ui/alert-dialog';

interface ProfileProps {
  user: { name: string; email: string } | null;
  onBack: () => void;
  onUpdate: (name: string, email: string) => void;
  onDelete: () => void;
}

export function Profile({ user, onBack, onUpdate, onDelete }: ProfileProps) {
  const [name, setName] = useState(user?.name || '');
  const [email, setEmail] = useState(user?.email || '');
  const [isEditing, setIsEditing] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onUpdate(name, email);
    setIsEditing(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={onBack}
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
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="name" className="text-white">이름</Label>
                <div className="relative">
                  <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    id="name"
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    disabled={!isEditing}
                    className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500 disabled:opacity-60"
                    required
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="email" className="text-white">이메일</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    disabled={!isEditing}
                    className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500 disabled:opacity-60"
                    required
                  />
                </div>
              </div>

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
                      className="flex-1 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white border-2 border-green-400"
                    >
                      <Save className="h-4 w-4 mr-2" />
                      저장
                    </Button>
                    <Button
                      type="button"
                      onClick={() => {
                        setIsEditing(false);
                        setName(user?.name || '');
                        setEmail(user?.email || '');
                      }}
                      variant="outline"
                      className="flex-1 bg-gray-700 hover:bg-gray-600 text-white border-gray-500"
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
                    className="w-full bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 border-2 border-red-400"
                  >
                    <Trash2 className="h-4 w-4 mr-2" />
                    회원 탈퇴
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
                      onClick={onDelete}
                      className="bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 text-white"
                    >
                      탈퇴하기
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
