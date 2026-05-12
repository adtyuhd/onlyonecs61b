package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // 元素总个数
    private int size;
    // 最大负载因子
    private double loadFactor;
    // 存所有 key，给 keySet 和 迭代器用
    private Set<K> keySet;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor=maxLoad;
        buckets=createTable(initialSize);
        size=0;
        keySet=new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new java.util.LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        // 把每一个桶都初始化
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    /** Removes all of the mappings from this map. */
   public  void clear(){
       for(int i=0;i< buckets.length;i++){
           buckets[i].clear();
       }
       size=0;
       keySet.clear();
    }
    /** Returns true if this map contains a mapping for the specified key. */
   public  boolean containsKey(K key){
       int h=key.hashCode();
       h=Math.abs(h);
       int n=h% buckets.length;
       for(Node node:buckets[n]){
           if(node.key.equals(key)){
               return  true;
           }
       }
       return  false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
   public  V get(K key){
       int h=key.hashCode();
       h=Math.abs(h);
       int n=h% buckets.length;
       for(Node node:buckets[n]){
           if(node.key.equals(key)){
               return  node.value;
           }
       }
       return  null;
   }

    /** Returns the number of key-value mappings in this map. */
   public int size(){
       return size;
   }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value){
        int h=key.hashCode();
        h=Math.abs(h);
        int n=h% buckets.length;
        for(Node node:buckets[n]){
            if(node.key.equals(key)){
                node.value=value;
                return;
            }
        }
        buckets[n].add(createNode(key,value));
        size++;
        keySet.add(key);
        double load=(double) size/ buckets.length;
        if(load>loadFactor){
            Collection<Node>[] newone=createTable(buckets.length*2);
            for(int i=0;i< buckets.length;i++){
                for(Node node:buckets[i]){
                    int hh=node.key.hashCode();
                    hh=Math.abs(hh);
                    int m=hh%newone.length;
                    newone[m].add(node);
                }
            }
            buckets=newone;
        }
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        return keySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key){
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
   public V remove(K key, V value){
       throw new UnsupportedOperationException();
   }
}
