package com.app.ll.page;

import static android.widget.LinearLayout.LayoutParams;
import static com.app.ll.ChoiceManager.Choice;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.app.ll.ChoiceManager;
import com.app.ll.MainActivity;
import com.app.ll.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

public final class QuizFragment extends AbstractPage {
    public static final String NAME = "ll.page.quiz";

    public static final int MAX_TRIES_COUNT = 3;
    private int mScore = 0, mTriesLeft = MAX_TRIES_COUNT;

    private ChoiceManager mChoiceManager;
    private MaterialTextView mQuestionTextView, mScoreView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChoiceManager = new ChoiceManager(requireContext(), R.raw.categories);
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

        new ViewLoader().initAnswerButtons(view);
        nextQuestion();
    }

    private void nextQuestion() {
        Choice choice = mChoiceManager.getRandom();
        String question = choice.getRandom();
        mQuestionTextView.setText(question);
        mQuestionTextView.setTextColor(mScoreView.getTextColors().getDefaultColor());
        mTriesLeft = MAX_TRIES_COUNT;
    }

    @SuppressLint("SetTextI18n")
    private void updateScore(int diff) {
        mScore +=diff;
        mScoreView.setText(getString(R.string.score)+ mScore);
    }

    private void skipQuestion() {
        String question = mQuestionTextView.getText().toString();
        String answer   = mChoiceManager.getChoiceThanCanContain(question).NAME;
        String message = String.format(
                Locale.getDefault(), "%s: %s\n%s: %s",
                getString(R.string.question), question,
                getString(R.string.answer), answer
        );
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.skipped)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .show();
        updateScore(-1);
        nextQuestion();
    }

    @Override
    public void unregisterReceivers() {
        super.unregisterReceivers();

    }

    @Override
    protected String name() {
        return NAME;
    }

    private final class ViewLoader {
        private void initAnswerButtons(View view) {
            LinearLayout layout = view.findViewById(R.id.key_labels);
            for(ChoiceManager.Choice choice: mChoiceManager) {
                layout.addView(constructButton(choice));
                layout.addView(constructSeperatorView());
            }

            Button skipButton = new MaterialButton(requireContext());
            skipButton.setOnClickListener(v -> skipQuestion());
            skipButton.setText(R.string.skip);
            skipButton.setLayoutParams(getButtonLayoutParams());
            layout.addView(skipButton);
        }

        private View constructSeperatorView() {
            View view = new View(requireContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            TypedValue typedValue = new TypedValue();
            requireActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOutline, typedValue, true);
            view.setBackgroundColor(typedValue.data);
            return view;
        }

        private Button constructButton(final ChoiceManager.Choice choice) {
            Button button = new Button(requireContext());
            button.setText(choice.NAME);
            button.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.droidsans_bold));
            button.setLayoutParams(getButtonLayoutParams());
            button.setOnClickListener(new AnswerChecker(choice));
            button.setTextSize(19);
            button.setBackgroundColor(Color.TRANSPARENT);
            return button;
        }

        private LayoutParams getButtonLayoutParams() {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            final int PADDING = 10;
            params.setMargins(PADDING, PADDING, PADDING, PADDING);
            return params;
        }
    }

    private final class AnswerChecker implements View.OnClickListener {
        private final VibrationEffect mOnCorrectVibrationEffect, mOnIncorrectVibrationEffect;

        private final Choice mChoice;

        private AnswerChecker(Choice choice) {
            mChoice = choice;
            mOnCorrectVibrationEffect   = VibrationEffect.createOneShot(50,  VibrationEffect.DEFAULT_AMPLITUDE);
            mOnIncorrectVibrationEffect = VibrationEffect.createOneShot(160, VibrationEffect.DEFAULT_AMPLITUDE);
        }

        @Override
        public void onClick(View ignored) {
            if(mChoice.canContain(mQuestionTextView.getText().toString())) {
                onCorrect();
            } else {
                onIncorrect();
            }
        }

        private void onCorrect() {
            updateScore(+1);
            nextQuestion();
            MainActivity.vibrate(mOnCorrectVibrationEffect);
        }

        private void onIncorrect() {
            mQuestionTextView.setTextColor(Color.RED);
            mTriesLeft--;
            Toast.makeText(requireContext(), requireContext().getString(R.string.remaining_tries)+mTriesLeft, Toast.LENGTH_SHORT).show();
            if(mTriesLeft==0) {
                skipQuestion();
                Toast.makeText(requireContext(), R.string.no_more_tries, Toast.LENGTH_SHORT).show();
            }
            MainActivity.vibrate(mOnIncorrectVibrationEffect);
        }
    }
}
