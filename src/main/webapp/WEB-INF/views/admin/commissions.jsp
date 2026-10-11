<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.5 Chiết khấu app (hoa hồng thu của shop). @owner: TV3 --%>
<html>
<head><title>Chiết khấu - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isManager" value="${area == 'manager'}"/>
<c:forEach var="x" items="${commissions}"><c:if test="${not empty param.editId and fn:trim(x.id) == param.editId}"><c:set var="editing" value="${x}"/></c:if></c:forEach>
<t:admin-shell active="commissions">
  <t:page-header title="Chiết khấu"
                 subtitle="% hoa hồng app thu trên doanh thu đơn đã giao của cửa hàng"/>

  <div class="row g-4">
    <div class="col-lg-7">
      <t:card title="Bảng chiết khấu">
        <c:choose>
          <c:when test="${empty commissions}">
            <t:empty-state variant="empty" title="Chưa thiết lập chiết khấu nào"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Áp dụng cho</th><th class="num">Tỉ lệ</th><th>Ghi chú</th><th class="actions">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="cm" items="${commissions}">
                  <tr>
                    <td><c:choose>
                      <c:when test="${empty cm.store}"><span style="font-weight:var(--fw-semibold)">Mặc định toàn sàn</span></c:when>
                      <c:otherwise><c:out value="${cm.store.name}"/></c:otherwise>
                    </c:choose></td>
                    <td class="num"><fmt:formatNumber value="${cm.rate}" maxFractionDigits="2"/>%</td>
                    <td class="muted"><c:out value="${cm.note}"/></td>
                    <td class="actions">
                      <c:if test="${not isManager or not empty cm.store}">
                        <a class="btn btn-sm btn-outline-secondary" href="${ctx}/${area}/commissions?editId=${cm.id}" title="Sửa">
                          <t:icon name="edit" size="16" label="Sửa"/></a>
                        <form method="post" action="${ctx}/${area}/commissions/${cm.id}/delete" class="d-inline"
                              data-confirm="Xoá chiết khấu ${empty cm.store ? 'mặc định toàn sàn' : fn:escapeXml(cm.store.name)}?">
                          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                          <button class="btn btn-sm btn-outline-danger" type="submit" title="Xoá"><t:icon name="delete" size="16" label="Xoá"/></button>
                        </form>
                      </c:if>
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
      <t:card title="${empty editing ? 'Thiết lập chiết khấu' : 'Sửa chiết khấu'}">
        <c:choose>
          <c:when test="${isManager and empty stores}">
            <t:empty-state variant="empty" title="Bạn chưa được phân công cửa hàng nào"/>
          </c:when>
          <c:otherwise>
            <form method="post" action="${ctx}/${area}/commissions">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <c:if test="${not empty editing}"><input type="hidden" name="id" value="${editing.id}"/></c:if>
              <div class="mb-3">
                <label class="form-label" for="f_storeId">Áp dụng cho <span style="color:var(--danger)">*</span></label>
                <select class="form-select" id="f_storeId" name="storeId" ${isManager ? 'required' : ''}>
                  <c:if test="${not isManager}"><option value="">Mặc định toàn sàn</option></c:if>
                  <c:forEach var="s" items="${stores}">
                    <option value="${s.id}" ${not empty editing.store and editing.store.id == s.id ? 'selected' : ''}><c:out value="${s.name}"/></option>
                  </c:forEach>
                </select>
                <div class="form-text muted">Mỗi cửa hàng chỉ có 1 dòng chiết khấu; shop chưa có dòng riêng dùng mức mặc định.</div>
              </div>
              <div class="mb-3">
                <label class="form-label" for="f_rate">Tỉ lệ (%) <span style="color:var(--danger)">*</span></label>
                <input class="form-control" id="f_rate" name="rate" type="number" min="0" max="100" step="0.01" required
                       value="${editing.rate}" placeholder="Vd: 10">
              </div>
              <t:form-field name="note" label="Ghi chú" value="${fn:escapeXml(editing.note)}" placeholder="Tuỳ chọn"/>
              <div class="d-flex gap-2">
                <button class="btn btn-brand btn-min" type="submit"><t:icon name="check" size="18"/> Lưu</button>
                <c:if test="${not empty editing}"><a class="btn btn-outline-secondary btn-min" href="${ctx}/${area}/commissions">Huỷ</a></c:if>
              </div>
            </form>
          </c:otherwise>
        </c:choose>
      </t:card>
    </div>
  </div>
</t:admin-shell>
</body>
</html>
