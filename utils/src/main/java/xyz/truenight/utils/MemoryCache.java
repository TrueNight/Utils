/**
 * Copyright (C) 2016 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class MemoryCache<K, V> {
    private final ConcurrentHashMap<K, CacheReference<V>> MAP = new ConcurrentHashMap<K, CacheReference<V>>();
    private CacheReference<V> NULL_KEY;

    public V get(Object key) {
        return key == null ? getKeyNull() : Utils.unwrap(MAP.get(key));
    }

    public <T> T get(Object key, Class<T> clazz) {
        return (T) (key == null ? getKeyNull() : Utils.unwrap(MAP.get(key)));
    }

    public int size() {
        return MAP.size() + (getKeyNull() == null ? 0 : 1);
    }

    public boolean isEmpty() {
        return MAP.isEmpty() && getKeyNull() == null;
    }

    public boolean containsKey(Object key) {
        return (key == null && Utils.unwrap(NULL_KEY) != null) || MAP.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return Utils.equal(getKeyNull(), value) || MAP.containsValue(reference(value));
    }

    public V put(K key, V value) {
        // // TODO: 05/09/16 check memory
        return key == null ?
                getAndSetKeyNull(value) :
                (
                        value == null ?
                                Utils.unwrap(MAP.remove(key)) :
                                Utils.unwrap(MAP.put(key, reference(value)))
                );
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(Object key) {
        return key == null ? getAndSetKeyNull(null) : Utils.unwrap(MAP.remove(key));
    }

    public void clear() {
        setKeyNull(null);
        MAP.clear();
    }

    public boolean compare(K key, V value) {
        Object storedValue = get(key);
        return storedValue == null ? value == null : storedValue.equals(reference(value));
    }

    private V getKeyNull() {
        return Utils.unwrap(NULL_KEY);
    }

    private void setKeyNull(V value) {
        NULL_KEY = reference(value);
    }

    private <T> CacheReference<T> reference(T value) {
        if (value == null) {
            return null;
        } else {
            return new CacheReference<>(value);
        }
    }

    private V getAndSetKeyNull(V value) {
        V object = getKeyNull();
        setKeyNull(value);
        return object;
    }

    public class CacheReference<T> extends SoftReference<T> {
        public CacheReference(T referent) {
            super(referent);
        }

        public CacheReference(T referent, ReferenceQueue<? super T> q) {
            super(referent, q);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CacheReference && Utils.equal(get(), ((CacheReference) obj).get());
        }
    }
}