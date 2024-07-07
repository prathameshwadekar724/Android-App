package com.example.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.R;
import com.example.android.adapters.LeaderBoardAdapter;
import com.example.android.adapters.PostAdapter;
import com.example.android.adapters.UserAdapter;
import com.example.android.adapters.UserAdapter2;
import com.example.android.databinding.ActivityMain2Binding;
import com.example.android.listeners.UserListeners;
import com.example.android.models.Post;
import com.example.android.models.User;
import com.example.android.utilities.Constants;
import com.example.android.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserListeners {
    private ActivityMain2Binding binding;
    private PreferenceManager preferenceManager;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView uname,user;

    private List<Post> originalPostList;
    private List<User> searchUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        getPost();
    }
    private void setListener(){
        drawerLayout = findViewById(R.id.lay_draw);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        uname = headerView.findViewById(R.id.fname);
        user = headerView.findViewById(R.id.fuser);

        toolbar = findViewById(R.id.toolb);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.home);
        checkForValidCredentials();

    }
    private void checkForValidCredentials(){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();


        if (firebaseUser==null){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        else {
            if (!firebaseUser.isEmailVerified()){
                showAlertDialog();
            }
            loading(true);
            showDetail();
        }
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }
    private void showDetail(){
        String id = preferenceManager.getString(Constants.KEY_ORG_NAME);
        String email = preferenceManager.getString(Constants.KEY_ORG_EMAIL);
        uname.setText(id);
        user.setText(email);
        loading(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent=null;
        if (id==R.id.home){

        } else if (id==R.id.post) {
            startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
        } else if (id==R.id.notification) {
            startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
        } else if (id==R.id.vList) {
            startActivity(new Intent(getApplicationContext(),ListActivity2.class));
        } else if (id==R.id.oList) {
            startActivity(new Intent(getApplicationContext(), ListActivity.class));
        } else if (id==R.id.profile) {
            startActivity(new Intent(getApplicationContext(), OrgProfileActivity.class));
        } else if (id==R.id.leaderboard) {
            startActivity(new Intent(getApplicationContext(),LeaderBoardActivity.class));
        } else if (id==R.id.logout) {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        }
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        if (menuItem != null) {
            menuItem.getIcon().setColorFilter(
                    ContextCompat.getColor(this, R.color.icon_color), PorterDuff.Mode.SRC_ATOP);
        }
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    restoreOriginalPostList();
                } else {
                    searchUsers(newText);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void searchUsers(String searchText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_COLLECTION);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_ORGANIZATION);
        Query query = reference.orderByChild(Constants.KEY_INTEREST);
        String searchTextLowercase = searchText.toLowerCase(Locale.getDefault());
        searchUserList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchUserList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user1 = dataSnapshot.getValue(User.class);
                    String FieldLowercase = user1.interest.toLowerCase(Locale.getDefault());
                    if (FieldLowercase.equals(searchTextLowercase)){
                        searchUserList.add(user1);
                        continue;
                    }
                    String nameLowerCase = user1.name.toLowerCase(Locale.getDefault());
                    if (nameLowerCase.equals(searchTextLowercase)){
                        searchUserList.add(user1);
                        continue;
                    }
                    String addressLowerCase = user1.address.toLowerCase(Locale.getDefault());
                    if (addressLowerCase.equals(searchTextLowercase)){
                        searchUserList.add(user1);
                        continue;
                    }
                    String locationLowerCase = user1.city.toLowerCase(Locale.getDefault());
                    if (locationLowerCase.equals(searchTextLowercase)){
                        searchUserList.add(user1);
                    }
                }
                if(!searchUserList.isEmpty()){
                    UserAdapter2 userAdapter2 = new UserAdapter2(searchUserList,MainActivity2.this,preferenceManager);
                    binding.postRecycle.setAdapter(userAdapter2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query2 = reference1.orderByChild(Constants.KEY_ORG_NAME);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchUserList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user1 = dataSnapshot.getValue(User.class);
                    String userName = user1.orgName.toLowerCase(Locale.getDefault());
                    String userLocation = user1.orgAddress.toLowerCase(Locale.getDefault());
                    if (userName.contains(searchTextLowercase) || userLocation.contains(searchTextLowercase)){
                        searchUserList.add(user1);
                    }
                }
                if (searchUserList.size()>0){
                    UserAdapter userAdapter = new UserAdapter(searchUserList,MainActivity2.this,preferenceManager);
                    binding.postRecycle.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getPost(){
        String id = preferenceManager.getString(Constants.KEY_ORG_ID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_POST_COLLECTION).child(Constants.KEY_POST_UPLOAD);
        loadPost(true);
        originalPostList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                originalPostList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String userId = dataSnapshot.child(Constants.KEY_ORG_ID).getValue(String.class);
                        if (userId.equals(id)){
                            Post post = dataSnapshot.getValue(Post.class);
                            originalPostList.add(post);
                        }

                    }
                    if (!originalPostList.isEmpty()){
                        PostAdapter postAdapter = new PostAdapter(originalPostList,MainActivity2.this,preferenceManager);
                        binding.postRecycle.setAdapter(postAdapter);
                        binding.postRecycle.setVisibility(View.VISIBLE);

                    }
                    loadPost(false);
                }else{
                    loadPost(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadPost(false);
            }
        });

    }
    private void restoreOriginalPostList() {
        if (originalPostList != null && !originalPostList.isEmpty()) {
            PostAdapter postAdapter = new PostAdapter(originalPostList, MainActivity2.this, preferenceManager);
            binding.postRecycle.setAdapter(postAdapter);
            binding.postRecycle.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (!searchUserList.isEmpty()) {
            restoreOriginalPostList();
        } else {
            super.onBackPressed();
        }
    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void loadPost(boolean isLoading){
        if (isLoading){
            binding.postProgress.setVisibility(View.VISIBLE);
        }else{
            binding.postProgress.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onUserClicked(User user) {
        String userType = preferenceManager.getString(Constants.USER_TYPE);
        if (Constants.USER_TYPE_VOLUNTEER.equals(userType)){
            Intent intent =new Intent(getApplicationContext(), DetailsActivity2.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        } else if (Constants.USER_TYPE_ORGANISATION.equals(userType)) {
            Intent intent =new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra(Constants.KEY_USER,user);
            startActivity(intent);
        }
    }
}