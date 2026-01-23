import { NextResponse } from "next/server";

type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

type CategoryResponse = {
  id: number;
  name: string;
};

const mockCategories: CategoryResponse[] = [
  { id: 1, name: "식품" },
  { id: 2, name: "생활" },
  { id: 3, name: "주방" },
  { id: 4, name: "욕실" },
];

export async function GET() {
  const body: RsData<CategoryResponse[]> = {
    resultCode: "200-1",
    msg: "카테고리 조회 성공(MOCK)",
    data: mockCategories,
  };

  return NextResponse.json(body, { status: 200 });
}
