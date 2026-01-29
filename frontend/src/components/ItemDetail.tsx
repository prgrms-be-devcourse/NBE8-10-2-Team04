import { ArrowLeft, Package, Calendar, Tag, Activity } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface Item {
  id: string;
  name: string;
  description: string;
  category: string;
  status: string;
  createdAt: string;
  imgUrl?: string;
}

interface ItemDetailProps {
  item: Item;
  onBack: () => void;
}

export function ItemDetail({ item, onBack }: ItemDetailProps) {
  const getCategoryColor = (category: string) => {
    const colors: Record<string, string> = {
      '무기': 'from-red-600 to-red-700',
      '장비': 'from-blue-600 to-blue-700',
      '메카': 'from-yellow-500 to-yellow-600',
      '파워 코인': 'from-pink-600 to-pink-700',
      '기타': 'from-gray-600 to-gray-700',
    };
    return colors[category] || 'from-gray-600 to-gray-700';
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      '활성': 'bg-green-600',
      '비활성': 'bg-gray-600',
      '수리중': 'bg-yellow-600',
      '분실': 'bg-red-600',
    };
    return colors[status] || 'bg-gray-600';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-600 via-pink-600 to-red-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={onBack}
              variant="outline"
              className="bg-purple-600 hover:bg-purple-700 text-white border-purple-400"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              돌아가기
            </Button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-purple-500 to-pink-500 mb-2">
            아이템 상세 정보
          </h2>
          <p className="text-gray-400">아이템의 세부 정보를 확인하세요</p>
        </div>

        <Card className="bg-black/80 backdrop-blur-sm border-4 border-purple-400 shadow-2xl">
          <CardHeader>
            <div className={`w-32 h-32 rounded-full bg-gradient-to-br ${getCategoryColor(item.category)} flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg`}>
              {item.imgUrl ? (
                <img 
                  src={item.imgUrl} 
                  alt={item.name} 
                  className="w-full h-full object-cover" 
                />
              ) : (
                /* imgUrl이 없으면 기존처럼 아이콘을 보여줌 */
                <Package className="h-12 w-12 text-white" />
              )}
            </div>
            <CardTitle className="text-white text-center text-3xl">{item.name}</CardTitle>
            <CardDescription className="text-gray-400 text-center text-lg">
              {item.description}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="bg-gray-900/50 p-4 rounded-lg border border-gray-700">
                <div className="flex items-center gap-3 mb-2">
                  <Tag className="h-5 w-5 text-blue-400" />
                  <h3 className="text-white">카테고리</h3>
                </div>
                <Badge variant="outline" className="border-gray-600 text-gray-300 text-lg">
                  {item.category}
                </Badge>
              </div>

              <div className="bg-gray-900/50 p-4 rounded-lg border border-gray-700">
                <div className="flex items-center gap-3 mb-2">
                  <Activity className="h-5 w-5 text-green-400" />
                  <h3 className="text-white">상태</h3>
                </div>
                <Badge className={`${getStatusColor(item.status)} text-lg`}>
                  {item.status}
                </Badge>
              </div>

              <div className="bg-gray-900/50 p-4 rounded-lg border border-gray-700">
                <div className="flex items-center gap-3 mb-2">
                  <Calendar className="h-5 w-5 text-yellow-400" />
                  <h3 className="text-white">등록일</h3>
                </div>
                <p className="text-gray-300 text-lg">{item.createdAt}</p>
              </div>

              <div className="bg-gray-900/50 p-4 rounded-lg border border-gray-700">
                <div className="flex items-center gap-3 mb-2">
                  <Package className="h-5 w-5 text-purple-400" />
                  <h3 className="text-white">아이템 ID</h3>
                </div>
                <p className="text-gray-300 text-lg">#{item.id}</p>
              </div>
            </div>

            <div className="bg-gray-900/50 p-6 rounded-lg border border-gray-700">
              <h3 className="text-white mb-3">상세 설명</h3>
              <p className="text-gray-300 leading-relaxed">
                {item.description}
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
