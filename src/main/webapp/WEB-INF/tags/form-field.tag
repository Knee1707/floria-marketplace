<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  form-field.tag — 1 field chuẩn (label + input + hint + lỗi). rules/frontend/05.
  Dùng: <t:form-field name="email" label="Email" type="email" value="${x}" required="true"/>
--%>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" %>
<%@ attribute name="value" %>
<%@ attribute name="required" %>
<%@ attribute name="hint" %>
<%@ attribute name="error" %>
<%@ attribute name="placeholder" %>
<div class="mb-3">
  <label class="form-label" for="f_${name}">${label}<c:if test="${required=='true'}"> <span style="color:var(--danger)">*</span></c:if></label>
  <c:choose>
    <c:when test="${type=='textarea'}">
      <textarea class="form-control" id="f_${name}" name="${name}" rows="4"
        <c:if test="${required=='true'}">required</c:if> placeholder="${placeholder}">${value}</textarea>
    </c:when>
    <c:otherwise>
      <input class="form-control" id="f_${name}" name="${name}" type="${empty type?'text':type}"
        value="${value}" placeholder="${placeholder}" <c:if test="${required=='true'}">required</c:if>>
    </c:otherwise>
  </c:choose>
  <c:if test="${not empty hint}"><div class="form-text muted">${hint}</div></c:if>
  <c:if test="${not empty error}"><div style="color:var(--danger);font-size:var(--text-xs)">${error}</div></c:if>
</div>
