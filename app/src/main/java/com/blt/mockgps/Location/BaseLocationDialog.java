package com.blt.mockgps.Location;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blt.mockgps.Database.Entitiy.PositionsEntitiy;
import com.blt.mockgps.R;

public class BaseLocationDialog extends DialogFragment {

    protected View view;
    protected Button btnAdd, btnCancel;
    protected TextInputEditText etLng,etLat;
    protected PositionsEntitiy bundle_position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_location,container,false);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnCancel = view.findViewById(R.id.btnCancel);
        etLng = view.findViewById(R.id.etLng);
        etLat = view.findViewById(R.id.etLat);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle!=null){
            bundle_position  = bundle.getParcelable(getString(R.string.KEY_PARCABLE_POSITION));
        }
    }

    protected double getNumberFromEdithText(TextInputEditText et){
        if(et != null && et.getText()!= null && et.getText().length()>0)
            return Double.valueOf(et.getText().toString());
        else
            return 0;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }
    }
}
