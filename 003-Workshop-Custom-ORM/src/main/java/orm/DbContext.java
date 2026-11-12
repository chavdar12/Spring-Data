package orm;

import java.sql.SQLException;

public interface DbContext<E> {

    boolean persist(E entity) throws SQLException, IllegalAccessException;

    Iterable<E> find(Class<E> table) throws SQLException, ReflectiveOperationException;

    Iterable<E> find(Class<E> table, String where) throws SQLException, ReflectiveOperationException;

    E findFirst(Class<E> table) throws SQLException, ReflectiveOperationException;

    E findFirst(Class<E> table, String where) throws SQLException, ReflectiveOperationException;

    void doCreate(Class<E> entityClass) throws SQLException;

    void doAlter(Class<E> entityClass) throws SQLException;

    boolean delete(E entity) throws SQLException, IllegalAccessException;
}
