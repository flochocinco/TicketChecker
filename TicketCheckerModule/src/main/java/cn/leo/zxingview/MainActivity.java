package cn.leo.zxingview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.leo.produce.ZxingView;
import cn.leo.produce.decode.ResultCallBack;

public class MainActivity extends AppCompatActivity {

    Set<String> listItems=new HashSet<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ZxingView zxingView = findViewById(R.id.zxingView);
        zxingView.bind(this)
            .subscribe(new ResultCallBack() {
                @Override
                public void onResult(String result) {
                    String res = listItems.add(result) ? "OK: " : "Déjà utilisé: ";
                    res += result;
                    adapter.insert(res, 0);
                }
            });

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>(listItems)){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int color;
                String item = this.getItem(position);
                if(item != null && item.contains(" utilis")){
                    color = getResources().getColor(android.R.color.holo_red_dark);

                }else{
                    color = getResources().getColor(android.R.color.holo_green_dark);
                }
                view.setBackgroundColor(color);
                return view;
            }
        };
        ((ListView)findViewById(R.id.listview)).setAdapter(adapter);

    }
}
