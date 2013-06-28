android-code-generator-plugin-fx
================================

Eclipse plugin for faster activity creation from xml layouts

This project based on http://code.google.com/p/android-code-generator-plugin/.

List of new features:
=====================
 - auto creating packages if they are not exist; 
 - dividing code from "onCreate()" to several methods: 
	- initView() which contains all "findViewById";
	- setFonts() which contains all "setTypeface" to support Roboto fonts to Android 2.3.3 and less.
	- setListeners which contains all listeners.
 - auto creating adapter class with classic holder implementation. It calls dialog to choose a layout for adapter item.

How to use it
=============
1. Select xml files with activity layout.
2. Right click -&rt; Android Code Generator.
3. Wait few seconds.
4. Use files from %packagename%.activity and %packagename%.adapter

How it works
============

Original activity layout file
-----------------------------

<pre>
<code class="xml">
&lt;?xml version="1.0" encoding="utf-8"?&rt;
&lt;RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" &rt;

    &lt;ListView
        android:id="@+id/news_list_activity_news_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#eeeeee"
        android:cacheColorHint="@android:color/transparent"
        android:divider="@null"
        android:dividerHeight="0dp"
        android:drawingCacheQuality="low"
        android:persistentDrawingCache="scrolling"
        android:listSelector="@android:color/transparent"
        android:paddingLeft="0dp"
        android:paddingRight="0dp" /&rt;

    &lt;ImageView
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:background="@drawable/abs_shadow" /&rt;

    &lt;RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignBottom="@id/news_list_activity_news_list"
        android:layout_alignTop="@id/news_list_activity_news_list" &rt;

        &lt;ProgressBar
            android:id="@+id/articles_progress"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:visibility="gone" /&rt;

        &lt;TextView
            android:id="@+id/articles_exist_text_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:drawableTop="@drawable/no_news"
            android:padding="15dp"
            android:shadowColor="#ffffff"
            android:shadowDx="0"
            android:shadowDy="2"
            android:shadowRadius="1"
            android:text="@string/mock_text"
            android:textColor="#8a8a8a"
            android:textSize="15dp"
            android:visibility="gone" /&rt;

	&lt;Button
            android:id="@+id/articles_exist_button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/&rt;
    &lt;/RelativeLayout&rt;

&lt;/RelativeLayout&rt; 
</code>
</pre>

Original item layout file
-----------------------------

<pre>
<code class="xml">
&lt;?xml version="1.0" encoding="utf-8"?&rt;
&lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:paddingLeft="15dp"
    android:paddingRight="15dp" &rt;

    &lt;TextView
        android:id="@+id/row_news_date_header_text"
        style="@style/sectionHeader"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="2dp"
        android:layout_marginTop="20dp"
        android:paddingLeft="10.5dp"
        android:textColor="#3875b5"
        android:textSize="15dp" /&rt;

    &lt;RelativeLayout
        android:id="@+id/news_main_layout"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/news_item_state"
        android:paddingTop="3.5dp" &rt;

        &lt;FrameLayout
            android:id="@+id/row_news_image_container"
            android:layout_width="96dp"
            android:layout_height="68dp"
            android:layout_alignParentLeft="true"
            android:layout_alignParentTop="true"
            android:layout_marginBottom="5.5dp"
            android:layout_marginLeft="5.5dp"
            android:background="@drawable/shadow_news_item_image" &rt;

            &lt;ImageView
                android:id="@+id/row_news_icon"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="fitXY" /&rt;
        &lt;/FrameLayout&rt;

        &lt;TextView
            android:id="@+id/row_news_title_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignTop="@id/row_news_image_container"
            android:layout_marginLeft="6.5dp"
            android:layout_marginTop="0dp"
            android:layout_toRightOf="@id/row_news_image_container"
            android:ellipsize="end"
            android:lineSpacingMultiplier="0.85"
            android:maxLines="2"
            android:shadowColor="#ffffff"
            android:shadowDx="0"
            android:shadowDy="2"
            android:shadowRadius="1"
            android:textColor="#242424"
            android:textSize="16dp" /&rt;

        &lt;TextView
            android:id="@+id/row_news_type_text"
            android:layout_width="fill_parent"
            android:layout_height="wrap_content"
            android:layout_alignLeft="@id/row_news_title_text"
            android:layout_below="@id/row_news_title_text"
            android:layout_marginTop="-1.5dp"
            android:maxLines="1"
            android:shadowColor="#ffffff"
            android:shadowDx="0"
            android:shadowDy="2"
            android:shadowRadius="1"
            android:text="123"
            android:textColor="#8a8a8a"
            android:textSize="13dp" /&rt;

        &lt;ImageView
            android:id="@+id/row_news_date_icon"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignLeft="@id/row_news_title_text"
            android:layout_below="@id/row_news_type_text"
            android:layout_marginTop="-0.5dp"
            android:src="@drawable/icon_clock" /&rt;

        &lt;TextView
            android:id="@+id/row_news_date_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignTop="@id/row_news_date_icon"
            android:layout_marginLeft="4.5dp"
            android:layout_marginTop="-3dp"
            android:layout_toRightOf="@id/row_news_date_icon"
            android:ellipsize="end"
            android:maxLines="1"
            android:shadowColor="#ffffff"
            android:shadowDx="0"
            android:shadowDy="2"
            android:shadowRadius="1"
            android:text="2 часа назад"
            android:textColor="#8a8a8a"
            android:textSize="13dp" /&rt;
    &lt;/RelativeLayout&rt;

    &lt;View
        android:id="@+id/row_news_items_divider"
        android:layout_width="fill_parent"
        android:layout_height="1dp"
        android:background="@drawable/divider_gray_horizontal" /&rt;

&lt;/LinearLayout&rt;
</code>
</pre>

Result Activity.java
--------------------

<pre><code class="java">
package com.test.activity;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Typeface;
import com.test.R;
import android.widget.ListView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;

public class NewsListActivityActivity extends Activity {
	private static final String TAG = NewsListActivityActivity.class.getSimpleName();

	private ProgressBar articlesProgress;
	private ListView newsListActivityNewsList;
	private TextView articlesExistTextView;
	private Button articlesExistButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list_activity);

		initActionBar();
		initViews();
		setFonts();
		setListeners();
	}

	private void initActionBar(){
	}

	private void initViews(){
		articlesProgress = (ProgressBar) findViewById(R.id.articles_progress);
		newsListActivityNewsList = (ListView) findViewById(R.id.news_list_activity_news_list);
		articlesExistTextView = (TextView) findViewById(R.id.articles_exist_text_view);
		articlesExistButton = (Button) findViewById(R.id.articles_exist_button);
	}

	private void setFonts(){
		Typeface roboto = null;//TODO init this by utils
		articlesExistTextView.setTypeface(roboto);
	}

	private void setListeners(){
		articlesExistButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}
}
</code></pre>

Result Adapter.java
--------------------

<pre><code class="java">
package com.test.adapter;

import com.test.R;
import android.graphics.Typeface;
import android.content.Context;
import java.util.List;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.view.View;

public class NewsListActivityAdapter extends ArrayAdapter&lt;String&rt;{
	private static final String TAG = NewsListActivityAdapter.class.getSimpleName();

	private Context context;
	private LayoutInflater inflater;


	public NewsListActivityAdapter(Context context, List&lt;String&rt; objects) {
		super(context, R.layout.news_list_item, objects);
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.news_list_item, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String item = getItem(position);
		if (item != null){
			holder.populateForm(item);		}
		return convertView;
	}

	private class ViewHolder{

		private TextView rowNewsDateHeaderText;
		private TextView rowNewsDateText;
		private TextView rowNewsTypeText;
		private TextView rowNewsTitleText;

		public ViewHolder(View v){
			initViews(v);
			setFonts();
		}
		private void initViews(View v){
			rowNewsDateHeaderText = (TextView) v.findViewById(R.id.row_news_date_header_text);
			rowNewsDateText = (TextView) v.findViewById(R.id.row_news_date_text);
			rowNewsTypeText = (TextView) v.findViewById(R.id.row_news_type_text);
			rowNewsTitleText = (TextView) v.findViewById(R.id.row_news_title_text);
		}

		private void setFonts(){
			Typeface roboto = null;//TODO init this by utils
			rowNewsDateHeaderText.setTypeface(roboto);
			rowNewsDateText.setTypeface(roboto);
			rowNewsTypeText.setTypeface(roboto);
			rowNewsTitleText.setTypeface(roboto);
		}

		public void populateForm(String item) {

		}
	}
}
</code></pre>


