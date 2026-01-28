package com.app.ll.util;

import androidx.annotation.NonNull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IterableNodeList implements Iterable<Node> {
    private final NodeList mNodeList;

    public IterableNodeList(NodeList mNodeList) {
        this.mNodeList = mNodeList;
    }

    @NonNull
    @Override
    public Iterator<Node> iterator() {
        return new Iter(mNodeList);
    }

    @Override
    public void forEach(@NonNull Consumer<? super Node> action) {
        for (int i = 0; i < mNodeList.getLength(); i++) {
            Node node = mNodeList.item(i);
            action.accept(node);
        }
    }

    @NonNull
    @Override
    public Spliterator<Node> spliterator() {
        throw new UnsupportedOperationException();
    }

    private static final class Iter implements Iterator<Node> {
        private Node[] mNodes;
        private int mIndex = -1;
        private boolean mNextCalled = false;

        public Iter(NodeList nodes) {
            mNodes = new Node[nodes.getLength()];
            for (int i = 0; i < nodes.getLength(); i++) {
                mNodes[i] = nodes.item(i);
            }
        }

        @Override
        public boolean hasNext() {
            return mIndex+1 < mNodes.length;
        }

        @Override
        public Node next() {
            if (!hasNext())
                throw new NoSuchElementException();
            mNextCalled = true;
            return mNodes[++mIndex];
        }

        @Override
        public void remove() {
            if (!mNextCalled)
                throw new IllegalStateException();
            mNextCalled = false;

            Node[] newNodes = new Node[mNodes.length - 1];
            System.arraycopy(mNodes, 0, newNodes, 0, mIndex);
            System.arraycopy(mNodes, mIndex+1, newNodes, mIndex, mNodes.length-mIndex-1);

            mNodes = newNodes;
            mIndex--;
        }
    }
}
