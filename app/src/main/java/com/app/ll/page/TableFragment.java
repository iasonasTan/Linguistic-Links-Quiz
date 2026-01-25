package com.app.ll.page;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.app.ll.MainActivity;
import com.app.ll.R;
import com.app.ll.util.IterableNodeList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TableFragment extends AbstractPage {
    public static final String NAME = "ll.page.table";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.table_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLinkViews(view.findViewById(R.id.container));
        ImageButton tableButton = view.findViewById(R.id.home_button);
        tableButton.setOnClickListener(v -> {
            Intent showTalbeIntent = new Intent(MainActivity.ACTION_CHANGE_PAGE);
            showTalbeIntent.putExtra(MainActivity.PAGE_NAME_EXTRA, QuizFragment.NAME);
            showTalbeIntent.setPackage(requireContext().getPackageName());
            requireContext().sendBroadcast(showTalbeIntent);
        });
    }

    private ViewGroup.LayoutParams getParams() {
        var out = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        out.setMargins(5, 10, 5, 10);
        return out;
    }

    private void loadLinkViews(ViewGroup root) {
        try (InputStream inputStream = requireContext().getResources().openRawResource(R.raw.categories)) {
            ViewBuilder viewBuilder = new ViewBuilder(inputStream);
            viewBuilder.appendViews(root, getParams());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String name() {
        return NAME;
    }

    private final class ViewBuilder {
        private final Document mDocument;

        public ViewBuilder(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            mDocument = documentBuilder.parse(inputStream);
        }

        public void appendViews(ViewGroup root, ViewGroup.LayoutParams params) {
            NodeList nodes = mDocument.getElementsByTagName("category");
            for(Node node: new IterableNodeList(nodes)) {
                if(node instanceof Element) {
                    Element element = (Element)node;
                    appendView(element, root, params);
                }
            }
        }

        private void appendView(Element element, ViewGroup root, ViewGroup.LayoutParams params) {
            TextView textView = new TextView(requireContext());
            String name = element.getAttribute("name");
            String words = element.getTextContent().replace("/", ", ");
            // noinspection all
            textView.setText(name+": "+words);
            textView.setLayoutParams(params);
            textView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.droidsans_bold));
            textView.setTextSize(25);
            textView.setPadding(5, 5, 5, 5);
            textView.setBackgroundResource(R.drawable.text_background);
            root.addView(textView);
        }
    }
}
