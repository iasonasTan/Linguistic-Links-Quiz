package com.app.ll.page;

import static android.widget.GridLayout.LayoutParams;
import static com.app.ll.ChoiceManager.Choice;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ll.ChoiceManager;
import com.app.ll.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public final class QuizFragment extends AbstractPage {
    public static final String NAME = "ll.page.quiz";

    private int mScore = 0;
    private ChoiceManager mChoiceManager;
    private MaterialTextView mQuestionTextView, mScoreView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChoiceManager = new ChoiceManager(requireContext(), R.raw.categories);
    }

    private void nextQuestion() {
        Choice choice = mChoiceManager.getRandom();
        String question = choice.getRandom();
        Log.d("question", question);
        mQuestionTextView.setText(question);
        mQuestionTextView.setTextColor(mScoreView.getTextColors().getDefaultColor());
    }

    @SuppressLint("SetTextI18n")
    private void updateScore(int diff) {
        mScore+=diff;
        mScoreView.setText(getString(R.string.score)+mScore);
    }

    // Requires Updates
    private void initAnswerButtons(View view) {
        GridLayout layout = view.findViewById(R.id.key_labels);
        for(ChoiceManager.Choice choice: mChoiceManager) {
            layout.addView(constructButton(choice));
        }
        MaterialButton skipButton = new MaterialButton(requireContext());
        skipButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Skipped")
                    .setMessage(getString(R.string.answer)+mChoiceManager.getChoiceThanCanContain(mQuestionTextView.getText().toString()).NAME)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                    .show();
            updateScore(-1);
            nextQuestion();
        });
        skipButton.setText(R.string.skip);
        skipButton.setLayoutParams(getButtonLayoutParams());
        layout.addView(skipButton);
    }

    private Button constructButton(final ChoiceManager.Choice choice) {
        MaterialButton button = new MaterialButton(requireContext());
        button.setText(choice.NAME);
        button.setLayoutParams(getButtonLayoutParams());
        button.setOnClickListener(v -> {
            if(choice.canContain(mQuestionTextView.getText().toString())) {
                updateScore(+1);
                nextQuestion();
            } else {
                mQuestionTextView.setTextColor(Color.RED);
                Toast.makeText(requireContext(), R.string.wrong_answer, Toast.LENGTH_SHORT).show();
            }
        });
        return button;
    }

    private LayoutParams getButtonLayoutParams() {
        LayoutParams params = new LayoutParams();
        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;
        final int PADDING = 10;
        params.setMargins(PADDING, PADDING, PADDING, PADDING);
        return params;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quiz_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuestionTextView = view.findViewById(R.id.question_view);
        mScoreView = view.findViewById(R.id.score_view);
        initAnswerButtons(view);
        nextQuestion();
    }

    @Override
    protected String name() {
        return NAME;
    }
}
