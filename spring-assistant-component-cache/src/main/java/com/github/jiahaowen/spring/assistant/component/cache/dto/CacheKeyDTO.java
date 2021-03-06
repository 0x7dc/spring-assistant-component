package com.github.jiahaowen.spring.assistant.component.cache.dto;

import java.io.Serializable;

/**
 * 缓存Key
 *
 * @author jiahaowen
 */
public final class CacheKeyDTO implements Serializable {

    private static final long serialVersionUID = 7229320497442357252L;

    private final String namespace;

    private final String key; // 缓存Key

    private final String hfield; // 设置哈希表中的字段，如果设置此项，则用哈希表进行存储

    public CacheKeyDTO(String namespace, String key, String hfield) {
        this.namespace = namespace;
        this.key = key;
        this.hfield = hfield;
    }

    public String getCacheKey() {
        if (null != this.namespace && this.namespace.length() > 0) {
            return new StringBuilder(this.namespace).append(":").append(this.key).toString();
        }
        return this.key;
    }

    public String getLockKey() {
        StringBuilder key = new StringBuilder(getCacheKey());
        if (null != hfield && hfield.length() > 0) {
            key.append(":").append(hfield);
        }
        key.append(":lock");
        return key.toString();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    public String getHfield() {
        return hfield;
    }
}
