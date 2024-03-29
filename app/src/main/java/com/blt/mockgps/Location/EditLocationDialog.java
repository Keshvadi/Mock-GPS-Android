package com.blt.mockgps.Location;

import android.view.View;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import com.blt.mockgps.Application.MyApp;
import com.blt.mockgps.Database.Entitiy.PositionsEntitiy;
import com.blt.mockgps.R;


public class EditLocationDialog extends BaseLocationDialog {

    @Override
    public void onResume() {
        super.onResume();

        if (bundle_position == null)
            return;

        etLat.setText(String.valueOf(bundle_position.getLat()));
        etLng.setText(String.valueOf(bundle_position.getLng()));
        btnAdd.setText(R.string.Edit);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoxStore boxStore = ((MyApp) getActivity().getApplication()).getBoxStore();
                Box<PositionsEntitiy> positionBox = boxStore.boxFor(PositionsEntitiy.class);

                PositionsEntitiy positionsEntitiy = bundle_position;
                positionsEntitiy.setLat(getNumberFromEdithText(etLat));
                positionsEntitiy.setLng(getNumberFromEdithText(etLng));
                positionBox.put(positionsEntitiy);

                dismiss();
            }
        });
    }
}
