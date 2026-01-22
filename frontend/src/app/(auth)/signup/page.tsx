"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function SignupPage() {
  const [email, setEmail] = useState(""); // 이름 (닉네임)
  const [loginId, setLoginId] = useState(""); // 아이디
  const [password, setPassword] = useState(""); // 비밀번호


  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    
    try {
      // 2. API 호출
      const response = await fetch("http://localhost:8080/api/v1/user/signup", { 
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        // 회원가입 직후 자동 로그인 처리를 하거나, 서버 설정에 따라 필요할 수 있어 include 유지
        credentials: "include", 
        body: JSON.stringify({          
          loginId: loginId,
          password: password,
          email: email,
        }),
      });

      // 3. 응답 처리
      if (response.ok) {
        alert("회원가입이 완료되었습니다.");
        router.push("/login"); // 가입 성공 시 로그인 페이지로 이동
      } else {
        // 에러 처리
        console.error("회원가입 실패");
        alert("회원가입에 실패했습니다. 입력 정보를 확인해주세요.");
      }
    } catch (error) {
      console.error("에러 발생:", error);
      alert("서버 통신 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="">
      <div className="w-full max-w-sm p-6 bg-white rounded-lg shadow-md border">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">회원가입</h2>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          
          {/* 아이디 입력 */}
          <div className="flex flex-col gap-1">
            <label htmlFor="loginId" className="text-sm font-medium text-gray-700">
              아이디
            </label>
            <input
              id="loginId"
              type="text"
              value={loginId}
              placeholder="아이디를 입력하세요"
              onChange={(e) => setLoginId(e.target.value)}
              className="p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-black"
              required
            />
          </div>

          {/* 비밀번호 입력 */}
          <div className="flex flex-col gap-1">
            <label htmlFor="password" className="text-sm font-medium text-gray-700">
              비밀번호
            </label>
            <input
              id="password"
              type="password"
              placeholder="비밀번호를 입력하세요"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-black"
              required
            />
          </div>

          {/* 이메일 입력 */}
          <div className="flex flex-col gap-1">
            <label htmlFor="email" className="text-sm font-medium text-gray-700">
              이메일
            </label>
            <input
              id="email"
              type="email"
              value={email}
              placeholder="이메일을 입력하세요"
              onChange={(e) => setEmail(e.target.value)}
              className="p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-black"
              required
            />
          </div>


          {/* 가입 버튼 */}
          <button
            type="submit"
            className="mt-4 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors font-semibold"
          >
            가입하기
          </button>
        </form>
      </div>
    </div>
  );
}