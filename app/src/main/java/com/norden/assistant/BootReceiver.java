<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#140A14">

    <ImageView
        android:id="@+id/character"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="fitCenter"
        android:src="@drawable/character" />

    <Switch
        android:id="@+id/powerSwitch"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="top|end"
        android:layout_margin="16dp"
        android:text="Norden"
        android:textColor="#FFFFFF"
        android:textSize="18sp" />
</FrameLayout>
