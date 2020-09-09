package com.stackdrive.architecture.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.stackdrive.architecture.R;
import com.stackdrive.architecture.adapters.NoteAdapter;
import com.stackdrive.architecture.models.Note;
import com.stackdrive.architecture.services.ApiBuilder;
import com.stackdrive.architecture.viewmodels.NoteViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        ApiBuilder.apiRequest().getPosts().enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                if (checkNetwork(MainActivity.this)){
                    Toast.makeText(MainActivity.this, "With internet", Toast.LENGTH_LONG).show();
                    if (!response.isSuccessful()){
                        fromRoom(adapter);
                        return;
                    }

                    adapter.setNotes(response.body());
                }else {
                    Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_LONG).show();
                    fromRoom(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Failed feting data", Toast.LENGTH_SHORT).show();
                fromRoom(adapter);
            }
        });
    }

    private void fromRoom(NoteAdapter adapter){
        noteViewModel = ViewModelProviders.of(MainActivity.this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(MainActivity.this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.setNotes(notes);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteAll:
                Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean checkNetwork(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = null;
        if (manager != null) netinfo = manager.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }
}