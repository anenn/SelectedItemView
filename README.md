# SelectedItemView

## 一、概述

SelectedItemView 是一个方便我们进行条件选择的 Android Library，同时在选择过程中支持动画过度，效果如下：

![](https://cloud.githubusercontent.com/assets/7321351/18161692/9ddff9f2-7065-11e6-9f8f-1bcd9f114371.gif)

## 二、安装

### Gradle
```
compile 'com.anenn.selecteditemview:SelectedItemViewLib:0.0.2'
```

### Maven

```groovy
<dependency>
  <groupId>com.anenn.selecteditemview</groupId>
  <artifactId>SelectedItemViewLib</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```

## 三、使用

* 添加布局: 

```xml
<com.anenn.selecteditemview.SelectedItemView
      android:id="@+id/siv"
      android:layout_width="match_parent"
      android:layout_height="40dp"
      android:paddingBottom="8dp"
      android:paddingLeft="5dp"
      android:paddingRight="5dp"
      android:paddingTop="8dp"
      android:background="#F4F4F4"
      app:default_bg="#EAEAEA"
      app:selected_bg="#ED5296"
      app:default_text_color="#222222"
      app:selected_text_color="#ffffff"
      app:text_size="15sp"
      app:item_margin="10dp" />
```

* 自定义属性:

| 名称 | 类型 | 描述 |
| ---- | ---- | ---- |
| selected_bg | color | item 被选中的背景色 |
| default_bg  | color | item 默认的背景色 |
| selected\_text_color | color | item 被选中的文本颜色 |
| default_text_color | color | item 默认的文本颜色 |
| text_size | dimension | item 的文本字体大小 |
| item_margin | dimension | item 之间的间隙 |

* 添加数据源:

```java
 final List<String> textArray = new ArrayList<>();
textArray.add("本日");
textArray.add("本周");
textArray.add("本月");
textArray.add("近3月");
textArray.add("自定义");
SelectedItemView.setSelectItemList(textArray);
```

* 设置监听回调:

```java
SelectedItemView selectedItemView = (SelectedItemView) findViewById(R.id.siv);
selectedItemView.setOnItemSelectedListener(new SelectedItemView.OnItemSelectedListener() {
  @Override public void onItemSelected(int position) {
    Toast.makeText(MainActivity.this, position + ", " + textArray.get(position),
        Toast.LENGTH_SHORT).show();
  }
});
```

# License

    Copyright 2016 Anenn

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
