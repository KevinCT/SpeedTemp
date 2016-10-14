package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface Map<K, V> extends java.util.Map<K, V> {
    public <X, Y> Map<X, Y> map(Lists.EntryMapping<X, Y> mapping);
    public <E> List<E> transform(Lists.Mapping<Map.Entry<K, V>, E> mapping);
    public Set<K> keys();

    public void forEach(Client<Entry<K, V>> client);

    @NonNull
    public Set<V> values();

    public Map<V, K> invert();
    public Map<K, V> filter(Query<Entry<K, V>> query);
    public Map<K, V> reject(Query<Entry<K, V>> query);
    public Map<K, V> nonNull();
}