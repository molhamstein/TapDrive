package com.brain_socket.tapdrive.controllers.inApp.viewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.delegates.OrderUpdatedEvent;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.orders.OrderStatus;
import com.brain_socket.tapdrive.utils.Helpers;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EYADOOS-PC on 8/5/2017.
 */

public class OrderItemViewHolder extends RecyclerView.ViewHolder {

    Context context;

    private CardView itemCardView;
    private TextViewCustomFont userName;
    private TextViewCustomFont itemName;
    private TextViewCustomFont totalCostTextView;
    private TextViewCustomFont timeFromTextView;
    private TextViewCustomFont timeToTextView;
    private RoundedImageView itemImage;
    private TextViewCustomFont itemStatus;
    private CircleImageView userImageView;

    private void findViews(View rootView) {
        itemCardView = (CardView) rootView.findViewById(R.id.item_card_view);
        userName = (TextViewCustomFont) rootView.findViewById(R.id.user_name);
        itemName = (TextViewCustomFont) rootView.findViewById(R.id.item_name);
        totalCostTextView = (TextViewCustomFont) rootView.findViewById(R.id.total_cost_text_view);
        timeFromTextView = (TextViewCustomFont) rootView.findViewById(R.id.time_from_text_view);
        timeToTextView = (TextViewCustomFont) rootView.findViewById(R.id.time_to_text_view);
        itemImage = (RoundedImageView) rootView.findViewById(R.id.item_image);
        itemStatus = (TextViewCustomFont) rootView.findViewById(R.id.item_status);
        userImageView = (CircleImageView) rootView.findViewById(R.id.user_profile_image);
    }


    public OrderItemViewHolder(Context context, View itemView) {
        super(itemView);
        findViews(itemView);
        this.context = context;
    }

    public void bind(final Order order, boolean isPartner, final int position) {

        Glide.with(context).load(order.getItem().getPhoto()).into(itemImage);
        itemName.setText(order.getItem().getName());

        if (!order.getTotal().equalsIgnoreCase("")) {
            totalCostTextView.setText(order.getTotal() + context.getString(R.string.currency));
        } else {
            totalCostTextView.setText("0" + context.getString(R.string.currency));
        }

        timeFromTextView.setText(Helpers.getFormattedDateString(order.getStartDate()));
        timeToTextView.setText(Helpers.getFormattedDateString(order.getEndDate()));

        Glide.with(context).load(order.getUser().getPhoto()).into(userImageView);
        userName.setText(order.getUser().getUsername());

        if (isPartner) {
            itemStatus.setText(OrderStatus.getInstance().getStatuses().get(order.getStatus()));

            if (order.getStatus() != null) {
                if (!order.getStatus().equalsIgnoreCase("Done")) {
                    itemStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataStore.getInstance().changeItemStatus(Integer.parseInt(order.getId()), OrderStatus.getInstance().getNewStatuses().get(order.getStatus()), new DataStore.DataRequestCallback() {
                                @Override
                                public void onDataReady(ServerResult result, boolean success) {
                                    if (result.getPairs().containsKey("order")) {
                                        Order updatedOrder = (Order) result.get("order");
                                        EventBus.getDefault().post(new OrderUpdatedEvent(updatedOrder, position));
                                    }
                                }
                            });
                        }
                    });
                } else {
                    itemStatus.setOnClickListener(null);
                }
            }
        } else {
            itemStatus.setText(order.getStatus());
        }

    }

}
