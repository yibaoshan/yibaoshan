package com.android.designpattern.structural.bridge;

import android.widget.ImageView;

public interface IImageLoader {

    void loadUrlIntoImageView(ImageView view, String url);

}
