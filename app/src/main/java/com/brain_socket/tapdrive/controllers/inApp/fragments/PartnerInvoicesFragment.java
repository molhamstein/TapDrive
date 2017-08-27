package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.adapters.InvoicesAdapter;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.partner.Invoice;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PartnerInvoicesFragment extends Fragment {

    @BindView(R.id.invoices_recycler_view)
    RecyclerView dataRecyclerView;
    @BindView(R.id.loader_view)
    ProgressBar loaderView;
    @BindView(R.id.empty_data_text_view)
    TextViewCustomFont emptyTextView;
    Unbinder unbinder;

    InvoicesAdapter invoicesAdapter;

    public PartnerInvoicesFragment() {
        // Required empty public constructor
    }

    public static PartnerInvoicesFragment newInstance() {
        Bundle args = new Bundle();
        PartnerInvoicesFragment fragment = new PartnerInvoicesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_partner_invoices, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataStore.getInstance().getPartnerInvoices(invoicesDataRequestCallback);

        invoicesAdapter = new InvoicesAdapter(getActivity(), new ArrayList<Invoice>());
        invoicesAdapter.notifyDataSetChanged();
        dataRecyclerView.setAdapter(invoicesAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public DataStore.DataRequestCallback invoicesDataRequestCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<Invoice> orders;
                try {

                    if (result.getPairs().containsKey("invoices")) {
                        orders = new ArrayList<>();
                        @SuppressWarnings("unchecked")
                        ArrayList<Invoice> receivedOrders = (ArrayList<Invoice>) result.get("invoices");
                        orders.addAll(receivedOrders);

                        updateDataAdapter(orders);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateDataAdapter(ArrayList<Invoice> orders) {

        loaderView.setVisibility(View.GONE);

        if (orders.size() > 0) {
            invoicesAdapter.setData(orders);
            invoicesAdapter.notifyDataSetChanged();
        } else {
            dataRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

    }

}
