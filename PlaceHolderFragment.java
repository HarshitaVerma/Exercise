package com.harshita.goolglesigninout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Teaspl-1 on 25-08-2016.
 */
public class PlaceHolderFragment extends Fragment implements View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    SignInButton signInButton;
    Button buttonSignout;
    TextView mStatusTextView,id,email;
    NetworkImageView img;
    ImageLoader imageLoader;
    View rv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rv = inflater.inflate(R.layout.activity_placeholder, container, false);


        // Step1:Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Step2:Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
               /* .enableAutoManage(this.getActivity()*//*  FragmentActivity*//*,)*//* OnConnectionFailedListener*/
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return rv;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Step3: Customizing Sign in Button
     signInButton = (SignInButton) rv.findViewById(R.id.sign_in_button);
 mStatusTextView=(TextView)rv.findViewById(R.id.textView);
        id=(TextView)rv.findViewById(R.id.textView2);
        email=(TextView)rv.findViewById(R.id.textView4);
        img=(NetworkImageView)rv.findViewById(R.id.imageView);
        buttonSignout=(Button) rv.findViewById(R.id.button);
        buttonSignout.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == signInButton)

        {
            signIn();
        }
        if (view == buttonSignout)

        {
            if (mGoogleApiClient.isConnected())
            signOut();
            else
                Toast.makeText(this.getActivity(),"You Are Not Logged In",Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mStatusTextView.setText("OnREsult");
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                        revokeAccess();
                    }

                    private void revokeAccess() {
                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                                        mStatusTextView.setText("REVOKE");
                                    }
                                });
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    //After you retrieve the sign-in result, you can check if sign-in succeeded with the isSuccess method.
    // If sign-in succeeded, you can call the getSignInAccount method
    // to get a GoogleSignInAccount object that contains information about the signed-in user, such as the user's name.
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            mStatusTextView.setText( personName);
            id.setText( personId);
            email.setText( personEmail);
            //set Profile Picture
            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(getActivity().getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(img,
                            R.mipmap.ic_launcher,
                            R.mipmap.ic_launcher));

            //Loading image
            //Loading image
            img.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);


            updateUI(true);
          mGoogleApiClient.connect();
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }

    }

    private void updateUI(boolean b) {
    }
}