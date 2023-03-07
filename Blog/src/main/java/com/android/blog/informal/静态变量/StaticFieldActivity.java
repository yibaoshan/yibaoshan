package com.android.blog.informal.静态变量;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blog.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

public class StaticFieldActivity extends AppCompatActivity {

    private TextView tvClassLoader;
    private TextView tvReference;

    private DexClassLoader mDexClassLoader;
    private WeakReference<Field> mStaticFieldReference = new WeakReference<>(null);
    private Field mField ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_field);
        tvClassLoader = findViewById(R.id.tv_classloader);
        tvReference = findViewById(R.id.tv_reference);
        updateStatus();
    }

    public void onInstallClassLoaderClick(View view) {
        try {
            String dexPath = getFilesDir().getAbsolutePath() + "/classes.dex";
            mDexClassLoader = new DexClassLoader(dexPath, null, null, getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateStatus();
        if (mDexClassLoader != null) Toast.makeText(this, "dex class loader install succeed", Toast.LENGTH_SHORT).show();
    }

    public void onToastStaticFieldClick(View view) {
        String toast = null;
        try {
            if (mDexClassLoader != null) {
                Field field = mDexClassLoader.loadClass("com.java.library.Test").getField("MyDream");
                mStaticFieldReference = new WeakReference<>(field);
                mField = field;
            }
//            toast = mStaticFieldReference.get() == null ? "null" : (String) mStaticFieldReference.get().get(null);
            toast = mField == null ? "null" : (String) mField.get(null);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        updateStatus();
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void onUninstallClassLoaderClick(View view) {
        mDexClassLoader = null;
        Runtime.getRuntime().gc();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException var2) {
            throw new AssertionError();
        }
        System.runFinalization();
        updateStatus();
        Toast.makeText(this, "dex class loader uninstall succeed", Toast.LENGTH_SHORT).show();
    }

    private void updateStatus() {
        tvClassLoader.setText("mDexClassLoader ：\n\n" + mDexClassLoader);
        tvReference.setText("mStaticFieldReference ：\n\n " + mStaticFieldReference.get());
    }
}
