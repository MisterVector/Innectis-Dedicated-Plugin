package net.innectis.innplugin.util;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Hret
 *
 * Containts database tools to convert or do stuff with databases.
 */
public final class DatabaseTools {

    private DatabaseTools() {
    }

    /**
     * Converts a java util date to an SQL timestamp
     * @param date
     * @return
     */
    public static Timestamp dateToTimeStamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * This will parse the resultset to object of the given class type.<br/>
     * @param clazz The class the object it should be cast to
     * @param resultset with the colums needed for the entity. <br/>
     * <b>Remark!</b><br/>
     * The case of the <u>columns</u> and <u>fields</u> should be the same.
     * The parser is case-sensitive. Meaning <b>chestId</b> will not parse into
     * <b>chestid</b>. You can use the '<b>AS</b>' keyword to set the names right.
     * @return List of the given entities.
     * @throws SQLException
     * This is thrown when something happens from the SQL handler.
     * @throws ObjectParseException
     * When this class for any reason does not have access to the given object it
     * might throw a ObjectParseException. The innerexception will have more details.
     */
    public static <T> T parseToObject(Class<T> clazz, ResultSet result) throws SQLException, ObjectParseException {
        // Get the metadata and class fields
        ResultSetMetaData sqlData = result.getMetaData();
        int columnCount = sqlData.getColumnCount();

        try {
            // Loop through results
            if (result.next()) {
                // Make new object
                T object = clazz.newInstance();

                String fieldName;
                Field tmpField;

                // Loop through columns
                for (int i = 0; i < columnCount; i++) {
                    fieldName = sqlData.getColumnLabel(i + 1);
                    try {
                        tmpField = clazz.getDeclaredField(fieldName);
                        if (tmpField == null) {
                            continue;
                        }
                        // Set accessible
                        tmpField.setAccessible(true);

                        setValue(result, object, tmpField, sqlData.getColumnType(i + 1), i + 1);
                    } catch (NoSuchFieldException ex) {
                    } catch (SecurityException ex) {
                        ex.printStackTrace();
                    }
                }

                return object;
            }

            return null;
        } catch (InstantiationException ex) {
            throw new ObjectParseException("Can't create an instance of that object! See inner exception!", ex);
        } catch (IllegalAccessException ex) {
            throw new ObjectParseException("No access to the class! See inner exception!", ex);
        }
    }

    /**
     * This will parse the resultset to objects of the given class type.<br/>
     * @param clazz The class the objects should be cast to
     * @param resultset with the colums needed for the entity. <br/>
     * <b>Remark!</b><br/>
     * The case of the <u>columns</u> and <u>fields</u> should be the same.
     * The parser is case-sensitive. Meaning <b>chestId</b> will not parse into
     * <b>chestid</b>. You can use the '<b>AS</b>' keyword to set the names right.
     * @return List of the given entities.
     * @throws SQLException
     * This is thrown when something happens from the SQL handler.
     * @throws ObjectParseException
     * When this class for any reason does not have access to the given object it
     * might throw a ObjectParseException. The innerexception will have more details.
     */
    public static <T> List<T> parseToObjects(Class<T> clazz, ResultSet result) throws SQLException, ObjectParseException {
        // Get the metadata and class fields
        ResultSetMetaData data = result.getMetaData();
        int columnCount = data.getColumnCount();

        // Make arrays to keep references
        Field[] columFields = new Field[columnCount];
        Integer[] columTypes = new Integer[columnCount];

        // Get the fields and types
        getFields(clazz, data, columFields, columTypes);

        // Make the returnlist
        List<T> entityList = new ArrayList<T>();
        T obj = null;
        Field tmpField;
        try {
            // Loop through results
            while (result.next()) {
                // Make new object
                obj = clazz.newInstance();

                // Loop through columns
                for (int i = 0; i < columnCount; i++) {
                    tmpField = columFields[i];

                    // Check if the column is present in the entity class
                    if (columFields[i] != null) {
                        setValue(result, obj, tmpField, columTypes[i], i + 1);
                    }
                }

                // Add it to the list
                entityList.add(obj);
            }
        } catch (InstantiationException ex) {
            throw new ObjectParseException("Can't create an instance of that object! See inner exception!", ex);
        } catch (IllegalAccessException ex) {
            throw new ObjectParseException("No access to the class! See inner exception!", ex);
        }

        return entityList;
    }

    /**
     * Returnrs lists of the fields that are in the class.
     * @param clazz - The class with the objects
     * @param sqlData - Data of the resultset
     * @param columnFields - Array will be filled with the field objects (or null if field not present)
     * <b>This uses the same index as columnTypes</b>
     * @param columnTypes - The SQL types of columns that are connected to the field.
     * <b>This uses the same index as columnFields</b>
     * @throws SQLException
     * When this class for any reason does not have access to the given object it
     * @throws SecurityException
     */
    private static <T> void getFields(Class<T> clazz, ResultSetMetaData sqlData, Field[] columnFields, Integer[] columnTypes) throws SQLException, SecurityException {
        // Make temp vars
        String fieldName;
        Field tmpField;
        for (int i = 0; i < sqlData.getColumnCount(); i++) {
            fieldName = sqlData.getColumnLabel(i + 1);
            try {
                tmpField = clazz.getDeclaredField(fieldName);
                if (tmpField == null) {
                    continue;
                }
                // Set accessible
                tmpField.setAccessible(true);
                // Set in array
                columnTypes[i] = sqlData.getColumnType(i + 1);
                columnFields[i] = tmpField;
            } catch (NoSuchFieldException ex) {
            } catch (SecurityException ex) {
            }
        }
        // Clear objects
        fieldName = null;
        tmpField = null;
    }

    /**
     * Sets the value of the given field of the given object to the value in the given resultset.
     * It will handle special SQL types to make sure it fits.
     * <b> This will auto convert timestamps to java.util.Date </b>
     * @param sourceSet - The resultset with the values
     * @param destinationObject - The object where the field should be set.
     * @param field - The field that needs to be set with the value.
     * @param columType - The Intvalue of the sqltype. See <b>java.sql.Types</b>.
     * @param columnNumber - The column numer in the resultset.
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @see java.sql.Types
     */
    private static <T> void setValue(ResultSet sourceSet, T destinationObject, Field field, Integer columType, int columnNumber) throws SQLException, IllegalAccessException, IllegalArgumentException {
        // Handle some types different then others.
        switch (columType) {
            case Types.BIGINT: // Long
                if (field.getType() == long.class) {
                    field.setLong(destinationObject, sourceSet.getLong(columnNumber));
        } else {
                    field.set(destinationObject, sourceSet.getLong(columnNumber));
        }
                break;
            case Types.BIT:
            case Types.BOOLEAN:
                if (field.getType() == boolean.class) {
                    field.setBoolean(destinationObject, sourceSet.getBoolean(columnNumber));
        } else {
                    field.set(destinationObject, sourceSet.getBoolean(columnNumber));
        }
                break;
            case Types.CHAR:
                if (field.getType().isAssignableFrom(Character.class)) {
                    char[] chararr = sourceSet.getString(columnNumber).toCharArray();
                    field.setChar(destinationObject, (chararr.length > 0 ? chararr[0] : ' '));
                } else {
                    field.set(destinationObject, sourceSet.getString(columnNumber));
        }
                break;
            case Types.DOUBLE:
                if (field.getType() == double.class) {
                    field.setDouble(destinationObject, sourceSet.getDouble(columnNumber));
        } else {
                    field.set(destinationObject, sourceSet.getLong(columnNumber));
        }
                break;
            case Types.TIMESTAMP:
                // Cast to superclass!
                field.set(destinationObject, (Date) sourceSet.getTimestamp(columnNumber));
                break;
            case Types.INTEGER:
                if (field.getType() == int.class) {
                    field.setInt(destinationObject, sourceSet.getInt(columnNumber));
        } else {
                    field.set(destinationObject, sourceSet.getInt(columnNumber));
        }
                break;
            default:
                try {
                    field.set(destinationObject, sourceSet.getString(columnNumber));
                } catch (IllegalArgumentException ex) {
                }
        }
    }
    
}
