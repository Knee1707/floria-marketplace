<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  status-badge.tag — SSOT trạng thái (rules/frontend/05). Truyền enum thô, KHÔNG tự đặt màu.
  Dùng: <t:status-badge value="${order.status}"/>
--%>
<%@ attribute name="value" required="true" %>
<c:choose>
  <c:when test="${value=='NEW'}"><c:set var="cls" value="info"/><c:set var="lbl" value="Đơn mới"/></c:when>
  <c:when test="${value=='CONFIRMED'}"><c:set var="cls" value="primary"/><c:set var="lbl" value="Đã xác nhận"/></c:when>
  <c:when test="${value=='PICKED_UP'}"><c:set var="cls" value="accent"/><c:set var="lbl" value="Đã lấy hàng"/></c:when>
  <c:when test="${value=='SHIPPING' || value=='DELIVERING'}"><c:set var="cls" value="warning"/><c:set var="lbl" value="Đang giao"/></c:when>
  <c:when test="${value=='DELIVERED'}"><c:set var="cls" value="success"/><c:set var="lbl" value="Đã giao"/></c:when>
  <c:when test="${value=='CANCELLED'}"><c:set var="cls" value="neutral"/><c:set var="lbl" value="Đã huỷ"/></c:when>
  <c:when test="${value=='RETURNED'}"><c:set var="cls" value="danger"/><c:set var="lbl" value="Trả/hoàn"/></c:when>
  <c:when test="${value=='ACTIVE'}"><c:set var="cls" value="success"/><c:set var="lbl" value="Đang bán"/></c:when>
  <c:when test="${value=='HIDDEN'}"><c:set var="cls" value="neutral"/><c:set var="lbl" value="Ẩn"/></c:when>
  <c:when test="${value=='BANNED'}"><c:set var="cls" value="danger"/><c:set var="lbl" value="Bị cấm"/></c:when>
  <%-- Trạng thái quản trị (TV3): shop duyệt/chờ, tài khoản, bật/tắt --%>
  <c:when test="${value=='APPROVED'}"><c:set var="cls" value="success"/><c:set var="lbl" value="Đã duyệt"/></c:when>
  <c:when test="${value=='PENDING'}"><c:set var="cls" value="warning"/><c:set var="lbl" value="Chờ duyệt / bị cấm"/></c:when>
  <c:when test="${value=='ENABLED'}"><c:set var="cls" value="success"/><c:set var="lbl" value="Hoạt động"/></c:when>
  <c:when test="${value=='DISABLED'}"><c:set var="cls" value="neutral"/><c:set var="lbl" value="Đã tắt"/></c:when>
  <c:when test="${value=='LOCKED'}"><c:set var="cls" value="danger"/><c:set var="lbl" value="Bị khoá"/></c:when>
  <c:when test="${value=='UNVERIFIED'}"><c:set var="cls" value="warning"/><c:set var="lbl" value="Chưa kích hoạt"/></c:when>
  <c:otherwise><c:set var="cls" value="neutral"/><c:set var="lbl" value="${value}"/></c:otherwise>
</c:choose>
<span class="ss-badge ${cls}">${lbl}</span>
