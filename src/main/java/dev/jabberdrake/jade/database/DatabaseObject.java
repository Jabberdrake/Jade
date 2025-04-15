package dev.jabberdrake.jade.database;

import java.util.List;

public interface DatabaseObject<T, ID> {

    T fetch(ID id);

    List<T> fetchAll();

    void save(T t);

    ID create(T t);

    void delete(ID id);
}
