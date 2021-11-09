<h1 align="center">LineMatchingView</h1></br>
<p align="center">
:balloon: 连线题自定义ViewGroup
</p>
<p align="center"><a href="https://img.shields.io/badge/platform-android-green.svg"><img alt="Platform" src="https://img.shields.io/badge/platform-android-green.svg"/></a>
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=16"><img alt="API" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://jitpack.io/#zaaach/LineMatchingView"><img alt="jitpack" src="https://jitpack.io/v/zaaach/LineMatchingView.svg"/></a>
  <p align="center"></br>
<img src="https://github.com/zaaach/imgbed/blob/master/arts/line_matching_view_screen_1.png" width="30%"/>
<img src="https://github.com/zaaach/imgbed/blob/master/arts/line_matching_view_screen_2.png" width="30%"/>
<img src="https://github.com/zaaach/imgbed/blob/master/arts/line_matching_view.gif" width="30%"/>
</p>



## Install

#### Gradle 
项目根目录的build.gradle添加如下配置：
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
**module**添加依赖：

```gradle
dependencies {
    implementation "com.github.zaaach:LineMatchingView:x.y"
}
```
记得把x.y替换为[![jitpack](https://jitpack.io/v/zaaach/LineMatchingView.svg)](https://jitpack.io/#zaaach/LineMatchingView)中的数字

## Usage

#### xml

```xml
<com.zaaach.linematchingview.LineMatchingView
        android:id="@+id/line_matching_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@color/white"
        android:padding="8dp"
        app:lmv_item_width="120dp"
        app:lmv_item_height="60dp"
        app:lmv_horizontal_padding="80dp"
        app:lmv_vertical_padding="16dp"
        app:lmv_line_width="2dp"
        app:lmv_line_color_normal="@color/line_normal_color"
        app:lmv_line_color_correct="@color/correct_color"
        app:lmv_line_color_error="@color/error_color"/>
```
#### java
```java
LineMatchingView<ItemInfo> lineMatchingView;

List<ItemInfo> left = new ArrayList<>();
List<ItemInfo> right = new ArrayList<>();

lineMatchingView.init(new LineMatchingView.LinkableAdapter<ItemInfo>() {
            @Override
            public View getView(ItemInfo item, ViewGroup parent, int itemType, int position) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, null);
                return view;
            }

            @Override
            public int getItemType(ItemInfo item, int position) {
                return 0;
            }

            @Override
            public void onBindView(ItemInfo item, View view, int position) {
                //...
            }

            @Override
            public void onItemStateChanged(ItemInfo item, View view, int state, int position) {
                //...
            }

            @Override
            public boolean isCorrect(ItemInfo left, ItemInfo right, int l, int r) {
                return TextUtils.equals(left.desc, right.desc);
            }
        }).setItems(left, right);
```

## 支持的attrs属性 


| Attributes             | Format               | Description               |
| ---------------------- | -------------------- | ------------------------- |
| lmv_horizontal_padding | dimension\|reference | 两列间距，默认180px       |
| lmv_vertical_padding   | dimension\|reference | 垂直方向的间距，默认48px  |
| lmv_item_width         | dimension\|reference | item宽度，默认300px       |
| lmv_item_height        | dimension\|reference | item高度，默认120px       |
| lmv_line_width         | dimension\|reference | 线的宽度，默认6px         |
| lmv_line_color_normal  | color\|reference     | 正常颜色，默认Color.GRAY  |
| lmv_line_color_correct | color\|reference     | 正确颜色，默认Color.GREEN |
| lmv_line_color_error   | color\|reference     | 错误颜色，默认Color.RED   |

# About me

掘金：[ https://juejin.im/user/56f3dfe8efa6310055ac719f ](https://juejin.im/user/56f3dfe8efa6310055ac719f)

简书：[ https://www.jianshu.com/u/913a8bb93d12 ](https://www.jianshu.com/u/913a8bb93d12)

淘宝店：[ LEON家居生活馆 （动漫摆件）]( https://shop238932691.taobao.com)

![LEON](https://raw.githubusercontent.com/zaaach/imgbed/master/arts/leon_shop_qrcode.png)

:wink:淘宝店求关注:wink:

# License

```
Copyright (c) 2021 zaaach

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```