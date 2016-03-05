package net.avicus.quest.model;

import net.avicus.quest.database.DatabaseException;
import net.avicus.quest.query.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModelSelect<M extends Model> implements Filterable {
    private final Table<M> table;
    private final Select select;

    public ModelSelect(Table<M> table) {
        this.table = table;
        this.select = new Select(table.getDatabase(), table.getName());
        this.select.columns(this.table.getColumns());
    }

    public ModelSelect<M> order(String field) {
        this.select.order(field);
        return this;
    }

    public ModelSelect<M> order(String field, String direction) {
        this.select.order(field, direction);
        return this;
    }

    @Override
    public ModelSelect<M> where(String field, Object value) {
        return this.where(field, value, Operator.EQUALS);
    }

    @Override
    public ModelSelect<M> where(String field, Object value, Operator operator) {
        return this.where(new Filter(field, value, operator));
    }

    @Override
    public ModelSelect<M> where(Filter filter) {
        this.select.where(filter);
        return this;
    }

    public ModelSet<M> execute() throws DatabaseException {
        PreparedStatement statement = this.table.getDatabase().createQueryStatement(this.select.build(), false);
        try {
            return new ModelSet<>(this.table, statement.executeQuery());
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public ModelByModelSet<M> executeByModel() throws DatabaseException {
        PreparedStatement statement = this.table.getDatabase().createQueryStatement(this.select.build(), true);
        try {
            return new ModelByModelSet<>(this.table, statement.executeQuery());
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
}
