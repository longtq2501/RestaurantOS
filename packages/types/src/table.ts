export enum TableStatus {
    EMPTY = "EMPTY",
    OCCUPIED = "OCCUPIED",
    RESERVED = "RESERVED",
    CLEANING = "CLEANING"
}

export interface Table {
    id: string;
    tableNumber: number;
    capacity: number;
    qrCodeToken?: string;
    status: TableStatus;
    section?: string;
    currentOrderId?: string;
    restaurantId: string;
    createdAt: string;
    updatedAt: string;
}

export interface CreateTableRequest {
    tableNumber: number;
    capacity: number;
    section?: string;
}

export interface UpdateTableRequest {
    tableNumber?: number;
    capacity?: number;
    section?: string;
    status?: TableStatus;
}
