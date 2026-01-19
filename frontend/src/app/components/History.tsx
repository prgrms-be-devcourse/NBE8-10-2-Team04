import { ArrowLeft, History as HistoryIcon, Clock, User, Package } from 'lucide-react';
import { Button } from '@/app/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';

interface HistoryProps {
  onBack: () => void;
}

interface HistoryItem {
  id: string;
  action: string;
  itemName: string;
  timestamp: string;
  user: string;
  type: 'create' | 'update' | 'delete' | 'replace';
}

export function History({ onBack }: HistoryProps) {
  const historyItems: HistoryItem[] = [
    { id: '1', action: '아이템 등록', itemName: '파워 소드', timestamp: '2026-01-16 14:30', user: '레드 레인저', type: 'create' },
    { id: '2', action: '아이템 수정', itemName: '파워 블래스터', timestamp: '2026-01-16 13:15', user: '블루 레인저', type: 'update' },
    { id: '3', action: '아이템 교체', itemName: '메가조드', timestamp: '2026-01-16 12:00', user: '옐로우 레인저', type: 'replace' },
    { id: '4', action: '아이템 삭제', itemName: '구형 통신기', timestamp: '2026-01-16 11:45', user: '핑크 레인저', type: 'delete' },
    { id: '5', action: '아이템 등록', itemName: '파워 코인', timestamp: '2026-01-16 10:30', user: '블랙 레인저', type: 'create' },
    { id: '6', action: '아이템 수정', itemName: '파워 소드', timestamp: '2026-01-15 16:20', user: '레드 레인저', type: 'update' },
  ];

  const getActionColor = (type: string) => {
    const colors: Record<string, string> = {
      'create': 'bg-green-600',
      'update': 'bg-blue-600',
      'delete': 'bg-red-600',
      'replace': 'bg-yellow-600',
    };
    return colors[type] || 'bg-gray-600';
  };

  const getActionIcon = (type: string) => {
    const colors: Record<string, string> = {
      'create': 'from-green-600 to-green-700',
      'update': 'from-blue-600 to-blue-700',
      'delete': 'from-red-600 to-red-700',
      'replace': 'from-yellow-600 to-yellow-700',
    };
    return colors[type] || 'from-gray-600 to-gray-700';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Header */}
      <div className="bg-gradient-to-r from-yellow-500 via-orange-500 to-red-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <Button
              onClick={onBack}
              variant="outline"
              className="bg-yellow-600 hover:bg-yellow-700 text-white border-yellow-400"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              돌아가기
            </Button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-yellow-500 to-orange-500 mb-2">
            이력 조회
          </h2>
          <p className="text-gray-400">모든 활동 기록을 확인하세요</p>
        </div>

        <Card className="bg-black/80 backdrop-blur-sm border-4 border-yellow-400 shadow-2xl">
          <CardHeader>
            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-yellow-600 to-yellow-700 flex items-center justify-center mx-auto mb-4 border-4 border-white/30 shadow-lg">
              <HistoryIcon className="h-8 w-8 text-white" />
            </div>
            <CardTitle className="text-white text-center">활동 이력</CardTitle>
            <CardDescription className="text-gray-400 text-center">
              최근 활동 기록
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {historyItems.map((item, index) => (
                <div
                  key={item.id}
                  className="bg-gray-900/50 p-4 rounded-lg border border-gray-700 hover:border-yellow-500/50 transition-colors"
                >
                  <div className="flex items-start gap-4">
                    <div className={`w-12 h-12 rounded-full bg-gradient-to-br ${getActionIcon(item.type)} flex items-center justify-center flex-shrink-0 border-2 border-white/30`}>
                      <Package className="h-6 w-6 text-white" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="text-white">{item.action}</h3>
                        <Badge className={getActionColor(item.type)}>
                          {item.action}
                        </Badge>
                      </div>
                      <p className="text-gray-300 mb-2">
                        아이템: <span className="text-yellow-400">{item.itemName}</span>
                      </p>
                      <div className="flex items-center gap-4 text-sm text-gray-500">
                        <div className="flex items-center gap-1">
                          <User className="h-4 w-4" />
                          {item.user}
                        </div>
                        <div className="flex items-center gap-1">
                          <Clock className="h-4 w-4" />
                          {item.timestamp}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
