<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Khuyến mãi cửa hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="promotions">
  <t:page-header title="Khuyến mãi & Mã giảm giá"
                 subtitle="Tạo và quản lý các chương trình ưu đãi riêng cho cửa hàng ${store.name}"/>

  <div class="row g-4">
    <%-- Cột trái: Form tạo mới Voucher của Shop (F4.5) --%>
    <div class="col-lg-4">
      <t:card title="Tạo mã giảm giá mới">
        <form method="post" action="${ctx}/vendor/promotions/create">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

          <t:form-field name="code" label="Mã Voucher" required="true"
                        placeholder="Ví dụ: SHOP15, HOATUOI50K"/>

          <div class="mb-3">
            <label class="form-label" for="promoType">Loại khuyến mãi <span style="color:var(--danger)">*</span></label>
            <select class="form-select" id="promoType" name="type" required>
              <option value="PRODUCT_PERCENT">Giảm theo % giá trị sản phẩm</option>
              <option value="SHIPPING">Giảm phí vận chuyển (VNĐ)</option>
            </select>
          </div>

          <t:form-field name="value" label="Giá trị giảm (% hoặc VNĐ)" type="number" required="true"
                        placeholder="Ví dụ: 15 (nếu %) hoặc 20000 (nếu ship)"/>

          <t:form-field name="minOrder" label="Đơn tối thiểu (VNĐ)" type="number" value="200000"/>

          <t:form-field name="maxDiscount" label="Giảm tối đa (VNĐ)" type="number" value="50000"/>

          <t:form-field name="quota" label="Số lượt sử dụng tối đa" type="number" value="100" required="true"/>

          <button type="submit" class="btn btn-brand btn-min w-100 d-inline-flex align-items-center justify-content-center gap-2">
            <t:icon name="add" size="18"/> Tạo Voucher Shop
          </button>
        </form>
      </t:card>
    </div>

    <%-- Cột phải: Danh sách Voucher hiện có của Shop --%>
    <div class="col-lg-8">
      <t:card title="Danh sách Voucher của cửa hàng">
        <c:choose>
          <c:when test="${empty vouchers}">
            <t:empty-state variant="empty" title="Cửa hàng chưa tạo mã giảm giá nào"/>
          </c:when>
          <c:otherwise>
            <div class="table-responsive">
              <table class="ss-table">
                <thead>
                  <tr>
                    <th>Mã Voucher</th>
                    <th>Loại ưu đãi</th>
                    <th class="num">Mức giảm</th>
                    <th class="num">Đơn tối thiểu</th>
                    <th class="num">Đã dùng / Giới hạn</th>
                    <th>Trạng thái</th>
                    <th class="num">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="v" items="${vouchers}">
                    <tr>
                      <td style="font-weight:var(--fw-bold);color:var(--color-primary)">${v.code}</td>
                      <td>
                        <span class="ss-badge ${v.type == 'PRODUCT_PERCENT' ? 'primary' : 'info'}">
                          ${v.type == 'PRODUCT_PERCENT' ? 'Giảm % SP' : 'Hỗ trợ Ship'}
                        </span>
                      </td>
                      <td class="num price">
                        <c:choose>
                          <c:when test="${v.type == 'PRODUCT_PERCENT'}">
                            <fmt:formatNumber value="${v.value}" type="number"/>%
                          </c:when>
                          <c:otherwise>
                            <fmt:formatNumber value="${v.value}" type="number"/> đ
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="num"><fmt:formatNumber value="${empty v.minOrder ? 0 : v.minOrder}" type="number"/> đ</td>
                      <td class="num">${empty v.usedCount ? 0 : v.usedCount} / ${empty v.quota ? 0 : v.quota}</td>
                      <td>
                        <span class="ss-badge ${v.active ? 'success' : 'neutral'}">
                          ${v.active ? 'Đang bật' : 'Tạm ngưng'}
                        </span>
                      </td>
                      <td class="num">
                        <form method="post" action="${ctx}/vendor/promotions/${v.id}/toggle" class="d-inline m-0">
                          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                          <input type="hidden" name="active" value="${not v.active}"/>
                          <button type="submit" class="btn btn-sm ${v.active ? 'btn-outline-secondary' : 'btn-brand'}">
                            ${v.active ? 'Tạm ngưng' : 'Kích hoạt'}
                          </button>
                        </form>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </t:card>
    </div>
  </div>
</t:vendor-shell>
</body>
</html>
