package dao;

import java.util.List;

public interface CommonDAO<T> {
    void add(T object);
    List<T> selectAll();
    T getById(int id);
    void update(T object);
    void delete(int id);
}