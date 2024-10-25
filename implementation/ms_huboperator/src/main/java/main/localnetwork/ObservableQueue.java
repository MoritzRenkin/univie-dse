package main.localnetwork;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObservableQueue<T> implements Queue<T> {
    private final ConcurrentLinkedQueue<T> queue;
    private final PropertyChangeSupport listeners;
    private final Logger logger = LoggerFactory.getLogger(ObservableQueue.class);

    public ObservableQueue() {
        queue = new ConcurrentLinkedQueue<>();
        listeners = new PropertyChangeSupport(this);
    }

    public void addListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return queue.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        var value = this.queue.add(t);
        listeners.firePropertyChange(new PropertyChangeEvent(this, "Element added", null, t));
        return value;
    }

    @Override
    public boolean offer(T t) {
        return this.queue.offer(t);
    }

    @Override
    public T remove() {
        return this.queue.remove();
    }

    @Override
    public T poll() {
        return this.queue.poll();
    }

    @Override
    public T element() {
        return this.queue.element();
    }

    @Override
    public T peek() {
        return this.queue.peek();
    }

    @Override
    public boolean remove(Object o) {
        return this.queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return queue.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return queue.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return queue.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return queue.retainAll(collection);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObservableQueue<?> that = (ObservableQueue<?>) o;

        if (!Objects.equals(queue, that.queue)) return false;
        return Objects.equals(listeners, that.listeners);
    }

    @Override
    public int hashCode() {
        int result = queue.hashCode();
        result = 31 * result + (listeners != null ? listeners.hashCode() : 0);
        return result;
    }
}
