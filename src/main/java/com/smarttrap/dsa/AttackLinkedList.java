package com.smarttrap.dsa;

import com.smarttrap.model.AttackLog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DSA Component 1: Custom Doubly Linked List for attack log management.
 * Demonstrates DSA with Java — O(1) insertions at head for live feed.
 */
@Component
public class AttackLinkedList implements Iterable<AttackLog> {

    private Node head;
    private Node tail;
    private int size;
    private static final int MAX_SIZE = 500;

    private static class Node {
        AttackLog data;
        Node next;
        Node prev;

        Node(AttackLog data) {
            this.data = data;
        }
    }

    public synchronized void addFirst(AttackLog log) {
        Node newNode = new Node(log);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
        if (size > MAX_SIZE) removeLast();
    }

    public synchronized void removeLast() {
        if (tail == null) return;
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
    }

    public synchronized List<AttackLog> getFirst(int k) {
        List<AttackLog> result = new ArrayList<>();
        Node cur = head;
        int count = 0;
        while (cur != null && count < k) {
            result.add(cur.data);
            cur = cur.next;
            count++;
        }
        return result;
    }

    public synchronized List<AttackLog> searchByIp(String ip) {
        List<AttackLog> result = new ArrayList<>();
        Node cur = head;
        while (cur != null) {
            if (cur.data.getSourceIp().equals(ip)) {
                result.add(cur.data);
            }
            cur = cur.next;
        }
        return result;
    }

    /** O(n) — Filter by attack type */
    public synchronized List<AttackLog> filterByType(AttackLog.AttackType type) {
        List<AttackLog> result = new ArrayList<>();
        Node cur = head;
        while (cur != null) {
            if (cur.data.getAttackType() == type) {
                result.add(cur.data);
            }
            cur = cur.next;
        }
        return result;
    }

    public int getSize() { return size; }

    public boolean isEmpty() { return size == 0; }

    @Override
    public Iterator<AttackLog> iterator() {
        return new Iterator<>() {
            Node cur = head;
            @Override public boolean hasNext() { return cur != null; }
            @Override public AttackLog next() {
                AttackLog d = cur.data;
                cur = cur.next;
                return d;
            }
        };
    }
}
