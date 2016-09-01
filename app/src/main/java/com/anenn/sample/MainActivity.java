package com.anenn.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.anenn.selecteditemview.SelectedItemView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final List<String> textArray = new ArrayList<>();
    textArray.add("本日");
    textArray.add("本周");
    textArray.add("本月");
    textArray.add("近3月");
    textArray.add("自定义");

    SelectedItemView selectedItemView = (SelectedItemView) findViewById(R.id.siv);
    selectedItemView.setSelectItemList(textArray);
    selectedItemView.setOnItemSelectedListener(new SelectedItemView.OnItemSelectedListener() {
      @Override public void onItemSelected(int position) {
        Toast.makeText(MainActivity.this, position + ", " + textArray.get(position),
            Toast.LENGTH_SHORT).show();
      }
    });
  }
}
