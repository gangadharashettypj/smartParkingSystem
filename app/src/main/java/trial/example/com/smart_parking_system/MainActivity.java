package trial.example.com.smart_parking_system;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import trial.example.com.smart_parking_system.startpage.choose;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    private final int requestCode = 20;
    private StorageReference sRef;
    private ProgressDialog pd;
    private DatabaseReference mData,mCarsData;
    private RecyclerView mCarsRecycler;
    private Spinner slotSpinner;
    private ArrayAdapter arrayAdapter;
    private List<String> slots=null,slotsNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //===================================

        mAuth=FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference();
        mData.keepSynced(true);
        mCarsData= FirebaseDatabase.getInstance().getReference().child("cars");
        sRef=FirebaseStorage.getInstance().getReference().child("cars");




        pd=new ProgressDialog(this);
        pd.setTitle("Uploading...");
        pd.setMessage("Please wait...");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewCar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);

            }
        });

        mCarsRecycler=findViewById(R.id.carsRecycler);
        final GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),2);
        mCarsRecycler.setLayoutManager(gridLayoutManager);

        mCarsRecycler.setHasFixedSize(true);

        final FirebaseRecyclerAdapter<rowCars,carsViewHolder> firebaseAdapter=new FirebaseRecyclerAdapter<rowCars, carsViewHolder>(
                rowCars.class,
                R.layout.row_cars,
                carsViewHolder.class,
                mCarsData.child("sit")
        ) {
            @Override
            protected void populateViewHolder(final carsViewHolder viewHolder, final rowCars model, final int position) {
                getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.hasChild("flag")){

                            viewHolder.setData(model.getSlot(),model.getName());
                            viewHolder.setImage(model.getUrl(),getApplicationContext());
                            viewHolder.mView.findViewById(R.id.btDelete).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    HashMap<String,String> map=new HashMap<>();
                                    mCarsData.child("slots").child(model.getSlot()).setValue("true");
                                    map= (HashMap<String, String>) dataSnapshot.getValue();
                                    mCarsData.child("sit_backup").push().setValue(map);
                                    mCarsData.child("sit").child(dataSnapshot.getKey()).setValue(null);
                                    Log.i("mainlogic",dataSnapshot.getKey());

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mCarsRecycler.setAdapter(firebaseAdapter);






    }


    public static class carsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public carsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setData(String num, String car)
        {
            TextView sl=mView.findViewById(R.id.tvnum);
            TextView ca=mView.findViewById(R.id.tvCar);
            sl.setText(num);
            ca.setText(car);
        }

        public void setImage(final String url,Context c){
            final ImageView im=mView.findViewById(R.id.ivCar);
            Picasso.with(c).load(url).placeholder(R.drawable.bg13).networkPolicy(NetworkPolicy.OFFLINE).into(im, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mView.getContext()).load(url).placeholder(R.drawable.bg13).into(im);
                }
            });

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){

            fnAddCar(data);


            //String partFilename = currentDateFormat();
            //storeCameraPhotoInSDCard(bitmap, partFilename);

            // display the image from SD Card to ImageView Control
            //String storeFilename = "photo_" + partFilename + ".jpg";
            //Bitmap mBitmap = getImageFileFromSDCard(storeFilename);

        }
    }




    @SuppressLint("CutPasteId")
    public void fnAddCar(Intent data){
        final Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] bytedata = baos.toByteArray();


        View view=View.inflate(getApplicationContext(),R.layout.cars_details_layout,null);
        final Dialog dialog=new Dialog(this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        slotSpinner=view.findViewById(R.id.slotSpinner);
        mData.child("cars").child("slots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,String> map=new HashMap<String, String>();
                map= (HashMap<String, String>) dataSnapshot.getValue();
                slots=new ArrayList<String>(map.keySet());
                arrayAdapter =new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,slots);

                arrayAdapter.notifyDataSetChanged();
                slotSpinner.setAdapter(arrayAdapter);
                slotSpinner.notifyAll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        ImageView imageHolder=view.findViewById(R.id.imageV);
        imageHolder.setImageBitmap(bitmap);

        final EditText vehicleno = view.findViewById(R.id.VehicleNo);
        final EditText mobile = view.findViewById(R.id.Mobile);
        final EditText name = view.findViewById(R.id.Name);
        Button btsave=view.findViewById(R.id.btSave);
        final Spinner slot=view.findViewById(R.id.slotSpinner);


        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                final HashMap<String,String> usermap=new HashMap<>();


                UploadTask uploadTask = sRef.child(currentDateFormat()+".jpg").putBytes(bytedata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        usermap.put("vehicleno",vehicleno.getText().toString());
                        usermap.put("mobile",mobile.getText().toString());
                        usermap.put("name",name.getText().toString());
                        usermap.put("url",downloadUrl.toString());
                        usermap.put("slot",slot.getSelectedItem().toString());
                        mCarsData.child("slots").child(slot.getSelectedItem().toString()).setValue(null);
                        mData.child("cars").child("sit").push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Toast.makeText(getApplicationContext(),"Car added sucessfully...",Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        });


        dialog.show();
    }

    @SuppressLint("NewApi")
    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate){
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap getImageFileFromSDCard(String filename){
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this,choose.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            mAuth.signOut();
            if(mAuth.getCurrentUser()==null){
                startActivity(new Intent(MainActivity.this,choose.class));
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
