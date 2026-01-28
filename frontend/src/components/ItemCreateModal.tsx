import { useState, useEffect, useRef } from "react";
import { useToast } from '@/contexts/ToastContext';
import { FolderOpenDotIcon, X, Upload, Image as ImageIcon } from "lucide-react";

type Category = {
  id: number;
  name: string;
};

interface ItemCreateFormProps {
  onClose: () => void; // 모달 닫기 함수
  onCreate: () => void; // 생성 완료 시 실행할 함수
}

export default function ItemCreateModal({ onClose, onCreate }: ItemCreateFormProps) {
  // 서울 기준 오늘 날짜를 구하는 함수
  const getSeoulToday = () => {
    const now = new Date();
    // Intl API를 사용해 Asia/Seoul 시간대의 날짜만 추출
    // 'en-CA' 로케일은 YYYY-MM-DD 형식을 반환함
    return new Intl.DateTimeFormat('en-CA', {
      timeZone: 'Asia/Seoul',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).format(now);
  };

  // 카테고리 상태 관리
  const [categories, setCategories] = useState<Category[]>([]);

  // 폼 상태 관리
  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [imgUrl, setImgUrl] = useState('')
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [startDate, setStartDate] = useState(getSeoulToday());
  const [cycleValue, setCycleValue] = useState('');
  const [cycleUnit, setCycleUnit] = useState('m'); // 기본값 'm' (월)

  // input값 에러 여부 관리
  const [nameError, setNameError] = useState('');
  const [categoryError, setCategoryError] = useState('');
  const [cycleError, setCycleError] = useState('');
  const { showToast } = useToast();

  const fileInputRef = useRef<HTMLInputElement>(null);

  // 카테고리 목록 불러오기
  const fetchCategories = async () => {
    try {
      const categoryResponse = await fetch(`http://localhost:8080/api/v1/categories`, { credentials: 'include' });
      if (categoryResponse.ok) {
        const categoryData = await categoryResponse.json();

        // 카테고리 설정
        setCategories(categoryData.data);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);
  
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      // 미리보기 URL 생성
      const objectUrl = URL.createObjectURL(file);
      setPreview(objectUrl);
      // 파일 올리면 URL 입력칸은 비워주는 UX (선택사항)
      setImgUrl("");
    }
  };

  // [추가] 파일 선택 취소
  const clearFile = () => {
    setImageFile(null);
    setPreview(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleSave = async () => {
    // 에러 초기화
    setNameError('');
    setCategoryError('');
    setCycleError('');
    let isValid = true;

    // 이름 검증
    if (!name || name.trim() === '') {
      setNameError('아이템 이름을 입력해주세요.');
      isValid = false;
    }

    // 주기 검증
    if (!cycleValue || Number(cycleValue) < 1) {
      setCycleError('주기는 숫자 1 이상이어야 합니다.');
      isValid = false;
    }

    // 카테고리 검증
    if (!categoryId) {
      setCategoryError('카테고리를 선택해주세요.');
      isValid = false;
    }

    if (!isValid) return;

    const formData = new FormData();
    formData.append("name", name);
    formData.append("categoryId", String(categoryId));
    formData.append("startDate", startDate);
    formData.append("cycleDays", `${cycleValue}${cycleUnit}`);
    
    // 파일이 있으면 파일 추가
    if (imageFile) {
      formData.append("image", imageFile); 
    }
    
    // URL이 있으면 URL 추가 (백엔드 로직에 따라 파일이 우선순위)
    if (imgUrl) {
      formData.append("imgUrl", imgUrl);
    }

    // 등록 요청
    try {
      const response = await fetch('http://localhost:8080/api/v1/items', {
        method: 'POST',
        credentials: 'include',
        body: formData,
      });
      if (response.ok) {
        showToast('success', '등록이 완료되었습니다.');
        onCreate();
        onClose();
      }
    } catch (error) {
      console.error('아이템 등록 오류 발생:', error);
      showToast('error', '아이템 등록에 실패했습니다.');
    }
  };

  return (
    // 모달 배경 overlay
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
      {/* 모달 컨테이너 */}
      <div className="relative w-full max-w-md rounded-2xl border-2 border-green-500 bg-[#0a0a0a] p-6 shadow-2xl">
        {/* 헤더 */}
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-bold text-white">새 아이템 등록</h2>
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
                nameError ? 'border-red-500' : 'border-gray-800 focus:border-green-500'
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
                value={categoryId ?? ''}
                onChange={(e) => {
                  const v = e.target.value;
                  setCategoryId(v === '' ? null : Number(v));
                  if (categoryError) setCategoryError('');
                }}
                className={`w-full appearance-none rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${
                  categoryError ? 'border-red-500' : 'border-gray-800 focus:border-green-500'
                }`}
              >
                <option value="">카테고리를 선택해주세요</option>

                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>

              <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-gray-400">▼</div>
            </div>
            {categoryError && (
              <p className="mt-1.5 flex items-center gap-1 text-xs text-red-500">
                <span className="text-yellow-400">⚠️</span> {categoryError}
              </p>
            )}
          </div>

          {/* 이미지 URL */}
          <div className="space-y-3">
            <label className="block text-sm font-medium text-white">이미지 (선택)</label>
            
            {/*  파일 업로드  */}
            <div className="flex items-start gap-4">
              {/* 미리보기 박스 */}
              <div 
                className={`relative flex h-20 w-20 flex-shrink-0 items-center justify-center overflow-hidden rounded-lg border ${preview ? 'border-green-500' : 'border-dashed border-gray-600 bg-[#161b26]'}`}
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
                  id="image-upload"
                />
                <label 
                  htmlFor="image-upload"
                  className="inline-flex cursor-pointer items-center gap-2 rounded-lg bg-gray-800 px-4 py-2 text-sm text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
                >
                  <Upload size={16} />
                  파일 선택
                </label>
                <p className="mt-1 text-xs text-gray-500">jpg, png, webp (최대 10MB)</p>
              </div>
            </div>

            <div className="relative flex items-center py-1">
                <div className="flex-grow border-t border-gray-800"></div>
                <span className="flex-shrink-0 mx-2 text-xs text-gray-500">OR</span>
                <div className="flex-grow border-t border-gray-800"></div>
            </div>

            {/* URL 입력 영역 */}
            <input
              type="text"
              value={imgUrl}
              onChange={(e) => {
                setImgUrl(e.target.value);
                // URL 입력 시 파일 선택 해제 (UX 선택사항)
                if (e.target.value && imageFile) clearFile(); 
              }}
              className="w-full rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white placeholder-gray-600 focus:border-green-500 focus:outline-none text-sm"
              placeholder="이미지 주소를 직접 입력하세요 (https://...)"
            />
          </div>

          {/* 시작일 */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-white">시작일</label>
            <div className="relative">
              <input
                type="date" // 달력 기능 내장, yyyy-mm-dd으로 자동 반환
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="w-full rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white focus:border-green-500 focus:outline-none scheme-dark"
              />
            </div>
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
                className={`flex-1 rounded-lg border bg-[#161b26] p-3 text-white focus:outline-none ${cycleError ? 'border-red-500' : 'border-gray-800 focus:border-green-500'}`}
                placeholder="1"
              />
              <div className="relative flex-1">
                <select
                  value={cycleUnit}
                  onChange={(e) => setCycleUnit(e.target.value)}
                  className="w-full appearance-none rounded-lg border border-gray-800 bg-[#161b26] p-3 text-white focus:border-green-500 focus:outline-none"
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

        {/* 등록 버튼 */}
        <button
          onClick={handleSave}
          className="mt-8 w-full rounded-lg bg-[#00A63E] py-3 text-lg font-bold text-white transition-colors hover:bg-[#008236] cursor-pointer"
        >
          등록
        </button>
      </div>
    </div>
  );
}
