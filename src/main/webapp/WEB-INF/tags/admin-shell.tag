<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  admin-shell.tag — App Shell "Admin" (rules/frontend/01 §2): Side nav trái + Content. Dùng cho Admin + Manager.
  Vì default.jsp (Leader) là layout chung, shell Admin được dựng bên trong phần body bằng tag này
  thay vì đẻ decorator thứ 2. Khung thanh bên dùng chung .ss-shell (main.css + side-shell.js):
  sát mép trái, cao trọn màn hình, thu gọn được; nội dung chiếm phần còn lại.
  Kèm: toast (flash "toast"/"toastError"), modal xác nhận dùng chung,
  script admin.js (xác nhận thao tác nhạy cảm + trạng thái loading cho nút submit).
  Dùng: <t:admin-shell active="users"> ...nội dung trang... </t:admin-shell>
  "area" (admin|manager) do AdminAreaAdvice đưa vào request.
  @owner: TV3
--%>
<%@ attribute name="active" required="true" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isManager" value="${area == 'manager'}"/>
<c:set var="base" value="${ctx}/${isManager ? 'manager' : 'admin'}"/>

<div class="ss-shell admin-shell">
  <script src="${ctx}/assets/js/side-shell.js"></script>
  <nav id="sideNav" class="app-sidebar ss-side admin-side" aria-label="Menu quản trị">
    <div class="ss-side-head">
      <span class="ss-side-title">${isManager ? 'Quản lý cửa hàng' : 'Quản trị hệ thống'}</span>
      <button type="button" class="ss-side-toggle" data-side-toggle aria-controls="sideNav" aria-expanded="true"
              aria-label="Thu gọn menu" title="Thu gọn menu"><t:icon name="sidebar" size="20"/></button>
    </div>
    <a href="${base}" title="Tổng quan" class="${active=='dashboard'?'active':''}" ${active=='dashboard'?'aria-current="page"':''}><t:icon name="chart" size="18"/> <span class="ss-side-label">Tổng quan</span></a>
    <a href="${base}/users" title="Người dùng" class="${active=='users'?'active':''}" ${active=='users'?'aria-current="page"':''}><t:icon name="user" size="18"/> <span class="ss-side-label">Người dùng</span></a>
    <a href="${base}/stores" title="Cửa hàng" class="${active=='stores'?'active':''}" ${active=='stores'?'aria-current="page"':''}><t:icon name="store" size="18"/> <span class="ss-side-label">Cửa hàng</span></a>
    <a href="${base}/products" title="Sản phẩm" class="${active=='products'?'active':''}" ${active=='products'?'aria-current="page"':''}><t:icon name="package" size="18"/> <span class="ss-side-label">Sản phẩm</span></a>
    <a href="${base}/categories" title="Danh mục" class="${active=='categories'?'active':''}" ${active=='categories'?'aria-current="page"':''}><t:icon name="tag" size="18"/> <span class="ss-side-label">Danh mục</span></a>
    <c:if test="${not isManager}">
      <a href="${base}/styles" title="Thuộc tính SP" class="${active=='styles'?'active':''}" ${active=='styles'?'aria-current="page"':''}><t:icon name="layers" size="18"/> <span class="ss-side-label">Thuộc tính SP</span></a>
    </c:if>
    <a href="${base}/commissions" title="Chiết khấu" class="${active=='commissions'?'active':''}" ${active=='commissions'?'aria-current="page"':''}><t:icon name="percent" size="18"/> <span class="ss-side-label">Chiết khấu</span></a>
    <a href="${base}/promotions" title="Khuyến mãi" class="${active=='promotions'?'active':''}" ${active=='promotions'?'aria-current="page"':''}><t:icon name="ticket" size="18"/> <span class="ss-side-label">Khuyến mãi</span></a>
    <a href="${base}/deliveries" title="Vận chuyển" class="${active=='deliveries'?'active':''}" ${active=='deliveries'?'aria-current="page"':''}><t:icon name="truck" size="18"/> <span class="ss-side-label">Vận chuyển</span></a>
  </nav>
  <div class="ss-side-backdrop" aria-hidden="true"></div>

  <section class="ss-content admin-content">
    <%-- Mobile: thanh bên ẩn thành ngăn kéo → nút mở --%>
    <button type="button" class="btn btn-outline-secondary btn-min ss-side-open" data-side-toggle
            aria-controls="sideNav" aria-expanded="false"><t:icon name="sidebar" size="18"/> Menu</button>
    <c:if test="${not empty toast}">
      <div class="ss-toast success" role="status"><t:icon name="check" size="18"/> <span><c:out value="${toast}"/></span></div>
    </c:if>
    <c:if test="${not empty toastError}">
      <div class="ss-toast danger" role="alert"><t:icon name="x" size="18"/> <span><c:out value="${toastError}"/></span></div>
    </c:if>
    <jsp:doBody/>
  </section>
</div>

<%-- Modal xác nhận dùng chung: mọi <form data-confirm="..."> sẽ hỏi lại trước khi gửi (admin.js) --%>
<t:modal id="confirmModal" title="Xác nhận thao tác">
  <p id="confirmMessage" style="margin-bottom:var(--space-4)">Bạn chắc chắn muốn thực hiện thao tác này?</p>
  <div class="d-flex justify-content-end gap-2">
    <button type="button" class="btn btn-outline-secondary btn-min" data-bs-dismiss="modal">Huỷ</button>
    <button type="button" class="btn btn-danger btn-min" id="confirmOk">Đồng ý</button>
  </div>
</t:modal>
<script src="${ctx}/assets/js/admin.js"></script>
