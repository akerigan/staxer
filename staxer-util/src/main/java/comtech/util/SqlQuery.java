package comtech.util;

import java.util.ArrayList;
import java.util.List;

public class SqlQuery {

    private static final int TYPE_SELECT = 1;
    private static final int TYPE_UPDATE = 2;

    private int type;
    // select related
    private String columns;
    private String from;
    private String groupBy;
    private String order;
    // update related
    private String tableName;
    private List<String> setExpressions;
    // unified
    private String where;
    private String suffix;

    private String sql;

    public static SqlQuery select() {
        return select(null, null);
    }

    public static SqlQuery select(String columns) {
        return select(columns, null);
    }

    public static SqlQuery select(String columns, String from) {
        SqlQuery result = new SqlQuery();
        result.type = TYPE_SELECT;
        result.columns = columns;
        result.from = from;
        return result;
    }

    public static SqlQuery update() {
        return update(null);
    }

    public static SqlQuery update(String tableName) {
        SqlQuery result = new SqlQuery();
        result.type = TYPE_UPDATE;
        result.tableName = tableName;
        return result;
    }

    public SqlQuery tableName(String expression) {
        if (type == TYPE_UPDATE && !StringUtils.isEmpty(expression)) {
            SqlQuery result = copy();
            tableName = expression;
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery set(String expression) {
        if (type == TYPE_UPDATE && !StringUtils.isEmpty(expression)) {
            SqlQuery result = copy();
            if (result.setExpressions == null) {
                result.setExpressions = new ArrayList<String>();
            }
            result.setExpressions.add(expression.trim());
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery where(String where) {
        if (!StringUtils.isEmpty(where)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.where)) {
                result.where = where.trim();
            } else {
                result.where = result.where + " " + where.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery suffix(String suffix) {
        if (!StringUtils.isEmpty(suffix)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.suffix)) {
                result.suffix = suffix.trim();
            } else {
                result.suffix = result.suffix + " " + suffix.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery order(String order) {
        if (type == TYPE_SELECT && !StringUtils.isEmpty(order)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.order)) {
                result.order = order.trim();
            } else {
                result.order = result.order + "," + order.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery groupBy(String groupBy) {
        if (type == TYPE_SELECT && !StringUtils.isEmpty(groupBy)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.groupBy)) {
                result.groupBy = groupBy.trim();
            } else {
                result.groupBy = result.groupBy + "," + groupBy.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery column(String column) {
        if (type == TYPE_SELECT && !StringUtils.isEmpty(column)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.columns)) {
                result.columns = column.trim();
            } else {
                result.columns = result.columns + "," + column.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    public SqlQuery from(String from) {
        if (type == TYPE_SELECT && !StringUtils.isEmpty(from)) {
            SqlQuery result = copy();
            if (StringUtils.isEmpty(result.from)) {
                result.from = from.trim();
            } else {
                result.from = result.from + "," + from.trim();
            }
            return result;
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        if (sql == null) {
            StringBuilder result = new StringBuilder();
            if (type == TYPE_SELECT) {
                result.append("select ");
                result.append(columns);
                result.append(" from ");
                result.append(from);
                if (where != null) {
                    result.append(" where ");
                    result.append(where);
                }
                if (groupBy != null) {
                    result.append(" group by ");
                    result.append(groupBy);
                }
                if (order != null) {
                    result.append(" order by ");
                    result.append(order);
                }
            } else if (type == TYPE_UPDATE) {
                result.append("update ");
                result.append(tableName);
                result.append(" set ");
                boolean notFirst = false;
                for (String setExpression : setExpressions) {
                    if (notFirst) {
                        result.append(", ");
                    } else {
                        notFirst = true;
                    }
                    result.append(setExpression);
                }
                if (where != null) {
                    result.append(" where ");
                    result.append(where);
                }
            }
            if (suffix != null) {
                result.append(" ");
                result.append(suffix);
            }
            sql = result.toString();
        }
        return sql;
    }

    private SqlQuery copy() {
        SqlQuery result = new SqlQuery();
        result.type = type;
        result.suffix = suffix;
        if (type == TYPE_SELECT) {
            result.columns = columns;
            result.from = from;
            result.where = where;
            result.order = order;
            result.groupBy = groupBy;
        } else if (type == TYPE_UPDATE) {
            result.tableName = tableName;
            if (setExpressions != null) {
                result.setExpressions = new ArrayList<String>(setExpressions);
            }
            result.where = where;
        }
        return result;
    }

}