import { NextResponse, NextRequest } from 'next/server';
import { headers } from 'next/headers';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function GET(request: NextRequest) {
  try {
    const h = await headers();
    const cookie = h.get('cookie') ?? '';

    const { searchParams } = new URL(request.url);
    const limit = searchParams.get('limit') || '10';

    const targetUrl = `${BASE_URL}/api/v1/items/statistics/most-replaced?limit=${limit}`;
    console.log('Fetching from Spring Boot:', targetUrl);

    const res = await fetch(targetUrl, {
      method: 'GET',
      cache: 'no-store',
      headers: {
        'Content-Type': 'application/json',
        ...(cookie ? { Cookie: cookie } : {}),
      },
    });

    const contentType = res.headers.get('content-type');
    if (contentType && contentType.includes('text/html')) {
      const text = await res.text();
      console.error('Back-end returned HTML instead of JSON:', text.substring(0, 200));
      return NextResponse.json({ message: '백엔드 서버 에러(HTML 반환)' }, { status: 500 });
    }

    const data = await res.json();
    return NextResponse.json(data, { status: res.status });
  } catch (error: any) {
    console.error('Most replaced items API Route Error:', error);
    return NextResponse.json({ resultCode: '500-1', message: error.message, data: null }, { status: 500 });
  }
}
