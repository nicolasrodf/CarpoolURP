package com.nicolasrf.carpoolurp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;


import com.nicolasrf.carpoolurp.R;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    OnFragmentInteractionListener mListener;

    private DatePicker datePicker;

    public interface DateDialogListener {
        void onFinishDialog(Date date);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);
        datePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        datePicker.setMinDate(System.currentTimeMillis()); //NEW. OK
        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
//                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int mon = datePicker.getMonth();
                                int day = datePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,mon,day).getTime(); //creo q esto esta demas?? pq se crea en el activity.
                                DateDialogListener activity = (DateDialogListener) getActivity();
                                activity.onFinishDialog(date);
                                dismiss();

                                mListener.onDateData(day, mon, year);
                            }
                        })
                .create();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (RuntimeException e) {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void onDateData(int day, int mon, int year); //este metodo se creo por nosotrosy reemplazo al autogenerado.
    }
}
