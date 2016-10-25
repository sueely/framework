package com.pci.controller.activiti;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.pci.controller.activiti.util.HistoryProcessInstanceDiagramCmd;
import com.pci.controller.activiti.util.WorkflowUtils;
import com.pci.controller.base.BaseController;
import com.pci.entity.common.AjaxJson;
import com.pci.entity.common.DataGrid;


/**
 * @Description: TODO(工作流程定义与实例等资源处理类)
 * @author liujinghua
 *
 */
@Controller
@RequestMapping("/activitiController")
public class ActivitiController extends BaseController{

	private static final Logger logger = Logger.getLogger(ActivitiController.class);
	
	@Autowired
	protected RepositoryService repositoryService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private ProcessEngine processEngine;

	/**
	 * 流程定义列表
	 */
	@RequestMapping(value = "processList")
	public ModelAndView processList(HttpServletRequest request) {
		ModelAndView mv  = new ModelAndView("activiti/process/processlist");
		String message = request.getParameter("su");
		if(StringUtils.isNotEmpty(message)){
			mv.addObject("message", message);
		}
		return mv;
	}
	
	
	/**
	 * 我的流程定义
	 */
	@RequestMapping(params = "myProcessList")
	public ModelAndView myProcessList(HttpServletRequest request) {
			return new ModelAndView("activiti/process/startProcessList");
	}
	
	
	
	/**
	 * 流程启动表单选择
	 */
	@RequestMapping(params = "startPageSelect")
	public ModelAndView startPageSelect(@RequestParam("startPage") String startPage,HttpServletRequest request) {
		
			return new ModelAndView("activiti/my/"+startPage.substring(0, startPage.lastIndexOf(".")));
	}
	
	
	/**
	 * easyui 运行中流程列表页面
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "runningProcessList")
	public ModelAndView runningProcessList(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		return new ModelAndView("activiti/process/runninglist");
	}
	
	/**
	 * easyui 运行中流程列表数据
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "runningProcessDataGrid")
	public void runningProcessDataGrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		
		/*List<HistoricProcessInstance> historicProcessInstances = historyService
                .createHistoricProcessInstanceQuery()
                .unfinished().list();*/
		 ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
	     List<ProcessInstance> list = processInstanceQuery.list();
		
		StringBuffer rows = new StringBuffer();
		for(ProcessInstance hi : list){
			rows.append("{'id':"+hi.getId()+",'processDefinitionId':'"+hi.getProcessDefinitionId() +"','processInstanceId':'"+hi.getProcessInstanceId()+"','activityId':'"+hi.getActivityId()+"'},");
		}
		
		
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+list.size()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}
	
	
	/**
	 * 读取工作流定义的图片或xml
	 * @throws Exception
	 */
	@RequestMapping(params = "resourceRead")
    public void resourceRead(@RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("resourceType") String resourceType,
                                 HttpServletResponse response) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }
	
	/**
	 * 读取带跟踪的流程图片
	 * @throws Exception
	 */
	@RequestMapping(params = "traceImage")
    public void traceImage(@RequestParam("processInstanceId") String processInstanceId,
    		HttpServletResponse response) throws Exception {
    	
		Command<InputStream> cmd = new HistoryProcessInstanceDiagramCmd(
                processInstanceId);

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine(); 
        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        
        int len = 0;
        byte[] b = new byte[1024];

        while ((len = is.read(b, 0, 1024)) != -1) {
        	response.getOutputStream().write(b, 0, len);
        }
    }
	
	/**
	 * easyui 流程历史页面
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "viewProcessInstanceHistory")
	public ModelAndView viewProcessInstanceHistory(@RequestParam("processInstanceId") String processInstanceId,
			HttpServletRequest request, HttpServletResponse respone,Model model) {
		
		model.addAttribute("processInstanceId", processInstanceId);
		
		return new ModelAndView("activiti/process/viewProcessInstanceHistory");
	}
	
	/**
	 * easyui 流程历史数据获取
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "taskHistoryList")
	public void taskHistoryList(@RequestParam("processInstanceId") String processInstanceId,
			HttpServletRequest request, HttpServletResponse response,DataGrid dataGrid) {
		
        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
        
        StringBuffer rows = new StringBuffer();
        for(HistoricTaskInstance hi : historicTasks){
			rows.append("{'name':'"+hi.getName()+"','processInstanceId':'"+hi.getProcessInstanceId() +"','startTime':'"+hi.getStartTime()+"','endTime':'"+hi.getEndTime()+"','assignee':'"+hi.getAssignee()+"','deleteReason':'"+hi.getDeleteReason()+"'},");
        	//System.out.println(hi.getName()+"@"+hi.getAssignee()+"@"+hi.getStartTime()+"@"+hi.getEndTime());
        }
		
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+historicTasks.size()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}
	
	
	/**
     * 删除部署的流程，级联删除流程实例
     * @param deploymentId 流程部署ID
     */
	@RequestMapping(params = "del")
	@ResponseBody
	public AjaxJson del(@RequestParam("deploymentId") String deploymentId, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		String message = "删除成功";
		try {
			repositoryService.deleteDeployment(deploymentId, true);
		} catch (Exception e) {
			message = "删除错误："+e.getMessage();
		}
		j.setMsg(message);
		return j;
	}
	
	
	/**
	 * easyui AJAX请求数据
	 * @param request
	 * @param response
	 * @param dataGrid
	 */

	@RequestMapping(params = "datagrid")
	public void datagrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		
		StringBuffer rows = new StringBuffer();
		int i = 0;
		for(ProcessDefinition pi : list){
			i++;
			rows.append("{'id':"+i+",'processDefinitionId':'"+pi.getId() +"','startPage':'"+pi.getDescription()+"','resourceName':'"+pi.getResourceName()+"','deploymentId':'"+pi.getDeploymentId()+"','key':'"+pi.getKey()+"','name':'"+pi.getName()+"','version':'"+pi.getVersion()+"','isSuspended':'"+pi.isSuspended()+"'},");
		}
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+query.count()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}
	
	
	/**
	 * easyui 待领任务页面
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "waitingClaimTask")
	public ModelAndView waitingClaimTask() {
		
		return new ModelAndView("activiti/process/waitingClaimTask");
	}
	
	/**
	 * easyui AJAX请求数据 待领任务
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "waitingClaimTaskDataGrid")
	public void waitingClaimTaskDataGrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		
		String userId = "hruser";
		TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).active().list();//.taskCandidateGroup("hr").active().list();
		
		StringBuffer rows = new StringBuffer();
		for(Task t : tasks){
			rows.append("{'name':'"+t.getName() +"','taskId':'"+t.getId()+"','processDefinitionId':'"+t.getProcessDefinitionId()+"'},");
		}
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+tasks.size()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}
	
	/**
	 * easyui 待办任务页面
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "claimedTask")
	public ModelAndView claimedTask() {
		
		return new ModelAndView("activiti/process/claimedTask");
	}
	
	/**
	 * easyui AJAX请求数据 待办任务
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "claimedTaskDataGrid")
	public void claimedTaskDataGrid(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		
		String userId = "leaderuser";
		TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).list();
		
		StringBuffer rows = new StringBuffer();
		for(Task t : tasks){
			rows.append("{'name':'"+t.getName() +"','description':'"+t.getDescription()+"','taskId':'"+t.getId()+"','processDefinitionId':'"+t.getProcessDefinitionId()+"','processInstanceId':'"+t.getProcessInstanceId()+"'},");
		}
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+tasks.size()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}
	
	/**
	 * easyui 已办任务页面
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "finishedTask")
	public ModelAndView finishedTask() {
		
		return new ModelAndView("activiti/process/finishedTask");
	}
	
	/**
	 * easyui AJAX请求数据 已办任务
	 * @param request
	 * @param response
	 * @param dataGrid
	 */
	@RequestMapping(params = "finishedTaskDataGrid")
	public void finishedTask(HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
		
		String userId = "leaderuser";
		List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery().taskAssignee(userId)
                .finished().list();
		
		StringBuffer rows = new StringBuffer();
		for(HistoricTaskInstance t : historicTasks){
			rows.append("{'name':'"+t.getName() +"','description':'"+t.getDescription()+"','taskId':'"+t.getId()+"','processDefinitionId':'"+t.getProcessDefinitionId()+"','processInstanceId':'"+t.getProcessInstanceId()+"'},");
		}
		String rowStr = StringUtils.substringBeforeLast(rows.toString(), ",");
		
		JSONObject jObject = JSONObject.fromObject("{'total':"+historicTasks.size()+",'rows':["+rowStr+"]}");
		responseDatagrid(response, jObject);
	}

	/**
     * 签收任务
     * @param taskId
     */
	@RequestMapping(params = "claimTask")
	@ResponseBody
	public AjaxJson claimTask(@RequestParam("taskId") String taskId, HttpServletRequest request) {
		AjaxJson j = new AjaxJson();
		
		String userId = "leaderuser";
		
		TaskService taskService = processEngine.getTaskService();
        taskService.claim(taskId, userId);
		
		String message = "签收成功";
		j.setMsg(message);
		return j;
	}
	
		// -----------------------------------------------------------------------------------
		// 以下各函数可以提成共用部件 (Add by Quainty)
		// -----------------------------------------------------------------------------------
		public void responseDatagrid(HttpServletResponse response, JSONObject jObject) {
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter pw=response.getWriter();
				pw.write(jObject.toString());
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	    @RequestMapping(value = "/deploy")
	    public String deploy(@RequestParam(value = "file", required = false) MultipartFile file) {
	    	String reuslt="redirect:processList?su=部署成功";
	        String fileName = file.getOriginalFilename();

	        try {
	            InputStream fileInputStream = file.getInputStream();
	            Deployment deployment = null;

	            String extension = FilenameUtils.getExtension(fileName);
	            if (extension.equals("zip") || extension.equals("bar")) {
	                ZipInputStream zip = new ZipInputStream(fileInputStream);
	                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
	            } else {
	                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
	            }

	            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

//	            for (ProcessDefinition processDefinition : list) {
//	                WorkflowUtils.exportDiagramToFile(repositoryService, processDefinition, exportDir);
//	            }

	        } catch (Exception e) {
	            logger.error("error on deploy process, because of file input stream", e);
	            reuslt="redirect:processList?su=部署失败："+e.getMessage();
	        }

	        return reuslt;
	    }
}
