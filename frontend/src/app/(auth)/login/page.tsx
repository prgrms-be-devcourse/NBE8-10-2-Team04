"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function Page() {  
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");

    const router = useRouter(); //

    // 폼 제출 핸들러
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      // 1. API 호출
      const response = await fetch("http://localhost:8080/api/v1/user/login", { 
        method: "POST", 
        headers: {
          "Content-Type": "application/json", // JSON 형식으로 보낸다고 명시
        },
        credentials: "include",
        body: JSON.stringify({
          loginId: loginId,
          password: password,
        }),
      });

      // 3. 응답 처리
      if (response.ok) {
        // 성공 시 (Status Code 200~299)
        const data = await response.json();
        console.log("로그인 성공:", data);
        
        

        alert("로그인 되었습니다.");
        router.push("/"); // 메인 페이지로 이동
      } else {
        // 실패 시 (400, 401, 500 등)
        console.error("로그인 실패");
        alert("이메일이나 비밀번호를 확인해주세요.");
      }
    } catch (error) {
      // 네트워크 에러 등 예외 처리
      console.error("에러 발생:", error);
      alert("서버 통신 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="">
        <div className="w-full max-w-sm p-6 bg-white rounded-lg shadow-md border">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">로그인</h2>
        
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          {/* 아이디 입력 */}
          <div className="flex flex-col gap-1">
            <label htmlFor="email" className="text-sm font-medium text-gray-700">
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

          {/* 로그인 버튼 */}
          <button
            type="submit"
            className="mt-4 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors font-semibold"
          >
            로그인하기
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-gray-600">
          계정이 없으신가요?{" "}
          <Link 
            href="/signup" 
            className="text-blue-600 hover:underline font-medium ml-1"
          >
            회원가입
          </Link>
        </div>

      </div>
    </div>
  );
}
