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

import cn.devezhao.persist4j.engine.ID;
import com.alibaba.fastjson.JSON;
import com.rebuild.server.TestSupportWithUser;
import org.junit.Test;

/**
 * @author devezhao zhaofang123@gmail.com
 * @since 2019/08/21
 */
public class RecycleStoreTest extends TestSupportWithUser {

    @Test
    public void serialize() {
        ID test = addRecordOfTestAllFields();
        JSON s = new RecycleBean(test).serialize();
        System.out.println(s);
    }

    @Test
    public void store() {
        ID test1 = addRecordOfTestAllFields();
        ID test2 = addRecordOfTestAllFields();

        RecycleStore recycleStore = new RecycleStore(getSessionUser());
        recycleStore.add(test1);
        recycleStore.add(test2);
        recycleStore.removeLast();

        recycleStore.add(test2, test1);

        int s = recycleStore.store();
        System.out.println(s);
    }
}