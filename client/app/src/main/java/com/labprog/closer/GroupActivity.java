package com.labprog.closer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private TextView groupNameDisplay;
    private Button inviteToGroupButton;
    private Button fabCamera;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Image> imageUrlList;
    private int groupId;
    private String groupName;
    private String userEmail;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupNameDisplay = findViewById(R.id.group_name_display);
        inviteToGroupButton = findViewById(R.id.invite_to_group_button);
        fabCamera = findViewById(R.id.fabCamera);
        recyclerView = findViewById(R.id.recyclerView);

        imageUrlList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrlList);

        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        groupId = getIntent().getIntExtra("GROUP_ID", -1);
        groupName = getIntent().getStringExtra("GROUP_NAME");
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        groupNameDisplay.setText(groupName);

        inviteToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupActivity.this, InviteToGroupActivity.class);
                intent.putExtra("GROUP_ID", groupId);
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        fetchImagesFromServer(groupId);

        // Initialize the ActivityResultLauncher
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getExtras() != null) {
                                Bitmap photo = (Bitmap) data.getExtras().get("data");
                                if (photo != null) {
                                    sendPhotoToServer(photo);
                                }
                            }
                        }
                    }
                }
        );
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(takePictureIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permissão para usar a câmera foi negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendPhotoToServer(Bitmap photo) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        OkHttpClient client = new OkHttpClient();

        String json = new Gson().toJson(new PhotoMessage(groupId, encodedImage, userEmail));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/closer_war_exploded/post-message")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Erro ao enviar a foto", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Foto enviada com sucesso", Toast.LENGTH_SHORT).show());
                    fetchImagesFromServer(groupId);
                } else {
                    runOnUiThread(() -> Toast.makeText(GroupActivity.this, "Erro ao enviar a foto", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    public void fetchImagesFromServer(Integer group_id) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/closer_war_exploded/get-messages-by-group-id?group_id=" + group_id)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Image>>(){}.getType();
                    List<Image> messages = gson.fromJson(json, listType);

                    imageUrlList.clear();
                    for (Image message : messages) {
                        imageUrlList.add(message);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    class Message {
        private int groupId;
        private String imageUrl;
        private int userId;

        public int getGroupId() {
            return groupId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getUserId() {
            return userId;
        }
    }

    class PhotoMessage {
        private int groupId;
        private String imageUrl;
        private String email;

        public PhotoMessage(int groupId, String imageUrl, String email) {
            this.email = email;
            this.groupId = groupId;
            this.imageUrl = imageUrl;
        }

        public int getGroupId() {
            return groupId;
        }

        public String getImage() {
            return imageUrl;
        }

        public String getUserEmail() {
            return userEmail;
        }
    }
}
