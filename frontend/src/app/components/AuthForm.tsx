import { useState } from 'react';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/app/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import { User, Mail, Lock } from 'lucide-react';

interface AuthFormProps {
  onLogin: (email: string, password: string) => void;
  onSignup: (name: string, email: string, password: string) => void;
}

export function AuthForm({ onLogin, onSignup }: AuthFormProps) {
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [signupName, setSignupName] = useState('');
  const [signupEmail, setSignupEmail] = useState('');
  const [signupPassword, setSignupPassword] = useState('');

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    onLogin(loginEmail, loginPassword);
  };

  const handleSignup = (e: React.FormEvent) => {
    e.preventDefault();
    onSignup(signupName, signupEmail, signupPassword);
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center p-4"
      style={{
        background: 'linear-gradient(135deg, #DC143C 0%, #22C55E 25%, #1E90FF 50%, #FF1493 75%, #FFD700 100%)'
      }}
    >
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-5xl font-black text-white mb-2 drop-shadow-lg">POWER RANGERS</h1>
          <p className="text-white/90">관리 시스템</p>
        </div>

        <Card className="border-4 border-white/30 shadow-2xl bg-black/80 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-white">레인저 인증</CardTitle>
            <CardDescription className="text-gray-300">레인저가 되어 시스템을 관리하세요</CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue="login" className="w-full">
              <TabsList className="grid w-full grid-cols-2 bg-gray-800">
                <TabsTrigger value="login" className="data-[state=active]:bg-red-600">로그인</TabsTrigger>
                <TabsTrigger value="signup" className="data-[state=active]:bg-blue-600">회원가입</TabsTrigger>
              </TabsList>

              <TabsContent value="login">
                <form onSubmit={handleLogin} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="login-email" className="text-white">이메일</Label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="login-email"
                        type="email"
                        placeholder="ranger@powerrangers.com"
                        value={loginEmail}
                        onChange={(e) => setLoginEmail(e.target.value)}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        required
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="login-password" className="text-white">비밀번호</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="login-password"
                        type="password"
                        placeholder="••••••••"
                        value={loginPassword}
                        onChange={(e) => setLoginPassword(e.target.value)}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        required
                      />
                    </div>
                  </div>
                  <Button type="submit" className="w-full bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 hover:to-red-800 text-white border-2 border-red-400">
                    레인저 로그인
                  </Button>
                </form>
              </TabsContent>

              <TabsContent value="signup">
                <form onSubmit={handleSignup} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="signup-name" className="text-white">이름</Label>
                    <div className="relative">
                      <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="signup-name"
                        type="text"
                        placeholder="레드 레인저"
                        value={signupName}
                        onChange={(e) => setSignupName(e.target.value)}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        required
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="signup-email" className="text-white">이메일</Label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="signup-email"
                        type="email"
                        placeholder="ranger@powerrangers.com"
                        value={signupEmail}
                        onChange={(e) => setSignupEmail(e.target.value)}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        required
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="signup-password" className="text-white">비밀번호</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                      <Input
                        id="signup-password"
                        type="password"
                        placeholder="••••••••"
                        value={signupPassword}
                        onChange={(e) => setSignupPassword(e.target.value)}
                        className="pl-10 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                        required
                      />
                    </div>
                  </div>
                  <Button type="submit" className="w-full bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 text-white border-2 border-blue-400">
                    레인저 등록
                  </Button>
                </form>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}