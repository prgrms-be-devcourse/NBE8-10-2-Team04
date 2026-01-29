'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { User, Lock, Mail } from 'lucide-react';
import { useToast } from '@/contexts/ToastContext';

export default function AuthPage() {
  const router = useRouter();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'login' | 'signup'>('login');

  // [1] 로그인 상태
  const [loginId, setLoginId] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8080/api/v1/user/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ loginId, password: loginPassword }),
      });

      const rsData = await response.json();

      if (rsData.resultCode && rsData.resultCode.startsWith("2")) {
        
        console.log('로그인 성공:', rsData.msg);

        setTimeout(() => {
          showToast('success', rsData.msg);
        }, 200);
        router.push('/');
      } else {
        showToast('error', rsData.msg);
      }
    } catch (error) {
      console.error('에러:', error);
      showToast('error', '시스템 통신 오류가 발생했습니다.');
    }
  };

  // ==========================================
  // [2] 회원가입 상태 및 검증 로직
  // ==========================================
  const [signupLoginId, setSignupLoginId] = useState('');
  const [signupPassword, setSignupPassword] = useState('');
  const [signupEmail, setSignupEmail] = useState('');

  // 에러 메시지를 담을 상태 추가
  const [signupErrors, setSignupErrors] = useState({
    loginId: '',
    password: '',
    email: '',
  });

  // 유효성 검사 함수
  const validateSignup = () => {
    let isValid = true;
    const newErrors = { loginId: '', password: '', email: '' };

    // 1. 아이디 검사
    if (signupLoginId.length < 2) {
      newErrors.loginId = '아이디는 최소 2자 이상이어야 합니다.';
      isValid = false;
    } else if (signupLoginId.length > 30) {
      newErrors.loginId = '아이디는 30자를 초과할 수 없습니다.';
      isValid = false;
    }

    // 2. 비밀번호 검사
    if (signupPassword.length < 2) {
      newErrors.password = '비밀번호는 최소 2자 이상이어야 합니다.';
      isValid = false;
    } else if (signupPassword.length > 30) {
      newErrors.password = '비밀번호는 30자를 초과할 수 없습니다.';
      isValid = false;
    }

    // 3. 이메일 검사
    if (signupEmail.length < 2) {
      newErrors.email = '이메일은 최소 2자 이상이어야 합니다.';
      isValid = false;
    } else if (signupEmail.length > 30) {
      newErrors.email = '이메일은 30자를 초과할 수 없습니다.';
      isValid = false;
    }

    setSignupErrors(newErrors);
    return isValid;
  };

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();

    // 요청 보내기 전에 검증 먼저 수행
    if (!validateSignup()) {
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/v1/user/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({
          loginId: signupLoginId,
          password: signupPassword,
          email: signupEmail,
        }),
      });

      const rsData = await response.json();

      if (rsData.resultCode && rsData.resultCode.startsWith("2")) {
        showToast('success', rsData.msg);
        setActiveTab('login');
        setSignupLoginId('');
        setSignupPassword('');
        setSignupEmail('');

        setSignupErrors({ loginId: '', password: '', email: '' });
      } else {
        console.error('회원가입 실패');
        showToast('error', rsData.msg);
      }
    } catch (error) {
      console.error('에러 발생:', error);
      showToast('error', '서버 통신 중 오류가 발생했습니다.');
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center p-4"
      style={{
        background: 'linear-gradient(135deg, #DC143C 0%, #22C55E 25%, #1E90FF 50%, #FF1493 75%, #FFD700 100%)',
      }}
    >
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-5xl font-black text-white mb-2 drop-shadow-lg">POWER RANGERS</h1>
          <p className="text-white/90 font-medium">관리 시스템</p>
        </div>

        <div className="border-4 border-white/30 shadow-2xl bg-black/80 backdrop-blur-sm rounded-xl overflow-hidden">
          <div className="p-6 pb-2">
            <h2 className="text-2xl font-bold text-white mb-1">레인저 인증</h2>
            <p className="text-gray-300 text-sm">
              {activeTab === 'login' ? '시스템에 접속하세요' : '새로운 레인저로 등록하세요'}
            </p>
          </div>

          <div className="p-6 pt-2">
            <div className="grid w-full grid-cols-2 bg-gray-800 rounded-lg p-1 mb-6">
              <button
                onClick={() => setActiveTab('login')}
                className={`py-1.5 text-sm font-medium rounded-md transition-all ${
                  activeTab === 'login' ? 'bg-red-600 text-white shadow' : 'text-gray-400 hover:text-white'
                }`}
              >
                로그인
              </button>
              <button
                onClick={() => setActiveTab('signup')}
                className={`py-1.5 text-sm font-medium rounded-md transition-all ${
                  activeTab === 'signup' ? 'bg-blue-600 text-white shadow' : 'text-gray-400 hover:text-white'
                }`}
              >
                회원가입
              </button>
            </div>

            {/* ---------------- 로그인 폼 ---------------- */}
            {activeTab === 'login' && (
              <form onSubmit={handleLogin} className="space-y-4">
                <div className="space-y-2">
                  <label className="text-white text-sm font-medium">아이디</label>
                  <div className="relative">
                    <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                    <input
                      type="text"
                      placeholder="ranger_id"
                      value={loginId}
                      onChange={(e) => setLoginId(e.target.value)}
                      className="w-full pl-10 pr-3 py-2 bg-gray-900 border border-gray-700 rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-red-500"
                      required
                    />
                  </div>
                </div>
                <div className="space-y-2">
                  <label className="text-white text-sm font-medium">비밀번호</label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                    <input
                      type="password"
                      placeholder="••••••••"
                      value={loginPassword}
                      onChange={(e) => setLoginPassword(e.target.value)}
                      className="w-full pl-10 pr-3 py-2 bg-gray-900 border border-gray-700 rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-red-500"
                      required
                    />
                  </div>
                </div>
                <button
                  type="submit"
                  className="w-full py-2 px-4 bg-gradient-to-r from-red-600 to-red-700 hover:from-red-700 text-white font-medium rounded-md border-2 border-red-400 shadow-lg transform active:scale-95 transition-all"
                >
                  레인저 로그인
                </button>
              </form>
            )}

            {/* ---------------- 회원가입 폼 ---------------- */}
            {activeTab === 'signup' && (
              <form onSubmit={handleSignup} className="space-y-4">
                {/* 1. 아이디 입력 */}
                <div className="space-y-2">
                  <label htmlFor="signup-id" className="text-white text-sm font-medium">
                    아이디
                  </label>
                  <div className="relative">
                    <User className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                    <input
                      id="signup-id"
                      type="text"
                      placeholder="아이디를 입력하세요"
                      value={signupLoginId}
                      onChange={(e) => setSignupLoginId(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 bg-gray-900 border rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 ${
                        signupErrors.loginId
                          ? 'border-red-500 focus:ring-red-500' // 에러 시 빨간 테두리
                          : 'border-gray-700 focus:ring-blue-500'
                      }`}
                      required
                    />
                  </div>
                  {signupErrors.loginId && (
                    <p className="text-red-400 text-xs mt-1 font-medium pl-1">{signupErrors.loginId}</p>
                  )}
                </div>

                {/* 2. 비밀번호 입력 */}
                <div className="space-y-2">
                  <label htmlFor="signup-pw" className="text-white text-sm font-medium">
                    비밀번호
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                    <input
                      id="signup-pw"
                      type="password"
                      placeholder="비밀번호를 입력하세요"
                      value={signupPassword}
                      onChange={(e) => setSignupPassword(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 bg-gray-900 border rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 ${
                        signupErrors.password
                          ? 'border-red-500 focus:ring-red-500'
                          : 'border-gray-700 focus:ring-blue-500'
                      }`}
                      required
                    />
                  </div>
                  {signupErrors.password && (
                    <p className="text-red-400 text-xs mt-1 font-medium pl-1">{signupErrors.password}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <label htmlFor="signup-email" className="text-white text-sm font-medium">
                    이메일
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                    <input
                      id="signup-email"
                      type="email"
                      placeholder="이메일을 입력하세요"
                      value={signupEmail}
                      onChange={(e) => setSignupEmail(e.target.value)}
                      className={`w-full pl-10 pr-3 py-2 bg-gray-900 border rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 ${
                        signupErrors.email ? 'border-red-500 focus:ring-red-500' : 'border-gray-700 focus:ring-blue-500'
                      }`}
                      required
                    />
                  </div>
                  {signupErrors.email && (
                    <p className="text-red-400 text-xs mt-1 font-medium pl-1">{signupErrors.email}</p>
                  )}
                </div>

                <button
                  type="submit"
                  className="w-full py-2 px-4 bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 text-white font-medium rounded-md border-2 border-blue-400 shadow-lg transform active:scale-95 transition-all"
                >
                  가입하기
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
