package com.example.kdaig.lab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kdaig.lab.dao.RestaurantDAO;
import com.example.kdaig.lab.dao.RestaurantServiceDAO;
import com.example.kdaig.lab.model.ClassRestaurant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //khai báo DAO Service Object để thực hiện các chức năng CRUD
    private RestaurantServiceDAO restaurantServiceDAO;

    //Progress Dialog Object - dùng để hiển thị trong quá trình bất đồng bộ
    ProgressDialog prgDialog;

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

    // lấy tất cả danh sách nhà hàng (bất đồng bộ từ restfull api)
    public void asyncReadAll(){
        //Async task
        AsyncTask task = new AsyncTask() {
            //khoi dong tien trình xu ly
            @Override
            protected Object doInBackground(Object[] objects) {
                //lau ds tu server
                List<ClassRestaurant> listItem = restaurantServiceDAO.getAll();
                //tra ve object  sang onpost execute(object o) sau khi hoan thanh cong viec
                return listItem;
            }
            //cap nhat tac vu khac khi tien trinh dang xu ly
            @Override
            protected void onProgressUpdate(Object[] values){super.onProgressUpdate(values);}

            //tien trinh xu ly cong viec 9 hoan thanh
            @Override
            protected  void  onPostExecute(Object o){
                super.onPostExecute(o);
                prgDialog.dismiss();

                List<ClassRestaurant> result = (List<ClassRestaurant>)o;

                //lam xoa het item trong classroomList
                MainActivity.this.classRestaurantList.clear();

                //cap nhat lai danh sach lasy tu server ve
                classRestaurantList.addAll(result);

                //thong bao du lieu thay doi(de refest listview)
                MainActivity.this.listViewAdapter.notifyDataSetChanged();

                String message = "there are"+String.valueOf(result.size())+"classes";
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    // xóa nhà hàng (bất đồng bộ từ restfull api)
    public  void asyncDelete(final ClassRestaurant classRestaurant){
        //async task
        AsyncTask task = new AsyncTask() {
            //khoi dong tien trinh xu ly
            protected void onPreExecute(){
                super.onPreExecute();
                prgDialog.show();
            }

            //khai bao cong viec chinh can xu ly
            @Override
            protected  Object doInBackground(Object[] objects){
                try {
                    boolean deleteOk = restaurantServiceDAO.delete(classRestaurant.getId());
                    return deleteOk;
                }
                catch (Exception ex){
                    return false;
                }
            }
            //cap nhat tac vu khac trong khi tien tronh chinh dang xu ly
            @Override
            protected void onProgressUpdate(Object[] values){super.onProgressUpdate(values);
            }
            //tien trinh xu ly cong viec chinh hoan thanh
            @Override
            protected  void onPostExecute(Object o){
                super.onPostExecute(o);
                prgDialog.dismiss();

                boolean result = (boolean)o;
                if(result == true){
                    //xoa item trong classroomlist neu da hoan thanh coong viev tren server
                    //die nay giup han che request tai lai ds
                    MainActivity.this.classRestaurantList.remove(classRestaurant);
                    //refresh listview
                    MainActivity.this.listViewAdapter.notifyDataSetChanged();

                    //hien thi thong diep
                    String messgae = "Reoving restaurant " +classRestaurant.getId()+ " successfully";
                    Toast.makeText(MainActivity.this,messgae,Toast.LENGTH_LONG).show();
                } else {
                    String message = "Fail! Removing " +classRestaurant.getId();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ánh xạ
        listView = (ListView)findViewById(R.id.listView);
        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnExit = (Button)findViewById(R.id.btnExit);

        restaurantServiceDAO = new RestaurantServiceDAO();
        //instantiate progress dialog object
        prgDialog = new ProgressDialog(this);

        //set progress dialog text
        prgDialog.setMessage("Please wait....");

        //set cancelable as false
        prgDialog.setCancelable(false);

        //Định nghĩa 1 Adapter
        //1 - Context
        //2 - Layout cho các dòng
        //3 - ID của TextView mà dữ liệu sẽ được ghi vào
        //4 - Danh sách dữ liệu
        listViewAdapter = new ArrayAdapter<ClassRestaurant>(MainActivity.this, android.R.layout.simple_list_item_1,
                android.R.id.text1,MainActivity.this.classRestaurantList);

        //Đăng ký Adapter cho ListView
        MainActivity.this.listView.setAdapter(this.listViewAdapter);

        // tai ds nha hang hc bat dong bo tu server
        asyncReadAll();

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

        //Xóa nhà hàng
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int posittion, long id) {
                final ClassRestaurant selectedItem = (ClassRestaurant) parent.getItemAtPosition(posittion);

                //Hỏi trước khi xóa
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(selectedItem.getName() + ". Bạn muốn xóa nhà hàng?")
                        .setCancelable(false)
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //xoa lop hc bat dong bo tu server
                                asyncDelete(selectedItem);
                            }
                        })
                        .setNegativeButton("Không",null)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);

            //refresh listview
            if (needRefresh){
                asyncReadAll();
            }
        }
    }
}
