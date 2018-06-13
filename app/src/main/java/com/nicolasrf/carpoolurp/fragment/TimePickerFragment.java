package com.nicolasrf.carpoolurp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nicolasrf.carpoolurp.R;


public class TimePickerFragment extends DialogFragment {

    OnFragmentInteractionListener mListener;

    private TimePicker timePicker;
    public interface TimeDialogListener {
        void onFinishDialog(String time);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time,null);

        //el TimePickerFragment esta asociado al dialog_time_picker.xml
        timePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
//                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = 0;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    hour = timePicker.getHour();
                                }else{
                                    hour = timePicker.getCurrentHour();
                                }
                                int minute = 0;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    minute = timePicker.getMinute();
                                }else{
                                    minute = timePicker.getCurrentMinute();
                                }
                                TimeDialogListener activity = (TimeDialogListener) getActivity();
                                activity.onFinishDialog(updateTime(hour,minute));
                                dismiss();

                                mListener.onTimeData(hour,minute);

                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "You clicked Cancel \n No Item was selected !!", Toast.LENGTH_SHORT).show();
                    }

                })
                .create();
    }

    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String myTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return myTime;

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
        void onTimeData(int hourData, int minuteData); //este metodo se creo por nosotrosy reemplazo al autogenerado.
    }



}
