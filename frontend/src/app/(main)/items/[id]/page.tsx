'use client';

import { useParams } from "next/navigation";

export default function ItemPage() {
  const { id: idStr } = useParams<{ id: string }>();
  const id = parseInt(idStr);

  return <div>ItemPage {id}</div>;
}