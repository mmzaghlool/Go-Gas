package com.gas.go.mostafa.gogas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class BookingActivity extends AppCompatActivity {
    private final short PICK_DATE_AND_TIME = 0;
    private DialogCash dialogCash;
    private TextView tvTotalPayment;
    private TextView tvTime;
    private Spinner typeSpinner;
    private Spinner qtySpinner;
    private Spinner paymentSpinner;

    private int selectedQty = 1;
    private double selectedType = 1.5;

    //create a list of items for the types spinner.
    private String[] typeItems = new String[]{"Small: 1.500 OMR", "Medium: 2.800 OMR", "Large: 5.500 OMR"};

    //create a list of items for the qty spinner.
    private String[] qtyItems = new String[]{"1", "2", "3", "4", "5"};

    //create a list of items for the payment spinner.
    private String[] paymentItems = new String[]{"Cash", "Credit card"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        setTitle("Book an order");

//        tvPaymentMethod = (TextView) findViewById(R.id.tv_payment_val);
        tvTime = (TextView) findViewById(R.id.tv_time_val);
        tvTotalPayment = (TextView) findViewById(R.id.tv_total_payment_val);

        //get the spinner from the xml.
        typeSpinner = findViewById(R.id.spinner_type);
        qtySpinner = findViewById(R.id.spinner_qty);
        paymentSpinner = findViewById(R.id.spinner_payment);
        //create an adapter to describe how the items are displayed.
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeItems);
        //set the spinners adapter to the previously created one.
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<String> qtyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, qtyItems);
        qtySpinner.setAdapter(qtyAdapter);

        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paymentItems);
        paymentSpinner.setAdapter(paymentAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                double[] types = {1.5, 2.8, 5.5};
                selectedType = types[position];
                String val = selectedType * selectedQty + " OMR";
                tvTotalPayment.setText(val);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedQty = position + 1;
                String val = selectedType * selectedQty + " OMR";
                tvTotalPayment.setText(val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_DATE_AND_TIME) {
                String result = data.getStringExtra("result");
                timeStamp = data.getStringExtra("timeStamp");
                Log.v("result", data.getExtras().toString());
                tvTime.setText(result);
            }
        }

    }*/


    public void buttonConfirmBooking(View view) {

    }


    public void buttonChoosePayment(View view) {
        dialogCash = new DialogCash();
        dialogCash.show(getSupportFragmentManager(), "missiles");
    }

    public void buttonChooseTime(View view) {

        String type = typeSpinner.getSelectedItem().toString();
        String payment = paymentSpinner.getSelectedItem().toString();
        String qty = qtySpinner.getSelectedItem().toString();

        Intent i = new Intent(this, DateTimeActivity.class);
//        startActivityForResult(i, PICK_DATE_AND_TIME);
        Intent getData = getIntent();
        i.putExtras(getData);
        i.putExtra("qty", qty);
        i.putExtra("payment", payment);
        i.putExtra("type", type);

        startActivity(i);
    }

/*
    public void buttonCreditCard(View view) {
        tvPaymentMethod.setText("Credit card");
        dialogCash.dismiss();
    }

    public void buttonCash(View view) {
        tvPaymentMethod.setText("Cash");
        dialogCash.dismiss();
    }*/
}
