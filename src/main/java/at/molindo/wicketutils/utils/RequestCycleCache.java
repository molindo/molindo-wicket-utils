/**
 * Copyright 2010 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.wicketutils.utils;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.RequestCycle;

import at.molindo.utils.data.Pair;

/**
 * Utility methods for (ab)using {@link RequestCycle#getMetaData(MetaDataKey)}
 * and {@link RequestCycle#setMetaData(MetaDataKey, Object)} as request-scope
 * cache.
 * 
 * @author stf@molindo.at
 * 
 */
public class RequestCycleCache {
	private RequestCycleCache() {

	}

	public static <K, V> void put(final MetaDataKey<Pair<K, V>> metaDataKey, final K key, final V value) {
		put(RequestCycle.get(), metaDataKey, key, value);
	}

	public static <K, V> void put(final RequestCycle cycle, final MetaDataKey<Pair<K, V>> metaDataKey, final K key,
			final V value) {
		if (metaDataKey == null) {
			throw new NullPointerException("metaDataKey");
		}
		if (key == null) {
			throw new NullPointerException("key");
		}

		cycle.setMetaData(metaDataKey, Pair.pair(key, value));
	}

	public static <K, V> V get(final MetaDataKey<Pair<K, V>> metaDataKey, final K key) {
		return get(RequestCycle.get(), metaDataKey, key);
	}

	public static <K, V> V get(final RequestCycle cycle, final MetaDataKey<Pair<K, V>> metaDataKey, final K key) {
		if (metaDataKey == null) {
			throw new NullPointerException("metaDataKey");
		}
		if (key == null) {
			return null;
		}

		final Pair<K, V> pair = RequestCycle.get().getMetaData(metaDataKey);
		return pair == null || !pair.getKey().equals(key) ? null : pair.getValue();
	}

	/**
	 * tries to get value for key from cache or invokes function to generate it
	 * from key.
	 */
	public static <K, V> V getOrCreate(final MetaDataKey<Pair<K, V>> metaDataKey, final K key, Function<K, V> function) {
		return getOrCreate(RequestCycle.get(), metaDataKey, key, function);
	}

	/**
	 * tries to get value for key from cache or invokes function to generate it
	 * from key.
	 */
	public static <K, V> V getOrCreate(final RequestCycle cycle, final MetaDataKey<Pair<K, V>> metaDataKey,
			final K key, Function<K, V> function) {
		V value = get(cycle, metaDataKey, key);
		if (value == null) {
			value = function.invoke(key);
			put(cycle, metaDataKey, key, value);
		}
		return value;
	}

	public static <K, V> void remove(final MetaDataKey<Pair<K, V>> metaDataKey) {
		if (metaDataKey != null) {
			RequestCycle.get().setMetaData(metaDataKey, null);
		}
	}

	public interface Function<K, V> {
		V invoke(K key);
	}
}
