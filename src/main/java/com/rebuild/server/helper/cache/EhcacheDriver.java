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

package com.rebuild.server.helper.cache;

import com.rebuild.server.Application;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.io.Serializable;

/**
 * ehcache
 * 
 * @author devezhao
 * @since 01/02/2019
 */
public class EhcacheDriver<V extends Serializable> implements CacheTemplate<V> {

	private CacheManager ehcacheManager;

	protected EhcacheDriver(CacheManager ehcacheManager) {
		this.ehcacheManager = ehcacheManager;
	}
	
	@Override
	public String get(String key) {
		ValueWrapper w = cache().get(key);
		return w == null ? null : (String) w.get();
	}

	@Override
	public void put(String key, String value) {
		put(key, value, -1);
	}

	@Override
	public void put(String key, String value, int seconds) {
		Element el = new Element(key, value);
		if (seconds > -1) {
			el.setTimeToLive(seconds);
		}
		((Ehcache) cache().getNativeCache()).put(el);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getx(String key) {
		ValueWrapper w = cache().get(key);
		return w == null ? null : (V) w.get();
	}

	@Override
	public void putx(String key, V value) {
		putx(key, value, -1);
	}

	@Override
	public void putx(String key, V value, int seconds) {
		Element el = new Element(key, value);
		if (seconds > -1) {
			el.setTimeToLive(seconds);
		}
		((Ehcache) cache().getNativeCache()).put(el);
	}

	@Override
	public void evict(String key) {
		cache().evict(key);
	}
	
	/**
	 * @return
	 */
	public Cache cache() {
		return ehcacheManager.getCache("rebuild");
	}
	
	/**
	 * 关闭 Ehcache 以便将缓存持久化到硬盘
	 */
	public void shutdown() {
		Application.LOG.info("Ehcache shutdown ...");
		((EhCacheCacheManager) ehcacheManager).getCacheManager().shutdown();
	}
}
