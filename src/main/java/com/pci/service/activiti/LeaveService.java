package com.pci.service.activiti;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pci.dao.DaoSupport;
import com.pci.util.PageData;

@Service
@Transactional
public class LeaveService {
	
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Autowired
    private IdentityService identityService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 启动请假流程
	 * @param leave
	 * @throws Exception 
	 */
	public void leaveWorkFlowStart(PageData pd) throws Exception{
		
		dao.save("LeaveMapper.save", pd);
        logger.debug("save entity: {}", pd);
        
        String businessKey = String.valueOf(pd.get("id"));
        ProcessInstance processInstance = null;
        try {
            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
            identityService.setAuthenticatedUserId(pd.getString("userId"));

            Map<String, Object> variables = new HashMap<String, Object>();
            processInstance = runtimeService.startProcessInstanceByKey("leave", "businessKey", variables);
            String processInstanceId = processInstance.getId();
            pd.put("processInstanceId", processInstanceId);
//            entity.setProcessInstanceId(processInstanceId);
            dao.update("LeaveMapper.edit", pd);
            logger.debug("start process of {key={}, bkey={}, pid={}, variables={}}", new Object[]{"leave", businessKey, processInstanceId, variables});
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
		
	}
	
	@Transactional(readOnly = true)
	public PageData getLeave(PageData pd) throws Exception{
		return (PageData)dao.findForObject("LeaveMapper.findById", pd);
	}
	
}
