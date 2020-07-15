<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ page import="com.rebuild.utils.AppUtils"%>
<%@ page import="cn.devezhao.commons.web.ServletUtils"%>
<%@ page import="com.rebuild.server.helper.language.Languages" %>
<%
String errorMsg = AppUtils.getErrorMessage(request, exception);
if (ServletUtils.isAjaxRequest(request)) {
	out.print(errorMsg);
	return;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="shortcut icon" href="${baseUrl}/assets/img/favicon.png">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/lib/material-design-iconic-font.min.css">
<link rel="stylesheet" type="text/css" href="${baseUrl}/assets/css/rb-base.css">
<title>${appName}</title>
</head>
<body class="rb-splash-screen">
<div class="rb-wrapper rb-error">
	<div class="rb-content m-0">
		<div class="main-content container">
			<div class="error-container">
				<div class="error-number mb-0"><i class="zmdi zmdi-alert-circle text-primary"></i></div>
				<div class="error-description" id="error"><%=errorMsg%></div>
				<div class="error-goback-button">
					<a class="btn btn-xl btn-space btn-secondary" href="${baseUrl}/dashboard/home" id="goHome"><%=Languages.lang("ReturnHome")%></a>
					<div class="mt-4">
						<a href="https://getrebuild.com/report-issue?title=error-40x" target="_blank"><%=Languages.lang("ReportIssue")%></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
if (self != top) {
	var btn = document.getElementById('goHome')
	btn.parentNode.removeChild(btn)
	if (location.href.indexOf('/view/') > -1 && parent.RbViewModal) {
		try {
			var viewid = location.href.split('/view/')[1].split('?')[0]
			parent.RbViewModal.holder(viewid).hideLoading()
		} catch (e) { }
	}
}
if (location.href.indexOf('unsupported-browser') > -1) document.getElementById('error').innerHTML = '<%=Languages.lang("UnsupportIE10Tip")%>'
</script>
</body>
</html>