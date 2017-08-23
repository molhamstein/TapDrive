package com.brain_socket.tapdrive.controllers.inApp.viewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.delegates.BookVehicleButtonClicked;
import com.brain_socket.tapdrive.model.partner.Car;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by MhannaCloudAppers on 7/20/2017.
 */

public class VehicleItemViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private CardView itemCardView;
    private RoundedImageView itemImage;
    private TextViewCustomFont itemName;
    private TextViewCustomFont itemDailyPrice;
    private TextViewCustomFont itemHourlyPrice;
    private TextViewCustomFont itemDriveNowButton;

    private Boolean enableClickButton;

    private void findViews(View rootView) {
        itemCardView = (CardView) rootView.findViewById(R.id.item_card_view);
        itemImage = (RoundedImageView) rootView.findViewById(R.id.item_image);
        itemName = (TextViewCustomFont) rootView.findViewById(R.id.item_name);
        itemDailyPrice = (TextViewCustomFont) rootView.findViewById(R.id.item_daily_price);
        itemHourlyPrice = (TextViewCustomFont) rootView.findViewById(R.id.item_hourly_price);
        itemDriveNowButton = (TextViewCustomFont) rootView.findViewById(R.id.item_drive_now_button);
    }

    public void bindData(final Car car) {

        Glide.with(context).load(car.getPhoto()).into(itemImage);
        itemName.setText(car.getEnglishName());
        itemDailyPrice.setText(car.getDailyPrice() + " AED");
        itemHourlyPrice.setText(car.getHourlyPrice() + " AED");

        itemDriveNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new BookVehicleButtonClicked(car));
            }
        });
        itemDriveNowButton.setVisibility(enableClickButton ? View.VISIBLE : View.GONE);
    }

    public VehicleItemViewHolder(Context context, View itemView, Boolean enableButton) {
        super(itemView);
        findViews(itemView);
        this.context = context;
        this.enableClickButton = enableButton;
    }

    public CardView getItemCardView() {
        return itemCardView;
    }

    public RoundedImageView getItemImage() {
        return itemImage;
    }

    public TextViewCustomFont getItemName() {
        return itemName;
    }

    public TextViewCustomFont getItemDailyPrice() {
        return itemDailyPrice;
    }

    public TextViewCustomFont getItemHourlyPrice() {
        return itemHourlyPrice;
    }

    public TextViewCustomFont getItemDriveNowButton() {
        return itemDriveNowButton;
    }
}
