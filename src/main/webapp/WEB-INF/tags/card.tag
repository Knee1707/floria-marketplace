<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" %>
<%-- card.tag — bề mặt thẻ dùng chung (rules/frontend/05) --%>
<div class="ss-card">
  <c:if test="${not empty title}"><h3>${title}</h3></c:if>
  <jsp:doBody/>
</div>
