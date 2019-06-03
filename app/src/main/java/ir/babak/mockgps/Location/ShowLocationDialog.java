package ir.babak.mockgps.Location;

import android.view.View;


public class ShowLocationDialog extends BaseLocationDialog {

    @Override
    public void onResume() {
        super.onResume();

        if(bundle_position!=null){
            etLat.setText(String.valueOf(bundle_position.getLat()));
            etLng.setText(String.valueOf(bundle_position.getLng()));
        }
        btnAdd.setVisibility(View.GONE);
    }
}
