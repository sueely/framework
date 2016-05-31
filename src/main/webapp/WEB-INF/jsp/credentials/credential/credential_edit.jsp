<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	<base href="<%=basePath%>">
	<!-- 下拉框 -->
	<link rel="stylesheet" href="static/ace/css/chosen.css" />
	<!-- jsp文件头和头部 -->
	<%@ include file="../../system/index/top.jsp"%>
	<!-- 日期框 -->
	<link rel="stylesheet" href="static/ace/css/datepicker.css" />
</head>
<body class="no-skin">
<!-- /section:basics/navbar.layout -->
<div class="main-container" id="main-container">
	<!-- /section:basics/sidebar -->
	<div class="main-content">
		<div class="main-content-inner">
			<div class="page-content">
				<div class="row">
					<div class="col-xs-12">
					
					<form action="credential/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="CREDENTIAL_ID" id="CREDENTIAL_ID" value="${pd.CREDENTIAL_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">姓名:</td>
								<td><input type="text" name="STU_NAME" id="STU_NAME" value="${pd.STU_NAME}" maxlength="255" placeholder="这里输入姓名" title="姓名" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">性别 1-男 2-女:</td>
								<td><input type="text" name="STU_SEX" id="STU_SEX" value="${pd.STU_SEX}" maxlength="255" placeholder="这里输入性别 1-男 2-女" title="性别 1-男 2-女" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">学习时间:</td>
								<td><input type="text" name="STU_LTIME" id="STU_LTIME" value="${pd.STU_LTIME}" maxlength="255" placeholder="这里输入学习时间" title="学习时间" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">身份证号:</td>
								<td><input type="text" name="STU_CARD" id="STU_CARD" value="${pd.STU_CARD}" maxlength="255" placeholder="这里输入身份证号" title="身份证号" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">证书图片:</td>
								<td><input type="text" name="STU_PIC" id="STU_PIC" value="${pd.STU_PIC}" maxlength="255" placeholder="这里输入证书图片" title="证书图片" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
						
					</form>
	
					<div id="zhongxin2" class="center" style="display:none"><img src="static/images/jzx.gif" style="width: 50px;" /><br/><h4 class="lighter block green"></h4></div>
					</div>
					<!-- /.col -->
				</div>
				<!-- /.row -->
			</div>
			<!-- /.page-content -->
		</div>
	</div>
	<!-- /.main-content -->
</div>
<!-- /.main-container -->


	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
		<script type="text/javascript">
		$(top.hangge());
		//保存
		function save(){
			if($("#STU_NAME").val()==""){
				$("#STU_NAME").tips({
					side:3,
		            msg:'请输入姓名',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STU_NAME").focus();
			return false;
			}
			if($("#STU_SEX").val()==""){
				$("#STU_SEX").tips({
					side:3,
		            msg:'请输入性别 1-男 2-女',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STU_SEX").focus();
			return false;
			}
			if($("#STU_LTIME").val()==""){
				$("#STU_LTIME").tips({
					side:3,
		            msg:'请输入学习时间',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STU_LTIME").focus();
			return false;
			}
			if($("#STU_CARD").val()==""){
				$("#STU_CARD").tips({
					side:3,
		            msg:'请输入身份证号',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STU_CARD").focus();
			return false;
			}
			if($("#STU_PIC").val()==""){
				$("#STU_PIC").tips({
					side:3,
		            msg:'请输入证书图片',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#STU_PIC").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
		});
		</script>
</body>
</html>