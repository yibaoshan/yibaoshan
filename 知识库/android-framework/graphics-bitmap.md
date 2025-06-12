
在 Android 中，JPG/PNG/WebP 等位图资源，最终都会转为 Bitmap 显示。

# 内存占用计算

Bitmap 占用的大小和图片磁盘占用大小无关，和 imageview 的宽高也无关，几个很小的图片，也可以配上一个很大的 iv 空间

取决于：

- Bitmap#Config 的颜色配置，它决定每个像素占用多大空间，常用 888 = 4 bytes，565  = 2 bytes
- 图片的尺寸，宽*高 得到的 像素总和
- 采样率 inSampleSize，向下取整的 2 的幂次方
- 总的内存占用 = 宽/inSampleSize * 高/inSampleSize * bytes_per_pixel

如果直接使用 decodeXXX 直接加载一张图片到内存，没有任何的优化方案，很有可能触发 OOM。

以 100 * 100 的图片举例，如果不设置 inSampleSize，getWidth()/getHeight() 获取宽高，就是图片的像素大小

此时占用的内存大小取决于 Options 的颜色配置

- Bitmap.Config.ARGB_8888
  - ARGB，每通道 8 bits，共 32 bits = 4 字节
  - 100 * 100 * 4 = 40,000 bits = 39 KB
- Bitmap.Config.RGB_565，无 Alpha 通道
  - R 5 bits，G 6 bits，B 5 bits，总共 16 bits = 2 字节
  - 100 * 100 * 2 = 20,000 bits = 19.5 KB

优化，Android 8.0 及以后，bitmap 内存被分配到 native，不受 jvm 的 256/512 的限制

Android 8.0 以下的设备可以参考字节的 mSponge 方案

# 基本使用

一些常用的 API 介绍

- Bitmap
  - getWidth/getHeight，获取图片的宽高
  - createBitmap()，创建空白 bitmap
  - getConfig，Bitmap 的像素配置（如 ARGB_8888, RGB_565）
  - getRowBytes，返回 Bitmap 的每一行像素所占用的字节数。通常是 getWidth() * bytes_per_pixel
  - getByteCount，占用的总字节数 getHeight() * getRowBytes()
  - hasAlpha，检查 Bitmap 是否包含 Alpha 通道（即是否支持透明度）
  - isRecycled，是否已经被回收
  - compress，将 Bitmap 压缩到指定的输出流中
  - recycle，回收 Bitmap
- BitmapFactory，加载、接码
  - decodeFile、decodeResource、decodeByteArray、decodeStream、decodeFileDescriptor
- BitmapFactory.Options
  - inJustDecodeBounds，true 表示只读取图片文件的头信息，图片的宽度、高度和 MIME 类型
  - outWidth，outHeight，配合  inJustDecodeBounds 使用，读取原始宽高用
  - inSampleSize，采样率，重要，宽高都会除掉该值，并且总是向下取整的 2 的幂次方
  - inBitmap，如果你设置了 inBitmap 值，并解码成功，那该 bitmap 用的就是 inBitmap 的值， 原先 inBitmap 的数据会被覆盖，一般用于 bitmap 复用池，比如 glide

使用采样率 inSampleSize 来降低图片的像素大小，减少内存占用

1. 首次解码（读取图片宽高），将 BitmapFactory.Options 的 inJustDecodeBounds 设置为 true。然后调用 BitmapFactory.decode* 方法。此时，方法不会返回一个 Bitmap 对象，但会填充 options.outWidth 和 options.outHeight，让你知道图片的原始尺寸。
2. 配合控件大小计算 inSampleSize 的值，根据获取到的原始图片尺寸（options.outWidth, options.outHeight）和你想要加载的目标尺寸（例如 ImageView 的宽高），计算出合适的 inSampleSize 值。目标是找到最小的 inSampleSize，使得缩放后的图片尺寸大于或等于目标显示尺寸。
3. 二次解码，第二次解码（加载缩放后的像素）： 将 options.inJustDecodeBounds 设置为 false，并设置计算好的 inSampleSize 值。再次调用 BitmapFactory.decode* 方法，这次它会返回一个缩放后的 Bitmap 对象。
