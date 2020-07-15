/*
rebuild - Building your business-systems freely.
Copyright (C) 2018 devezhao <zhaofang123@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package com.rebuild.server.service.base;

import cn.devezhao.persist4j.engine.ID;
import com.rebuild.server.Application;

/**
 * 取消共享
 * 
 * @author devezhao
 * @since 12/19/2018
 */
public class BulkUnshare extends BulkOperator {

	protected BulkUnshare(BulkContext context, GeneralEntityService ges) {
		super(context, ges);
	}

	@Override
	protected Integer exec() {
		final ID[] records = prepareRecords();
		this.setTotal(records.length);
		
		final ID realTarget = context.getTargetRecord();

		// 只需要验证主记录权限
		if (!Application.getSecurityManager().allowShare(context.getOpUser(), realTarget)) {
			this.setCompleted(records.length);
			return 0;
		}
		
		for (ID id : records) {
			int a = ges.unshare(realTarget, id);
			if (a > 0) {
				this.addSucceeded();
			}
			this.addCompleted();
		}

		return getSucceeded();
	}
}
