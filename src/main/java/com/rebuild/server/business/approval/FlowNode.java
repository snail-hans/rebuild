/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.server.business.approval;

import cn.devezhao.persist4j.engine.ID;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.server.Application;
import com.rebuild.server.service.bizz.UserHelper;
import com.rebuild.utils.JSONUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 流程节点
 * 
 * @author devezhao-mbp zhaofang123@gmail.com
 * @since 2019/07/06
 */
public class FlowNode {

	// 特殊节点

	public static final String NODE_ROOT = "ROOT";
	public static final String NODE_CANCELED = "CANCELED";
	public static final String NODE_REVOKED = "REVOKED";

	// 节点类型
	
	public static final String TYPE_START = "start";
	public static final String TYPE_APPROVER = "approver";
	public static final String TYPE_CC = "cc";
	public static final String TYPE_CONDITION = "condition";
	public static final String TYPE_BRANCH = "branch";
	
	// 人员类型
	
	public static final String USER_ALL = "ALL";
	public static final String USER_SELF = "SELF";
	public static final String USER_SPEC = "SPEC";
	public static final String USER_OWNS = "OWNS";
	
	// 多人联合审批类型
	
	public static final String SIGN_AND = "AND";  // 会签
	public static final String SIGN_OR = "OR";	  // 或签
	public static final String SIGN_ALL = "ALL";  // 逐个审批
	
	// --
	
	private String nodeId;
	private String type;
	private JSONObject dataMap;
	
	protected String prevNodes;
	
	/**
	 * @param nodeId
	 * @param type
	 * @param dataMap
	 */
	protected FlowNode(String nodeId, String type, JSONObject dataMap) {
		super();
		this.nodeId = nodeId;
		this.type = type;
		this.dataMap = dataMap;
	}

	/**
	 * @return
	 */
	public String getNodeId() {
		return nodeId;
	}
	
	/**
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return
	 */
	public JSONObject getDataMap() {
		return dataMap == null ? JSONUtils.EMPTY_OBJECT : dataMap;
	}
	
	/**
	 * @return
	 */
	public String getSignMode() {
		return StringUtils.defaultIfBlank(getDataMap().getString("signMode"), SIGN_OR);
	}
	
	/**
	 * @return
	 */
	public boolean allowSelfSelecting() {
		if (getDataMap().containsKey("selfSelecting")) {
			return getDataMap().getBooleanValue("selfSelecting");
		}
		return false;
	}
	
	/**
	 * 获取相关人员（提交人/审批人/抄送人）
	 * 
	 * @param operator
	 * @param record
	 * @return
	 */
	public Set<ID> getSpecUsers(ID operator, ID record) {
		JSONArray userDefs = getDataMap().getJSONArray("users");
		if (userDefs == null || userDefs.isEmpty()) {
			return Collections.emptySet();
		}

		String userType = userDefs.getString(0);
		if (USER_SELF.equalsIgnoreCase(userType)) {
			Set<ID> users = new HashSet<>();
			ID owning = Application.getRecordOwningCache().getOwningUser(record);
			users.add(owning);
			return users;
		}
		
		List<String> defsList = new ArrayList<>();
		for (Object o : userDefs) {
			defsList.add((String) o);
		}
		return UserHelper.parseUsers(defsList, null);
	}
	
	@Override
	public String toString() {
		String string = String.format("Id:%s, Type:%s", nodeId, type);
		if (prevNodes != null) {
			string += ", Prev:" + prevNodes;
		}
		if (dataMap != null) {
			string += ", Data:" + dataMap.toJSONString();
		}
		return string;
	}

	@Override
	public int hashCode() {
		return this.nodeId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
            return false;
        }
		return obj instanceof FlowNode && obj.hashCode() == this.hashCode();
	}

	/**
	 * 节点可编辑字段
	 *
	 * @return
	 */
	public JSONArray getEditableFields() {
		JSONArray editableFields = dataMap == null ? null : dataMap.getJSONArray("editableFields");
		if (editableFields == null) {
			return null;
		}

		editableFields = (JSONArray) JSONUtils.clone(editableFields);
		for (Object o : editableFields) {
			JSONObject field = (JSONObject) o;
			field.put("nullable", !((Boolean) field.remove("notNull")));
		}
		return editableFields;
	}

	// --

	/**
	 * @param node
	 * @return
	 */
	public static FlowNode valueOf(JSONObject node) {
		return new FlowNode(
				node.getString("nodeId"), node.getString("type"), node.getJSONObject("data"));
	}
}
