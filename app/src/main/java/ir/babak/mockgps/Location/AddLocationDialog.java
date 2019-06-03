package ir.babak.mockgps.Location;

import android.view.View;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import ir.babak.mockgps.Application.MyApp;
import ir.babak.mockgps.Database.Entitiy.PositionsEntitiy;


public class AddLocationDialog extends BaseLocationDialog {

    @Override
    public void onResume() {
        super.onResume();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoxStore boxStore = ((MyApp) getActivity().getApplication()).getBoxStore();
                Box<PositionsEntitiy> positionBox = boxStore.boxFor(PositionsEntitiy.class);

                PositionsEntitiy positionsEntitiy = new PositionsEntitiy();
                positionsEntitiy.setLat(getNumberFromEdithText(etLat));
                positionsEntitiy.setLng(getNumberFromEdithText(etLng));
                positionBox.put(positionsEntitiy);

                dismiss();
            }
        });
    }
}
