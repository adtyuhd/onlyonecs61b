package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    // 1. 内部结点类
    private class BSTNode {
        K key;
        V val;
        BSTNode left, right;

        BSTNode(K key, V val) {
            this.key = key;
            this.val = val;
            left = right = null;
        }
    }

    // 根节点
    private BSTNode root;
    private int size;

    // 构造函数
    public BSTMap() {
        root = null;
        size = 0;
    }

    // ---------------- 必须实现的方法 ----------------
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        BSTNode node = find(root, key);
        return node == null ? null : node.val;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    // ---------------- 辅助函数 ----------------
    private BSTNode find(BSTNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) return find(node.left, key);
        if (cmp > 0) return find(node.right, key);
        return node;
    }

    private BSTNode insert(BSTNode node, K key, V val) {
        if (node == null) {
            size++;
            return new BSTNode(key, val);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = insert(node.left, key, val);
        else if (cmp > 0) node.right = insert(node.right, key, val);
        else node.val = val;
        return node;
    }

    // ---------------- 额外要求：printInOrder() ----------------
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.println(node.key);
        printInOrder(node.right);
    }

    // ---------------- 不用实现，直接抛异常 ----------------
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
