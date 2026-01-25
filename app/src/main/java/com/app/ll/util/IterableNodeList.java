package com.app.ll.util;

import androidx.annotation.NonNull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IterableNodeList implements Iterable<Node> {
    private final NodeList mNodeList;

    public IterableNodeList(NodeList mNodeList) {
        this.mNodeList = mNodeList;
    }

    // TODO make own iterator
    @NonNull
    @Override
    public Iterator<Node> iterator() {
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < mNodeList.getLength(); i++) {
            Node node = mNodeList.item(i);
            nodeList.add(node);
        }
        return nodeList.iterator();
    }

    @Override
    public void forEach(@NonNull Consumer<? super Node> action) {
        for (int i = 0; i < mNodeList.getLength(); i++) {
            Node node = mNodeList.item(i);
            action.accept(node);
        }
    }

    // TODO make own spliterator
    @NonNull
    @Override
    public Spliterator<Node> spliterator() {
        List<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < mNodeList.getLength(); i++) {
            Node node = mNodeList.item(i);
            nodeList.add(node);
        }
        return nodeList.spliterator();
    }
}
