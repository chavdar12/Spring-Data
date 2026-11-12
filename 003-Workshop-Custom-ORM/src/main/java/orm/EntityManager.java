package orm;

import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.Id;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Minimal reflection-based ORM. The WHERE strings of find/findFirst are raw SQL, as in the workshop spec:
 * never build them from user input.
 */
public class EntityManager<E> implements DbContext<E> {

    private final Connection connection;

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean persist(E entity) throws SQLException, IllegalAccessException {
        Field idField = idField(entity.getClass());
        idField.setAccessible(true);
        Object id = idField.get(entity);

        return id == null || ((Number) id).longValue() <= 0 ? insert(entity, idField) : update(entity, idField, id);
    }

    private boolean insert(E entity, Field idField) throws SQLException, IllegalAccessException {
        List<Field> columns = columnFields(entity.getClass());
        String sql = "INSERT INTO %s (%s) VALUES (%s)".formatted(tableName(entity.getClass()),
                columns.stream().map(EntityManager::columnName).collect(Collectors.joining(", ")),
                columns.stream().map(f -> "?").collect(Collectors.joining(", ")));

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, entity, columns);
            int affected = statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    setValue(idField, entity, keys.getLong(1));
                }
            }
            return affected == 1;
        }
    }

    private boolean update(E entity, Field idField, Object id) throws SQLException, IllegalAccessException {
        List<Field> columns = columnFields(entity.getClass());
        String sql = "UPDATE %s SET %s WHERE %s = ?".formatted(tableName(entity.getClass()),
                columns.stream().map(f -> columnName(f) + " = ?").collect(Collectors.joining(", ")),
                columnName(idField));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, entity, columns);
            statement.setObject(columns.size() + 1, id);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public Iterable<E> find(Class<E> table) throws SQLException, ReflectiveOperationException {
        return find(table, null);
    }

    @Override
    public Iterable<E> find(Class<E> table, String where) throws SQLException, ReflectiveOperationException {
        return select(table, where, null);
    }

    @Override
    public E findFirst(Class<E> table) throws SQLException, ReflectiveOperationException {
        return findFirst(table, null);
    }

    @Override
    public E findFirst(Class<E> table, String where) throws SQLException, ReflectiveOperationException {
        List<E> result = select(table, where, 1);
        return result.isEmpty() ? null : result.getFirst();
    }

    private List<E> select(Class<E> table, String where, Integer limit) throws SQLException, ReflectiveOperationException {
        String sql = "SELECT * FROM " + tableName(table)
                     + (where == null ? "" : " WHERE " + where)
                     + (limit == null ? "" : " LIMIT " + limit);

        List<E> result = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                E entity = table.getDeclaredConstructor().newInstance();
                for (Field field : allFields(table)) {
                    setValue(field, entity, rs.getObject(columnName(field)));
                }
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public void doCreate(Class<E> entityClass) throws SQLException {
        Field idField = idField(entityClass);
        String columns = columnFields(entityClass).stream()
                .map(f -> columnName(f) + " " + sqlType(f.getType()))
                .collect(Collectors.joining(", "));
        String sql = "CREATE TABLE %s (%s BIGINT PRIMARY KEY AUTO_INCREMENT%s)".formatted(
                tableName(entityClass), columnName(idField), columns.isEmpty() ? "" : ", " + columns);

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Override
    public void doAlter(Class<E> entityClass) throws SQLException {
        String table = tableName(entityClass);
        Set<String> existing = new HashSet<>();
        try (ResultSet rs = connection.getMetaData().getColumns(connection.getCatalog(), null, table, null)) {
            while (rs.next()) {
                existing.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        List<String> newColumns = columnFields(entityClass).stream()
                .filter(f -> !existing.contains(columnName(f).toLowerCase()))
                .map(f -> "ADD COLUMN " + columnName(f) + " " + sqlType(f.getType()))
                .toList();
        if (newColumns.isEmpty()) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE " + table + " " + String.join(", ", newColumns));
        }
    }

    @Override
    public boolean delete(E entity) throws SQLException, IllegalAccessException {
        Field idField = idField(entity.getClass());
        idField.setAccessible(true);
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM %s WHERE %s = ?".formatted(tableName(entity.getClass()), columnName(idField)))) {
            statement.setObject(1, idField.get(entity));
            return statement.executeUpdate() == 1;
        }
    }

    private void bind(PreparedStatement statement, E entity, List<Field> columns) throws SQLException, IllegalAccessException {
        for (int i = 0; i < columns.size(); i++) {
            Field field = columns.get(i);
            field.setAccessible(true);
            statement.setObject(i + 1, field.get(entity));
        }
    }

    private static void setValue(Field field, Object entity, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (value == null) {
            if (!type.isPrimitive()) {
                field.set(entity, null);
            }
        } else if (type == int.class || type == Integer.class) {
            field.set(entity, ((Number) value).intValue());
        } else if (type == long.class || type == Long.class) {
            field.set(entity, ((Number) value).longValue());
        } else if (type == double.class || type == Double.class) {
            field.set(entity, ((Number) value).doubleValue());
        } else if (type == LocalDate.class) {
            field.set(entity, value instanceof java.sql.Date date ? date.toLocalDate() : LocalDate.parse(value.toString()));
        } else {
            field.set(entity, value.toString());
        }
    }

    private static String sqlType(Class<?> type) {
        if (type == int.class || type == Integer.class) return "INT";
        if (type == long.class || type == Long.class) return "BIGINT";
        if (type == double.class || type == Double.class) return "DOUBLE";
        if (type == LocalDate.class) return "DATE";
        if (type == String.class) return "VARCHAR(255)";
        throw new UnsupportedOperationException("Unsupported field type: " + type.getName());
    }

    private static String tableName(Class<?> type) {
        Entity entity = type.getAnnotation(Entity.class);
        if (entity == null) {
            throw new UnsupportedOperationException(type.getName() + " is not annotated with @Entity");
        }
        return entity.name();
    }

    private static Field idField(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(type.getName() + " has no @Id field"));
    }

    private static List<Field> columnFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Column.class) && !f.isAnnotationPresent(Id.class))
                .toList();
    }

    private static List<Field> allFields(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Column.class)).toList();
    }

    private static String columnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column == null ? field.getName() : column.name();
    }
}
