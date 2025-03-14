package com.example.pgerak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomActivity extends AppCompatActivity {

    private TextView userName, userEmail, userNim;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userNim = findViewById(R.id.user_nim);
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userEmail.setText(currentUser.getEmail()); // Menampilkan email langsung dari Firebase Auth
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        userName.setText(name);
                    } else {
                        Toast.makeText(HomActivity.this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(HomActivity.this, "Gagal mengambil data user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User belum login!", Toast.LENGTH_SHORT).show();
        }
        btn = findViewById(R.id.btn_logout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang akan dilakukan saat tombol diklik
                Toast.makeText(HomActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();

                // Logout pengguna dari Firebase Auth
                auth.signOut();

                // Pindah ke MainActivity setelah logout
                Intent intent = new Intent(HomActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Menghapus aktivitas sebelumnya dari stack
                startActivity(intent);
                finish(); // Menutup aktivitas saat ini
            }
        });
    }
}