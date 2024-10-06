import { BACKEND_URL } from '@/config/config';
import axios, { AxiosInstance } from 'axios';
import { getCookie, setCookie, deleteCookie } from 'cookies-next';
import { IncomingMessage, ServerResponse } from 'http';
import Swal from 'sweetalert2';

// 로그인이 필요없는 axios
export const defaultAxios: AxiosInstance = axios.create({
  baseURL: `${BACKEND_URL}`,
  withCredentials: true, // 쿠키 전송 허용
});

// 토큰 재발급
export const refreshUserAPI = async (context?: {
  req: IncomingMessage;
  res: ServerResponse;
}): Promise<string | null> => {
  try {
    const res = await defaultAxios.post(`auth/reissue`);
    const newToken: string = res.headers.accesstoken;
    return newToken;
  } catch (err) {
    deleteCookie(
      'accessToken',
      context ? { req: context.req, res: context.res } : {},
    );
    // 추후 다른 alert로 수정
    if (typeof window !== 'undefined') {
      alert('로그인 시간이 만료되었습니다. 다시 로그인해주세요.');
      window.location.href = '/';
    }
    return null;
  }
};

// 로그인 후 사용 가능한 axios 인스턴스 생성
export const createAuthAxios = (context?: {
  req: IncomingMessage;
  res: ServerResponse;
}): AxiosInstance => {
  const accessToken = getCookie(
    'accessToken',
    context
      ? {
          req: context.req,
          res: context.res,
        }
      : {},
  );

  const instance = axios.create({
    baseURL: `${BACKEND_URL}`,
    headers: {
      Authorization: `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
    withCredentials: true, // 쿠키 전송 허용
  });

  // Axios 인터셉터 설정
  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response.status === 401) {
        const newToken = context
          ? await refreshUserAPI(context)
          : await refreshUserAPI();
        if (newToken) {
          // 새로운 토큰으로 헤더 업데이트
          const updatedConfig = {
            ...error.config,
            headers: {
              ...error.config.headers,
              Authorization: `Bearer ${newToken}`, // 새로운 토큰 설정
            },
          };

          setCookie(
            'accessToken',
            newToken,
            context
              ? {
                  req: context.req,
                  res: context.res,
                  maxAge: 60 * 60 * 24 * 14,
                  path: '/',
                }
              : {
                  maxAge: 60 * 60 * 24 * 14,
                  path: '/',
                },
          );

          return axios(updatedConfig); // 요청 재시도
        }
      }
      return Promise.reject(error);
    },
  );

  return instance;
};

export const sweetalertConfirm = (header: string, content: string) => {
  Swal.fire({
    title: header,
    text: content,
    icon: 'success',
    confirmButtonText: '확인',
  });
};

export const sweetalertError = (header: string, content: string) => {
  Swal.fire({
    title: header,
    text: content,
    icon: 'error',
    confirmButtonColor: 'var(--RED)',
    confirmButtonText: '확인',
  });
};
