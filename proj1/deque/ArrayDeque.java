package deque;
public class ArrayDeque<T> implements Deque<T>{
    private T[] arr;
    private int size;
    private int front;   // 队首下标
    private int back;
    public ArrayDeque() {
        arr = (T[]) new Object[8];
        size = 0;
        front = 0;
        back = 0;
    }
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
    // 向队列头部添加元素
    public void addFirst(T item){
        if (size == arr.length) {
            resize(arr.length * 2);
        }

        // front 往前挪一格，取模循环
        front = (front - 1 + arr.length) % arr.length;
        arr[front] = item;
        size++;
    }

    // 向队列尾部添加元素
    public void addLast(T item){
        if (size == arr.length) {
            resize(arr.length * 2);
        }
        arr[back] = item;
        back = (back + 1) % arr.length;
        size++;
    }

    // 返回队列元素个数
    public int size(){
        return size;
    }

    // 从头到尾打印所有元素，空格分隔，末尾换行
    public void printDeque(){
        for (int i = 0; i < size; i++) {
            int index = (front + i) % arr.length;
            System.out.print(arr[index] + " ");
        }
        System.out.println();
    }

    // 删除并返回队首元素，空队列返回 null
    public T removeFirst(){
        if(size==0){
            return null;
        }
        T ans=arr[front];
        front=(front+1)%arr.length;
        size--;
        return ans;
    }

    // 删除并返回队尾元素，空队列返回 null
    public T removeLast(){
        if(size==0){
            return  null;
        }
        back = (back - 1 + arr.length) % arr.length;
        T ans = arr[back];
        size--;
        return ans;
    }

    // 获取指定下标元素（0 为队首），下标非法返回 null，不修改队列
    public T get(int index){
        if(index<0||index>=size){
            return null;
        }
        int realIndex = (front + index) % arr.length;
        return arr[realIndex];
    }
}

