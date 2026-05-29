package org.blog.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    List<T> getList();
    Optional<T> getById(long id);
    long create(T entity);
    long update(long id, T entity);
    void delete(long id);
}
