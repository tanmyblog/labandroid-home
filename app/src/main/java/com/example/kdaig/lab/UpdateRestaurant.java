package com.example.kdaig.lab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.kdaig.lab.dao.RestaurantDAO;
import com.example.kdaig.lab.model.ClassRestaurant;

public class UpdateRestaurant extends AppCompatActivity {

    private RestaurantDAO restaurantDAO;
    private boolean needRefresh = false;

    private EditText edtId;
    private EditText edtName;
    private EditText edtAddress;
    private RadioGroup type;
    private RadioButton takeout;
    private RadioButton sitdown;
    private RadioButton delivery;

    private Button btnCancel;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_restaurant);

        edtId = (EditText) findViewById(R.id.edtId);
        edtName = (EditText) findViewById(R.id.edtName);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        type = (RadioGroup) findViewById(R.id.type);
        takeout = (RadioButton) findViewById(R.id.take_out);
        sitdown = (RadioButton) findViewById(R.id.sit_down);
        delivery = (RadioButton) findViewById(R.id.delivery);

        // thiết lập readOnly cho edtId
        edtId.setEnabled(false);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        restaurantDAO = new RestaurantDAO(this);
        // Nhận dữ liệu từ intent được gửi qua từ MainActivity khi thao tác chức năng Update
        Intent intent = this.getIntent();
        ClassRestaurant classRestaurant = (ClassRestaurant) intent.getSerializableExtra("classRestaurant");

        // hiển thị dữ liệu lên các view
        edtId.setText(classRestaurant.getId());
        edtName.setText(classRestaurant.getName());
        edtAddress.setText(classRestaurant.getAddress());

        int i=0;
        String getType = classRestaurant.getType();
        Toast.makeText(this, getType +"", Toast.LENGTH_SHORT).show();
        switch(getType) {
            case "Take Out":
                i = 1;
                break;
            case "Sit Down":
                i=2;
                break;
            case "Delivery":
                i=3;
                break;
        }

        if(i == 1) {
            type.check(R.id.take_out);
        } else if(i == 2){
            type.check(R.id.sit_down);
        } else if(i == 3){
            type.check(R.id.delivery);
        }

    }

    public void cancel(View v) {
        // khong lam gi ca tro ve mainActivity
        this.onBackPressed();
    }

    public void editRestaurant(View v) {
        ClassRestaurant r = new ClassRestaurant();

        r.setId(edtId.getText().toString());
        r.setName(edtName.getText().toString());
        r.setAddress(edtAddress.getText().toString());

        RadioGroup type = (RadioGroup) findViewById(R.id.type);
        switch (type.getCheckedRadioButtonId()) {
            case R.id.take_out:
                r.setType("Take Out");
                break;
            case R.id.sit_down:
                r.setType("Sit Down");
                break;
            case R.id.delivery:
                r.setType("Delivery");
                break;
        }

        restaurantDAO.update(r);
        this.needRefresh = true;
        // trở lại MainActivity
        this.onBackPressed();
    }

    // khi activity này hoàn thành
    // có thể cần gửi phản hồi gì đó về cho Activity đã gọi nó.
    @Override
    public void finish() {
        // chuẩn bị dữ liệu intent
        Intent data = new Intent();
        // yêu cầu MainActivity refresh lại ListView hoặc không
        data.putExtra("needRefresh", needRefresh);

        // Activity đã hoàn thành OK, trả về dữ liệu
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }
}
