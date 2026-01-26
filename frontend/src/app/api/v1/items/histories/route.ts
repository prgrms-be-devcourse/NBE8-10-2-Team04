import { NextResponse } from "next/server";

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type HistoryItem = {
  id: number;
  itemName: string;
  timestamp: string; // "YYYY-MM-DD HH:mm"
};

// ✅ mock 이력 데이터 (행위 표현 제거)
const mockHistories: HistoryItem[] = [
  {
    id: 1,
    itemName: "파워 소드",
    timestamp: "2026-01-16 14:30",
  },
  {
    id: 2,
    itemName: "파워 블래스터",
    timestamp: "2026-01-16 13:15",
  },
  {
    id: 3,
    itemName: "메가조드",
    timestamp: "2026-01-16 12:00",
  },
  {
    id: 4,
    itemName: "구형 통신기",
    timestamp: "2026-01-16 11:45",
  },
  {
    id: 5,
    itemName: "파워 코인",
    timestamp: "2026-01-16 10:30",
  },
  {
    id: 6,
    itemName: "파워 소드",
    timestamp: "2026-01-15 16:20",
  },
];

export async function GET() {
  const body: RsData<HistoryItem[]> = {
    resultCode: "200-1",
    msg: "아이템 이력 조회 성공 (MOCK)",
    data: mockHistories,
  };

  return NextResponse.json(body, { status: 200 });
}
