package com.labprog.closer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> images;
    private Context context;
    private int userId; // Assuming userId is available in the adapter

    public ImageAdapter(List<Image> imageUrlList, Context context, int userId) {
        this.images = imageUrlList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image base64Image = images.get(position);
        byte[] decodedString = Base64.decode(base64Image.getImageUrl(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageView.setImageBitmap(decodedByte);
        holder.usernameTextView.setText(base64Image.getUsername());

        holder.likeButton.setOnClickListener(v -> {
            sendRatingToServer(base64Image.getImageId(), "Like");
        });

        holder.dislikeButton.setOnClickListener(v -> {
            sendRatingToServer(base64Image.getImageId(), "Dislike");
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        ImageView imageView;
        Button likeButton;
        Button dislikeButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
        }
    }

    private void sendRatingToServer(int imageId, String rating) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8080/closer_war_exploded/post-rating";

        String json = "{\"imageId\":" + imageId + ",\"userId\":" + userId + ",\"rating\":\"" + rating + "\"}";
        Log.d("ImageAdapter", "Sending JSON: " + json); // Log the JSON payload
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Failed to post rating");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("ImageAdapter", "Response code: " + response.code()); // Log the response code
                Log.d("ImageAdapter", "Response body: " + response.body().string()); // Log the response body

                if (response.isSuccessful()) {
                    showToast("Rating posted successfully");
                } else {
                    showToast("Failed to post rating");
                }
            }
        });
    }

    private void showToast(final String message) {
        ((GroupActivity) context).runOnUiThread(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
