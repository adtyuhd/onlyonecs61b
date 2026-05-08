package deque;
public interface Deque<T>{
    // 向队列头部添加元素
    public void addFirst(T item);

    // 向队列尾部添加元素
    public void addLast(T item);

    // 判断队列是否为空
    default public boolean isEmpty(){
        return size()==0;
    }

    // 返回队列元素个数
    public int size();

    // 从头到尾打印所有元素，空格分隔，末尾换行
    public void printDeque();

    // 删除并返回队首元素，空队列返回 null
    public T removeFirst();

    // 删除并返回队尾元素，空队列返回 null
    public T removeLast();

    // 获取指定下标元素（0 为队首），下标非法返回 null，不修改队列
    public T get(int index);

}