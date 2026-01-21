/*"use client"

import { useState } from 'react';
import { AuthForm } from '@/app/components/AuthForm';
import { Dashboard } from '@/app/components/Dashboard';
import { Profile } from '@/app/components/Profile';
import { ItemManagement } from '@/app/components/ItemManagement';
import { ItemDetail } from '@/app/components/ItemDetail';
import { History } from '@/app/components/History';
import { Categories } from '@/app/components/Categories';
import { Toaster } from '@/app/components/ui/sonner';
import { toast } from 'sonner';

interface User {
  name: string;
  email: string;
}

interface Item {
  id: string;
  name: string;
  description: string;
  category: string;
  status: string;
  createdAt: string;
}

type View = 'auth' | 'dashboard' | 'profile' | 'items' | 'item-detail' | 'history' | 'categories';

export default function App() {
  const [currentView, setCurrentView] = useState<View>('auth');
  const [user, setUser] = useState<User | null>(null);
  const [selectedItem, setSelectedItem] = useState<Item | null>(null);

  const handleLogin = (email: string, password: string) => {
    // Mock login
    const mockUser: User = {
      name: '레드 레인저',
      email: email,
    };
    setUser(mockUser);
    setCurrentView('dashboard');
    toast.success('로그인 성공! 환영합니다, 레인저!', {
      description: '파워레인저 관리 시스템에 접속하였습니다.',
    });
  };

  const handleSignup = (name: string, email: string, password: string) => {
    // Mock signup
    const mockUser: User = {
      name: name,
      email: email,
    };
    setUser(mockUser);
    setCurrentView('dashboard');
    toast.success('회원가입 성공! 레인저가 되신 것을 환영합니다!', {
      description: '이제 파워레인저 관리 시스템을 사용하실 수 있습니다.',
    });
  };

  const handleLogout = () => {
    setUser(null);
    setCurrentView('auth');
    toast.info('로그아웃 되었습니다', {
      description: '다시 만나요, 레인저!',
    });
  };

  const handleUpdateProfile = (name: string, email: string) => {
    if (user) {
      setUser({ name, email });
      toast.success('프로필 업데이트 완료!', {
        description: '정보가 성공적으로 수정되었습니다.',
      });
    }
  };

  const handleDeleteAccount = () => {
    setUser(null);
    setCurrentView('auth');
    toast.error('회원 탈퇴 완료', {
      description: '계정이 삭제되었습니다.',
    });
  };

  const handleViewDetail = (item: Item) => {
    setSelectedItem(item);
    setCurrentView('item-detail');
  };

  const renderView = () => {
    switch (currentView) {
      case 'auth':
        return <AuthForm onLogin={handleLogin} onSignup={handleSignup} />;
      case 'dashboard':
        return (
          <Dashboard
            user={user}
            onNavigate={(view) => setCurrentView(view as View)}
            onLogout={handleLogout}
          />
        );
      case 'profile':
        return (
          <Profile
            user={user}
            onBack={() => setCurrentView('dashboard')}
            onUpdate={handleUpdateProfile}
            onDelete={handleDeleteAccount}
          />
        );
      case 'items':
        return (
          <ItemManagement
            onBack={() => setCurrentView('dashboard')}
            onViewDetail={handleViewDetail}
          />
        );
      case 'item-detail':
        return selectedItem ? (
          <ItemDetail
            item={selectedItem}
            onBack={() => setCurrentView('items')}
          />
        ) : null;
      case 'history':
        return <History onBack={() => setCurrentView('dashboard')} />;
      case 'categories':
        return <Categories onBack={() => setCurrentView('dashboard')} />;
      default:
        return <AuthForm onLogin={handleLogin} onSignup={handleSignup} />;
    }
  };

  return (
    <>
      {renderView()}
      <Toaster position="top-right" />
    </>
  );
}
*/