package com.app.ll;

import android.content.Context;

import androidx.annotation.NonNull;

import com.app.ll.util.IterableNodeList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class ChoiceManager implements Iterable<ChoiceManager.Choice>, Randomizer<ChoiceManager.Choice> {
    private final Context context;
    private final List<Choice> mChoices = new ArrayList<>();

    public ChoiceManager(Context ctx, int resID) {
        context = ctx;
        loadChoices(resID);
    }

    private void loadChoices(int resID) {
        try (InputStream inputStream = context.getResources().openRawResource(resID)) {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            NodeList nodes = document.getElementsByTagName("category");
            for (Node node: new IterableNodeList(nodes)) {
                if(node instanceof Element) {
                    Element element = (Element)node;
                    mChoices.add(Choice.fromElement(element));
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public Choice getChoiceThanCanContain(String text) {
        for(Choice choice: this) {
            if(choice.canContain(text))
                return choice;
        }
        throw new IllegalArgumentException("Unrecognizable text");
    }

    @Override
    public void forEach(@NonNull Consumer<? super Choice> action) {
        mChoices.forEach(action);
    }

    @NonNull
    @Override
    public Iterator<Choice> iterator() {
        return mChoices.iterator();
    }

    @NonNull
    @Override
    public Spliterator<Choice> spliterator() {
        return mChoices.spliterator();
    }

    @Override
    public Choice getRandom() {
        int randIndex = (int)(Math.random()*mChoices.size());
        return mChoices.get(randIndex);
    }

    public static final class Choice implements Randomizer<String> {
        public static Choice fromElement(Element element) {
            String name = element.getAttribute("name");
            String id = element.getAttribute("id");
            String[] compatibles = element.getTextContent().split("/");
            return new Choice(name, id, compatibles);
        }

        public final String NAME, ID;
        public final String[] COMPATIBLES;

        public Choice(String name, String id, String[] compatibles) {
            NAME = name;
            ID = id;
            COMPATIBLES = compatibles;
        }

        public boolean canContain(String text) {
            return List.of(COMPATIBLES).contains(text);
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("Choice %s(%s){%s}", NAME, ID, List.of(COMPATIBLES));
        }

        @Override
        public String getRandom() {
            int randIndex = (int)(Math.random()*COMPATIBLES.length);
            return COMPATIBLES[randIndex];
        }
    }
}
