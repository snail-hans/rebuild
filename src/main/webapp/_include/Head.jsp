<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="cn.devezhao.persist4j.engine.ID"%>
<%@ page import="com.rebuild.utils.AppUtils"%>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="shortcut icon" href="${baseUrl}/assets/img/favicon.png">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/material-design-iconic-font.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/widget/perfect-scrollbar.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/widget/bootstrap-datetimepicker.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/animate.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/widget/select2.min.css?v=4.0.13">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/widget/mprogress.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/jquery-ui.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/css/rb-base.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/css/rb-page.css">
<meta name="rb.env" content="<%=AppUtils.devMode() ? "dev" : "production"%>">
<meta name="rb.locale" content="${bundle.locale()}">
<meta name="rb.baseUrl" content="${baseUrl}">
<meta name="rb.appName" content="${appName}">
<meta name="rb.storageUrl" content="${storageUrl}">
<meta name="rb.fileSharable" content="${fileSharable}">
<%
ID currentUser = AppUtils.getRequestUser(request);
if (currentUser != null) {
%>
<meta name="rb.currentUser" content="<%=currentUser%>">
<%if (AppUtils.isAdminUser(request)){%>
<meta name="rb.isAdminUser" content="true">
<meta name="rb.isAdminVerified" content="<%=AppUtils.isAdminVerified(request)%>">
<%}}%>
<%if (AppUtils.isIE(request)) {%>
<script id="ie-polyfill" src="${baseUrl}/assets/lib/react/polyfill.min.js?v=7.6.0"></script>
<!--[if lt IE 10]><script>location.href='${baseUrl}/error/unsupported-browser'</script><![endif]-->
<%}%>
<c:if test="${markWatermark}"><script src="${baseUrl}/assets/lib/watermark.js?v=2.3.2.2"></script></c:if>