package com.brain_socket.tapdrive.controllers.inApp.viewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.orders.ServerNotification;
import com.brain_socket.tapdrive.utils.Helpers;
import com.bumptech.glide.Glide;

/**
 * Created by EYADOOS-PC on 8/8/2017.
 */

public class ServerNotificationsItemViewHolder extends RecyclerView.ViewHolder {

    Context context;

    private CardView itemCardView;
    private TextViewCustomFont userName;
    private TextViewCustomFont itemName;
    private TextViewCustomFont totalCostTextView;
    private TextViewCustomFont timeFromTextView;
    private TextViewCustomFont timeToTextView;
    private RoundedImageView itemImage;
    private TextViewCustomFont itemStatus;

    private void findViews(View rootView) {
        itemCardView = (CardView)rootView.findViewById( R.id.item_card_view );
        userName = (TextViewCustomFont)rootView.findViewById( R.id.user_name );
        itemName = (TextViewCustomFont)rootView.findViewById( R.id.item_name );
        totalCostTextView = (TextViewCustomFont)rootView.findViewById( R.id.total_cost_text_view );
        timeFromTextView = (TextViewCustomFont)rootView.findViewById( R.id.time_from_text_view );
        timeToTextView = (TextViewCustomFont)rootView.findViewById( R.id.time_to_text_view );
        itemImage = (RoundedImageView)rootView.findViewById( R.id.item_image );
        itemStatus = (TextViewCustomFont)rootView.findViewById( R.id.item_status );
    }


    public ServerNotificationsItemViewHolder(Context context, View itemView) {
        super(itemView);
        findViews(itemView);
        this.context = context;
    }

    public void bind(ServerNotification serverNotification) {

        Glide.with(context).load(serverNotification.getObject().getItem().getPhoto()).into(itemImage);
        itemName.setText(serverNotification.getObject().getItem().getEnglishName());
        itemStatus.setText(serverNotification.getObject().getStatus());
        if (!serverNotification.getObject().getTotal().equalsIgnoreCase("")) {
            totalCostTextView.setText("Total Cost: " + serverNotification.getObject() + " AED");
        } else {
            totalCostTextView.setText("Total Cost: 0 AED");
        }

        timeFromTextView.setText("From: " + Helpers.getFormattedDateString(serverNotification.getObject().getStartDate()));
        timeToTextView.setText("To: " + Helpers.getFormattedDateString(serverNotification.getObject().getEndDate()));


    }

}
