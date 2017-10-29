package net.innectis.innplugin.util;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 *
 * @author Hret
 */
public final class Queue<T> {

    protected T[] items;
    protected int currentItem;
    protected int increment;

    public Queue(int increment) {
        currentItem = 0;
        this.increment = increment;
    }

    public void putAll(Collection collection) {
        Object[] arr = collection.toArray();
        if (items == null || arr == null || items[0].getClass() != arr[0].getClass()) {
            throw new ClassCastException("The collection is not of the same class as the queue!");
        }

        for (int i = 0; i < arr.length; i++) {
            resize(arr[i].getClass());
            items[currentItem++] = (T) arr[i];
        }
    }

    public T get(T item) {
        for (int i = 0; i < currentItem; i++) {
            if (items[i].equals(item)) {
                item = items[i];
                for (int j = i + 1; j <= currentItem; j++, i++) {
                    items[i] = items[j];
                }
                currentItem--;
                return item;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return currentItem == 0;
    }

    public T peek() {
        return items[currentItem - 1];
    }

    public T pop() {
        if (currentItem > 0) {
            resize(items[0].getClass());
            return items[--currentItem];
        }
        throw new ArrayIndexOutOfBoundsException("Queue is empty!");
    }

    public int size() {
        return currentItem;
    }

    public void put(T item) {
        resize(item.getClass());
        items[currentItem++] = item;
    }

    protected void resize(Class clazz) {
        if (items == null || currentItem == items.length || (currentItem == items.length - increment - 1)) {
            T[] olditems = items;

            if (items == null || currentItem == items.length) {
                items = (T[]) Array.newInstance(clazz, currentItem + increment);
            } else if (currentItem == items.length - increment - 1 && items.length - increment > 0) {
                items = (T[]) Array.newInstance(clazz, items.length - increment);
            }

            if (olditems != null) {
                System.arraycopy(olditems, 0, items, 0, currentItem);
            }
        }
    }
    
}
