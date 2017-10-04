package edu.upc.eseiaat.pma.quiz;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String USER_ANSWER = "user_answer";
    private int ids_answers[]={
            R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4
    };
    private int correct_answer;
    private int current_question;
    private String[] all_questions;
    private int[] user_answer;
    private TextView text_question;
    private boolean[] answer_is_correct;
    private RadioGroup group;
    private Button btn_next, btn_previous;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CORRECT_ANSWER, correct_answer);
        outState.putInt(CURRENT_QUESTION, current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT, answer_is_correct);
        outState.putIntArray(USER_ANSWER, user_answer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        text_question = (TextView) findViewById(R.id.text_question);
        group = (RadioGroup) findViewById(R.id.answers_group);
        btn_next = (Button) findViewById(R.id.btn_check);
        btn_previous = (Button) findViewById(R.id.btn_previous);

        all_questions = getResources().getStringArray(R.array.all_questions);
        if (savedInstanceState == null){
            startOver();
        }
        else {
            Bundle state = savedInstanceState;
            correct_answer = state.getInt(CORRECT_ANSWER);
            current_question = state.getInt(CURRENT_QUESTION);
            answer_is_correct = state.getBooleanArray(ANSWER_IS_CORRECT);
            user_answer = state.getIntArray(USER_ANSWER);
            show_question();
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_answer();
                if (current_question < all_questions.length-1) {
                    current_question++;
                    show_question();
                }
                else {
                    checkResults();
                }
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                check_answer();
                if (current_question > 0){
                    current_question--;
                    show_question();
                }
            }
        });

    }

    private void startOver() {
        answer_is_correct = new boolean[all_questions.length];
        user_answer = new int[all_questions.length];
        for (int i=0; i<user_answer.length; i++){
            user_answer[i]=-1;
        }
        current_question = 0;
        show_question();
    }

    private void checkResults() {
        int right=0, wrong=0, notanswered=0;
        for (int i=0; i<all_questions.length; i++){
            if (answer_is_correct[i]){
                right++;
            }
            else if (user_answer[i] == -1){
                notanswered++;
            }
            else {
                wrong++;
            }
        }
        String result = String.format("Right: %d\nWrong: %d\nNot answered: %d\n", right, wrong, notanswered);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle (R.string.results);
        builder.setMessage(result);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startOver();
            }
        });
        builder.create().show();
    }

    private void check_answer() {
        int active = group.getCheckedRadioButtonId();
        int answer = -1;
        for (int i=0; i<ids_answers.length; i++){
            int id = ids_answers[i];
            if (id==active){
                answer = i;
            }
        }

        answer_is_correct[current_question] = (answer==correct_answer);
        user_answer[current_question] = answer;
    }

    private void show_question() {
        String line = all_questions[current_question];
        String[] parts = line.split(";");

        group.clearCheck();

        text_question.setText(parts[0]);
        for (int i=0; i<ids_answers.length; i++){
            RadioButton rb = (RadioButton) findViewById(ids_answers[i]);
            String answer = parts[i+1];
            if (answer.charAt(0) == '*'){
                correct_answer = i;
                answer = answer.substring(1);
            }
            rb.setText(answer);
            if (user_answer[current_question] == i){
                rb.setChecked(true);
            }
        }

        if (current_question==0){
            btn_previous.setVisibility(View.INVISIBLE);
        } else {
            btn_previous.setVisibility(View.VISIBLE);
        }
        if (current_question == all_questions.length-1){
            btn_next.setText(R.string.finish);
        } else {
            btn_next.setText(R.string.next);
        }
    }
}
