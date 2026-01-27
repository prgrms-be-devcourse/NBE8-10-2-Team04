import { useState, useEffect } from "react";
import { X } from "lucide-react"; // X 아이콘을 위해 lucide-react 사용 (없을 경우 일반 텍스트 'X'로 대체 가능)

type ItemDetail = {
  id: number;
  name: string;
  categoryId: number;
  categoryName: string | null;
  cycleDays: string | null;
  startDate: string | null;
  nextReplacementDate: string | null;
  dDay: number;
  isActive: boolean;
  imgUrl: string | null;
};

type Category = {
  id: number;
  name: string;
}

interface ItemModifyFormProps {
  itemId: number;
  onClose: () => void; // 모달 닫기 함수
  onUpdate: () => void; // 업데이트 시 실행할 함수
}

export default function ItemModifyForm({ itemId, onClose, onUpdate }: ItemModifyFormProps) {
  const [item, setItem] = useState<ItemDetail | null>(null);
  const [isItemLoading, setIsItemLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);

  // 폼 상태 관리
  const [name, setName] = useState("");
  const [categoryId, setCategoryId] = useState<number | string>("");
  const [imgUrl, setImgUrl] = useState("")
  const [cycleValue, setCycleValue] = useState("");
  const [cycleUnit, setCycleUnit] = useState("m"); // 기본값 'm' (월)

  // input값 에러 여부 관리
  const [nameError, setNameError] = useState("");
  const [cycleError, setCycleError] = useState("");


  // cycleDays 파싱 함수 ('3d' -> {value: '3', unit: 'd'})
  const parseCycleDays = (cycle: string | null) => {
    if (!cycle) return { value: "", unit: "m" };
    const value = cycle.replace(/[^0-9]/g, "");
    const unit = cycle.replace(/[0-9]/g, "");
    return { value, unit: unit || "m" };
  };

  // 아이템 정보 불러오기
  const fetchItem = async () => {
    try {
      // 카테고리 목록과 아이템 상세 정보를 동시에 요청
      const [categoryResponse, itemResponse] = await Promise.all([
        fetch(`http://localhost:8080/api/v1/categories`, { credentials: "include" }),
        fetch(`http://localhost:8080/api/v1/items/${itemId}`, { credentials: "include" })
      ]);

      if (categoryResponse.ok && itemResponse.ok) {
        const categoryData = await categoryResponse.json();
        const itemData = await itemResponse.json();

        // 카테고리 설정
        setCategories(categoryData.data);

        // 아이템 설정
        const item: ItemDetail = itemData.data;
        setItem(item);
        setName(item.name);
        setCategoryId(item.categoryId || 1);
        setImgUrl(item.imgUrl || "");
        const { value, unit } = parseCycleDays(item.cycleDays);
        setCycleValue(value);
        setCycleUnit(unit);
      }
    } catch (error) {
      console.error("Error fetching item:", error);
    } finally {
      setIsItemLoading(false);
    }
  };

  useEffect(() => {
    fetchItem();
  }, [itemId]);

  const handleSave = async () => {
    // 에러 초기화
    setNameError("");
    setCycleError("");
    let isValid = true;

    // 이름 검증
    if (!name || name.trim() === '') {
      setNameError('상품 이름을 입력해주세요.');
      isValid = false;
    }

    // 주기 검증
    if (!cycleValue || Number(cycleValue) < 1) {
      setCycleError('주기는 숫자 1 이상이어야 합니다.');
      isValid = false;
    }

    if (!isValid) return;

    // 수정 요청
    try {
      const response = await fetch(`http://localhost:8080/api/v1/items/${itemId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          name,
          categoryId: Number(categoryId),
          imgUrl,
          cycleDays: `${cycleValue}${cycleUnit}`,
          isActive: item?.isActive
        })
      });
      if (response.ok) {
        alert('수정이 완료되었습니다.');
        onUpdate(); // 데이터 갱신 요청
        onClose(); // 모달 닫기 요청
      }
    } catch (error) {
      console.error('아이템 수정 오류 발생:', error);
    }
  };

  if (isItemLoading)
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
        <div className="text-white">로딩 중...</div>
      </div>
    );

  if (!item)
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm">
        <div className="text-white">아이템을 찾을 수 없습니다</div>
      </div>
    );

  return (
    // 모달 배경 overlay
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
      {/* 모달 컨테이너 */}
      <div className="relative w-full max-w-md rounded-2xl border-2 border-orange-500 bg-[#0a0a0a] p-6 shadow-2xl">
        {/* 헤더 */}
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-bold text-white">아이템 수정</h2>
          <button onClick={onClose} className="text-white hover:opacity-70 cursor-pointer">
            <X size={24} />
          </button>
        </div>

        {/* 폼 섹션 */}
        <div className="space-y-4">
          {/* 이름 */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-white">이름</label>
            <input
              type="text"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                if (nameError) setNameError(""); // 입력 시 에러 초기화
              }}
              className={`w-full rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${nameError ? "border-red-500" : "border-gray-800 focus:border-orange-500"
                }`}
              placeholder='이름'
            />
            {nameError && (
              <p className="mt-1.5 flex items-center gap-1 text-xs text-red-500">
                <span className="text-yellow-400">⚠️</span> {nameError}
              </p>
            )}
          </div>

          {/* 카테고리 */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-white">카테고리</label>
            <div className="relative">
              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="w-full appearance-none rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white focus:border-orange-500 focus:outline-none"
              >
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
              <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">
                ▼
              </div>
            </div>
          </div>

          {/* 이미지 URL */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-white">이미지 URL (선택)</label>
            <input
              type="text"
              value={imgUrl}
              onChange={(e) => setImgUrl(e.target.value)}
              className="w-full rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white focus:border-orange-500 focus:outline-none"
              placeholder="https://example.com/img.jpg"
            />
          </div>

          {/* 교체 주기 */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-white">교체 주기</label>
            <div className="flex gap-2">
              <input
                type="number"
                value={cycleValue}
                min="1"
                onChange={(e) => {
                  setCycleValue(e.target.value);
                  if (cycleError) setCycleError(""); // 입력 시 에러 초기화
                }}
                className={`flex-1 rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${cycleError ? "border-red-500" : "border-gray-800 focus:border-orange-500"}`}
                placeholder="1"
              />
              <div className="relative flex-1">
                <select
                  value={cycleUnit}
                  onChange={(e) => setCycleUnit(e.target.value)}
                  className="w-full appearance-none rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white focus:border-orange-500 focus:outline-none"
                >
                  <option value="d">일</option>
                  <option value="m">개월</option>
                  <option value="y">년</option>
                </select>
                <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">
                  ▼
                </div>
              </div>
            </div>
            {cycleError && (
              <p className="mt-1.5 flex items-center gap-1 text-xs text-red-500">
                <span className="text-yellow-400">⚠️</span> {cycleError}
              </p>
            )}
          </div>
        </div>

        {/* 저장 버튼 */}
        <button
          onClick={handleSave}
          className="mt-8 w-full rounded-lg bg-[#b45309] py-3 text-lg font-bold text-white transition-colors hover:bg-[#92400e] cursor-pointer"
        >
          저장
        </button>
      </div>
    </div>
  );
}