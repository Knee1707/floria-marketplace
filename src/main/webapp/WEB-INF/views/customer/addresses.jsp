<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Địa chỉ nhận hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Địa chỉ nhận hàng"/>

<div class="row g-4">
  <div class="col-md-6">
    <t:card title="Danh sách địa chỉ">
      <c:choose>
        <c:when test="${empty addresses}"><t:empty-state variant="empty" title="Chưa có địa chỉ"/></c:when>
        <c:otherwise>
          <c:forEach var="a" items="${addresses}">
            <div class="d-flex justify-content-between align-items-start" style="padding:var(--space-2) 0;border-bottom:1px solid var(--color-border)">
              <div><b>${a.receiverName}</b> · ${a.phone} <c:if test="${a['default']}"><span class="ss-badge primary">Mặc định</span></c:if>
                <div class="muted" style="font-size:var(--text-sm)">${a.addressLine}, ${a.ward}, ${a.province}</div></div>
              <form method="post" action="${ctx}/account/addresses/delete">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="id" value="${a.id}"/>
                <button class="btn btn-sm btn-outline-danger"><t:icon name="delete" size="16" label="Xoá"/></button>
              </form>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </t:card>
  </div>
  <div class="col-md-6">
    <t:card title="Thêm địa chỉ">
      <form method="post" action="${ctx}/account/addresses">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <t:form-field name="receiverName" label="Người nhận" required="true"/>
        <t:form-field name="phone" label="Số điện thoại" required="true"/>
        <t:form-field name="addressLine" label="Địa chỉ" required="true"/>
        <t:form-field name="ward" label="Phường/Xã"/>
        <t:form-field name="province" label="Tỉnh/Thành"/>
        <button class="btn btn-brand btn-min w-100">Lưu địa chỉ</button>
      </form>
    </t:card>
  </div>
</div>
</body>
</html>
