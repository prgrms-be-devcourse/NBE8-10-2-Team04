"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { User, Lock, Mail } from "lucide-react"; // 아이콘

export default function AuthPage() {
  const router = useRouter();
  
  // 탭 상태 관리 ("login" 또는 "signup")
  const [activeTab, setActiveTab] = useState<"login" | "signup">("login");

  // ==========================================
  // [1] 로그인 상태 및 로직
  // ==========================================
  const [loginId, setLoginId] = useState("");
  const [loginPassword, setLoginPassword] = useState("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch("http://localhost:8080/api/v1/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ loginId: loginId, password: loginPassword }),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("로그인 성공:", data);
        alert("레인저 인증 완료! 시스템에 접속합니다.");
        router.push("/"); // 메인으로 이동
      } else {
        alert("인증 실패: 아이디나 비밀번호를 확인하세요.");
      }
    } catch (error) {
      console.error("에러:", error);
      alert("시스템 통신 오류가 발생했습니다.");
    }
  };

  // ==========================================
  // [2] 회원가입 상태 및 로직 (작성해주신 코드 반영)
  // ==========================================
  const [signupLoginId, setSignupLoginId] = useState(""); // 아이디
  const [signupPassword, setSignupPassword] = useState(""); // 비밀번호
  const [signupEmail, setSignupEmail] = useState("");       // 이메일

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/v1/user/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          loginId: signupLoginId,
          password: signupPassword,
          email: signupEmail,
        }),
      });

      if (response.ok) {
        alert("회원가입이 완료되었습니다. 로그인해주세요.");
        // router.push("/login") 대신 탭을 전환하여 바로 로그인하게 유도
        setActiveTab("login"); 
        
        // (선택) 회원가입 폼 초기화
        setSignupLoginId("");
        setSignupPassword("");
        setSignupEmail("");
      } else {
        console.error("회원가입 실패");
        alert("회원가입에 실패했습니다. 입력 정보를 확인해주세요.");
      }
    } catch (error) {
      console.error("에러 발생:", error);
      alert("서버 통신 중 오류가 발생했습니다.");
    }
  };

  // ==========================================
  // [3] UI 렌더링 (파워레인저 테마)
  // ==========================================
  return (
    <div
      className="min-h-screen flex items-center justify-center p-4"
      style={{
        background:
          "linear-gradient(135deg, #DC143C 0%, #22C55E 25%, #1E90FF 50%, #FF1493 75%, #FFD700 100%)",
      }}
    >
      <div className="w-full max-w-md">
        {/* 타이틀 섹션 */}
        <div className="text-center mb-8">
          <h1 className="text-5xl font-black text-white mb-2 drop-shadow-lg">
            POWER RANGERS
          </h1>
          <p className="text-white/90 font-medium">관리 시스템</p>
        </div>

        {/* 카드 섹션 */}
        <div className="border-4 border-white/30 shadow-2xl bg-black/80 backdrop-blur-sm rounded-xl overflow-hidden">
          
          <div className="p-6 pb-2">
            <h2 className="text-2xl font-bold text-white mb-1">레인저 인증</h2>
            <p className="text-gray-300 text-sm">
              {activeTab === "login" ? "시스템에 접속하세요" : "새로운 레인저로 등록하세요"}
            </p>
          </div>

          <div className="p-6 pt-2">
            {/* 탭 버튼 */}
            <div className="grid w-full grid-cols-2 bg-gray-800 rounded-lg p-1 mb-6">
              <button
                onClick={() => setActiveTab("login")}
                className={`py-1.5 text-sm font-medium rounded-md transition-all ${
                  activeTab === "login"
                    ? "bg-red-600 text-white shadow"
                    : "text-gray-400 hover:text-white"
                }`}
              >
                로그인
              </button>
              <button
                onClick={() => setActiveTab("signup")}
                className={`py-1.5 text-sm font-medium rounded-md transition-all ${
                  activeTab === "signup"
                    ? "bg-blue-600 text-white shadow"
                    : "text-gray-400 hover:text-white"
                }`}
              >
                회원가입
              </button>
            </div>

            {/* ---------------- 로그인 폼 ---------------- */}
            {activeTab === "login" && (
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

            {/* ---------------- 회원가입 폼 (작성해주신 로직 반영) ---------------- */}
            {activeTab === "signup" && (
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
                      className="w-full pl-10 pr-3 py-2 bg-gray-900 border border-gray-700 rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>
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
                      className="w-full pl-10 pr-3 py-2 bg-gray-900 border border-gray-700 rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>
                </div>

                {/* 3. 이메일 입력 */}
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
                      className="w-full pl-10 pr-3 py-2 bg-gray-900 border border-gray-700 rounded-md text-white placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>
                </div>

                {/* 가입 버튼 */}
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