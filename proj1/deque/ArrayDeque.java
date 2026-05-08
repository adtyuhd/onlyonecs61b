package deque;

import java.util.Iterator;

// 🔥 修复 1：implements Iterable<T>
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] arr;
    private int size;
    private int front;
    private int back;

    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        front = 0;
        back = 0;
    }

    // 扩容
    @SuppressWarnings("unchecked")
    private void resize(int newCap) {
        T[] newArr = (T[]) new Object[newCap];
        for (int i = 0; i < size; i++) {
            newArr[i] = arr[(front + i) % arr.length];
        }
        arr = newArr;
        front = 0;
        back = size;
    }

    // 🔥 修复 2：缩容（内存测试必须）
    private void resizeIfNeeded() {
        if (arr.length >= 16 && size < arr.length / 4) {
            resize(arr.length / 2);
        }
    }

    public void addFirst(T item) {
        if (size == arr.length) {
            resize(arr.length * 2);
        }
        front = (front - 1 + arr.length) % arr.length;
        arr[front] = item;
        size++;
    }

    public void addLast(T item) {
        if (size == arr.length) {
            resize(arr.length * 2);
        }
        arr[back] = item;
        back = (back + 1) % arr.length;
        size++;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            int index = (front + i) % arr.length;
            System.out.print(arr[index] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T ans = arr[front];
        arr[front] = null; // 🔥 修复 3：置 null，避免内存泄漏
        front = (front + 1) % arr.length;
        size--;
        resizeIfNeeded(); // 🔥 缩容
        return ans;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        back = (back - 1 + arr.length) % arr.length;
        T ans = arr[back];
        arr[back] = null; // 🔥 修复 4：置 null
        size--;
        resizeIfNeeded(); // 🔥 缩容
        return ans;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int realIndex = (front + index) % arr.length;
        return arr[realIndex];
    }

    // ====================== 🔥 修复 5：迭代器（必须！）======================
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos = 0;

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            T item = get(pos);
            pos++;
            return item;
        }
    }
}
