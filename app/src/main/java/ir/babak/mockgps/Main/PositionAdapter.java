package ir.babak.mockgps.Main;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.babak.mockgps.Database.Entitiy.PositionsEntitiy;
import ir.babak.mockgps.Location.EditLocationDialog;
import ir.babak.mockgps.Location.ShowLocationDialog;
import ir.babak.mockgps.R;


public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.ViewHolder> {

    private List<PositionsEntitiy> list_Positions;
    private AppCompatActivity activity;


    // Provide a suitable constructor (depends on the kind of dataset)
    public PositionAdapter(AppCompatActivity activity, List<PositionsEntitiy> list_Positions) {
        this.activity = activity;
        this.list_Positions = list_Positions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tvId;
        TextView tvLan;
        TextView tvLng;

        public ViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.tvId);
            tvLan = v.findViewById(R.id.tvLan);
            tvLng = v.findViewById(R.id.tvLng);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemLayoutViewMain = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_setting, viewGroup, false);
        return new ViewHolder(itemLayoutViewMain);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        PositionsEntitiy positionsEntitiy = list_Positions.get(holder.getAdapterPosition());

        holder.tvId.setText(String.valueOf(positionsEntitiy.getId()));
        holder.tvLan.setText(String.valueOf(positionsEntitiy.getLat()));
        holder.tvLng.setText(String.valueOf(positionsEntitiy.getLng()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick(holder);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClick(holder);
                return false;
            }
        });
    }

    private Bundle getBundle(ViewHolder holder){
        PositionsEntitiy positionsEntitiy = list_Positions.get(holder.getAdapterPosition());
        Bundle bundle = new Bundle();
        bundle.putParcelable(activity.getString(R.string.KEY_PARCABLE_POSITION),positionsEntitiy);
        return bundle;
    }

    private void itemClick(ViewHolder holder){
        ShowLocationDialog showLocationDialog = new ShowLocationDialog();
        showLocationDialog.setArguments(getBundle(holder));
        showLocationDialog.show(activity.getSupportFragmentManager(),"");
    }

    private void itemLongClick(ViewHolder holder) {
        EditLocationDialog editLocationDialog = new EditLocationDialog();
        editLocationDialog.setArguments(getBundle(holder));
        editLocationDialog.show(activity.getSupportFragmentManager(),"");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list_Positions.size();
    }

}
