package com.hmelizarraraz.testbilling.skulist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hmelizarraraz.testbilling.R;
import com.hmelizarraraz.testbilling.billing.BillingProvider;
import com.hmelizarraraz.testbilling.skulist.row.RowViewHolder;
import com.hmelizarraraz.testbilling.skulist.row.SkuRowData;

import java.util.List;

public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder>
        implements  RowViewHolder.OnButtonClickListener {

    private List<SkuRowData> listData;
    private BillingProvider billingProvider;

    public SkusAdapter(BillingProvider billingProvider) {
        this.billingProvider    = billingProvider;
    }

    void updateData(List<SkuRowData> data) {
        this.listData   = data;
        notifyDataSetChanged();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sku_details_row, parent, false);
        return new RowViewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        SkuRowData data = getData(position);
        if (data != null) {
            holder.title.setText(data.getTitle());
            holder.description.setText(data.getDescription());
            holder.price.setText(data.getPrice());
            holder.button.setEnabled(true);
        }
        switch (data.getSku()) {
            case "gas":
                holder.skuIcon.setImageResource(R.drawable.gas_icon);
                break;
            case "premium":
                holder.skuIcon.setImageResource(R.drawable.premium_icon);
                break;
            case "gold_monthly":
            case "gold_yearly":
                holder.skuIcon.setImageResource(R.drawable.gold_icon);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public void onButtonClicked(int position) {
        SkuRowData data = getData(position);

        // todo billingProvider.getBillingManager().

    }

    private SkuRowData getData(int position) {
        return listData == null ? null : listData.get(position);
    }
}
