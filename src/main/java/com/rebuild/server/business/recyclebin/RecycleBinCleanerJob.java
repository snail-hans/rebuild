/*
rebuild - Building your business-systems freely.
Copyright (C) 2019 devezhao <zhaofang123@gmail.com>

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

package com.rebuild.server.business.recyclebin;

import cn.devezhao.commons.CalendarUtils;
import cn.devezhao.persist4j.Entity;
import com.rebuild.server.Application;
import com.rebuild.server.helper.ConfigurableItem;
import com.rebuild.server.helper.SysConfiguration;
import com.rebuild.server.metadata.EntityHelper;
import com.rebuild.server.metadata.MetadataHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * 回收站/变更历史清理
 *
 * @author devezhao
 * @since 2019/8/21
 */
public class RecycleBinCleanerJob extends QuartzJobBean {

    private static final Log LOG = LogFactory.getLog(RecycleBinCleanerJob.class);

    // 永久保留
    private static final int KEEPING_FOREVER = 9999;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        // 回收站
        final int rbDays = SysConfiguration.getInt(ConfigurableItem.RecycleBinKeepingDays);
        if (rbDays < KEEPING_FOREVER) {
            LOG.info("RecycleBin clean running ... " + rbDays);
            Entity entity = MetadataHelper.getEntity(EntityHelper.RecycleBin);
            Date before = CalendarUtils.addDay(-rbDays);

            String delSql = String.format("delete from `%s` where `%s` < '%s 00:00:00'",
                    entity.getPhysicalName(),
                    entity.getField("deletedOn").getPhysicalName(),
                    CalendarUtils.getUTCDateFormat().format(before));
            int del = Application.getSQLExecutor().execute(delSql, 120);
            LOG.warn("RecycleBin cleaned : " + del);

            // TODO 相关引用也在此时一并删除，因为记录已经彻底删除了

        }

        // 变更历史
        final int rhDays = SysConfiguration.getInt(ConfigurableItem.RevisionHistoryKeepingDays);
        if (rhDays < KEEPING_FOREVER) {
            LOG.info("RevisionHistory clean running ... " + rhDays);
            Entity entity = MetadataHelper.getEntity(EntityHelper.RevisionHistory);
            Date before = CalendarUtils.addDay(-rhDays);

            String delSql = String.format("delete from `%s` where `%s` < '%s 00:00:00'",
                    entity.getPhysicalName(),
                    entity.getField("revisionOn").getPhysicalName(),
                    CalendarUtils.getUTCDateFormat().format(before));
            int del = Application.getSQLExecutor().execute(delSql, 120);
            LOG.warn("RevisionHistory cleaned : " + del);
        }
    }
}
