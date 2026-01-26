// types/api.ts
export type RsData<T> = {
  resultCode: string;
  msg: string;
  data: T;
};

export type Category = {
  id: number;
  name: string;
};
