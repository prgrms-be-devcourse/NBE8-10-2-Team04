/**
 * 백엔드와 일치하는 유효성 검사 유틸리티
 * 
 * 백엔드 규칙:
 * - 회원가입/로그인: loginId (2-30자), password (2-30자), email (2-30자, @Email)
 * - 회원정보 수정: email (2-30자, @Email), password (2-20자)
 */

export type ValidationResult = {
  isValid: boolean;
  error?: string;
};

/**
 * 이메일 유효성 검사
 * @param email 검사할 이메일
 * @returns ValidationResult
 */
export function validateEmail(email: string): ValidationResult {
  if (!email || email.trim().length === 0) {
    return {
      isValid: false,
      error: "이메일을 입력해주세요.",
    };
  }

  const trimmedEmail = email.trim();

  // 길이 검사 (2-30자)
  if (trimmedEmail.length < 2 || trimmedEmail.length > 30) {
    return {
      isValid: false,
      error: "이메일은 2자 이상 30자 이하여야 합니다.",
    };
  }

  // 이메일 형식 검사
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(trimmedEmail)) {
    return {
      isValid: false,
      error: "올바른 이메일 형식이 아닙니다.",
    };
  }

  return {
    isValid: true,
  };
}

/**
 * 비밀번호 유효성 검사 (회원가입/로그인용: 2-30자)
 * @param password 검사할 비밀번호
 * @returns ValidationResult
 */
export function validatePassword(password: string): ValidationResult {
  if (!password || password.length === 0) {
    return {
      isValid: false,
      error: "비밀번호를 입력해주세요.",
    };
  }

  // 길이 검사 (2-30자)
  if (password.length < 2 || password.length > 30) {
    return {
      isValid: false,
      error: "비밀번호는 2자 이상 30자 이하여야 합니다.",
    };
  }

  return {
    isValid: true,
  };
}

/**
 * 비밀번호 유효성 검사 (회원정보 수정용: 2-20자)
 * @param password 검사할 비밀번호
 * @returns ValidationResult
 */
export function validatePasswordForUpdate(password: string): ValidationResult {
  if (!password || password.length === 0) {
    return {
      isValid: false,
      error: "비밀번호를 입력해주세요.",
    };
  }

  // 길이 검사 (2-20자)
  if (password.length < 2 || password.length > 20) {
    return {
      isValid: false,
      error: "비밀번호는 2자 이상 20자 이하여야 합니다.",
    };
  }

  return {
    isValid: true,
  };
}

/**
 * 로그인 ID 유효성 검사 (2-30자)
 * @param loginId 검사할 로그인 ID
 * @returns ValidationResult
 */
export function validateLoginId(loginId: string): ValidationResult {
  if (!loginId || loginId.trim().length === 0) {
    return {
      isValid: false,
      error: "아이디를 입력해주세요.",
    };
  }

  const trimmedLoginId = loginId.trim();

  // 길이 검사 (2-30자)
  if (trimmedLoginId.length < 2 || trimmedLoginId.length > 30) {
    return {
      isValid: false,
      error: "아이디는 2자 이상 30자 이하여야 합니다.",
    };
  }

  return {
    isValid: true,
  };
}

/**
 * 여러 필드의 유효성 검사를 한번에 수행
 * @param validations ValidationResult 배열
 * @returns 첫 번째 에러가 있으면 해당 에러, 모두 유효하면 null
 */
export function validateMultiple(
  ...validations: ValidationResult[]
): ValidationResult {
  for (const validation of validations) {
    if (!validation.isValid) {
      return validation;
    }
  }
  return { isValid: true };
}

/**
 * 회원가입 폼 전체 유효성 검사
 */
export function validateSignupForm(
  loginId: string,
  password: string,
  email: string
): ValidationResult {
  return validateMultiple(
    validateLoginId(loginId),
    validatePassword(password),
    validateEmail(email)
  );
}

/**
 * 로그인 폼 전체 유효성 검사
 */
export function validateLoginForm(
  loginId: string,
  password: string
): ValidationResult {
  return validateMultiple(validateLoginId(loginId), validatePassword(password));
}

/**
 * 회원정보 수정 폼 전체 유효성 검사
 */
export function validateUpdateForm(
  email: string,
  password: string
): ValidationResult {
  return validateMultiple(
    validateEmail(email),
    validatePasswordForUpdate(password)
  );
}
