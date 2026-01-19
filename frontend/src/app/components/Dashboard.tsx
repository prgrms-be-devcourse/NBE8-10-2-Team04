import { User, Package, History, Grid, LogOut, UserCircle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';

interface DashboardProps {
  user: { name: string; email: string } | null;
  onNavigate: (view: string) => void;
  onLogout: () => void;
}

export function Dashboard({ user, onNavigate, onLogout }: DashboardProps) {
  const rangerColors = [
    { name: '아이템 관리', icon: Package, color: 'from-red-600 to-red-700', border: 'border-red-400', view: 'items' },
    { name: '내 정보', icon: UserCircle, color: 'from-blue-600 to-blue-700', border: 'border-blue-400', view: 'profile' },
    { name: '이력 조회', icon: History, color: 'from-yellow-500 to-yellow-600', border: 'border-yellow-400', view: 'history' },
    { name: '카테고리', icon: Grid, color: 'from-pink-600 to-pink-700', border: 'border-pink-400', view: 'categories' },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-red-600 via-blue-600 to-yellow-500 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex justify-between items-center">
              <div>
                <h1 className="text-3xl font-black text-white">POWER RANGERS</h1>
                <p className="text-sm text-gray-300">관리 시스템</p>
              </div>
              <div className="flex items-center gap-4">
                <div className="text-right">
                  <p className="text-white">{user?.name}</p>
                  <p className="text-sm text-gray-400">{user?.email}</p>
                </div>
                <Button
                  onClick={onLogout}
                  variant="outline"
                  className="bg-red-600 hover:bg-red-700 text-white border-red-400"
                >
                  <LogOut className="h-4 w-4 mr-2" />
                  로그아웃
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-blue-500 to-yellow-500 mb-2">
            레인저 미션 센터
          </h2>
          <p className="text-gray-400">원하는 미션을 선택하세요</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {rangerColors.map((ranger, index) => {
            const Icon = ranger.icon;
            return (
              <Card
                key={index}
                className={`bg-black/80 backdrop-blur-sm border-4 ${ranger.border} hover:scale-105 transition-all cursor-pointer shadow-2xl`}
                onClick={() => onNavigate(ranger.view)}
              >
                <CardHeader>
                  <div className={`w-16 h-16 rounded-full bg-gradient-to-br ${ranger.color} flex items-center justify-center mb-4 border-4 border-white/30 shadow-lg`}>
                    <Icon className="h-8 w-8 text-white" />
                  </div>
                  <CardTitle className="text-white">{ranger.name}</CardTitle>
                  <CardDescription className="text-gray-400">
                    {ranger.view === 'items' && '아이템을 등록, 조회, 수정, 삭제하세요'}
                    {ranger.view === 'profile' && '내 정보를 조회하고 수정하세요'}
                    {ranger.view === 'history' && '이력을 확인하세요'}
                    {ranger.view === 'categories' && '카테고리를 관리하세요'}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <Button className={`w-full bg-gradient-to-r ${ranger.color} hover:opacity-90 text-white border-2 ${ranger.border}`}>
                    미션 시작
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>
    </div>
  );
}
