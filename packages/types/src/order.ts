export enum OrderStatus {
  PENDING = "PENDING",
  CONFIRMED = "CONFIRMED",
  PREPARING = "PREPARING",
  READY = "READY",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
}

export enum OrderItemStatus {
  PENDING = "PENDING",
  PREPARING = "PREPARING",
  READY = "READY",
  SERVED = "SERVED",
}

export enum PaymentStatus {
  UNPAID = "UNPAID",
  PARTIAL = "PARTIAL",
  PAID = "PAID",
  REFUNDED = "REFUNDED",
}

export enum PaymentMethod {
  CASH = "CASH",
  MOMO = "MOMO",
  VNPAY = "VNPAY",
  BANK_TRANSFER = "BANK_TRANSFER",
}

export interface OrderItem {
  id: string;
  menuItemId: string;
  itemName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  status: OrderItemStatus;
  specialInstructions?: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  tableId: string;
  tableNumber: string;
  customerName?: string;
  customerPhone?: string;
  status: OrderStatus;
  subtotal: number;
  discountAmount: number;
  taxAmount: number;
  totalAmount: number;
  paymentMethod?: PaymentMethod;
  paymentStatus: PaymentStatus;
  paidAt?: string;
  specialInstructions?: string;
  createdAt: string;
  items: OrderItem[];
}
