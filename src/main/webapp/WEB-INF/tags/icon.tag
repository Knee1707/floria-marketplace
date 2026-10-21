<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  icon.tag — REGISTRY ICON DUY NHẤT (Lucide, stroke 1.5, currentColor).
  LUẬT rules/frontend/04-icon.md: KHÔNG emoji; gọi theo Ý NGHĨA.
  Dùng: <t:icon name="cart" size="20"/>
--%>
<%@ attribute name="name" required="true" %>
<%@ attribute name="size" %>
<%@ attribute name="label" %>
<c:set var="s" value="${empty size ? 20 : size}" />
<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
     stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"
     <c:choose><c:when test="${empty label}">aria-hidden="true"</c:when><c:otherwise>role="img" aria-label="${label}"</c:otherwise></c:choose>>
<c:choose>
  <c:when test="${name=='cart'}"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2 2h2l2.6 12.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L22 6H5.1"/></c:when>
  <c:when test="${name=='search'}"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></c:when>
  <c:when test="${name=='filter'}"><polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/></c:when>
  <c:when test="${name=='sort'}"><path d="m21 16-4 4-4-4"/><path d="M17 20V4"/><path d="m3 8 4-4 4 4"/><path d="M7 4v16"/></c:when>
  <c:when test="${name=='add'}"><path d="M5 12h14"/><path d="M12 5v14"/></c:when>
  <c:when test="${name=='edit'}"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4Z"/></c:when>
  <c:when test="${name=='delete'}"><path d="M3 6h18"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></c:when>
  <c:when test="${name=='view' || name=='eye'}"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></c:when>
  <c:when test="${name=='upload'}"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><path d="M17 8l-5-5-5 5"/><path d="M12 3v12"/></c:when>
  <c:when test="${name=='bell'}"><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/></c:when>
  <c:when test="${name=='user'}"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></c:when>
  <c:when test="${name=='store'}"><path d="M2 7l1.5-4h17L22 7"/><path d="M4 7v13h16V7"/><path d="M9 20v-6h6v6"/></c:when>
  <c:when test="${name=='truck'}"><path d="M14 18V6H2v12"/><path d="M14 9h5l3 3v6h-8"/><circle cx="7" cy="18" r="2"/><circle cx="17" cy="18" r="2"/></c:when>
  <c:when test="${name=='chart'}"><path d="M3 3v18h18"/><rect x="7" y="10" width="3" height="8"/><rect x="12" y="6" width="3" height="12"/><rect x="17" y="13" width="3" height="5"/></c:when>
  <c:when test="${name=='check'}"><path d="M20 6 9 17l-5-5"/></c:when>
  <c:when test="${name=='x'}"><path d="M18 6 6 18"/><path d="M6 6l12 12"/></c:when>
  <c:when test="${name=='chevron-right'}"><path d="m9 18 6-6-6-6"/></c:when>
  <c:when test="${name=='heart'}"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8Z"/></c:when>
  <c:when test="${name=='logout'}"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="m16 17 5-5-5-5"/><path d="M21 12H9"/></c:when>
  <c:when test="${name=='package'}"><path d="M21 16V8a2 2 0 0 0-1-1.7l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.7l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="M3.3 7 12 12l8.7-5"/><path d="M12 22V12"/></c:when>
  <c:when test="${name=='tag'}"><path d="M12.6 2.6A2 2 0 0 0 11.2 2H4a2 2 0 0 0-2 2v7.2a2 2 0 0 0 .6 1.4l8.7 8.7a2.4 2.4 0 0 0 3.4 0l6.6-6.6a2.4 2.4 0 0 0 0-3.4z"/><circle cx="7.5" cy="7.5" r=".5"/></c:when>
  <c:when test="${name=='home'}"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 22V12h6v10"/></c:when>
  <c:when test="${name=='inbox'}"><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.5 5.5 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.5-6.5A2 2 0 0 0 16.8 4H7.2a2 2 0 0 0-1.7 1.5Z"/></c:when>
  <%-- Bổ sung cho khu quản trị (TV3): khoá/mở tài khoản, cấm, chiết khấu, thuộc tính SP, voucher --%>
  <c:when test="${name=='lock'}"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></c:when>
  <c:when test="${name=='unlock'}"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 9.9-1"/></c:when>
  <c:when test="${name=='ban'}"><circle cx="12" cy="12" r="10"/><path d="m4.9 4.9 14.2 14.2"/></c:when>
  <c:when test="${name=='percent'}"><path d="M19 5 5 19"/><circle cx="6.5" cy="6.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/></c:when>
  <c:when test="${name=='layers'}"><path d="m12.83 2.18a2 2 0 0 0-1.66 0L2.6 6.08a1 1 0 0 0 0 1.83l8.58 3.91a2 2 0 0 0 1.66 0l8.58-3.9a1 1 0 0 0 0-1.83Z"/><path d="m22 17.65-9.17 4.16a2 2 0 0 1-1.66 0L2 17.65"/><path d="m22 12.65-9.17 4.16a2 2 0 0 1-1.66 0L2 12.65"/></c:when>
  <c:when test="${name=='ticket'}"><path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/><path d="M13 5v2"/><path d="M13 17v2"/><path d="M13 11v2"/></c:when>
  <%-- Thu gọn / mở thanh bên (Lucide panel-left) — dùng chung các shell có sidebar (TV3, F8) --%>
  <c:when test="${name=='sidebar'}"><rect width="18" height="18" x="3" y="3" rx="2"/><path d="M9 3v18"/></c:when>
  <c:otherwise><circle cx="12" cy="12" r="9"/></c:otherwise>
</c:choose>
</svg>
