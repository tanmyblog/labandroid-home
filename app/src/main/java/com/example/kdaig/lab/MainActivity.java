package com.example.kdaig.lab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.kdaig.lab.dao.RestaurantDAO;
import com.example.kdaig.lab.model.ClassRestaurant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Khai báo DAO object để thực hiện các chức năng CRUD
    private final RestaurantDAO restaurantDAO = new RestaurantDAO(this);

    //Khai báo code khi thực hiện gọi Active thông qua Intent
    private static final int REQUEST_CODE = 1000;

    //Khai báo các biến tham chiếu với các điều khiển
    private ListView listView;
    private Button btnCreate, btnExit;

    //Khai báo đối tượng danh sách lớp học, sẽ được nạp từ CSDL thông qua DAO object
    private final List<ClassRestaurant> classRestaurantList = new ArrayList<ClassRestaurant>();

    //Khai báo đối tượng Adapter làm trung gian để gắn dữ liệu trên listViewe và classRoom
    //Ngoài ra đối tượng này cũng dùng để định dạng thông tin hiể thị trên listView
    private ArrayAdapter<ClassRestaurant> listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ánh xạ
        listView = (ListView)findViewById(R.id.listView);
        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnExit = (Button)findViewById(R.id.btnExit);

        //Load dữ liệu từ CSDL
        List<ClassRestaurant> list = restaurantDAO.readAll();
        this.classRestaurantList.addAll(list);

        //Định nghĩa 1 Adapter
        //1 - Context
        //2 - Layout cho các dòng
        //3 - ID của TextView mà dữ liệu sẽ được ghi vào
        //4 - Danh sách dữ liệu
        this.listViewAdapter = new ArrayAdapter<ClassRestaurant>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1,this.classRestaurantList);

        //Đăng ký Adapter cho ListView
        this.listView.setAdapter(this.listViewAdapter);

        //Cài đặt sự kiện click trên listView
        //Sự kiện này hỗ trợ chức năng, Xem chi tiết và chỉnh sửa tin được chọn
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id){
                ClassRestaurant selectedItem = (ClassRestaurant) parent.getItemAtPosition(pos);
                Intent intent = new Intent(MainActivity.this,UpdateRestaurant.class);
                intent.putExtra("classRestaurant",selectedItem);

                MainActivity.this.startActivityForResult(intent,REQUEST_CODE);
            }
        });

        //Xóa lớp học
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                final ClassRestaurant selectedItem = (ClassRestaurant) parent.getItemAtPosition(pos);

                //Hỏi trước khi xóa
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Bạn muốn xóa nhà hàng: "+selectedItem.getName()+" ?")
                        .setCancelable(false)
                        .setPositiveButton("CÓ", (dialog,i) ->{
                            restaurantDAO.delete(selectedItem.getId());
                            MainActivity.this.classRestaurantList.remove(selectedItem);

                            //Refresh
                            MainActivity.this.listViewAdapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("KHÔNG",null)
                        .show();

                return true;
            }
        });
    }

    public void exit(View v) {
        finish();
        moveTaskToBack(true);
    }

    // phương thức xử lý khi click vào button Create
    public void create(View v) {
        Intent intent = new Intent(this, CreateRestaurant.class);
        // Start addEditNoteActivity, có lỗi phản hồi
        this.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);
            // Refresh ListView
            if (needRefresh) {
                this.classRestaurantList.clear();
                RestaurantDAO restaurantDAO = new RestaurantDAO(this);
                List<ClassRestaurant> list = restaurantDAO.readAll();
                this.classRestaurantList.addAll(list);
                // Thông báo dữ liệu thay đổi (để refresh ListView)
                this.listViewAdapter.notifyDataSetChanged();

            }
        }
    }
}
