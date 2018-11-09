package com.hmelizarraraz.testbilling.skulist.row;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmelizarraraz.testbilling.R;

public final class RowViewHolder extends RecyclerView.ViewHolder {
    public TextView title, description, price;
    public Button button;
    public ImageView skuIcon;

    /**
     * Controlador para clic en boton de una fila en particular
     */
    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }

    public RowViewHolder(View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        title       = itemView.findViewById(R.id.title);
        price       = itemView.findViewById(R.id.price);
        description = itemView.findViewById(R.id.description);
        skuIcon     = itemView.findViewById(R.id.sku_icon);
        button      = itemView.findViewById(R.id.state_button);

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onButtonClicked(getAdapterPosition());
                }
            });
        }
    }
}
