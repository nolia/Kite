package org.kite.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.kite.annotations.AsyncResult;
import org.kite.annotations.Wired;
import org.kite.wire.Wire;


public class CalcFragment extends Fragment {

    private Wire wire;

    @Wired
    private Substractor substractor;

    @Wired
    private AsyncCalc asyncCalc;

    @AsyncResult(AsyncCalc.ADD_RESULT)
    public void onAdd(Integer result){
        Toast.makeText(getActivity(), " 7 + 8 = " + result, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_five_sub_three:
                    int two = substractor.sub(5, 3);
                    Toast.makeText(getActivity(), "5 - 3 = " + two, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_add_async:
                    asyncCalc.asyncAdd(7, 8);
                    break;
            }
        }
    };


    public CalcFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(getActivity(), SampleService.class)
                .setAction(SampleService.ACTION_BIND_SUBSTRACTOR);
        wire = Wire.with(getActivity())
                .from(serviceIntent)
                .to(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        wire.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        wire.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calc, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.btn_five_sub_three)
                .setOnClickListener(btnListener);
        view.findViewById(R.id.btn_add_async)
                .setOnClickListener(btnListener);
        super.onViewCreated(view, savedInstanceState);
    }

}
