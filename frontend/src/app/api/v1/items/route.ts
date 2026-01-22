import { NextResponse } from "next/server";

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type ItemSummaryResponse = {
  id: number;
  name: string;
  categoryName: string | null;
  nextReplacementDate: string | null; // "YYYY-MM-DD"
  imgUrl: string | null;
  dDay: number;
  isActive: boolean;
};

// ✅ mock 데이터에 categoryId를 추가로 들고 있고(프론트에는 안 내려도 됨)
// query로 들어온 categoryId로 필터링만 해줄게.
type MockItem = ItemSummaryResponse & { categoryId: number | null };

const mockItems: MockItem[] = [
  {
    id: 1,
    name: "칫솔",
    categoryId: 4,
    categoryName: "욕실",
    nextReplacementDate: "2026-04-01",
    imgUrl: null,
    dDay: 7,
    isActive: true,
  },
  {
    id: 2,
    name: "행주",
    categoryId: 3,
    categoryName: "주방",
    nextReplacementDate: "2026-03-15",
    imgUrl: null,
    dDay: 3,
    isActive: true,
  },
  {
    id: 3,
    name: "샴푸",
    categoryId: 4,
    categoryName: "욕실",
    nextReplacementDate: "2026-05-01",
    imgUrl: null,
    dDay: 30,
    isActive: false,
  },
  {
    id: 4,
    name: "라면",
    categoryId: 1,
    categoryName: "식품",
    nextReplacementDate: null,
    imgUrl: null,
    dDay: 0,
    isActive: true,
  },
  {
    id: 5,
    name: "세제",
    categoryId: 2,
    categoryName: "생활",
    nextReplacementDate: "2026-02-01",
    imgUrl: null,
    dDay: -5,
    isActive: true,
  },
];

export async function GET(request: Request) {
  const url = new URL(request.url);
  const categoryIdParam = url.searchParams.get("categoryId");
  const categoryId = categoryIdParam ? Number(categoryIdParam) : null;

  const filtered = categoryId
    ? mockItems.filter((i) => i.categoryId === categoryId)
    : mockItems;

  // categoryId는 백 DTO에 없으니 내려줄 땐 제거
  const data: ItemSummaryResponse[] = filtered.map(({ categoryId: _cid, ...rest }) => rest);

  const body: RsData<ItemSummaryResponse[]> = {
    resultCode: "200-1",
    msg: "아이템 목록 조회 성공(MOCK)",
    data,
  };

  return NextResponse.json(body, { status: 200 });
}
