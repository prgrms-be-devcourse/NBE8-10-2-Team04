import { useState, useEffect, useRef } from 'react';
import { X, Upload, Image as ImageIcon } from 'lucide-react'; // 아이콘 추가
import { useToast } from '@/contexts/ToastContext';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

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
};

interface ItemModifyFormProps {
  itemId: number;
  onClose: () => void;
  onUpdate: () => void;
}

export default function ItemModifyModal({ itemId, onClose, onUpdate }: ItemModifyFormProps) {
  const { showToast } = useToast();
  const [item, setItem] = useState<ItemDetail | null>(null);
  const [isItemLoading, setIsItemLoading] = useState(true);
  const [categories, setCategories] = useState<Category[]>([]);

  // 폼 상태 관리
  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState<number | string>('');
  
  // [변경] 이미지 관련 상태
  const [imgUrl, setImgUrl] = useState('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  const [cycleValue, setCycleValue] = useState('');
  const [cycleUnit, setCycleUnit] = useState('m');

  // input값 에러 여부 관리
  const [nameError, setNameError] = useState('');
  const [cycleError, setCycleError] = useState('');

  const fileInputRef = useRef<HTMLInputElement>(null);

  // cycleDays 파싱 함수 ('3d' -> {value: '3', unit: 'd'})
  const parseCycleDays = (cycle: string | null) => {
    if (!cycle) return { value: '', unit: 'm' };
    const value = cycle.replace(/[^0-9]/g, '');
    const unit = cycle.replace(/[0-9]/g, '');
    return { value, unit: unit || 'm' };
  };

  // 아이템 정보 불러오기
  const fetchItem = async () => {
    try {
      const [categoryResponse, itemResponse] = await Promise.all([
        fetch(`${API_BASE}/api/v1/categories`, { credentials: 'include' }),
        fetch(`${API_BASE}/api/v1/items/${itemId}`, { credentials: 'include' }),
      ]);

      if (categoryResponse.ok && itemResponse.ok) {
        const categoryData = await categoryResponse.json();
        const itemData = await itemResponse.json();

        setCategories(categoryData.data);

        const item: ItemDetail = itemData.data;
        setItem(item);
        setName(item.name);
        setCategoryId(item.categoryId || 1);
        
        // [중요] 기존 이미지가 있으면 미리보기와 URL 상태에 세팅
        if (item.imgUrl) {
            setImgUrl(item.imgUrl);
            setPreview(item.imgUrl);
        }
        
        const { value, unit } = parseCycleDays(item.cycleDays);
        setCycleValue(value);
        setCycleUnit(unit);
      }
    } catch (error) {
      console.error('Error fetching item:', error);
    } finally {
      setIsItemLoading(false);
    }
  };

  useEffect(() => {
    fetchItem();
  }, [itemId]);

  // [추가] 파일 선택 핸들러
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      const objectUrl = URL.createObjectURL(file);
      setPreview(objectUrl);
      setImgUrl(""); // 파일 선택 시 URL 입력칸 비움
    }
  };

  // [추가] 파일/미리보기 초기화 핸들러
  const clearFile = () => {
    setImageFile(null);
    setPreview(null);
    setImgUrl(""); // URL도 초기화
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };


  const handleSave = async () => {
    // 에러 초기화
    setNameError('');
    setCycleError('');
    let isValid = true;

    if (!name || name.trim() === '') {
      setNameError('아이템 이름을 입력해주세요.');
      isValid = false;
    }
    if (!cycleValue || Number(cycleValue) < 1) {
      setCycleError('주기는 숫자 1 이상이어야 합니다.');
      isValid = false;
    }

    if (!isValid) return;

    // [변경] FormData 사용 (파일 업로드 대응)
    const formData = new FormData();
    formData.append("name", name);
    formData.append("categoryId", String(categoryId));
    formData.append("cycleDays", `${cycleValue}${cycleUnit}`);
    // isActive는 기존 값 유지 혹은 폼에서 제어 (현재 폼엔 isActive 제어 없음)
    if (item) {
        formData.append("isActive", String(item.isActive));
    }

    // 파일이 있으면 파일 추가
    if (imageFile) {
        formData.append("image", imageFile);
    }
    // URL이 있으면 URL 추가 (파일이 없을 때만 유효)
    if (imgUrl) {
        formData.append("imgUrl", imgUrl);
    }

    try {
      const response = await fetch(`${API_BASE}/api/v1/items/${itemId}`, {
        method: 'PUT', // 또는 PATCH (백엔드 구현에 따름)
        // [중요] Content-Type 헤더 삭제 (FormData 자동 설정)
        credentials: 'include',
        body: formData, 
      });

      if (response.ok) {
        showToast('success', '수정이 완료되었습니다.');
        onUpdate();
        onClose();
      }
    } catch (error) {
      console.error('아이템 수정 오류 발생:', error);
      showToast('error', '아이템 수정에 실패했습니다.');
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
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
      <div className="relative w-full max-w-md rounded-2xl border-2 border-orange-500 bg-[#0a0a0a] p-6 shadow-2xl max-h-[90vh] overflow-y-auto">
        
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
                if (nameError) setNameError(''); // 입력 시 에러 초기화
              }}
              className={`w-full rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${
                nameError ? 'border-red-500' : 'border-gray-800 focus:border-orange-500'
              }`}
              placeholder="이름"
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
              <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">▼</div>
            </div>
          </div>

          {/* [변경] 이미지 섹션 (파일 업로드 + URL) - 주황색 테마 적용 */}
          <div className="space-y-3">
            <label className="block text-sm font-medium text-white">이미지 (선택)</label>
            
            <div className="flex items-start gap-4">
              {/* 미리보기 박스 */}
              <div 
                className={`relative flex h-20 w-20 flex-shrink-0 items-center justify-center overflow-hidden rounded-lg border ${preview ? 'border-orange-500' : 'border-dashed border-gray-600 bg-[#161b26]'}`}
              >
                {preview ? (
                  <>
                    <img src={preview} alt="Preview" className="h-full w-full object-cover" />
                    <button 
                      onClick={clearFile}
                      className="absolute inset-0 flex items-center justify-center bg-black/50 opacity-0 hover:opacity-100 transition-opacity"
                    >
                      <X className="text-white" size={20} />
                    </button>
                  </>
                ) : (
                  <ImageIcon className="text-gray-500" size={24} />
                )}
              </div>

              <div className="flex-1">
                <input
                  type="file"
                  accept="image/*"
                  ref={fileInputRef}
                  onChange={handleFileChange}
                  className="hidden"
                  id="modify-image-upload"
                />
                <label 
                  htmlFor="modify-image-upload"
                  className="inline-flex cursor-pointer items-center gap-2 rounded-lg bg-gray-800 px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
                >
                  <Upload size={16} />
                  파일 변경
                </label>
                <p className="mt-1 text-xs text-gray-500">jpg, png, webp (최대 10MB)</p>
              </div>
            </div>

            <div className="relative flex items-center py-1">
                <div className="flex-grow border-t border-gray-800"></div>
                <span className="flex-shrink-0 mx-2 text-xs text-gray-500">OR</span>
                <div className="flex-grow border-t border-gray-800"></div>
            </div>

            {/* URL 입력 */}
            <input
              type="text"
              value={imgUrl}
              onChange={(e) => {
                setImgUrl(e.target.value);
                if (e.target.value && imageFile) clearFile(); 
              }}
              className="w-full rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white placeholder-gray-600 focus:border-orange-500 focus:outline-none text-sm"
              placeholder="이미지 주소 직접 입력"
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
                  if (cycleError) setCycleError(''); // 입력 시 에러 초기화
                }}
                className={`flex-1 rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${cycleError ? 'border-red-500' : 'border-gray-800 focus:border-orange-500'}`}
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
                <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">▼</div>
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
