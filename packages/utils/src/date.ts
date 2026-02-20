export const formatDate = (date: Date | string): string => {
  if (!date) return "";
  return new Date(date).toLocaleDateString("vi-VN");
};

export const formatDateTime = (date: Date | string): string => {
  if (!date) return "";
  return new Date(date).toLocaleString("vi-VN");
};
