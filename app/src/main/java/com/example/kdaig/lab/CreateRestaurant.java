package com.example.kdaig.lab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kdaig.lab.dao.RestaurantDAO;
import com.example.kdaig.lab.model.ClassRestaurant;

public class CreateRestaurant extends AppCompatActivity {

    private RestaurantDAO restaurantDAO;
    private boolean needRefresh = false;

    private EditText edtId;
    private EditText edtName;
    private EditText edtAddress;

    private Button btnCancel;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);

        edtId = (EditText) findViewById(R.id.edtId);
        edtName = (EditText) findViewById(R.id.edtName);
        edtAddress = (EditText) findViewById(R.id.edtAddress);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        restaurantDAO = new RestaurantDAO(this);
    }

    public void cancel(View v){
        // khong lam gi ca tro ve mainActivity
        this.onBackPressed();
    }

    public void addRestaurent(View v){
        ClassRestaurant r = new ClassRestaurant();

        r.setId(edtId.getText().toString());
        r.setName(edtName.getText().toString());
        r.setAddress(edtAddress.getText().toString());

        RadioGroup type = (RadioGroup) findViewById(R.id.type);
        switch(type.getCheckedRadioButtonId()) {
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

        restaurantDAO.create(r);
        this.needRefresh = true;

        // tro ve mainActivity
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


