<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>

<!-- jsp文件头和头部 -->
<head>
   <t:base type="jquery,easyui,tools,DatePicker,autocomplete"></t:base>
   <link rel="stylesheet" type="text/css" href="<%=basePath%>/static/ace/css/activiti/screen.css">
    <script type="text/javascript">
    $(function() {
    	$('#deploy').click(function() {
    		$('#deployFieldset').toggle('normal');
    	});
    });
    </script>
</head>

	<c:if test="${not empty message}">
	<div class="ui-widget">
			<div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"> 
				<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
				<strong>提示：</strong>${message}</p>
			</div>
		</div>
	</c:if>

	<div style="text-align: right;padding: 2px 1em 2px">
		<div id="message" class="info" style="display:inline;"><b>提示：</b>点击xml或者png链接可以查看具体内容！</div>
		<a id="deploy" href="#" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-primary" role="button" aria-disabled="false">
			<span class="ui-button-icon-primary ui-icon ui-icon-document"></span>
			<span class="ui-button-text">部署流程</span>
		</a>
	</div>
	<fieldset id="deployFieldset" style="display: none">
		<legend>部署新流程</legend>
		<div><b>支持文件格式：</b>zip、bar、bpmn、bpmn20.xml</div>
		<form action="<%=basePath%>/activitiController/deploy" method="post" enctype="multipart/form-data">
			<input type="file" name="file" />
			<input type="submit" value="Submit" />
		</form>
	</fieldset>

<div class="easyui-layout" fit="true">
<div region="center" style="padding: 1px;">
<table id="processList" style="width: 700px; height: 300px">
	<thead>
		<tr>
			<th field="id" hidden="hidden">编号</th>
			<th field="processDefinitionId" width="50">ProcessDefinitionId</th>
			<th field="deploymentId" width="50">DeploymentId</th>
			<th field="name" width="50">流程名称</th>
			<th field="key" width="50">KEY</th>
			<th field="version" width="20">版本</th>
			<th field="xml" width="50">流程文件</th>
			<th field="image" width="50">流程图片</th>
			<th field="isSuspended" width="50">是否挂起</th>
			<th field="opt" width="50">操作</th>
		</tr>
	</thead>
</table>

<script type="text/javascript">
        $(top.hangge());
		//查看流程xml或流程图片
		function readProcessResouce(processDefinitionId,resourceType) {
			var url = "";
			var title = "";
			if(resourceType == "xml"){
				title = "查看流程文件";
				url = "<%=basePath%>/activitiController.do?resourceRead&processDefinitionId="+processDefinitionId+"&resourceType=xml&isIframe"
				//url = "activitiController.do?resourceRead&processDefinitionId=vacation:1:10&resourceType=image&isIframe"
			}

			if(resourceType == "image"){
				title = "查看流程图片";
				url = "<%=basePath%>/activitiController.do?resourceRead&processDefinitionId="+processDefinitionId+"&resourceType=image&isIframe"
			}
			window.open(url,"_blank");
		}
		
	    // 编辑初始化数据
		function getData(data){
			var rows = [];			
			var total = data.total;
			for(var i=0; i<data.rows.length; i++){
				rows.push({
					id: data.rows[i].id,
					processDefinitionId: data.rows[i].processDefinitionId,
					deploymentId: data.rows[i].deploymentId,
					name: data.rows[i].name,
					key: data.rows[i].key,
					version: data.rows[i].version,
					xml: "[<a href=\"#\" onclick=\"readProcessResouce('"+data.rows[i].processDefinitionId+"','xml')\">查看流程xml</a>]",
					image: "[<a href=\"#\" onclick=\"readProcessResouce('"+data.rows[i].processDefinitionId+"','image')\">查看流程图片</a>]",
					isSuspended: data.rows[i].isSuspended,
					opt: "[<a href=\"#\" onclick=\"delObj('<%=basePath%>/activitiController.do?del&deploymentId="+data.rows[i].deploymentId+"','processList')\">删除流程</a>]"
				});
			}
			var newData={"total":total,"rows":rows};
			return newData;
		}
	    // 筛选
		function jeecgEasyUIListsearchbox(value,name){
    		var queryParams=$('#processList').datagrid('options').queryParams;
    		queryParams[name]=value;
    		queryParams.searchfield=name;
    		$('#processList').datagrid('load');
    	}
	    // 刷新
	    function reloadTable(){
	    	$('#processList').datagrid('reload');
	    }
	    
		// 设置datagrid属性
		$('#processList').datagrid({
			title: '流程定义及部署管理',
	        idField: 'id',
	        fit:true,
	        loadMsg: '数据加载中...',
	        pageSize: 10,
	        pagination:true,
	        sortOrder:'asc',
	        rownumbers:true,
	        singleSelect:true,
	        fitColumns:true,
	        showFooter:true,
	        url:'<%=basePath%>/activitiController.do?datagrid',  
	        loadFilter: function(data){
	        	return getData(data);
	    	}
	        
	    }); 
	    //设置分页控件  
	    $('#processList').datagrid('getPager').pagination({  
	        pageSize: 10,  
	        pageList: [10,20,30],  
	        beforePageText: '',  
	        afterPageText: '/{pages}',
	        displayMsg: '{from}-{to}共{total}条',
	        showPageList:true,
	        showRefresh:true,
	        onBeforeRefresh:function(pageNumber, pageSize){
	            $(this).pagination('loading');
	            $(this).pagination('loaded');
	        }
	    });
	    // 设置筛选
    	$('#jeecgEasyUIListsearchbox').searchbox({
    		searcher:function(value,name){
    			jeecgEasyUIListsearchbox(value,name);
    		},
    		menu:'#jeecgEasyUIListmm',
    		prompt:'请输入查询关键字'
    	});
	</script></div>
</div>