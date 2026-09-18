# HƯỚNG DẪN 03 — TV2: Vendor + Shipper + Realtime/Media

> Viết vào ĐÂU: chỉ các package/thư mục có nhãn `@owner: TV2`. Viết THẾ NÀO: theo bảng dưới.
> Đọc trước: `HUONG_DAN_02_quy_uoc_code.md` + `CLAUDE.md`.

## 0. Bạn sở hữu những thư mục này
```
service/vendor/      ← viết Impl cho các interface Leader đã khai báo
service/shipper/
service/common/CloudinaryService (đã có bean, viết upload)
controller/vendor/   controller/shipper/
views/vendor/        views/shipper/
config/WebSocketConfig (đã có endpoint) + NotificationService (viết thân)
Flyway: V4__seed_vendor.sql (seed shop + sản phẩm) — V3 do Leader dùng (user_address)
```
Entity/Repository do Leader tạo sẵn — bạn chỉ dùng. Cần thêm query repo → báo Leader.

## 1. Vendor (D4) — làm theo thứ tự
| Leaf | Viết ở đâu | Làm thế nào |
|---|---|---|
| F4.1 Đăng ký shop | `service/vendor/StoreService` + `controller/vendor/StoreController` + `views/vendor/register-store.jsp` | Form → `StoreService.registerStore(form, currentUserId)`: tạo `Store(owner=user, approved=false)`, nâng role user → VENDOR. |
| F4.2 Trang chủ shop | `controller/vendor/VendorController` + `views/vendor/dashboard.jsp` | Đọc shop của mình; sửa mô tả/ảnh (ảnh qua `CloudinaryService.upload`). |
| F4.3 CRUD sản phẩm | `service/vendor/VendorProductService` + `controller/vendor/VendorProductController` + `views/vendor/products.jsp`, `product-form.jsp` | Dùng `ProductRepository.findByStoreId`. Thêm/sửa/ẩn SP **của shop mình** (kiểm ownership!). Ảnh → Cloudinary. Style/StyleValue do Admin tạo, bạn chọn. |
| F4.4 Đơn shop (7 trạng thái) | `service/vendor/VendorOrderService` + `controller/vendor/VendorOrderController` + `views/vendor/orders.jsp` | `OrderRepository.findByStoreId`. Nút đổi trạng thái: NEW→CONFIRMED→PICKED_UP→(phân shipper)→... Dùng `<t:status-badge>`. |
| F4.5 Khuyến mãi | `service/vendor/PromotionService` + `controller/vendor/PromotionController` + `views/vendor/promotions.jsp` | Tạo `Voucher`/`Promotion` với `store_id` = shop mình. |
| F4.6 Ví shop | trong `VendorController` | Đọc `TransactionRepository.findByStoreId`; nút "rút tiền" tạo Transaction WITHDRAW. |
| F4.7 Doanh thu | `service/vendor/VendorStatsService` + `views/vendor/revenue.jsp` | Query tổng theo thời gian; vẽ Chart.js (canvas + JSON từ controller). |

## 2. Shipper (D5)
| Leaf | Ở đâu | Thế nào |
|---|---|---|
| F5.1 Đơn được phân công | `service/shipper/ShipperService` + `controller/shipper/ShipperController` + `views/shipper/assigned-orders.jsp` | `DeliveryAssignmentRepository.findByShipperId(currentUserId)`. |
| F5.2 Cập nhật giao | cùng service | `ShipperService.updateDelivery(assignmentId, status)`: SHIPPING/DELIVERED/FAILED; đồng bộ `Order.status`. Kiểm assignment thuộc shipper này. |
| F5.3 Thống kê | `views/shipper/stats.jsp` | Đếm assignment theo thời gian. |

## 3. Realtime & Media (D7 — dùng chung, bạn sở hữu)
| Leaf | Ở đâu | Thế nào |
|---|---|---|
| F7.1 WebSocket thông báo | `service/common/NotificationServiceImpl` | `notify(userId, type, content)`: lưu `Notification` + `simpMessagingTemplate.convertAndSendToUser(userId,"/queue/noti", dto)`. Gọi khi có đơn mới (cho Vendor), đổi trạng thái (cho User), phân đơn (cho Shipper). |
| F7.2 Chat (tuỳ chọn) | `controller/...ChatController` | STOMP `/app/chat`. |
| F7.3 Cloudinary | `service/common/CloudinaryServiceImpl` | `upload(MultipartFile)` → trả `secure_url`. Cấu hình bean đã có ở `CloudinaryConfig`. |

## 4. Luật khi làm (nhắc lại)
- Chỉ sửa file `@owner: TV2`. JSP dùng tag chung + đủ 4 trạng thái. Không emoji/hex cứng.
- Mọi thao tác đơn/SP: **kiểm ownership** (đúng shop/shipper của mình) — chống IDOR.
- Xong 1 leaf: đổi `@status: DONE`, kéo card GitHub sang Review, mở PR.

## 5. Seed của bạn (V4__seed_vendor.sql)
2–3 shop (owner = vendor@starshop.vn) + ~30 sản phẩm hoa + vài voucher shop + vài giao dịch mẫu.

> Tham chiếu luồng đơn 7 trạng thái xuyên vai: `../PHAN_CONG.md` §8.1.
