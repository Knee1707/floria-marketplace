<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.7 Nhà vận chuyển (tên + phí ship). @owner: TV3 --%>
<html>
<head><title>Vận chuyển - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:forEach var="x" items="${deliveries}"><c:if test="${not empty param.editId and fn:trim(x.id) == param.editId}"><c:set var="editing" value="${x}"/></c:if></c:forEach>
<t:admin-shell active="deliveries">
  <t:page-header title="Nhà vận chuyển" subtitle="Tên dịch vụ và phí ship áp dụng khi khách thanh toán"/>

  <div class="row g-4">
    <div class="col-lg-7">
      <t:card title="Danh sách">
        <c:choose>
          <c:when test="${empty deliveries}">
            <t:empty-state variant="empty" title="Chưa có nhà vận chuyển — khách sẽ không chọn được phương thức giao"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Nhà vận chuyển</th><th class="num">Phí ship</th><th class="actions">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="d" items="${deliveries}">
                  <tr>
                    <td><t:icon name="truck" size="16"/> <c:out value="${d.name}"/></td>
                    <td class="num"><fmt:formatNumber value="${d.shippingFee}" type="number" maxFractionDigits="0"/> đ</td>
                    <td class="actions">
                      <a class="btn btn-sm btn-outline-secondary" href="${ctx}/${area}/deliveries?editId=${d.id}" title="Sửa">
                        <t:icon name="edit" size="16" label="Sửa"/></a>
                      <form method="post" action="${ctx}/${area}/deliveries/${d.id}/delete" class="d-inline"
                            data-confirm="Xoá nhà vận chuyển ${fn:escapeXml(d.name)}?">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit" title="Xoá"><t:icon name="delete" size="16" label="Xoá"/></button>
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

    <div class="col-lg-5">
      <t:card title="${empty editing ? 'Thêm nhà vận chuyển' : 'Sửa nhà vận chuyển'}">
        <form method="post" action="${ctx}/${area}/deliveries">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <c:if test="${not empty editing}"><input type="hidden" name="id" value="${editing.id}"/></c:if>
          <t:form-field name="name" label="Tên dịch vụ" required="true" value="${fn:escapeXml(editing.name)}"
                        placeholder="Vd: Giao nhanh nội thành"/>
          <div class="mb-3">
            <label class="form-label" for="f_shippingFee">Phí ship (đ) <span style="color:var(--danger)">*</span></label>
            <input class="form-control" id="f_shippingFee" name="shippingFee" type="number" min="0" step="500" required
                   value="${editing.shippingFee}" placeholder="Vd: 30000">
          </div>
          <div class="d-flex gap-2">
            <button class="btn btn-brand btn-min" type="submit"><t:icon name="${empty editing ? 'add' : 'check'}" size="18"/>
              ${empty editing ? 'Thêm' : 'Lưu thay đổi'}</button>
            <c:if test="${not empty editing}"><a class="btn btn-outline-secondary btn-min" href="${ctx}/${area}/deliveries">Huỷ</a></c:if>
          </div>
        </form>
      </t:card>
    </div>
  </div>
</t:admin-shell>
</body>
</html>
