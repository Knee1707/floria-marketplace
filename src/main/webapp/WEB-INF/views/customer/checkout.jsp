<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Thanh toán - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Thanh toán"/>

<form method="post" action="${ctx}/checkout" class="row g-4">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  <%-- Giữ đúng các sản phẩm đã chọn từ giỏ sang bước đặt hàng --%>
  <c:forEach var="id" items="${itemIds}"><input type="hidden" name="itemIds" value="${id}"/></c:forEach>
  <div class="col-md-7">
    <t:card title="Địa chỉ nhận hàng">
      <c:choose>
        <c:when test="${empty addresses}">
          <p class="muted">Chưa có địa chỉ. <a href="${ctx}/account/addresses">Thêm địa chỉ</a> hoặc để trống dùng tên tài khoản.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="a" items="${addresses}">
            <label class="d-block mb-2"><input type="radio" name="addressId" value="${a.id}" ${a.isDefault?'checked':''}>
              ${a.receiverName} · ${a.phone} · ${a.addressLine}, ${a.ward}, ${a.district}, ${a.province}</label>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </t:card>
    <div style="height:var(--space-4)"></div>
    <t:card title="Phương thức thanh toán">
      <label class="d-block mb-2"><input type="radio" name="method" value="COD" checked> COD — thanh toán khi nhận hàng</label>
      <label class="d-block mb-2"><input type="radio" name="method" value="VNPAY"> VNPAY (sandbox)</label>
    </t:card>
  </div>
  <div class="col-md-5">
    <t:card title="Tóm tắt đơn">
      <c:url var="voucherUrl" value="/checkout">
        <c:param name="voucher" value="${voucher}"/>
        <c:forEach var="id" items="${itemIds}"><c:param name="itemIds" value="${id}"/></c:forEach>
      </c:url>
      <div class="d-flex mb-2"><input class="form-control" name="voucher" value="${voucher}" placeholder="Mã giảm giá">
        <a class="btn btn-outline-secondary ms-2" href="${voucherUrl}">Áp</a></div>
      <div class="d-flex justify-content-between"><span>Tạm tính</span><span><fmt:formatNumber value="${subtotal}" type="number"/> đ</span></div>
      <div class="d-flex justify-content-between"><span>Phí ship (mỗi shop)</span><span>30.000 đ</span></div>
      <div class="d-flex justify-content-between"><span>Giảm giá</span><span>- <fmt:formatNumber value="${discount}" type="number"/> đ</span></div>
      <hr>
      <div class="d-flex justify-content-between fw-bold"><span>Ước tính</span>
        <span class="price"><fmt:formatNumber value="${subtotal - discount}" type="number"/> đ + ship</span></div>
      <button class="btn btn-brand btn-min w-100 mt-3">Đặt hàng</button>
    </t:card>
  </div>
</form>
</body>
</html>
