import { ArrowLeft, Grid, Package, Sword, Shield, Zap, Star } from 'lucide-react';
import { Button } from '@/app/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/app/components/ui/card';

interface CategoriesProps {
  onBack: () => void;
}

interface Category {
  id: string;
  name: string;
  description: string;
  count: number;
  color: string;
  icon: any;
}

export function Categories({ onBack }: CategoriesProps) {
  const categories: Category[] = [
    {
      id: '1',
      name: '무기',
      description: '레인저들의 주요 전투 무기',
      count: 15,
      color: 'from-red-600 to-red-700',
      icon: Sword,
    },
    {
      id: '2',
      name: '장비',
      description: '보호 및 보조 장비',
      count: 23,
      color: 'from-blue-600 to-blue-700',
      icon: Shield,
    },
    {
      id: '3',
      name: '메카',
      description: '거대 로봇 및 메카 부품',
      count: 8,
      color: 'from-yellow-500 to-yellow-600',
      icon: Package,
    },
    {
      id: '4',
      name: '파워 코인',
      description: '레인저의 힘의 원천',
      count: 5,
      color: 'from-pink-600 to-pink-700',
      icon: Zap,
    },
    {
      id: '5',
      name: '기타',
      description: '기타 아이템 및 소모품',
      count: 12,
      color: 'from-purple-600 to-purple-700',
      icon: Star,
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-pink-600 via-purple-600 to-blue-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={onBack}
              variant="outline"
              className="bg-pink-600 hover:bg-pink-700 text-white border-pink-400"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              돌아가기
            </Button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-pink-500 to-purple-500 mb-2">
            카테고리 관리
          </h2>
          <p className="text-gray-400">아이템 카테고리를 확인하세요</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {categories.map((category) => {
            const Icon = category.icon;
            return (
              <Card
                key={category.id}
                className="bg-black/80 backdrop-blur-sm border-4 border-white/20 hover:border-white/40 hover:scale-105 transition-all shadow-2xl"
              >
                <CardHeader>
                  <div className={`w-20 h-20 rounded-full bg-gradient-to-br ${category.color} flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg`}>
                    <Icon className="h-10 w-10 text-white" />
                  </div>
                  <CardTitle className="text-white text-center text-2xl">
                    {category.name}
                  </CardTitle>
                  <CardDescription className="text-gray-400 text-center">
                    {category.description}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="text-center">
                    <div className={`inline-flex items-center justify-center w-full py-3 rounded-lg bg-gradient-to-r ${category.color} border-2 border-white/30`}>
                      <Package className="h-5 w-5 text-white mr-2" />
                      <span className="text-white text-xl">
                        {category.count}개 아이템
                      </span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>

        {/* Summary Card */}
        <Card className="mt-8 bg-black/80 backdrop-blur-sm border-4 border-gradient shadow-2xl">
          <CardHeader>
            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-purple-600 to-pink-600 flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg">
              <Grid className="h-8 w-8 text-white" />
            </div>
            <CardTitle className="text-white text-center">전체 통계</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
              {categories.map((category) => (
                <div
                  key={category.id}
                  className="text-center p-4 bg-gray-900/50 rounded-lg border border-gray-700"
                >
                  <p className="text-gray-400 text-sm mb-1">{category.name}</p>
                  <p className="text-2xl text-white">{category.count}</p>
                </div>
              ))}
            </div>
            <div className="mt-6 text-center">
              <p className="text-gray-400 mb-2">총 아이템 수</p>
              <p className="text-5xl font-black text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-yellow-500 to-pink-500">
                {categories.reduce((sum, cat) => sum + cat.count, 0)}
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
