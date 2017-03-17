package net.innectis.innplugin.util;

import java.lang.reflect.Field;

/**
 * Contains stuff used for SkylandsPlus (not my methods, but Jacek's)
 *
 * @author AlphaBlend
 */
public class SkylandsPlusUtil {

    /**
     * Gets the value of a field from an object.
     *
     * @param src        The class where the field is defined.
     * @param name        The name of the field.
     * @param type        The type of the field.
     * @param from        The object to get the field value from.
     *
     * @return        The value of the field.
     * @throws NoSuchFieldException                If the field could not be found
     * @throws SecurityException                If the field could not be made accessible
     */
    public static <T> T getFieldValue(Class<?> src, String name, Class<T> type, Object from) throws SecurityException, NoSuchFieldException {
            Field field = src.getDeclaredField(name);
            field.setAccessible(true);

            try{
                return type.cast(field.get(from));
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
    }
    
}
