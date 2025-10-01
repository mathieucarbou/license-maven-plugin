/*
 * Copyright (C) 2008-2025 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LazyMap<K, V> implements Map<K, V> {
  private final Map<K, Supplier<V>> suppliers;
  private final Function<K, V> alternative; // used when no supplier is present

  public LazyMap() {
    this.suppliers = new HashMap<>();
    this.alternative = k -> null;
  }

  public LazyMap(int initialCapacity) {
    this.suppliers = new HashMap<>(initialCapacity);
    this.alternative = k -> null;
  }

  public LazyMap(Function<K, V> alternative) {
    this.suppliers = new HashMap<>();
    this.alternative = alternative;
  }

  public LazyMap(int initialCapacity, Function<K, V> alternative) {
    this.suppliers = new HashMap<>(initialCapacity);
    this.alternative = alternative;
  }

  public Supplier<V> getSupplier(K key) {
    return suppliers.get(key);
  }

  public void putSupplier(K key, Supplier<V> supplier) {
    suppliers.put(key, supplier);
  }

  /**
   * Returns the value from a supplier associated with the given key.
   * If no supplier found, the alternative function is used to compute the value.
   */
  @Override
  @SuppressWarnings("unchecked")
  public V get(Object key) {
    Supplier<V> supplier = suppliers.get(key);
    return supplier == null ? alternative.apply((K) key) : supplier.get();
  }

  @Override
  public V put(K key, V value) {
    suppliers.put(key, () -> value);
    return null; // does not adhere to the Map contract, but is consistent with the behavior of
                 // this map implementation
  }

  @Override
  public V remove(Object key) {
    suppliers.remove(key);
    return null; // does not adhere to the Map contract, but is consistent with the behavior of
                 // this map implementation
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    suppliers.clear();
  }

  @Override
  public boolean containsKey(Object key) {
    return suppliers.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return suppliers.values().stream().anyMatch(supplier -> Objects.equals(supplier.get(), value));
  }

  @Override
  public int size() {
    return suppliers.size();
  }

  @Override
  public boolean isEmpty() {
    return suppliers.isEmpty();
  }

  @Override
  public Set<K> keySet() {
    return suppliers.keySet();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return suppliers.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> get(e.getKey()))).entrySet();
  }

  @Override
  public Collection<V> values() {
    return suppliers.values().stream().map(Supplier::get).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Map)) {
      return false;
    }

    Map<?, ?> m = (Map<?, ?>) o;
    if (m.size() != size()) {
      return false;
    }

    try {
      for (Entry<K, V> e : entrySet()) {
        K key = e.getKey();
        V value = e.getValue();
        if (value == null) {
          if (!(m.get(key) == null && m.containsKey(key))) {
            return false;
          }
        } else {
          if (!value.equals(m.get(key))) {
            return false;
          }
        }
      }
    } catch (ClassCastException unused) {
      return false;
    } catch (NullPointerException unused) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return suppliers.hashCode();
  }

  @Override
  public String toString() {
    Iterator<Entry<K, V>> i = entrySet().iterator();
    if (!i.hasNext()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder();
    sb.append('{');
    for (;;) {
      Entry<K, V> e = i.next();
      K key = e.getKey();
      V value = e.getValue();
      sb.append(key == this ? "(this Map)" : key);
      sb.append('=');
      sb.append(value == this ? "(this Map)" : value);
      if (!i.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',').append(' ');
    }
  }
}
