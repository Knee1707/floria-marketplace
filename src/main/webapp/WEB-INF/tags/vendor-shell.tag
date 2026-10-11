<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  vendor-shell.tag — App Shell "Vendor" (rules/frontend/01 §2): Side nav trái + Content + WebSocket STOMP.
  @owner: TV2 | F4.1–F4.7, F7.1
--%>
<%@ attribute name="active" required="true" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style>
  .vendor-shell{display:grid;grid-template-columns:240px 1fr;gap:var(--space-6);align-items:start}
  .vendor-side{border:1px solid var(--color-border);border-radius:var(--radius-md);padding:var(--space-3) 0;box-shadow:var(--shadow-sm)}
  .vendor-side-header{padding:var(--space-3) var(--space-4);border-bottom:1px solid var(--color-border);margin-bottom:var(--space-2)}
  .vendor-side a{display:flex;align-items:center;gap:var(--space-2);font-weight:var(--fw-medium)}
  .vendor-toast{display:flex;align-items:center;gap:var(--space-2);padding:var(--space-3) var(--space-4);border-radius:var(--radius);margin-bottom:var(--space-4);font-weight:var(--fw-medium)}
  .vendor-toast.success{background:var(--success-bg);color:var(--success)}
  .vendor-toast.danger{background:var(--danger-bg);color:var(--danger)}
  .vendor-toast.info{background:var(--info-bg);color:var(--info)}
  @media (max-width:991.98px){
    .vendor-shell{grid-template-columns:1fr}
    .vendor-side{display:flex;overflow-x:auto;padding:var(--space-2);gap:var(--space-1)}
    .vendor-side-header{display:none}
    .vendor-side a{white-space:nowrap;margin:0}
  }
</style>

<div class="vendor-shell">
  <nav class="app-sidebar vendor-side" aria-label="Menu kênh người bán">
    <div class="vendor-side-header">
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
    </div>

    <a href="${ctx}/vendor" class="${active=='dashboard'?'active':''}">
      <t:icon name="home" size="18"/> Tổng quan Shop
    </a>
    <a href="${ctx}/vendor/products" class="${active=='products'?'active':''}">
      <t:icon name="package" size="18"/> Sản phẩm
    </a>
    <a href="${ctx}/vendor/orders" class="${active=='orders'?'active':''}">
      <t:icon name="truck" size="18"/> Đơn hàng (7 TT)
    </a>
    <a href="${ctx}/vendor/promotions" class="${active=='promotions'?'active':''}">
      <t:icon name="tag" size="18"/> Khuyến mãi / Voucher
    </a>
    <a href="${ctx}/vendor/wallet" class="${active=='wallet'?'active':''}">
      <t:icon name="inbox" size="18"/> Ví cửa hàng
    </a>
    <a href="${ctx}/vendor/revenue" class="${active=='revenue'?'active':''}">
      <t:icon name="chart" size="18"/> Doanh thu & Biểu đồ
    </a>
  </nav>

  <section class="vendor-content">
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
