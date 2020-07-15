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

package com.rebuild.server.business.trigger.impl;

import cn.devezhao.persist4j.Entity;
import cn.devezhao.persist4j.Record;
import cn.devezhao.persist4j.engine.ID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rebuild.server.Application;
import com.rebuild.server.TestSupportWithUser;
import com.rebuild.server.business.trigger.ActionType;
import com.rebuild.server.business.trigger.TriggerAction;
import com.rebuild.server.business.trigger.TriggerWhen;
import com.rebuild.server.configuration.RobotTriggerManager;
import com.rebuild.server.metadata.EntityHelper;
import com.rebuild.server.metadata.MetadataHelper;
import com.rebuild.server.service.bizz.UserService;
import com.rebuild.server.service.configuration.RobotTriggerConfigService;
import org.junit.Test;

/**
 * @author devezhao zhaofang123@gmail.com
 * @since 2019/05/29
 */
public class FieldAggregationTest extends TestSupportWithUser {

    @Test
    public void testExecute() throws Exception {
        addExtTestEntities(false);

        // 添加配置
        Record triggerConfig = EntityHelper.forNew(EntityHelper.RobotTriggerConfig, UserService.SYSTEM_USER);
        triggerConfig.setString("belongEntity", "SalesOrderItem999");
        triggerConfig.setInt("when", TriggerWhen.CREATE.getMaskValue() + TriggerWhen.DELETE.getMaskValue());
        triggerConfig.setString("actionType", ActionType.FIELDAGGREGATION.name());
        String content = "{targetEntity:'SalesOrder999Id.SalesOrder999', items:[{sourceField:'',calcMode:'SUM', targetField:'totalAmount'}]}";
        triggerConfig.setString("actionContent", content);
        Application.getBean(RobotTriggerConfigService.class).create(triggerConfig);

        // 测试执行
        Entity test = MetadataHelper.getEntity("SalesOrderItem999");
        RobotTriggerManager.instance.clean(test);

        TriggerAction[] as = RobotTriggerManager.instance.getActions(ID.newId(test.getEntityCode()), TriggerWhen.CREATE);
        for (TriggerAction action : as) {
            action.execute(null);
        }

		// 清理
		Application.getBean(RobotTriggerConfigService.class).delete(triggerConfig.getPrimary());
    }

    @Test
    public void testEvaluator() throws Exception {
        addExtTestEntities(false);
        Entity sourceEntity = MetadataHelper.getEntity("SalesOrder999");

        JSONObject configUseFormula = JSON.parseObject("{ targetField:'totalAmount', calcMode:'FORMULA', sourceFormula:'{totalAmount$$$$SUM}*1.35' }");
        new AggregationEvaluator(
                configUseFormula, sourceEntity, "relatedAccount", null)
                .eval(ID.newId(sourceEntity.getEntityCode()));

        JSONObject configUseMAX = JSON.parseObject("{ targetField:'totalAmount', calcMode:'MAX', sourceField:'totalAmount' }");
        new AggregationEvaluator(
                configUseMAX, sourceEntity, "relatedAccount", "(2=2)")
                .eval(ID.newId(sourceEntity.getEntityCode()));
    }
}
