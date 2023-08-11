package com.prudhvi.spinwheel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private OnImageDownloadedListener listener;
    private static final String TAG = "ImageDownloader";
    public ImageDownloader(OnImageDownloadedListener listener) {
        this.listener = listener;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e(TAG, "Error downloading image: " + e.getMessage());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // Use the downloaded bitmap as needed (e.g., display it in an ImageView)
        if (listener != null) {
            listener.onImageDownloaded(bitmap);
        }
    }
    public interface OnImageDownloadedListener {
        void onImageDownloaded(Bitmap bitmap);
    }
}
