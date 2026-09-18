---
name: chuan-nganh
description: >
  Kiểm & chốt "chuẩn ngành" cho một quyết định/hạng mục kỹ thuật trước khi làm.
  Dùng khi user hỏi "cách chuẩn", "best practice", "nên hay không nên", "chuẩn ngành",
  "công ty lớn làm thế nào", hoặc phân vân giữa nhiều phương án kỹ thuật (query, index,
  kiến trúc, bảo mật, mở cổng, cache, cấu hình...). Skill thu THẬT ≥3 nguồn uy tín từ
  chuyên gia/công ty đầu ngành, đối chiếu có số liệu + logic, rồi đề xuất phương án hợp
  context của user. KHÔNG bịa nguồn.
---

# chuan-nganh — Kiểm chuẩn ngành (evidence-based)

**Nguyên tắc:** không phán theo cảm tính. Mọi khuyến nghị phải tựa trên ≥3 nguồn kiểm chứng + phù hợp **context** thật của user (ở đây thường là đồ án StarShop: 1 server, quy mô nhỏ, học thuật).

## Luồng chuẩn (lặp lại mỗi lần)
1. **Xác định hạng mục & context.** Ghi rõ câu hỏi 1 dòng + ràng buộc (quy mô, ngân sách, đội, deadline, môi trường).
2. **Thu ≥3 nguồn uy tín** (ưu tiên theo thứ tự):
   - Tài liệu chính thức (PostgreSQL docs, Spring docs, OWASP, RFC...).
   - Kỹ thuật của công ty đầu ngành (AWS/Google/Microsoft/GitLab/Stripe... engineering blog).
   - Chuyên gia/benchmark được công nhận (tác giả sách, maintainer, khảo sát StackOverflow/DB-Engines).
   → Dùng WebSearch/WebFetch để lấy nguồn thật; ghi **link + ngày**.
3. **Đối chiếu (có số liệu).** Bảng: mỗi nguồn nói gì, chỗ đồng thuận, chỗ mâu thuẫn, con số (ms, %, throughput, chi phí, rủi ro).
4. **Chiếu vào context user.** "Chuẩn cho quy mô lớn" ≠ "đúng cho đồ án nhỏ" — nói rõ khác biệt.
5. **Đề xuất.** 1 phương án nên chọn + vì sao (logic) + đánh đổi + khi nào nên đổi ý.

## Định dạng đầu ra (bắt buộc)
- **Câu hỏi & context** (1–2 dòng)
- **Bảng nguồn** (Nguồn · Quan điểm · Số liệu · Link)
- **Đối chiếu** (đồng thuận / khác biệt)
- **Khuyến nghị cho bạn** (kèm cấu hình/con số cụ thể)
- **Cảnh báo & khi nào xem lại**

## Luật chống bịa
- Không có nguồn thật → nói "chưa kiểm chứng được", KHÔNG bịa số/ý.
- Trích dẫn ngắn, ghi nguồn; không sao chép dài.
- Phân biệt rõ **fact có nguồn** vs **suy luận của mình**.
