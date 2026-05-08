package deque;
import java.util.Iterator;
 public class  LinkedListDeque<T> implements Deque<T>, Iterable<T>{
     private class Node {
         T item;
         Node prev;
         Node next;
         Node(T i, Node p, Node n) {
             item = i;
             prev = p;
             next = n;
         }
     }
     private Node sentinel;  // 哨兵节点
     private int size;      // 记录大小
     // 构造空链表双端队列
     public LinkedListDeque(){
         sentinel = new Node(null, null, null);
         sentinel.prev = sentinel;
         sentinel.next = sentinel;
         size = 0;
     }
     // 递归版 get 方法
     public T getRecursive(int index) {
         // 越界直接返回null
         if (index < 0 || index >= size) {
             return null;
         }
         // 从第一个元素开始递归
         return getRecursiveHelper(sentinel.next, index);
     }

     // 递归辅助方法（私有）
     private T getRecursiveHelper(Node current, int index) {
         // 递归终止条件：找到目标位置
         if (index == 0) {
             return current.item;
         }
         // 递归：往后走一步，index-1
         return getRecursiveHelper(current.next, index - 1);
     }
     // 向队列头部添加元素
     public void addFirst(T item){
         Node newNode = new Node(item, sentinel, sentinel.next);
         sentinel.next.prev = newNode;
         sentinel.next = newNode;
         size++;
     }

     // 向队列尾部添加元素
     public void addLast(T item){
         Node newNode = new Node(item, sentinel.prev, sentinel);
         sentinel.prev.next = newNode;
         sentinel.prev= newNode;
         size++;
     }
     // 返回队列元素个数
     public int size(){
         return size;
     }

     // 从头到尾打印所有元素，空格分隔，末尾换行
     public void printDeque(){
         Node current = sentinel.next;  // 从第一个元素开始

         // 遍历到哨兵就停止
         while (current != sentinel) {
             System.out.print(current.item);

             // 只要不是最后一个元素，就打印空格
             if (current.next != sentinel) {
                 System.out.print(" ");
             }

             current = current.next;
         }

         // 最后必须换行（项目要求）
         System.out.println();
     }

     // 删除并返回队首元素，空队列返回 null
     public T removeFirst(){
         if(size==0){
             return null;
         }

         else{
             Node p=sentinel.next;
              T ans=p.item;
             p.next.prev=sentinel;
             sentinel.next=p.next;
             size--;
             return ans;
         }

     }

     // 删除并返回队尾元素，空队列返回 null
     public T removeLast(){
         if(size==0){
             return null;
         }

         else{
             Node p=sentinel.prev;
             T ans=p.item;
             p.prev.next=sentinel;
             sentinel.prev=p.prev;
             size--;
             return ans;
         }
     }

     // 获取指定下标元素（0 为队首），下标非法返回 null，不修改队列
     public T get(int index){
         if(index<0||index>=size){
             return null;
         }
         Node p=sentinel.next;
         while(index!=0){
             p=p.next;
             index--;
         }
         return p.item;
     }
     // 迭代器：让双端队列支持 for-each 增强for循环遍历
     public Iterator<T> iterator() {
         return new LLIterator();
     }

     private class LLIterator implements Iterator<T> {
         private Node current = sentinel.next;

         public boolean hasNext() {
             return current != sentinel;
         }

         public T next() {
             T item = current.item;
             current = current.next;
             return item;
         }
     }

     // ====================== 已补全：equals ✅ ======================
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Deque)) return false;

         Deque<?> other = (Deque<?>) o;
         if (this.size() != other.size()) return false;

         Node p = sentinel.next;
         int i = 0;

         while (p != sentinel) {
             if (!p.item.equals(other.get(i))) {
                 return false;
             }
             p = p.next;
             i++;
         }
         return true;
     }
 }
