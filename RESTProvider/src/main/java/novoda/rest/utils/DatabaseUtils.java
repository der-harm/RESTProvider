
package novoda.rest.utils;

import novoda.rest.database.SQLTableCreator;
import novoda.rest.database.SQLiteType;

import android.content.ContentValues;
import android.net.Uri;

import java.util.Map.Entry;

public class DatabaseUtils extends android.database.DatabaseUtils {

    public static String contentValuestoTableCreate(ContentValues values, String table) {
        StringBuffer buf = new StringBuffer("CREATE TABLE ").append(table).append(" (");
        for (Entry<String, Object> entry : values.valueSet()) {
            buf.append(entry.getKey()).append(" TEXT").append(", ");
        }
        buf.delete(buf.length() - 2, buf.length());
        buf.append(");");
        return buf.toString();
    }

    public static String getCreateStatement(SQLTableCreator creator) {

        String primaryKey = creator.getPrimaryKey();
        SQLiteType primaryKeyType = creator.getType(primaryKey);
        boolean shouldAutoincrement = creator.shouldPKAutoIncrement();

        if (primaryKey == null) {
            primaryKey = "_id";
            primaryKeyType = SQLiteType.INTEGER;
            shouldAutoincrement = true;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(creator.getTableName()).append(" (").append(
                primaryKey).append(" ").append(primaryKeyType.name()).append(" PRIMARY KEY")
                .append(((shouldAutoincrement) ? " AUTOINCREMENT " : " "));

        for (String f : creator.getTableFields()) {
            if (f.equals(primaryKey)) {
                continue;
            }
            sql.append(", ").append(f).append(" ").append(creator.getType(f).name());
            sql.append(creator.isNullAllowed(f) ? "" : " NOT NULL");
            
            sql.append(creator.isUnique(f) ? " UNIQUE" : "");
            sql.append((creator.onConflict(f) != null)? " ON CONFLICT " + creator.onConflict(f) : "");
        }

        sql.append(");");
        return sql.toString();
    }
}