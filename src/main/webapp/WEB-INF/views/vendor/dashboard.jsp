<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Tổng quan cửa hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="dashboard">
  <t:page-header title="${store.name}" subtitle="${empty store.description ? 'Quản lý hoạt động kinh doanh cửa hàng của bạn' : store.description}">
    <a class="btn btn-brand btn-min d-inline-flex align-items-center gap-2" href="${ctx}/vendor/products/new">
      <t:icon name="add" size="18"/> Thêm sản phẩm mới
    </a>
  </t:page-header>

  <%-- Bốn ô KPI tổng quan của Shop (F4.2) --%>
  <div class="row g-3" style="margin-bottom:var(--space-6)">
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Tổng sản phẩm" value="${summary.totalProducts}" icon="package"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Đơn đang xử lý" value="${summary.pendingOrders}" icon="truck"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${summary.netRevenue}" type="number" var="netRevFmt"/>
      <t:stat-tile label="Doanh thu ròng (đ)" value="${netRevFmt}" icon="chart"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${summary.walletBalance}" type="number" var="walletFmt"/>
      <t:stat-tile label="Số dư ví (đ)" value="${walletFmt}" icon="inbox"/>
    </div>
  </div>

  <div class="row g-4">
    <%-- Cột trái: Đơn hàng mới nhất + Thông báo Realtime (F7.1) --%>
    <div class="col-lg-7">
      <t:card title="Đơn hàng gần đây">
        <c:choose>
          <c:when test="${empty recentOrders}">
            <t:empty-state variant="empty" title="Chưa có đơn hàng nào phát sinh tại cửa hàng"/>
          </c:when>
          <c:otherwise>
            <div class="table-responsive">
              <table class="ss-table">
                <thead>
                  <tr>
                    <th>Mã đơn</th>
                    <th>Khách hàng</th>
                    <th>Trạng thái</th>
                    <th class="num">Tổng tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="o" items="${recentOrders}">
                    <tr>
                      <td style="font-weight:var(--fw-semibold)">${o.code}</td>
                      <td>${o.buyer.fullName}</td>
                      <td><t:status-badge value="${o.status}"/></td>
                      <td class="num price"><fmt:formatNumber value="${o.total}" type="number"/> đ</td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <div class="text-end" style="margin-top:var(--space-3)">
              <a class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1" href="${ctx}/vendor/orders">
                Xem tất cả đơn hàng <t:icon name="chevron-right" size="16"/>
              </a>
            </div>
          </c:otherwise>
        </c:choose>
      </t:card>

      <div style="margin-top:var(--space-4)">
        <t:card title="Thông báo mới (${unreadCount} chưa đọc)">
          <c:choose>
            <c:when test="${empty notifications}">
              <t:empty-state variant="empty" title="Bạn chưa có thông báo nào"/>
            </c:when>
            <c:otherwise>
              <div class="d-flex flex-column gap-2">
                <c:forEach var="n" items="${notifications}">
                  <div class="d-flex justify-content-between align-items-center gap-2"
                       style="padding:var(--space-2) var(--space-3);border-radius:var(--radius);border:1px solid var(--color-border);background:${n.read ? 'var(--color-surface)' : 'var(--pink-50)'}">
                    <div class="d-flex align-items-center gap-2">
                      <t:icon name="bell" size="16"/>
                      <span><c:out value="${n.content}"/></span>
                    </div>
                    <c:if test="${not n.read}">
                      <form method="post" action="${ctx}/vendor/notifications/${n.id}/read" class="m-0">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-sm btn-outline-secondary">Đã đọc</button>
                      </form>
                    </c:if>
                  </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>
        </t:card>
      </div>
    </div>

    <%-- Cột phải: Chỉnh sửa hồ sơ & ảnh cửa hàng (F4.2 + F7.3 Cloudinary) --%>
    <div class="col-lg-5">
      <t:card title="Hồ sơ & Hình ảnh Cửa hàng">
        <c:if test="${not empty store.coverUrl}">
          <div style="margin-bottom:var(--space-3)">
            <img src="${store.coverUrl}" alt="Ảnh bìa ${store.name}"
                 style="width:100%;height:120px;object-fit:cover;border-radius:var(--radius)">
          </div>
        </c:if>
        <div class="d-flex align-items-center gap-3" style="margin-bottom:var(--space-4)">
          <img src="${empty store.avatarUrl ? 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=200&q=80' : store.avatarUrl}"
               alt="${store.name}"
               style="width:64px;height:64px;object-fit:cover;border-radius:var(--radius-full);border:2px solid var(--color-border)">
          <div>
            <div style="font-weight:var(--fw-bold)">${store.name}</div>
            <div class="muted" style="font-size:var(--text-sm)">Chủ sở hữu: ${store.owner.fullName}</div>
          </div>
        </div>

        <form method="post" action="${ctx}/vendor/store/update" enctype="multipart/form-data">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="storeId" value="${store.id}"/>

          <t:form-field name="name" label="Tên cửa hàng" value="${store.name}" required="true"/>
          <t:form-field name="description" label="Mô tả cửa hàng" type="textarea" value="${store.description}"/>

          <div class="mb-3">
            <label class="form-label" for="avatarFile">Tải lên ảnh đại diện mới (Cloudinary)</label>
            <input class="form-control" type="file" id="avatarFile" name="avatarFile" accept="image/*"/>
            <input class="form-control mt-2" type="text" name="avatarUrl" value="${store.avatarUrl}"
                   placeholder="Hoặc nhập trực tiếp URL ảnh đại diện"/>
          </div>

          <div class="mb-3">
            <label class="form-label" for="coverFile">Tải lên ảnh bìa mới (Cloudinary)</label>
            <input class="form-control" type="file" id="coverFile" name="coverFile" accept="image/*"/>
            <input class="form-control mt-2" type="text" name="coverUrl" value="${store.coverUrl}"
                   placeholder="Hoặc nhập trực tiếp URL ảnh bìa"/>
          </div>

          <button type="submit" class="btn btn-brand btn-min w-100 d-inline-flex align-items-center justify-content-center gap-2">
            <t:icon name="upload" size="18"/> Lưu thông tin cửa hàng
          </button>
        </form>
      </t:card>
    </div>
  </div>
</t:vendor-shell>
</body>
</html>
