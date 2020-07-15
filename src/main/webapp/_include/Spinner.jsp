<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="rb-spinner">
	<% if (com.rebuild.utils.AppUtils.isIE(request)) {%>
	<span class="spinner-border spinner-border-xl text-primary"></span>
	<% } else {%>
	<svg width="40px" height="40px" viewBox="0 0 66 66" xmlns="http://www.w3.org/2000/svg">
		<circle fill="none" stroke-width="4" stroke-linecap="round" cx="33" cy="33" r="30" class="circle"></circle>
	</svg>
	<% }%>
</div>