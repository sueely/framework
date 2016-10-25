package com.pci.tags.easyui;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.pci.util.oConvertUtils;


/**
 * 
 * @author  张代浩
 *
 */
public class BaseTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String type = "default";// 加载类型

	public void setType(String type) {
		this.type = type;
	}

	
	public int doStartTag() throws JspException {
		return EVAL_PAGE;
	}

	
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
			String path = request.getContextPath();
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
			JspWriter out = this.pageContext.getOut();
			StringBuffer sb = new StringBuffer();

			String types[] = type.split(",");
			if (oConvertUtils.isIn("jquery", types)) {
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"static/js/jquery-1.7.2.js\"></script>");
			}
//			if (oConvertUtils.isIn("ckeditor", types)) {
//				sb.append("<script type=\"text/javascript\" src=\"plugins/ckeditor/ckeditor.js\"></script>");
//				sb.append("<script type=\"text/javascript\" src=\"plugins/tools/ckeditorTool.js\"></script>");
//			}
//			if (oConvertUtils.isIn("ckfinder", types)) {
//				sb.append("<script type=\"text/javascript\" src=\"plugins/ckfinder/ckfinder.js\"></script>");
//				sb.append("<script type=\"text/javascript\" src=\"plugins/tools/ckfinderTool.js\"></script>");
//			}
			if (oConvertUtils.isIn("easyui", types)) {
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/tools/dataformat.js\"></script>");
				sb.append("<link id=\"easyuiTheme\" rel=\"stylesheet\" href=\""+basePath+"plugins/easyui/themes/default/easyui.css\" type=\"text/css\"></link>");
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/easyui/themes/icon.css\" type=\"text/css\"></link>");
				sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+basePath+"plugins/accordion/css/accordion.css\">");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/easyui/jquery.easyui.min.1.3.2.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/easyui/locale/easyui-lang-zh_CN.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/tools/syUtil.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/easyui/extends/datagrid-scrollview.js\"></script>");
			}
			if (oConvertUtils.isIn("DatePicker", types)) {
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/My97DatePicker/WdatePicker.js\"></script>");
			}
			if (oConvertUtils.isIn("jqueryui", types)) {
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/jquery-ui/css/ui-lightness/jquery-ui-1.9.2.custom.min.css\" type=\"text/css\"></link>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery-ui/js/jquery-ui-1.9.2.custom.min.js\"></script>");
			}
			if (oConvertUtils.isIn("jqueryui-sortable", types)) {
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/jquery-ui/css/ui-lightness/jquery-ui-1.9.2.custom.min.css\" type=\"text/css\"></link>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery-ui/js/ui/jquery.ui.core.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery-ui/js/ui/jquery.ui.widget.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery-ui/js/ui/jquery.ui.mouse.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery-ui/js/ui/jquery.ui.sortable.js\"></script>");
			}
			if (oConvertUtils.isIn("prohibit", types)) {
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/tools/prohibitutil.js\"></script>");		}

			if (oConvertUtils.isIn("tools", types)) {
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/tools/css/common.css\" type=\"text/css\"></link>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/lhgDialog/lhgdialog.min.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/tools/curdtools.js\"></script>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/tools/easyuiextend.js\"></script>");
			}
			if (oConvertUtils.isIn("toptip", types)) {
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/toptip/css/css.css\" type=\"text/css\"></link>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/toptip/manhua_msgTips.js\"></script>");
			}
			if (oConvertUtils.isIn("autocomplete", types)) {
				sb.append("<link rel=\"stylesheet\" href=\""+basePath+"plugins/jquery/jquery-autocomplete/jquery.autocomplete.css\" type=\"text/css\"></link>");
				sb.append("<script type=\"text/javascript\" src=\""+basePath+"plugins/jquery/jquery-autocomplete/jquery.autocomplete.min.js\"></script>");
			}
			out.print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

}
