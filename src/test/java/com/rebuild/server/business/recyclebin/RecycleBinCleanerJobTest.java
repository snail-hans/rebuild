/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.server.business.recyclebin;

import com.rebuild.server.TestSupport;
import org.junit.Test;

/**
 * @author devezhao
 * @since 2020/2/23
 */
public class RecycleBinCleanerJobTest extends TestSupport {

    @Test
    public void executeInternal() throws Exception {
        new RecycleBinCleanerJob().executeInternal(null);
    }
}