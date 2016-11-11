package xyz.truenight.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

public class CacheReference<T> extends SoftReference<T> {
    public CacheReference(T referent) {
        super(referent);
    }

    public CacheReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public int hashCode() {
        return Utils.hashCode(get());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CacheReference && Utils.equal(get(), ((CacheReference) obj).get());
    }
}