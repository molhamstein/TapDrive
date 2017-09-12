package com.brain_socket.tapdrive.customViews;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.filters.FieldOption;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by EYADOOS-PC on 7/31/2017.
 */

public class FilterTypeView extends RelativeLayout {

    View inflatedView;
    TextViewCustomFont filterValueText;

    LayoutInflater mInflater;
    private Pair<Integer, String> selectedItem;
    private CategoryField categoryField;
    private boolean hidden = false;
    private String parentOptionId = "";

    public FilterTypeView(Context context, CategoryField categoryField) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.setCategoryField(categoryField);
        init();
    }

    public FilterTypeView(Context context, CategoryField categoryField, boolean hidden) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.setCategoryField(categoryField);
        this.hidden = hidden;
        init();
    }

    public FilterTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        inflatedView = mInflater.inflate(R.layout.filter_type_layout, this, true);

        if (isHidden()) {
            inflatedView.setVisibility(GONE);
        }

        filterValueText = (TextViewCustomFont) inflatedView.findViewById(R.id.filter_value_text);

        filterValueText.setText(getCategoryField().getName());
        inflatedView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCategoryField().getEnglishName().equalsIgnoreCase("Year")) {
                    ArrayList<String> years = new ArrayList<String>();
                    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                    for (int i = thisYear; i >= 1900; i--) {
                        years.add(Integer.toString(i));
                    }

                    openOptionsDialog(years);
                } else {
                    openOptionsDialog(null);
                }
            }
        });
    }

    public void openOptionsDialog(final ArrayList<String> data) {

        if (!parentOptionId.equalsIgnoreCase("")) {
            openChildOptionsDialog();
            return;
        }

        new MaterialDialog.Builder(getContext())
                .title(getContext().getString(R.string.select_text) + getCategoryField().getName())
                .items(data == null ? getCategoryField().getOptions() : data)
                .itemsCallbackSingleChoice(getSelectedItem() == null ? 0 : getSelectedItem().first, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        if (text != null) {
                            if (!text.toString().equalsIgnoreCase("")) {
                                filterValueText.setText(getCategoryField().getName() + ": " + text);
                                if (data == null) {
                                    setSelectedItem(new Pair<>(which, getCategoryField().getOptions().get(which).getId()));
                                    EventBus.getDefault().post(new FilterSelectedEvent(FilterTypeView.this, getCategoryField().getOptions().get(which).getId()));
                                } else {
                                    setSelectedItem(new Pair<>(which, data.get(which)));
                                    EventBus.getDefault().post(new FilterSelectedEvent(FilterTypeView.this, data.get(which)));
                                }

                            }
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose_button_text)
                .show();

    }

    private void openChildOptionsDialog() {

        final ArrayList<FieldOption> fieldOptions = new ArrayList<>();
        fieldOptions.addAll(getCategoryField().getOptions());
        Iterator<FieldOption> optionIterator = fieldOptions.iterator();
        while (optionIterator.hasNext()) {
            FieldOption fieldOption = optionIterator.next();

            if (!fieldOption.getParentOptionId().equalsIgnoreCase(parentOptionId)) {
                optionIterator.remove();
            }
        }

        new MaterialDialog.Builder(getContext())
                .title(getContext().getString(R.string.select_text) + getCategoryField().getName())
                .items(fieldOptions)
                .itemsCallbackSingleChoice(getSelectedItem() == null ? 0 : getSelectedItem().first, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        /**
                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected radio button to actually be selected.
                         **/
                        if (text != null) {
                            if (!text.toString().equalsIgnoreCase("")) {
                                filterValueText.setText(getCategoryField().getName() + ": " + text);
                                setSelectedItem(new Pair<>(which, fieldOptions.get(which).getId()));
                                EventBus.getDefault().post(new FilterSelectedEvent(FilterTypeView.this, fieldOptions.get(which).getId()));
                            }
                        }
                        return true;
                    }
                })
                .positiveText(R.string.choose_button_text)
                .show();


    }

    public void clearSelected() {

        setSelectedItem(null);
        filterValueText.setText(getCategoryField().getName());

    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        if (hidden) {
            inflatedView.setVisibility(GONE);
        } else {
            inflatedView.setVisibility(VISIBLE);
        }
        this.hidden = hidden;
    }

    public CategoryField getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(CategoryField categoryField) {
        this.categoryField = categoryField;
    }

    public String getParentOptionId() {
        return parentOptionId;
    }

    public void setParentOptionId(String parentOptionId) {
        this.parentOptionId = parentOptionId;
    }

    public Pair<Integer, String> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Pair<Integer, String> selectedItem) {
        this.selectedItem = selectedItem;
    }
}
