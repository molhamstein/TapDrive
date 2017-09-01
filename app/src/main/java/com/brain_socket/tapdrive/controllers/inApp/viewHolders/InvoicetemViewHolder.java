package com.brain_socket.tapdrive.controllers.inApp.viewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.partner.Invoice;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.Helpers;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Molhamstein on 8/20/2017.
 *
 */
public class InvoicetemViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private CardView itemCardView;
    private TextViewCustomFont userName;
    private TextViewCustomFont itemName;
    private TextViewCustomFont totalCostTextView;
    private TextViewCustomFont timeFromTextView;
    private TextViewCustomFont itemStatus;
    private CircleImageView userImageView;

    private void findViews(View rootView) {
        itemCardView = (CardView) rootView.findViewById(R.id.item_card_view);
        userName = (TextViewCustomFont) rootView.findViewById(R.id.user_name);
        itemName = (TextViewCustomFont) rootView.findViewById(R.id.item_name);
        totalCostTextView = (TextViewCustomFont) rootView.findViewById(R.id.item_cost);
        timeFromTextView = (TextViewCustomFont) rootView.findViewById(R.id.date_text_view);
        itemStatus = (TextViewCustomFont) rootView.findViewById(R.id.item_status);
        userImageView = (CircleImageView) rootView.findViewById(R.id.user_profile_image);
    }

    public InvoicetemViewHolder(Context context, View itemView) {
        super(itemView);
        findViews(itemView);
        this.context = context;
    }

    public void bind(Invoice invoice) {

        UserModel user = invoice.getOrder().getUser();
        Order order = invoice.getOrder();

        itemName.setText(order.getItem().getEnglishName());
        itemStatus.setText(order.getStatus());
        if (!order.getTotal().equalsIgnoreCase("")) {
            totalCostTextView.setText(order.getTotal() + " AED");
        } else {
            totalCostTextView.setText("0 AED");
        }
        userName.setText(user.getUsername());

        timeFromTextView.setText(Helpers.getFormattedDateString(order.getStartDate()));
        Glide.with(context).load(order.getUser().getPhoto()).into(userImageView);

    }

}
