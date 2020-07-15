/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.web.notification;

import cn.devezhao.momentjava.Moment;
import cn.devezhao.persist4j.Record;
import cn.devezhao.persist4j.engine.ID;
import com.rebuild.server.Application;
import com.rebuild.server.business.approval.ApprovalState;
import com.rebuild.server.metadata.EntityHelper;
import com.rebuild.server.service.bizz.UserHelper;
import com.rebuild.server.service.notification.MessageBuilder;
import com.rebuild.utils.JSONUtils;
import com.rebuild.web.BasePageControll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 系统通知
 *
 * @author devezhao
 * @since 11/01/2018
 *
 * @see com.rebuild.server.service.notification.Message
 */
@Controller
public class NotificationControll extends BasePageControll {

	@RequestMapping("/notifications")
	public ModelAndView pageIndex(HttpServletRequest request) throws IOException {
		return createModelAndView("/notification/messages.jsp");
	}

	@RequestMapping("/notifications/todo")
	public ModelAndView pageTodo(HttpServletRequest request) throws IOException {
		return createModelAndView("/notification/todo.jsp");
	}

	@RequestMapping("/notification/check-state")
	public void checkMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int unread = Application.getNotifications().getUnreadMessage(getRequestUser(request));
		writeSuccess(response, JSONUtils.toJSONObject("unread", unread));
	}

	@RequestMapping("/notification/make-read")
	public void toggleUnread(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ID user = getRequestUser(request);
		String ids = getParameter(request, "id");

		if ("ALL".equalsIgnoreCase(ids)) {
			Object[][] unreads = Application.createQueryNoFilter(
					"select messageId from Notification where toUser = ?")
					.setParameter(1, user)
					.array();
			ids = "";
			for (Object[] o : unreads) {
				ids += o[0] + ",";
			}
		}

		for (String id : ids.split(",")) {
			if (!ID.isId(id)) {
				continue;
			}

			Record record = EntityHelper.forUpdate(ID.valueOf(id), user);
			record.setBoolean("unread", false);
			Application.getNotifications().update(record);
		}
		writeSuccess(response);
	}

	@RequestMapping("/notification/messages")
	public void listMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ID user = getRequestUser(request);
		int pn = getIntParameter(request, "pageNo", 1);
		int ps = getIntParameter(request, "pageSize", 40);
		int type = getIntParameter(request, "type", 0);
		boolean preview = getBoolParameter(request, "preview");

		String sql = "select fromUser,message,createdOn,unread,messageId,relatedRecord,type from Notification" +
                " where toUser = ? and (1=1) order by createdOn desc";
		if (type == 1) {
            sql = sql.replace("(1=1)", "unread = 'T'");
        } else if (type == 2) {
            sql = sql.replace("(1=1)", "unread = 'F'");
        } else if (type >= 10) {
            sql = sql.replace("(1=1)", String.format("(type >= %d and type < %d)", type, type + 10));
        }

		Object[][] array = Application.createQueryNoFilter(sql)
				.setParameter(1, user)
				.setLimit(ps, pn * ps - ps)
				.array();
		for (int i = 0; i < array.length; i++) {
			Object[] m = array[i];
			m[0] = new Object[] { m[0], UserHelper.getName((ID) m[0]) };
			m[1] = MessageBuilder.formatMessage((String) m[1], !preview, true);
			m[2] = Moment.moment((Date) m[2]).fromNow();
			array[i] = m;
		}
		writeSuccess(response, array);
	}

	@RequestMapping("/notification/approvals")
	public void listApprovals(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ID user = getRequestUser(request);
		int pn = getIntParameter(request, "pageNo", 1);
		int ps = getIntParameter(request, "pageSize", 40);

		Object[][] array = Application.createQueryNoFilter(
				"select fromUser,message,createdOn,relatedRecord,messageId" +
						" from Notification where toUser = ? and type = 20 and relatedRecord is not null order by createdOn desc")
				.setParameter(1, user)
				.setLimit(ps, pn * ps - ps)
				.array();

		for (int i = 0; i < array.length; i++) {
			Object[] m = array[i];
			m[0] = new Object[] { m[0], UserHelper.getName((ID) m[0]) };
			m[1] = MessageBuilder.formatMessage((String) m[1]);
			m[2] = Moment.moment((Date) m[2]).fromNow();

			// 审批状态
			ID approvalStep = (ID) m[3];
			Object[] stepState = Application.createQueryNoFilter(
					"select isCanceled,state from RobotApprovalStep where stepId = ?")
					.setParameter(1, approvalStep)
					.unique();
			if (stepState == null) {
				m[3] = new Object[] { 0 };
			} else {
				boolean canceled = (Boolean) stepState[0];
				ApprovalState state = (ApprovalState) ApprovalState.valueOf((Integer) stepState[1]);
				if (state == ApprovalState.DRAFT) {
					m[3] = canceled ? new Object[] { 2, "已处理" } : new Object[] { 1, "待处理" };
				} else if (state == ApprovalState.APPROVED) {
					m[3] = new Object[] { 10, "已同意" };
				} else if (state == ApprovalState.REJECTED) {
					m[3] = new Object[] { 11, "已驳回" };
				}
			}

			array[i] = m;
		}
		writeSuccess(response, array);
	}
}
