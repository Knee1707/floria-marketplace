<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đăng ký mở cửa hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:page-header title="Đăng ký mở cửa hàng hoa"
               subtitle="Trở thành đối tác bán hàng (Vendor) trên sàn Floria Marketplace"/>

<c:if test="${not empty toastError}">
  <div class="ss-card" style="background:var(--danger-bg);color:var(--danger);margin-bottom:var(--space-4)">
    <t:icon name="x" size="18"/> <c:out value="${toastError}"/>
  </div>
</c:if>

<div class="row justify-content-center">
  <div class="col-lg-7">
    <t:card title="Thông tin cửa hàng đăng ký">
      <form method="post" action="${ctx}/account/register-store">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <t:form-field name="name" label="Tên cửa hàng" required="true"
                      placeholder="Ví dụ: Floria Boutique Quận 3"
                      hint="Tên cửa hàng sẽ hiển thị trên mọi sản phẩm và đơn hàng của bạn."/>

        <t:form-field name="description" label="Mô tả giới thiệu cửa hàng" type="textarea"
                      placeholder="Giới thiệu phong cách cắm hoa, cam kết chất lượng và khu vực phục vụ..."/>

        <div class="d-flex justify-content-end gap-2" style="margin-top:var(--space-4)">
          <a class="btn btn-outline-secondary btn-min d-inline-flex align-items-center" href="${ctx}/">Huỷ</a>
          <button type="submit" class="btn btn-brand btn-min d-inline-flex align-items-center gap-2">
            <t:icon name="store" size="18"/> Hoàn tất đăng ký Shop
          </button>
        </div>
      </form>
    </t:card>
  </div>
</div>
</body>
</html>
