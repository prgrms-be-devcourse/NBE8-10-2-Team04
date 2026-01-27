import { NextResponse } from "next/server";

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type HistoryItem = {
  id: number | string;
  itemId: number | string;
  itemName: string;
  startDate: string;
  usedDays: number | null;
};

// mock 이력 데이터 (행위 표현 제거)
const mockHistories: HistoryItem[] = [
  {
    id: 1,
    itemId: 101,
    itemName: "파워 소드",
    startDate: "2026-01-16",
    usedDays: null, // 사용 중
  },
  {
    id: 2,
    itemId: 102,
    itemName: "파워 블래스터",
    startDate: "2026-01-16",
    usedDays: 3,
  },
  {
    id: 3,
    itemId: 103,
    itemName: "메가조드",
    startDate: "2026-01-16",
    usedDays: 7,
  },
  {
    id: 4,
    itemId: 104,
    itemName: "구형 통신기",
    startDate: "2026-01-15",
    usedDays: 1,
  },
  {
    id: 5,
    itemId: 105,
    itemName: "파워 코인",
    startDate: "2026-01-15",
    usedDays: 10,
  },
  {
    id: 6,
    itemId: 101,
    itemName: "파워 소드",
    startDate: "2026-01-14",
    usedDays: 2,
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
