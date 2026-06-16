package com.example.nutritioncalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class FoodExpandableAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> categoryList;
    private final Map<String, List<FoodItem>> foodData;
    private CustomizationHandler customizationHandler;

    public FoodExpandableAdapter(Context context, List<String> categoryList, Map<String, List<FoodItem>> foodData, CustomizationHandler customizationHandler) {
        this.context = context;
        this.categoryList = categoryList;
        this.foodData = foodData;
        this.customizationHandler = customizationHandler;
    }

    public void setCustomizationHandler(CustomizationHandler customizationHandler) {
        this.customizationHandler = customizationHandler;
    }

    @Override
    public int getGroupCount() {
        return categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String category = categoryList.get(groupPosition);
        List<FoodItem> items = foodData.get(category);
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categoryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String category = categoryList.get(groupPosition);
        return foodData.get(category).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String category = (String) getGroup(groupPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(category);
        AppStyler.styleListItemView(convertView, customizationHandler.getSettings());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        FoodItem item = (FoodItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        text1.setText(item.getName());
        String details = customizationHandler.formatFoodListDetails(item);
        text2.setText(details);
        text2.setVisibility(details.isEmpty() ? View.GONE : View.VISIBLE);
        AppStyler.styleListItemView(convertView, customizationHandler.getSettings());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
