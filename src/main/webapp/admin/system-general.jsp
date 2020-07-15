<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.rebuild.server.Application"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/_include/Head.jsp"%>
<title>通用配置</title>
<style type="text/css">
.syscfg a.img-thumbnail {
    display: inline-block;
    padding: 0.4rem 0.5rem;
    background-color: #fff;
    cursor: default;
}
</style>
</head>
<body>
<div class="rb-wrapper rb-fixed-sidebar rb-collapsible-sidebar rb-collapsible-sidebar-hide-logo rb-color-header">
<jsp:include page="/_include/NavTop.jsp">
	<jsp:param value="通用配置" name="pageTitle"/>
</jsp:include>
<jsp:include page="/_include/NavLeftAdmin.jsp">
	<jsp:param value="systems" name="activeNav"/>
</jsp:include>
<div class="rb-content">
	<div class="main-content container-fluid syscfg">
		<div class="row">
			<div class="col-lg-9 col-12">
				<div class="card">
					<div class="card-header card-header-divider">
                        通用配置
                        <a href="#modfiy" class="float-right"><i class="icon zmdi zmdi-edit"></i> 修改</a>
                    </div>
					<div class="card-body">
						<h5>通用</h5>
						<table class="table">
						<tbody>
							<tr>
								<td width="40%">名称</td>
								<td>${appName}</td>
							</tr>
							<tr>
								<td>LOGO</td>
								<td class="fs-0">
									<a class="img-thumbnail"><i class="logo-img"></i></a>
									<a class="img-thumbnail bg-primary ml-1"><i class="logo-img white"></i></a>
								</td>
							</tr>
							<tr>
								<td>主页地址/域名<p>基础 URL 所有链接将以此作为前缀</p></td>
								<td data-id="HomeURL">${HomeURL}</td>
							</tr>
							<tr>
								<td>公开注册</td>
								<td data-id="OpenSignUp" data-options="true:是;false:否">${OpenSignUp ? "是" : "否"}</td>
							</tr>
							<tr>
								<td>登录页每日一图</td>
								<td data-id="LiveWallpaper" data-options="true:是;false:否">${LiveWallpaper ? "是" : "否"}</td>
							</tr>
						</tbody>
						</table>
						<h5>系统安全</h5>
						<table class="table">
						<tbody>
							<tr>
								<td width="40%">登录密码等级</td>
								<td data-id="PasswordPolicy" data-options="1:低;2:中;3:高" data-value="${PasswordPolicy}">
								<c:choose>
									<c:when test="${PasswordPolicy >= 3}">高 (最低8位，必须同时包含数字、字母、特殊字符)</c:when>
									<c:when test="${PasswordPolicy == 2}">中 (最低6位，必须同时包含数字、字母)</c:when>
									<c:otherwise>低 (最低6位，无字符类型限制)</c:otherwise>
								</c:choose>
								</td>
							</tr>
							<tr>
								<td>允许分享文件</td>
								<td data-id="FileSharable" data-options="true:是;false:否">${FileSharable ? "是" : "否"}</td>
							</tr>
                            <tr>
								<td>显示页面水印</td>
								<td data-id="MarkWatermark" data-options="true:是;false:否">${MarkWatermark ? "是" : "否"}</td>
							</tr>
							<tr>
								<td>数据库自动备份<p>每日 0 点备份到数据目录，请预留足够磁盘空间</p></td>
								<td data-id="DBBackupsEnable" data-options="true:是;false:否">${DBBackupsEnable ? "是" : "否"}</td>
							</tr>
						</tbody>
						</table>
                        <div class="edit-footer">
                            <button class="btn btn-link">取消</button>
                            <button class="btn btn-primary">保存</button>
                        </div>
					</div>
				</div>
			</div>
			<div class="col-lg-3 col-12">
				<div class="card">
					<div class="card-header card-header-divider">关于 REBUILD</div>
					<div class="card-body">
						<p class="mb-1">系统版本 <a class="link" target="_blank" href="https://getrebuild.com/download?v=<%=Application.VER%>"><%=Application.VER%></a></p>
						<p class="mb-2">许可类型 <a class="link" target="_blank" href="https://getrebuild.com/authority?sn=${SN}" id="authType">开源社区版</a></p>
						<ul style="line-height:2">
							<li><a class="link" target="_blank" href="${baseUrl}/gw/server-status">系统状态</a></li>
							<li><a class="link" target="_blank" href="https://getrebuild.com/docs/">帮助文档</a></li>
							<li><a class="link" target="_blank" href="https://getrebuild.com/">技术支持</a></li>
						</ul>
						<div class="text-muted J_copyright">
							&copy; ${bundle.lang('RightsTip')}
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>
<%@ include file="/_include/Foot.jsp"%>
<script src="${baseUrl}/assets/js/admin/syscfg.jsx" type="text/babel"></script>
<script>
$(document).ready(function () {
	$.get('systems/query-authority', function (res) { $('#authType').text(res.data.authType) })
	$('.J_copyright a').attr('target', '_blank')
})
</script>
</body>
</html>
