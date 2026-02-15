# RESTAURANTOS - BACKEND MODULAR MONOLITH TASKS

## MODULES OVERVIEW

> 1. shared (foundation)
> 2. auth (authentication & user management)
> 3. restaurant (restaurant profile & branding)
> 4. table (table management & QR codes)
> 5. menu (categories & items)
> 6. inventory (ingredients & stock)
> 7. order (orders & order items)
> 8. payment (payment integration)
> 9. analytics (reports & dashboard)
> 10. notification (notification system)
> 11. staff (optional - staff management)
> 12. ai (optional - AI features)

---

MODULE 0: SHARED (Foundation)
Task 0.1: Project Setup

 0.1.1: Create Spring Boot project with dependencies
 0.1.2: Configure application.yml (MySQL, JPA, server port)
 0.1.4: Create shared package


Task 0.2: Shared - Base Entity

 0.2.1: Create shared/entity/BaseEntity.java (id, createdAt, updatedAt)
 0.2.2: Add JPA annotations (@MappedSuperclass, @EntityListeners)
 0.2.3: Enable JPA Auditing in main class


Task 0.3: Shared - Exception Handling

 0.3.1: Create shared/exception/ folder
 0.3.2: Create custom exceptions:

ResourceNotFoundException.java
AlreadyExistsException.java
InvalidInputException.java
UnauthorizedException.java


 0.3.3: Create shared/dto/ErrorResponse.java
 0.3.4: Create shared/dto/ApiResponse.java (generic wrapper)
 0.3.5: Create shared/exception/GlobalExceptionHandler.java (@RestControllerAdvice)
 0.3.6: Handle MethodArgumentNotValidException
 0.3.7: Handle all custom exceptions with proper HTTP status
 0.3.8: Write unit tests for exception handlers


Task 0.4: Shared - Configuration

 0.4.1: Create shared/config/WebConfig.java (CORS configuration)
 0.4.2: Create shared/config/SecurityConfig.java (placeholder, will complete in auth module)
 0.4.3: Create shared/util/ folder for utility classes


MODULE 1: AUTH (Authentication & User Management)
Task 1.1: Auth - Entities

 1.1.1: Create modules/auth/entity/User.java

Fields: username, email, passwordHash, fullName, phone, avatarUrl, role, isActive, lastLoginAt
Relationship: ManyToOne to Restaurant
Unique constraints on username, email


 1.1.2: Create modules/auth/entity/UserRole.java (enum: OWNER, MANAGER, STAFF, KITCHEN)
 1.1.3: Create modules/auth/entity/RefreshToken.java

Fields: token, expiresAt
Relationship: OneToOne to User
Method: isExpired()




Task 1.2: Auth - DTOs

 1.2.1: Create modules/auth/dto/request/RegisterRequest.java

Fields: username, email, password, fullName, restaurantName
Add validation annotations


 1.2.2: Create modules/auth/dto/request/LoginRequest.java
 1.2.3: Create modules/auth/dto/request/RefreshTokenRequest.java
 1.2.4: Create modules/auth/dto/response/AuthResponse.java

Fields: accessToken, refreshToken, userInfo


 1.2.5: Create modules/auth/dto/response/UserResponse.java


Task 1.3: Auth - Repositories

 1.3.1: Create modules/auth/repository/UserRepository.java extends JpaRepository
 1.3.2: Add query methods:

Optional<User> findByUsername(String username)
Optional<User> findByEmail(String email)
Boolean existsByUsername(String username)
Boolean existsByEmail(String email)


 1.3.3: Create modules/auth/repository/RefreshTokenRepository.java
 1.3.4: Add query methods:

Optional<RefreshToken> findByToken(String token)
void deleteByUser(User user)




Task 1.4: Auth - Services

 1.4.1: Create modules/auth/service/JwtService.java

Add JWT properties to application.yml
Method: generateAccessToken(String username)
Method: generateRefreshToken()
Method: getUsernameFromToken(String token)
Method: validateToken(String token)
Write unit tests


 1.4.2: Create modules/auth/service/AuthService.java

Inject: UserRepository, RefreshTokenRepository, PasswordEncoder, JwtService, RestaurantRepository
Method: register(RegisterRequest) → UserResponse
Method: login(LoginRequest) → AuthResponse
Method: refreshToken(String) → AuthResponse
Method: logout(String) → void
Write unit tests for all methods


 1.4.3: Create modules/auth/service/CustomUserDetailsService.java implements UserDetailsService

Override loadUserByUsername()




Task 1.5: Auth - Controllers

 1.5.1: Create modules/auth/controller/AuthController.java
 1.5.2: Create endpoint: POST /api/auth/register
 1.5.3: Create endpoint: POST /api/auth/login
 1.5.4: Create endpoint: POST /api/auth/refresh
 1.5.5: Create endpoint: POST /api/auth/logout
 1.5.6: Create endpoint: GET /api/auth/me (protected)
 1.5.7: Write integration tests for all endpoints


Task 1.6: Auth - Security Configuration

 1.6.1: Create modules/auth/config/JwtAuthenticationFilter.java extends OncePerRequestFilter

Extract JWT from header
Validate token
Set authentication in SecurityContext


 1.6.2: Update shared/config/SecurityConfig.java

Configure SecurityFilterChain
Disable CSRF
Configure session (STATELESS)
Add JWT filter
Define public endpoints
Define PasswordEncoder bean
Define AuthenticationManager bean


 1.6.3: Test security configuration


MODULE 2: RESTAURANT (Restaurant Management)
Task 2.1: Restaurant - Entities

 2.1.1: Create modules/restaurant/entity/Restaurant.java

Fields: name, slug, address, phone, email, logoUrl, themeColor, customDomain, plan, subscriptionExpiresAt, settings (JSON)
Relationship: OneToMany to User
Unique constraint on slug


 2.1.2: Create modules/restaurant/entity/SubscriptionPlan.java (enum: FREE, STARTER, PRO, ENTERPRISE)


Task 2.2: Restaurant - DTOs

 2.2.1: Create modules/restaurant/dto/request/RestaurantUpdateRequest.java
 2.2.2: Create modules/restaurant/dto/response/RestaurantResponse.java
 2.2.3: Add validation annotations


Task 2.3: Restaurant - Repositories

 2.3.1: Create modules/restaurant/repository/RestaurantRepository.java
 2.3.2: Add query methods:

Optional<Restaurant> findBySlug(String slug)
Boolean existsBySlug(String slug)




Task 2.4: Restaurant - Services

 2.4.1: Create modules/restaurant/service/RestaurantService.java

Method: getById(UUID id) → RestaurantResponse
Method: update(UUID id, RestaurantUpdateRequest) → RestaurantResponse
Create mapper methods (Entity ↔ DTO)
Write unit tests


 2.4.2: Create modules/restaurant/service/FileStorageService.java

Add AWS S3 or MinIO dependencies
Configure storage in application.yml
Method: uploadFile(MultipartFile, String folder) → String (URL)
Method: deleteFile(String url) → void
Validate file type and size
Write unit tests




Task 2.5: Restaurant - Controllers

 2.5.1: Create modules/restaurant/controller/RestaurantController.java
 2.5.2: Create endpoint: GET /api/restaurants/{id} with @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
 2.5.3: Create endpoint: PUT /api/restaurants/{id} with @PreAuthorize("hasRole('OWNER')")
 2.5.4: Create endpoint: POST /api/restaurants/{id}/upload-logo

Accept MultipartFile
Call FileStorageService
Update Restaurant entity


 2.5.5: Write integration tests


MODULE 3: TABLE (Table Management)
Task 3.1: Table - Entities

 3.1.1: Create modules/table/entity/Table.java

Fields: tableNumber, capacity, qrCodeToken, status, section, currentOrderId
Relationship: ManyToOne to Restaurant
Unique constraint (restaurantId + tableNumber)


 3.1.2: Create modules/table/entity/TableStatus.java (enum: EMPTY, OCCUPIED, RESERVED, CLEANING)


Task 3.2: Table - DTOs

 3.2.1: Create modules/table/dto/request/TableRequest.java
 3.2.2: Create modules/table/dto/response/TableResponse.java


Task 3.3: Table - Repositories

 3.3.1: Create modules/table/repository/TableRepository.java
 3.3.2: Add query methods:

List<Table> findByRestaurantIdOrderByTableNumberAsc(UUID restaurantId)
Optional<Table> findByQrCodeToken(String token)
Boolean existsByRestaurantIdAndTableNumber(UUID restaurantId, Integer tableNumber)




Task 3.4: Table - Services

 3.4.1: Create modules/table/service/TableService.java

Method: getAll(UUID restaurantId) → List<TableResponse>
Method: getById(UUID id) → TableResponse
Method: create(UUID restaurantId, TableRequest) → TableResponse (generate QR token)
Method: update(UUID id, TableRequest) → TableResponse
Method: delete(UUID id) → void
Write unit tests


 3.4.2: Create modules/table/service/QRCodeService.java

Add ZXing dependency
Method: generateQRCodeImage(String text, int width, int height) → byte[]
Write unit tests


 3.4.3: Create modules/table/service/PDFService.java

Add Apache PDFBox dependency
Method: generateQRCodesPDF(List<Table>, String restaurantSlug) → byte[]
Layout: 2 QR codes per page with labels
Write unit tests




Task 3.5: Table - Controllers

 3.5.1: Create modules/table/controller/TableController.java
 3.5.2: Create endpoint: GET /api/tables (requires restaurantId param)
 3.5.3: Create endpoint: GET /api/tables/{id}
 3.5.4: Create endpoint: POST /api/tables
 3.5.5: Create endpoint: PUT /api/tables/{id}
 3.5.6: Create endpoint: DELETE /api/tables/{id}
 3.5.7: Create endpoint: GET /api/tables/qr-codes (returns PDF)
 3.5.8: Add @PreAuthorize to protected endpoints
 3.5.9: Write integration tests


MODULE 4: MENU (Menu Management)
Task 4.1: Menu - Entities

 4.1.1: Create modules/menu/entity/MenuCategory.java

Fields: name, description, displayOrder, isActive
Relationship: ManyToOne to Restaurant, OneToMany to MenuItem


 4.1.2: Create modules/menu/entity/MenuItem.java

Fields: name, description, price, imageUrl, thumbnailUrl, prepTime, spicyLevel, isVegetarian, allergens, isAvailable, displayOrder, isFeatured, orderCount, ratingAvg, ratingCount
Relationships: ManyToOne to Restaurant, ManyToOne to MenuCategory
Add full-text search index (MySQL FULLTEXT on name + description)




Task 4.2: Menu - DTOs

 4.2.1: Create modules/menu/dto/request/CategoryRequest.java
 4.2.2: Create modules/menu/dto/response/CategoryResponse.java
 4.2.3: Create modules/menu/dto/request/MenuItemRequest.java
 4.2.4: Create modules/menu/dto/response/MenuItemResponse.java


Task 4.3: Menu - Repositories

 4.3.1: Create modules/menu/repository/MenuCategoryRepository.java
 4.3.2: Add query: List<MenuCategory> findByRestaurantIdAndIsActiveTrueOrderByDisplayOrderAsc(UUID restaurantId)
 4.3.3: Create modules/menu/repository/MenuItemRepository.java
 4.3.4: Add queries:

List<MenuItem> findByRestaurantId(UUID restaurantId)
List<MenuItem> findByCategoryId(UUID categoryId)
List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId)
Custom @Query with MySQL FULLTEXT for search




Task 4.4: Menu - Services

 4.4.1: Create modules/menu/service/MenuCategoryService.java with full CRUD
 4.4.2: Write unit tests for CategoryService
 4.4.3: Create modules/menu/service/MenuItemService.java

Method: getAll(UUID restaurantId, filters) → List<MenuItemResponse>
Method: getById(UUID id) → MenuItemResponse
Method: create(MenuItemRequest) → MenuItemResponse
Method: update(UUID id, MenuItemRequest) → MenuItemResponse
Method: delete(UUID id) → void (soft delete)
Method: toggleAvailability(UUID id) → MenuItemResponse
Method: bulkUpdate(List<UUID> ids, action) → void
Method: search(String query) → List<MenuItemResponse>
Write unit tests


 4.4.4: Update FileStorageService to support thumbnail generation

Add Thumbnailator dependency
Generate 300x300 thumbnail on upload




Task 4.5: Menu - Controllers

 4.5.1: Create modules/menu/controller/MenuCategoryController.java
 4.5.2: Create full CRUD endpoints for categories
 4.5.3: Write integration tests
 4.5.4: Create modules/menu/controller/MenuItemController.java
 4.5.5: Create endpoint: GET /api/menu-items (with filters: category, availability, search)
 4.5.6: Create endpoint: GET /api/menu-items/{id}
 4.5.7: Create endpoint: POST /api/menu-items
 4.5.8: Create endpoint: PUT /api/menu-items/{id}
 4.5.9: Create endpoint: DELETE /api/menu-items/{id}
 4.5.10: Create endpoint: POST /api/menu-items/{id}/upload-image
 4.5.11: Create endpoint: PUT /api/menu-items/{id}/availability
 4.5.12: Create endpoint: PUT /api/menu-items/bulk-update
 4.5.13: Add @PreAuthorize to all endpoints
 4.5.14: Write integration tests


MODULE 5: INVENTORY (Inventory Management)
Task 5.1: Inventory - Entities

 5.1.1: Create modules/inventory/entity/Ingredient.java

Fields: name, unit, currentStock, minStock, costPerUnit, supplierName, supplierPhone
Relationship: ManyToOne to Restaurant


 5.1.2: Create modules/inventory/entity/RecipeIngredient.java

Fields: menuItemId, ingredientId, quantity
Composite primary key (menuItemId + ingredientId)
Relationships: ManyToOne to MenuItem, ManyToOne to Ingredient


 5.1.3: Create modules/inventory/entity/InventoryHistory.java

Fields: ingredientId, date, usageQuantity, adjustmentType, reason
Relationship: ManyToOne to Ingredient




Task 5.2: Inventory - DTOs

 5.2.1: Create modules/inventory/dto/request/IngredientRequest.java
 5.2.2: Create modules/inventory/dto/response/IngredientResponse.java
 5.2.3: Create modules/inventory/dto/request/StockAdjustmentRequest.java
 5.2.4: Create modules/inventory/dto/request/RecipeRequest.java
 5.2.5: Create modules/inventory/dto/response/RecipeResponse.java


Task 5.3: Inventory - Repositories

 5.3.1: Create modules/inventory/repository/IngredientRepository.java
 5.3.2: Add query: List<Ingredient> findByRestaurantIdAndCurrentStockLessThanMinStock(UUID restaurantId)
 5.3.3: Create modules/inventory/repository/RecipeIngredientRepository.java
 5.3.4: Add query: List<RecipeIngredient> findByMenuItemId(UUID menuItemId)
 5.3.5: Create modules/inventory/repository/InventoryHistoryRepository.java


Task 5.4: Inventory - Services

 5.4.1: Create modules/inventory/service/IngredientService.java

Full CRUD methods
Method: getLowStockItems(UUID restaurantId) → List<IngredientResponse>
Method: adjustStock(UUID id, StockAdjustmentRequest) → IngredientResponse
Write unit tests


 5.4.2: Create modules/inventory/service/RecipeService.java

Method: getRecipeByMenuItem(UUID menuItemId) → RecipeResponse
Method: saveRecipe(UUID menuItemId, RecipeRequest) → RecipeResponse
Write unit tests


 5.4.3: Create modules/inventory/service/InventoryDeductionService.java

Method: deductInventoryForOrder(UUID orderId) → void
Listen to Order status change event
Deduct stock based on recipe
Log to InventoryHistory
Write unit tests




Task 5.5: Inventory - Controllers

 5.5.1: Create modules/inventory/controller/IngredientController.java
 5.5.2: Create full CRUD endpoints
 5.5.3: Create endpoint: POST /api/ingredients/{id}/adjust
 5.5.4: Create endpoint: GET /api/ingredients/low-stock
 5.5.5: Write integration tests
 5.5.6: Create modules/inventory/controller/RecipeController.java
 5.5.7: Create endpoint: GET /api/menu-items/{id}/recipe
 5.5.8: Create endpoint: POST /api/menu-items/{id}/recipe
 5.5.9: Write integration tests


MODULE 6: ORDER (Order Management)
Task 6.1: Order - Entities

 6.1.1: Create modules/order/entity/Order.java

Fields: orderNumber, tableId, customerPhone, customerName, assignedStaffId, status, subtotal, discountAmount, taxAmount, totalAmount, paymentMethod, paymentStatus, paidAt, specialInstructions, confirmedAt, completedAt, cancelledAt
Relationships: ManyToOne to Restaurant, ManyToOne to Table, OneToMany to OrderItem


 6.1.2: Create modules/order/entity/OrderStatus.java (enum: PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED)
 6.1.3: Create modules/order/entity/OrderItem.java

Fields: orderId, menuItemId, itemName, unitPrice, quantity, subtotal, specialInstructions, status, startedPreparingAt, readyAt, servedAt
Relationships: ManyToOne to Order, ManyToOne to MenuItem


 6.1.4: Create modules/order/entity/OrderItemStatus.java (enum: PENDING, PREPARING, READY, SERVED)


Task 6.2: Order - DTOs

 6.2.1: Create modules/order/dto/request/CreateOrderRequest.java
 6.2.2: Create modules/order/dto/request/UpdateOrderStatusRequest.java
 6.2.3: Create modules/order/dto/request/SplitBillRequest.java
 6.2.4: Create modules/order/dto/response/OrderResponse.java
 6.2.5: Create modules/order/dto/response/OrderItemResponse.java


Task 6.3: Order - Repositories

 6.3.1: Create modules/order/repository/OrderRepository.java
 6.3.2: Add queries:

List<Order> findByRestaurantIdAndStatusOrderByCreatedAtDesc(UUID restaurantId, OrderStatus status)
List<Order> findByRestaurantIdAndCreatedAtBetween(UUID restaurantId, LocalDateTime start, LocalDateTime end)


 6.3.3: Create modules/order/repository/OrderItemRepository.java
 6.3.4: Add queries:

List<OrderItem> findByOrderId(UUID orderId)
List<OrderItem> findByOrderIdAndStatus(UUID orderId, OrderItemStatus status)




Task 6.4: Order - Services

 6.4.1: Create modules/order/service/OrderService.java

Method: getAll(UUID restaurantId, filters) → List<OrderResponse>
Method: getById(UUID id) → OrderResponse
Method: create(CreateOrderRequest) → OrderResponse
Method: updateStatus(UUID id, OrderStatus) → OrderResponse
Method: cancel(UUID id, String reason) → OrderResponse
Method: splitBill(UUID id, SplitBillRequest) → List<OrderResponse>
Generate auto-increment order number per day
Write unit tests


 6.4.2: Create modules/order/service/OrderItemService.java

Method: updateItemStatus(UUID id, OrderItemStatus) → OrderItemResponse
Write unit tests




Task 6.5: Order - Controllers

 6.5.1: Create modules/order/controller/OrderController.java
 6.5.2: Create endpoint: GET /api/orders (with filters)
 6.5.3: Create endpoint: GET /api/orders/{id}
 6.5.4: Create endpoint: POST /api/orders
 6.5.5: Create endpoint: PUT /api/orders/{id}/status
 6.5.6: Create endpoint: PUT /api/orders/{id}/cancel
 6.5.7: Create endpoint: POST /api/orders/{id}/split
 6.5.8: Create endpoint: PUT /api/order-items/{id}/status
 6.5.9: Write integration tests


MODULE 7: PAYMENT (Payment Integration)
Task 7.1: Payment - Entities

 7.1.1: Create modules/payment/entity/Transaction.java

Fields: orderId, amount, paymentMethod, gatewayTransactionId, status, gatewayResponse (JSON)
Relationships: ManyToOne to Order


 7.1.2: Create modules/payment/entity/PaymentMethod.java (enum: CASH, MOMO, VNPAY)
 7.1.3: Create modules/payment/entity/TransactionStatus.java (enum: PENDING, COMPLETED, FAILED, REFUNDED)


Task 7.2: Payment - DTOs

 7.2.1: Create modules/payment/dto/request/MomoPaymentRequest.java
 7.2.2: Create modules/payment/dto/request/VNPayPaymentRequest.java
 7.2.3: Create modules/payment/dto/response/PaymentResponse.java


Task 7.3: Payment - Repositories

 7.3.1: Create modules/payment/repository/TransactionRepository.java
 7.3.2: Add query: Optional<Transaction> findByGatewayTransactionId(String id)


Task 7.4: Payment - Services

 7.4.1: Create modules/payment/service/MomoPaymentService.java

Add Momo SDK or HTTP client
Configure Momo credentials in application.yml
Method: initiatePayment(orderId, amount) → String (payment URL)
Method: handleCallback(callbackData) → Transaction
Write unit tests


 7.4.2: Create modules/payment/service/VNPayPaymentService.java

Add VNPay SDK or HTTP client
Configure VNPay credentials
Method: initiatePayment(orderId, amount) → String (payment URL)
Method: handleCallback(callbackData) → Transaction
Method: handleReturn(returnData) → Transaction
Write unit tests


 7.4.3: Create modules/payment/service/PaymentService.java (orchestrator)

Method: processPayment(orderId, paymentMethod) → PaymentResponse
Delegates to MomoPaymentService or VNPayPaymentService
Update Order paymentStatus
Write unit tests




Task 7.5: Payment - Controllers

 7.5.1: Create modules/payment/controller/PaymentController.java
 7.5.2: Create endpoint: POST /api/payments/momo/initiate
 7.5.3: Create endpoint: POST /api/payments/momo/callback (webhook)
 7.5.4: Create endpoint: POST /api/payments/vnpay/initiate
 7.5.5: Create endpoint: GET /api/payments/vnpay/return
 7.5.6: Create endpoint: POST /api/payments/vnpay/callback (IPN)
 7.5.7: Write integration tests


MODULE 8: ANALYTICS (Reports & Dashboard)
Task 8.1: Analytics - Entities

 8.1.1: Create modules/analytics/entity/DailyReport.java

Fields: restaurantId, reportDate, totalRevenue, totalOrders, avgOrderValue, topSellingItems (JSON), totalCustomers, newCustomers, avgPrepTime
Unique constraint (restaurantId + reportDate)




Task 8.2: Analytics - DTOs

 8.2.1: Create modules/analytics/dto/response/DashboardSummaryResponse.java
 8.2.2: Create modules/analytics/dto/response/RevenueReportResponse.java
 8.2.3: Create modules/analytics/dto/response/TopDishResponse.java


Task 8.3: Analytics - Repositories

 8.3.1: Create modules/analytics/repository/DailyReportRepository.java
 8.3.2: Add query: Optional<DailyReport> findByRestaurantIdAndReportDate(UUID restaurantId, LocalDate date)


Task 8.4: Analytics - Services

 8.4.1: Create modules/analytics/service/DashboardService.java

Method: getSummary(UUID restaurantId) → DashboardSummaryResponse
Calculate today's revenue, order count, top dishes, low stock alerts
Write unit tests


 8.4.2: Create modules/analytics/service/ReportService.java

Method: getRevenueReport(UUID restaurantId, dateRange) → RevenueReportResponse
Method: getTopDishes(UUID restaurantId, period) → List<TopDishResponse>
Method: exportReport(UUID restaurantId, format) → byte[] (CSV/Excel)
Write unit tests


 8.4.3: Create modules/analytics/service/DailyReportGeneratorService.java

Scheduled job (@Scheduled) to run daily at midnight
Aggregate data from orders, transactions
Save to DailyReport entity
Write unit tests




Task 8.5: Analytics - Controllers

 8.5.1: Create modules/analytics/controller/DashboardController.java
 8.5.2: Create endpoint: GET /api/dashboard/summary
 8.5.3: Write integration tests
 8.5.4: Create modules/analytics/controller/ReportController.java
 8.5.5: Create endpoint: GET /api/reports/revenue
 8.5.6: Create endpoint: GET /api/reports/top-dishes
 8.5.7: Create endpoint: GET /api/reports/export
 8.5.8: Write integration tests


MODULE 9: NOTIFICATION (Notification System)
Task 9.1: Notification - Entities

 9.1.1: Create modules/notification/entity/Notification.java

Fields: userId, type, title, message, actionUrl, isRead, readAt
Relationship: ManyToOne to User


 9.1.2: Create modules/notification/entity/NotificationType.java (enum: LOW_STOCK, NEW_ORDER, ORDER_READY, etc.)


Task 9.2: Notification - DTOs

 9.2.1: Create modules/notification/dto/response/NotificationResponse.java


Task 9.3: Notification - Repositories

 9.3.1: Create modules/notification/repository/NotificationRepository.java
 9.3.2: Add query: List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId)


Task 9.4: Notification - Services

 9.4.1: Create modules/notification/service/NotificationService.java

Method: create(userId, type, title, message) → Notification
Method: getAll(userId) → List<NotificationResponse>
Method: markAsRead(notificationId) → void
Method: markAllAsRead(userId) → void
Write unit tests




Task 9.5: Notification - Controllers

 9.5.1: Create modules/notification/controller/NotificationController.java
 9.5.2: Create endpoint: GET /api/notifications
 9.5.3: Create endpoint: PUT /api/notifications/{id}/read
 9.5.4: Create endpoint: PUT /api/notifications/read-all
 9.5.5: Write integration tests


MODULE 10: WEBSOCKET (Real-time Communication)
Task 10.1: WebSocket - Configuration

 10.1.1: Add Spring WebSocket dependencies
 10.1.2: Create shared/config/WebSocketConfig.java

Enable STOMP over SockJS
Configure message broker
Configure endpoints: /ws-kitchen, /ws-orders, /ws-dashboard




Task 10.2: WebSocket - Services

 10.2.1: Create shared/websocket/WebSocketService.java

Method: broadcastToKitchen(restaurantId, message) → void
Method: broadcastToCustomer(orderId, message) → void
Method: broadcastToDashboard(restaurantId, message) → void




Task 10.3: WebSocket - Integration

 10.3.1: Update OrderService to broadcast on order creation
 10.3.2: Update OrderItemService to broadcast on status change
 10.3.3: Update MenuItemService to broadcast on availability toggle
 10.3.4: Write integration tests


MODULE 11: STAFF (Optional - Staff Management)
Task 11.1: Staff - Entities

 11.1.1: Create modules/staff/entity/StaffShift.java
 11.1.2: Create modules/staff/entity/StaffTip.java

Task 11.2: Staff - DTOs, Repositories, Services, Controllers

 11.2.1: Follow same pattern as other modules
 11.2.2: (Defer to Phase 2 or later)


MODULE 12: AI (Optional - AI Features)
Task 12.1: AI - Entities

 12.1.1: Create modules/ai/entity/AiConversation.java
 12.1.2: Create modules/ai/entity/VisualSearchLog.java

Task 12.2: AI - Integration with Python Services

 12.2.1: Create Python FastAPI microservice (separate project)
 12.2.2: Create HTTP client in Spring Boot to call Python AI service
 12.2.3: (Defer AI to Phase 2)


PHASE COMPLETION: DevOps & Infrastructure
Task 13.1: Dockerization

 13.1.1: Create Dockerfile for Spring Boot backend
 13.1.2: Create docker-compose.yml (backend + MySQL)
 13.1.3: Test Docker setup locally


Task 13.2: Database Migrations

 13.2.1: Configure Flyway (optional, since using JPA ddl-auto=update)
 13.2.2: OR rely on Hibernate auto schema generation


Task 13.3: CI/CD

 13.3.1: Create GitHub Actions workflow for backend

Build on push
Run tests
Deploy to Railway/DigitalOcean


 13.3.2: Configure deployment environment variables


Task 13.4: API Documentation

 13.4.1: Add Springdoc OpenAPI dependency
 13.4.2: Configure Swagger UI
 13.4.3: Access docs at /swagger-ui.html