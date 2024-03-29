package com.nazmusashrafi.multi_timer_2_master;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoggedInSettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private DatabaseReference reference;
    private DatabaseReference referencedel;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private Spinner spinnerSoundSelector;


    String valueFromSpinner;

    //sounds
    MediaPlayer bellSound;
    MediaPlayer chilledSound;
    MediaPlayer softSound;
    MediaPlayer gearSound;
    MediaPlayer batmanSound;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logged_in_settings, container, false);

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        onlineUserID = mUser.getUid();

        Button logoutButton = (Button) view.findViewById(R.id.logOutButton);
        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        Button deleteAccountButton = (Button) view.findViewById(R.id.deleteAccountButton);
        final TextView wordCounter = view.findViewById(R.id.tvCounter);

        final LinearLayout emailTextView = view.findViewById(R.id.emailInput);
        final LinearLayout passwordTextView = view.findViewById(R.id.passwordInput);
        final LinearLayout nameTextView = view.findViewById(R.id.nameInput);
        final LinearLayout soundSpinnerView = view.findViewById(R.id.soundInput);

        final EditText mEmailText = view.findViewById(R.id.editTextTextEmailAddress);
        final EditText mPasswordText = view.findViewById(R.id.editTextTextPassword);
        final EditText mNameText = view.findViewById(R.id.editTextTextName);
        final ProgressBar progressBar = view.findViewById(R.id.progressBarSettingsPage);

        //word counter viewer
        mNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                wordCounter.setText(25 - s.toString().length() + "/25");

            }
        });

        //----


        //sound-----
        spinnerSoundSelector = view.findViewById(R.id.spinnerSoundSelector); //spinnerTextSize

        spinnerSoundSelector.setOnItemSelectedListener(this);

        String[] sounds = getResources().getStringArray(R.array.sound_selections); //textSizes

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, sounds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSoundSelector.setAdapter(adapter);

        spinnerSoundSelector.setVisibility(View.INVISIBLE);


        //

        //sounds
        bellSound = MediaPlayer.create(getActivity(), R.raw.bell);
        chilledSound = MediaPlayer.create(getActivity(), R.raw.chilled);
        softSound = MediaPlayer.create(getActivity(), R.raw.soft);
        gearSound = MediaPlayer.create(getActivity(), R.raw.gear);
        batmanSound = MediaPlayer.create(getActivity(), R.raw.batman);


        //------



        reference.child(onlineUserID).child("sound").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null){
                    spinnerSoundSelector.setVisibility(View.VISIBLE);
                    spinnerSoundSelector.setSelection(0);

                }else{
                    System.out.println("data snap " + dataSnapshot.getValue());
                    spinnerSoundSelector.setVisibility(View.VISIBLE);

                    if(dataSnapshot.getValue().equals("Bell")){
                        spinnerSoundSelector.setSelection(0);
                    }else if(dataSnapshot.getValue().equals("bell")){
                        spinnerSoundSelector.setSelection(0);
                    }else if(dataSnapshot.getValue().equals("Gear")){
                        spinnerSoundSelector.setSelection(1);
                    }else if(dataSnapshot.getValue().equals("Batman")){
                        spinnerSoundSelector.setSelection(2);
                    }else if(dataSnapshot.getValue().equals("Chilled")){
                        spinnerSoundSelector.setSelection(3);
                    }else if(dataSnapshot.getValue().equals("Soft")){
                        spinnerSoundSelector.setSelection(4);
                    }
                }

                //




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        //----------------

        //if anym
        if(mUser.isAnonymous()){

            progressBar.setVisibility(View.INVISIBLE);

            //layout changes
            logoutButton.setText("Link this account"); // sign up to link account
            nameTextView.setVisibility(View.INVISIBLE); //enter name to invisible
            saveButton.setVisibility(View.INVISIBLE); //save button to invisible
            soundSpinnerView.setVisibility(View.INVISIBLE); //sound chooser to invisible
            deleteAccountButton.setVisibility(View.INVISIBLE);//delete account to invisible

            //-----------------

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String email = mEmailText.getText().toString().trim();
                    final String password = mPasswordText.getText().toString().trim();

                    // checks
                    if(TextUtils.isEmpty(email)){
                        mEmailText.setError("Email is required");
                        mEmailText.requestFocus();
                        return;
                    }

                    else if(TextUtils.isEmpty(password)){
                        mPasswordText.setError("Password is required");
                        mPasswordText.requestFocus();
                        return;
                    }

                    else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) ){
                        mEmailText.setError("Email is required");
                        mPasswordText.setError("Password is required");
                        mEmailText.requestFocus();

                    }

                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        mEmailText.setError("Please provide valid email");
                        mEmailText.requestFocus();
                        return;

                    }

                    if(password.length()<6){
                        mPasswordText.setError("Password should be atleast 6 characters long");
                        mPasswordText.requestFocus();
                        return;
                    }

                    //----

                    AuthCredential credential = EmailAuthProvider.getCredential(email,password);

                    if(mUser!=null){
                        //progress bar visible

                        try {
                            mUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("email")
                                                .setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){


//                                                Toast.makeText(RegistrationActivity.this,"You have been registered successfully",Toast.LENGTH_LONG).show();
//                                                progressBar.setVisibility(View.GONE);
//
//                                                //redirect to login layout

                                                }else{

//                                                Toast.makeText(RegistrationActivity.this,"Failed to register, try again",Toast.LENGTH_LONG).show();
//                                                progressBar.setVisibility(View.GONE);

                                                }

                                            }
                                        });


                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("password")
                                                .setValue(password);

                                        //sounds initialization
                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("sound")
                                                .setValue("Bell");


                                        //

                                        FirebaseAuth.getInstance().signOut();

                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        startActivity(intent);

                                        Toast.makeText(getActivity(),"Account linked, please log in using new credentials",Toast.LENGTH_LONG).show();


                                    }else{


                                        Toast.makeText(getActivity(),"Error linking account",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        }catch (Error e){
                            System.out.println("error isss "+e);
                        }

                    }



                }
            });


        }else{ //if not anym

            //

//            mEmailText.setText(mUser.getEmail());

            //layout changes

            emailTextView.setVisibility(View.INVISIBLE); //enter email to invisible
            passwordTextView.setVisibility(View.INVISIBLE); //enter passowrd to invisible

            //---------

            passwordTextView.setVisibility(View.INVISIBLE);


            reference.child(onlineUserID).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);

                    if(dataSnapshot.getValue()==null){
                        reference.child(onlineUserID).child("name").setValue("");
                        mNameText.setText("");

                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        mNameText.setText(dataSnapshot.getValue().toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            view.findViewById(R.id.logOutButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseAuth.getInstance().signOut();

                    Intent intent;
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                }
            });



            reference.child(onlineUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //Save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(valueFromSpinner);

                    progressBar.setVisibility(View.VISIBLE);

                    //push valueFromSpinner to db
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("sound")
                            .setValue(valueFromSpinner).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);

                                Toast.makeText(getActivity(),"Settings saved",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    //---

                    //push email to db
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("name")
                            .setValue(mNameText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);

                                Toast.makeText(getActivity(),"Settings saved",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    //---
                }
            });

        }


        //delete account button

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // are you sure you want to delete
                final String[] colors = {"Yes", "No"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleting the account will result in completely removing your account from the system.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]

                        progressBar.setVisibility(View.VISIBLE);


                            System.out.println("Delete account");

                        //delete info from firebase
                        //REFERENCE---------

                        referencedel = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);

                        referencedel.removeValue();
                        referencedel.setValue(null);

                        //-----


                            //delete from authentication
                            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressBar.setVisibility(View.GONE);

                                    if(task.isSuccessful()){


                                        Intent intent;
                                        intent = new Intent(getActivity(), LoginActivity.class);

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        getActivity().startActivity(intent);

                                        Toast.makeText(getActivity(),"Your account has been deleted",Toast.LENGTH_LONG).show();


                                    }else{
                                        Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.out.println("No");
                    dialogInterface.dismiss();

                }
            });
                builder.setTitle( Html.fromHtml("<font color='#63c1e8'>Are you sure?</font>"));

                builder.show();

            }
        });


    }


    int counterSound = 0;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView.getId() == R.id.spinnerSoundSelector){

            valueFromSpinner = adapterView.getItemAtPosition(position).toString();
            
            //play sound on selected
            if(counterSound>1){

                if(position==2){
                    batmanSound.start();
                }else if(position==3){
                    chilledSound.start();
                }else if(position==4){
                    softSound.start();
                }else if(position==1){
                    gearSound.start();
                }else if(position==0){
                    bellSound.start();
                }


            }

            counterSound++;




        }




    }

    @Override
    public void onNothingSelected(final AdapterView<?> adapterView) {


    }
}


//logout on press of back button after anym sign in