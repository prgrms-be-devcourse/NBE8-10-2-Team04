import { useState } from 'react';
import { ArrowLeft, Plus, Package, Edit, Trash2, Eye, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { Textarea } from '@/components/ui/textarea';

interface Item {
  id: string;
  name: string;
  description: string;
  category: string;
  status: string;
  createdAt: string;
}

interface ItemManagementProps {
  onBack: () => void;
  onViewDetail: (item: Item) => void;
}

const categories = ['무기', '장비', '메카', '파워 코인', '기타'];
const statuses = ['활성', '비활성', '수리중', '분실'];

export function ItemManagement({ onBack, onViewDetail }: ItemManagementProps) {
  const [items, setItems] = useState<Item[]>([
    { id: '1', name: '파워 소드', description: '레드 레인저의 주무기', category: '무기', status: '활성', createdAt: '2026-01-10' },
    { id: '2', name: '파워 블래스터', description: '블루 레인저의 원거리 무기', category: '무기', status: '활성', createdAt: '2026-01-11' },
    { id: '3', name: '메가조드', description: '5대의 조드가 합체한 거대 로봇', category: '메카', status: '수리중', createdAt: '2026-01-12' },
  ]);
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<Item | null>(null);
  const [filterCategory, setFilterCategory] = useState<string>('전체');

  const [newItem, setNewItem] = useState({
    name: '',
    description: '',
    category: '',
    status: '활성',
  });

  const handleAddItem = () => {
    const item: Item = {
      id: String(items.length + 1),
      ...newItem,
      createdAt: new Date().toISOString().split('T')[0],
    };
    setItems([...items, item]);
    setNewItem({ name: '', description: '', category: '', status: '활성' });
    setIsAddDialogOpen(false);
  };

  const handleUpdateItem = () => {
    if (editingItem) {
      setItems(items.map(item => item.id === editingItem.id ? editingItem : item));
      setEditingItem(null);
    }
  };

  const handleDeleteItem = (id: string) => {
    setItems(items.filter(item => item.id !== id));
  };

  const handleReplaceItem = (id: string) => {
    const item = items.find(i => i.id === id);
    if (item) {
      const newItem: Item = {
        ...item,
        id: String(items.length + 1),
        name: item.name + ' (교체)',
        createdAt: new Date().toISOString().split('T')[0],
      };
      setItems([...items, newItem]);
    }
  };

  const filteredItems = filterCategory === '전체'
    ? items
    : items.filter(item => item.category === filterCategory);

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
      <div className="bg-gradient-to-r from-red-600 via-yellow-500 to-pink-600 p-1">
        <div className="bg-black/90 backdrop-blur-sm">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <div className="flex justify-between items-center">
              <Button
                onClick={onBack}
                variant="outline"
                className="bg-red-600 hover:bg-red-700 text-white border-red-400"
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                돌아가기
              </Button>

              <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
                <DialogTrigger asChild>
                  <Button className="bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white border-2 border-green-400">
                    <Plus className="h-4 w-4 mr-2" />
                    아이템 등록
                  </Button>
                </DialogTrigger>
                <DialogContent className="bg-black border-2 border-green-400">
                  <DialogHeader>
                    <DialogTitle className="text-white">새 아이템 등록</DialogTitle>
                    <DialogDescription className="text-gray-400">
                      새로운 아이템을 등록하세요
                    </DialogDescription>
                  </DialogHeader>
                  <div className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="name" className="text-white">아이템 이름</Label>
                      <Input
                        id="name"
                        value={newItem.name}
                        onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
                        className="bg-gray-900 border-gray-700 text-white"
                        placeholder="파워 소드"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="description" className="text-white">설명</Label>
                      <Textarea
                        id="description"
                        value={newItem.description}
                        onChange={(e) => setNewItem({ ...newItem, description: e.target.value })}
                        className="bg-gray-900 border-gray-700 text-white"
                        placeholder="아이템 설명을 입력하세요"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="category" className="text-white">카테고리</Label>
                      <Select value={newItem.category} onValueChange={(value) => setNewItem({ ...newItem, category: value })}>
                        <SelectTrigger className="bg-gray-900 border-gray-700 text-white">
                          <SelectValue placeholder="카테고리 선택" />
                        </SelectTrigger>
                        <SelectContent className="bg-gray-900 border-gray-700">
                          {categories.map(cat => (
                            <SelectItem key={cat} value={cat} className="text-white">{cat}</SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="status" className="text-white">상태</Label>
                      <Select value={newItem.status} onValueChange={(value) => setNewItem({ ...newItem, status: value })}>
                        <SelectTrigger className="bg-gray-900 border-gray-700 text-white">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent className="bg-gray-900 border-gray-700">
                          {statuses.map(status => (
                            <SelectItem key={status} value={status} className="text-white">{status}</SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <Button onClick={handleAddItem} className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white">
                      등록
                    </Button>
                  </div>
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8 text-center">
          <h2 className="text-4xl font-black text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-yellow-500 to-pink-500 mb-2">
            아이템 관리
          </h2>
          <p className="text-gray-400">레인저 장비를 관리하세요</p>
        </div>

        {/* Filter */}
        <div className="mb-6">
          <Select value={filterCategory} onValueChange={setFilterCategory}>
            <SelectTrigger className="w-48 bg-gray-900 border-gray-700 text-white">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-gray-900 border-gray-700">
              <SelectItem value="전체" className="text-white">전체</SelectItem>
              {categories.map(cat => (
                <SelectItem key={cat} value={cat} className="text-white">{cat}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Items Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredItems.map((item) => (
            <Card key={item.id} className={`bg-black/80 backdrop-blur-sm border-4 border-gradient shadow-2xl`}>
              <CardHeader>
                <div className={`w-16 h-16 rounded-full bg-gradient-to-br ${getCategoryColor(item.category)} flex items-center justify-center mb-4 border-4 border-white/30 shadow-lg`}>
                  <Package className="h-8 w-8 text-white" />
                </div>
                <CardTitle className="text-white">{item.name}</CardTitle>
                <CardDescription className="text-gray-400">{item.description}</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex gap-2">
                  <Badge className={getStatusColor(item.status)}>{item.status}</Badge>
                  <Badge variant="outline" className="border-gray-600 text-gray-300">{item.category}</Badge>
                </div>
                <p className="text-sm text-gray-500">등록일: {item.createdAt}</p>

                <div className="grid grid-cols-2 gap-2">
                  <Button
                    size="sm"
                    onClick={() => onViewDetail(item)}
                    className="bg-gradient-to-r from-blue-600 to-blue-700 text-white border border-blue-400"
                  >
                    <Eye className="h-3 w-3 mr-1" />
                    상세
                  </Button>
                  <Dialog open={editingItem?.id === item.id} onOpenChange={(open) => !open && setEditingItem(null)}>
                    <DialogTrigger asChild>
                      <Button
                        size="sm"
                        onClick={() => setEditingItem({ ...item })}
                        className="bg-gradient-to-r from-yellow-600 to-yellow-700 text-white border border-yellow-400"
                      >
                        <Edit className="h-3 w-3 mr-1" />
                        수정
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="bg-black border-2 border-yellow-400">
                      <DialogHeader>
                        <DialogTitle className="text-white">아이템 수정</DialogTitle>
                      </DialogHeader>
                      {editingItem && (
                        <div className="space-y-4">
                          <div className="space-y-2">
                            <Label className="text-white">이름</Label>
                            <Input
                              value={editingItem.name}
                              onChange={(e) => setEditingItem({ ...editingItem, name: e.target.value })}
                              className="bg-gray-900 border-gray-700 text-white"
                            />
                          </div>
                          <div className="space-y-2">
                            <Label className="text-white">설명</Label>
                            <Textarea
                              value={editingItem.description}
                              onChange={(e) => setEditingItem({ ...editingItem, description: e.target.value })}
                              className="bg-gray-900 border-gray-700 text-white"
                            />
                          </div>
                          <div className="space-y-2">
                            <Label className="text-white">카테고리</Label>
                            <Select value={editingItem.category} onValueChange={(value) => setEditingItem({ ...editingItem, category: value })}>
                              <SelectTrigger className="bg-gray-900 border-gray-700 text-white">
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent className="bg-gray-900 border-gray-700">
                                {categories.map(cat => (
                                  <SelectItem key={cat} value={cat} className="text-white">{cat}</SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                          </div>
                          <div className="space-y-2">
                            <Label className="text-white">상태</Label>
                            <Select value={editingItem.status} onValueChange={(value) => setEditingItem({ ...editingItem, status: value })}>
                              <SelectTrigger className="bg-gray-900 border-gray-700 text-white">
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent className="bg-gray-900 border-gray-700">
                                {statuses.map(status => (
                                  <SelectItem key={status} value={status} className="text-white">{status}</SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                          </div>
                          <Button onClick={handleUpdateItem} className="w-full bg-gradient-to-r from-yellow-600 to-yellow-700 text-white">
                            저장
                          </Button>
                        </div>
                      )}
                    </DialogContent>
                  </Dialog>
                  <Button
                    size="sm"
                    onClick={() => handleReplaceItem(item.id)}
                    className="bg-gradient-to-r from-purple-600 to-purple-700 text-white border border-purple-400"
                  >
                    <RefreshCw className="h-3 w-3 mr-1" />
                    교체
                  </Button>
                  <Button
                    size="sm"
                    onClick={() => handleDeleteItem(item.id)}
                    variant="destructive"
                    className="bg-gradient-to-r from-red-600 to-red-700 text-white border border-red-400"
                  >
                    <Trash2 className="h-3 w-3 mr-1" />
                    삭제
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}
