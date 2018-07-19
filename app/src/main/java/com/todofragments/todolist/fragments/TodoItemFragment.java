package com.todofragments.todolist.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;


import com.todofragments.todolist.R;
import com.todofragments.todolist.TodoItem;
import com.todofragments.todolist.util.DateUtil;
import com.todofragments.todolist.util.KeyboardUtil;

import java.util.Calendar;
import java.util.UUID;

public class TodoItemFragment extends android.app.Fragment {
    private static final String ARG_TODO_ITEM = "arg.todoitem";

    public static final int MODE_CREATION = 0;
    public static final int MODE_CHANGE = 1;

    private OnFragmentInteractionListener mListener;

    public TodoItemFragment() {
        // Required empty public constructor
    }


    public static TodoItemFragment newInstance(TodoItem todoItem) {
        TodoItemFragment fragment = new TodoItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO_ITEM, todoItem);
        fragment.setArguments(args);
        return fragment;
    }

    DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mSelectedDate.set(Calendar.YEAR, year);
            mSelectedDate.set(Calendar.MONTH, monthOfYear);
            mSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            openTimePicker();
        }
    };

    TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mSelectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mSelectedDate.set(Calendar.MINUTE, minute);

            updateDateLabel();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_fragment_todo_item_edit:
                    submit();
                    break;
                case R.id.action_fragment_todo_item_priority_increase:
                    increasePriority();
                    break;
                case R.id.action_fragment_todo_item_priority_decrease:
                    decreasePriority();
                    break;
                case R.id.label_fragment_todo_item_date:
                    openDatePicker();
                    break;
            }
        }
    };

    private TodoItem mTodoItem;

    private TextInputEditText mTitleInput;
    private TextInputEditText mDescriptionInput;
    private TextView mDateLabel;
    private CheckBox mReminderCheckBox;
    private CheckBox mRepeatCheckBox;
    private RadioGroup mRepeatRadioGroup;
    private TextView mPriorityLabel;

    private Calendar mSelectedDate = Calendar.getInstance();
    private int mPriority = TodoItem.PRIORITY_MIN;

    private int mMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTodoItem = getArguments().getParcelable(ARG_TODO_ITEM);
            if (mTodoItem == null) {
                mMode = MODE_CREATION;
            } else {
                mMode = MODE_CHANGE;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);

        updateDateLabel();

        if (mTodoItem != null) {
            fillData(mTodoItem);
        }
    }

    private void init(View root) {
        mTitleInput = root.findViewById(R.id.input_fragment_todo_item_title);
        mDescriptionInput = root.findViewById(R.id.input_fragment_todo_item_description);
        mReminderCheckBox = root.findViewById(R.id.checkbox_fragment_todo_item_reminder);
        mRepeatCheckBox = root.findViewById(R.id.checkbox_fragment_todo_item_repeat);
        mRepeatRadioGroup = root.findViewById(R.id.radio_fragment_todo_item_repeat);
        mRepeatRadioGroup.setVisibility(View.GONE);
        mPriorityLabel = root.findViewById(R.id.label_fragment_todo_item_priority_value);
        mDateLabel = root.findViewById(R.id.label_fragment_todo_item_date);

        root.findViewById(R.id.action_fragment_todo_item_edit).setOnClickListener(mOnClickListener);
        mDateLabel.setOnClickListener(mOnClickListener);
        root.findViewById(R.id.action_fragment_todo_item_priority_increase).setOnClickListener(mOnClickListener);
        root.findViewById(R.id.action_fragment_todo_item_priority_decrease).setOnClickListener(mOnClickListener);

        ((CheckBox)root.findViewById(R.id.checkbox_fragment_todo_item_repeat)).
                setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        toggleRepeatTypeGroupVisibility(isChecked);
                    }
                });
    }

    private void fillData(TodoItem todoItem) {
        mTitleInput.setText(todoItem.getTitle());
        mDescriptionInput.setText(todoItem.getDescription());
        mSelectedDate.setTime(todoItem.getDate());
        mDateLabel.setText(DateUtil.formatDateToLongStyle(mSelectedDate.getTime()));
        mReminderCheckBox.setChecked(todoItem.isShouldRemind());
        if (todoItem.getRepeatType() != null) {
            switch (todoItem.getRepeatType()) {
                case NONE:
                    mRepeatCheckBox.setChecked(false);
                    break;
                case DAILY:
                    mRepeatRadioGroup.check(R.id.radio_fragment_todo_item_daily);
                    mRepeatCheckBox.setChecked(true);
                    break;
                case WEEKLY:
                    mRepeatRadioGroup.check(R.id.radio_fragment_todo_item_weekly);
                    mRepeatCheckBox.setChecked(true);
                    break;
                case MONTHLY:
                    mRepeatCheckBox.setChecked(true);
                    mRepeatRadioGroup.check(R.id.radio_fragment_todo_item_monthly);
                    break;
            }
        }
        mPriorityLabel.setText(String.valueOf(todoItem.getPriority()));
    }

    private void submit() {
        KeyboardUtil.hideKeyboardFrom(getActivity(), getView());
        if (checkInput()) {
            if (mListener != null) {
                switch (mMode) {
                    case MODE_CREATION:
                        mListener.onItemCreated(createTodoItemFromInput());
                        break;
                    case MODE_CHANGE:
                        mListener.onItemChanged(createTodoItemFromInput());
                        break;
                }
            }
        }
    }

    private TodoItem createTodoItemFromInput() {
        if (mTodoItem == null) {
            // If item is newly created initialize with uuid
            mTodoItem = new TodoItem(UUID.randomUUID());
            mTodoItem.setId(UUID.randomUUID().toString());
        }
        mTodoItem.setTitle(mTitleInput.getText().toString());
        mTodoItem.setDescription(mDescriptionInput.getText().toString());
        mTodoItem.setDate(mSelectedDate.getTime());
        mTodoItem.setShouldRemind(mReminderCheckBox.isChecked());
        mTodoItem.setPriority(mPriority);
        if (mRepeatCheckBox.isChecked()) {
            switch (mRepeatRadioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_fragment_todo_item_daily:
                    mTodoItem.setRepeatType(TodoItem.Repeat.DAILY);
                    break;
                case R.id.radio_fragment_todo_item_weekly:
                    mTodoItem.setRepeatType(TodoItem.Repeat.WEEKLY);
                    break;
                case R.id.radio_fragment_todo_item_monthly:
                    mTodoItem.setRepeatType(TodoItem.Repeat.MONTHLY);
                    break;
                default:
                    mTodoItem.setRepeatType(TodoItem.Repeat.NONE);
            }
        }

        return mTodoItem;
    }

    private void increasePriority() {
        mPriority = Math.min(++mPriority, TodoItem.PRIORITY_MAX);
        mPriorityLabel.setText(String.valueOf(mPriority));
    }

    private void decreasePriority() {
        mPriority = Math.max(--mPriority, TodoItem.PRIORITY_MIN);
        mPriorityLabel.setText(String.valueOf(mPriority));
    }

    private void updateDateLabel() {
        mDateLabel.setText(DateUtil.formatDateToLongStyle(mSelectedDate.getTime()));
    }

    private boolean checkInput() {
        boolean isValid;
        isValid = checkTitle();

        return isValid;
    }

    private boolean checkTitle() {
        boolean isValid;
        if (isEmpty(mTitleInput)) {
            isValid = false;
            mTitleInput.setError("Title field is mandatory");
        } else {
            isValid = true;
            mTitleInput.setError(null);
        }

        return isValid;
    }

    private boolean isEmpty(TextInputEditText editText) {
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private void openDatePicker() {
        new DatePickerDialog(getActivity(), mOnDateSetListener, mSelectedDate.get(Calendar.YEAR),
                mSelectedDate.get(Calendar.MONTH),
                mSelectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        new TimePickerDialog(getActivity(), mOnTimeSetListener, mSelectedDate.get(Calendar.HOUR_OF_DAY),
                mSelectedDate.get(Calendar.MINUTE), true).show();
    }

    private void toggleRepeatTypeGroupVisibility(boolean visible) {
        if (visible) {
            mRepeatRadioGroup.setVisibility(View.VISIBLE);
        } else {
            mRepeatRadioGroup.setVisibility(View.GONE);
        }
    }

    public void setOnInteractionListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    /**
     * This interface can be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onItemCreated(TodoItem todoItem);
        void onItemChanged(TodoItem todoItem);
    }
}