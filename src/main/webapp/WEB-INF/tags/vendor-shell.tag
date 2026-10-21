<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  vendor-shell.tag — App Shell "Vendor" (rules/frontend/01 §2): Side nav trái + Content + WebSocket STOMP.
  @owner: TV2 | F4.1–F4.7, F7.1
--%>
<%@ attribute name="active" required="true" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- Khung thanh bên (sát mép trái, thu gọn được, ngăn kéo trên mobile) dùng chung .ss-shell: main.css + side-shell.js (TV3, F8) --%>
<style>
  .vendor-side-header{padding:var(--space-2) var(--space-4) var(--space-3);border-bottom:1px solid var(--color-border);margin-bottom:var(--space-2)}
  .vendor-toast{display:flex;align-items:center;gap:var(--space-2);padding:var(--space-3) var(--space-4);border-radius:var(--radius);margin-bottom:var(--space-4);font-weight:var(--fw-medium)}
  .vendor-toast.success{background:var(--success-bg);color:var(--success)}
  .vendor-toast.danger{background:var(--danger-bg);color:var(--danger)}
  .vendor-toast.info{background:var(--info-bg);color:var(--info)}
</style>

<div class="ss-shell vendor-shell">
  <script src="${ctx}/assets/js/side-shell.js"></script>
  <nav id="sideNav" class="app-sidebar ss-side vendor-side" aria-label="Menu kênh người bán">
    <div class="ss-side-head">
      <span class="ss-side-title">Kênh người bán</span>
      <button type="button" class="ss-side-toggle" data-side-toggle aria-controls="sideNav" aria-expanded="true"
              aria-label="Thu gọn menu" title="Thu gọn menu"><t:icon name="sidebar" size="20"/></button>
    </div>
    <div class="vendor-side-header ss-side-extra">
      <div class="d-flex align-items-center gap-2" style="font-weight:var(--fw-bold);color:var(--color-primary)">
        <t:icon name="store" size="20"/>
        <span>${not empty store ? store.name : 'Kênh Người Bán'}</span>
      </div>
      <c:if test="${not empty store}">
        <div style="margin-top:var(--space-1)">
          <span class="ss-badge ${store.approved ? 'success' : 'warning'}">
            ${store.approved ? 'Đã duyệt kinh doanh' : 'Chờ duyệt'}
          </span>
        </div>
      </c:if>
      <c:if test="${not empty myStores and myStores.size() > 1}">
        <form method="post" action="${ctx}/vendor/store/switch" style="margin-top:var(--space-2);margin-bottom:0">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <select name="storeId" class="form-select form-select-sm" aria-label="Chuyển đổi cửa hàng" onchange="this.form.submit()">
            <c:forEach var="stItem" items="${myStores}">
              <option value="${stItem.id}" ${not empty store and store.id == stItem.id ? 'selected' : ''}>
                ${stItem.name} ${stItem.approved ? '' : '(Chờ duyệt)'}
              </option>
            </c:forEach>
          </select>
        </form>
      </c:if>
    </div>

    <a href="${ctx}/vendor" title="Tổng quan Shop" class="${active=='dashboard'?'active':''}" ${active=='dashboard'?'aria-current="page"':''}>
      <t:icon name="home" size="18"/> <span class="ss-side-label">Tổng quan Shop</span>
    </a>
    <a href="${ctx}/vendor/products" title="Sản phẩm" class="${active=='products'?'active':''}" ${active=='products'?'aria-current="page"':''}>
      <t:icon name="package" size="18"/> <span class="ss-side-label">Sản phẩm</span>
    </a>
    <a href="${ctx}/vendor/orders" title="Đơn hàng" class="${active=='orders'?'active':''}" ${active=='orders'?'aria-current="page"':''}>
      <t:icon name="truck" size="18"/> <span class="ss-side-label">Đơn hàng (7 TT)</span>
    </a>
    <a href="${ctx}/vendor/promotions" title="Khuyến mãi / Voucher" class="${active=='promotions'?'active':''}" ${active=='promotions'?'aria-current="page"':''}>
      <t:icon name="tag" size="18"/> <span class="ss-side-label">Khuyến mãi / Voucher</span>
    </a>
    <a href="${ctx}/vendor/wallet" title="Ví cửa hàng" class="${active=='wallet'?'active':''}" ${active=='wallet'?'aria-current="page"':''}>
      <t:icon name="inbox" size="18"/> <span class="ss-side-label">Ví cửa hàng</span>
    </a>
    <a href="${ctx}/vendor/revenue" title="Doanh thu & Biểu đồ" class="${active=='revenue'?'active':''}" ${active=='revenue'?'aria-current="page"':''}>
      <t:icon name="chart" size="18"/> <span class="ss-side-label">Doanh thu & Biểu đồ</span>
    </a>
  </nav>
  <div class="ss-side-backdrop" aria-hidden="true"></div>

  <section class="ss-content vendor-content">
    <%-- Mobile: thanh bên ẩn thành ngăn kéo → nút mở --%>
    <button type="button" class="btn btn-outline-secondary btn-min ss-side-open" data-side-toggle
            aria-controls="sideNav" aria-expanded="false"><t:icon name="sidebar" size="18"/> Menu</button>
    <div id="wsRealtimeBanner" class="vendor-toast info d-none" role="status">
      <t:icon name="bell" size="18"/>
      <span id="wsRealtimeText">Thông báo mới</span>
    </div>
    <c:if test="${not empty toast}">
      <div class="vendor-toast success" role="status">
        <t:icon name="check" size="18"/> <span><c:out value="${toast}"/></span>
      </div>
    </c:if>
    <c:if test="${not empty toastError}">
      <div class="vendor-toast danger" role="alert">
        <t:icon name="x" size="18"/> <span><c:out value="${toastError}"/></span>
      </div>
    </c:if>
    <jsp:doBody/>
  </section>
</div>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script>
  (function() {
    try {
      var sock = new SockJS('${ctx}/ws');
      var stomp = Stomp.over(sock);
      stomp.debug = null;
      stomp.connect({}, function() {
        stomp.subscribe('/user/queue/noti', function(frame) {
          try {
            var data = JSON.parse(frame.body);
            var banner = document.getElementById('wsRealtimeBanner');
            var txt = document.getElementById('wsRealtimeText');
            if (banner && txt && data.content) {
              txt.textContent = data.content;
              banner.classList.remove('d-none');
            }
          } catch (e) {}
        });
      });
    } catch (err) {}
  })();
</script>
