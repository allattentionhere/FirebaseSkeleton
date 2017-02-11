package com.allattentionhere.show.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allattentionhere.show.MyApplication;
import com.allattentionhere.show.R;
import com.allattentionhere.show.model.User;
import com.allattentionhere.show.util.Extras;
import com.allattentionhere.show.util.SharedPrefsUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.data;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;
    @BindView(R.id.facebook_login)
    LoginButton facebook_login;

    @BindView(R.id.google_login)
    SignInButton google_login;
    @BindView(R.id.pb)
    ProgressBar pb;

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    CallbackManager mCallbackManager;

    GoogleApiClient mGoogleApiClient;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        if (!SharedPrefsUtil.getString(this, Extras.PREFS_FIREBASE_USERID).isEmpty()) {
            //logged in user
            goToMainActivity();

        } else {
            //facebook
            mCallbackManager = CallbackManager.Factory.create();
            facebook_login.setReadPermissions("email", "public_profile");
            facebook_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    Log.d("result", "fb login: " + MyApplication.getGson().toJson(loginResult));
                    setFacebookData(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                }
            });

            //google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, LoginActivity.this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            google_login.setSize(SignInButton.SIZE_STANDARD);


            //general
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                mUser = new User(account.getId(), account.getEmail(), account.getPhotoUrl().toString(), account.getGivenName(), account.getFamilyName());
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                hideProgress();
            }

        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        showProgress();
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgress();
                        } else {
                            //login success
                            Log.d("result", "providerId: " + task.getResult().getUser().getProviders().get(0));
                            registerUserOnFirebase(task.getResult().getUser().getUid(), task.getResult().getUser().getProviders().get(0));

                        }

                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgress();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgress();
                        } else {
                            Log.d("result", "providerId: " + task.getResult().getUser().getProviders().get(0));
                            registerUserOnFirebase(task.getResult().getUser().getUid(), task.getResult().getUser().getProviders().get(0));
                        }
                    }
                });
    }

    private void registerUserOnFirebase(String uid, String provider) {
        mUser.setFirebase_userid(uid);
        mUser.setLogin_provider(provider);
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            mUser.setPush_token(token);
        }
        mUser.setTime_stamp(ServerValue.TIMESTAMP);
        //set SharedPrefs
        SharedPrefsUtil.saveString(this, Extras.PREFS_LOGINPROVIDER_ID, mUser.getLoginprovider_id());
        SharedPrefsUtil.saveString(this, Extras.PREFS_EMAIL, mUser.getEmail());
        SharedPrefsUtil.saveString(this, Extras.PREFS_PROFILE_IMAGE, mUser.getProfileImageUrl());
        SharedPrefsUtil.saveString(this, Extras.PREFS_FIRST_NAME, mUser.getFirst_name());
        SharedPrefsUtil.saveString(this, Extras.PREFS_LAST_NAME, mUser.getLast_name());
        SharedPrefsUtil.saveString(this, Extras.PREFS_LOGIN_PROVIDER, mUser.getLogin_provider());
        SharedPrefsUtil.saveString(this, Extras.PREFS_PUSH_TOKEN, mUser.getPush_token());
        SharedPrefsUtil.saveString(this, Extras.PREFS_FIREBASE_USERID, mUser.getFirebase_userid());

        //push user data to firebase
        DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference();
        mDbRef.child(Extras.FIREBASE_DATABASE_USERS).child(mUser.getFirebase_userid()).setValue(mUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                goToMainActivity();
            }
        });


    }

    @OnClick(R.id.google_login)
    public void onGoogleLoginClick() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        hideProgress();

    }

    private void setFacebookData(final LoginResult login_result) {
        GraphRequest request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        if (response.getJSONObject() != null) {
                            try {
                                String email = response.getJSONObject().getString("email");
                                String firstName = response.getJSONObject().getString("first_name");
                                String lastName = response.getJSONObject().getString("last_name");
                                Profile profile = Profile.getCurrentProfile();
                                String id = profile.getId();
                                String image = "";
                                if (Profile.getCurrentProfile() != null) {
                                    image = Profile.getCurrentProfile().getProfilePictureUri(500, 500).toString();
                                }
                                mUser = new User(id, email, image, firstName, lastName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideProgress();
                            }
                            handleFacebookAccessToken(login_result.getAccessToken());
                        } else {
                            hideProgress();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void showProgress() {
        facebook_login.setVisibility(View.GONE);
        google_login.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        facebook_login.setVisibility(View.VISIBLE);
        google_login.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);

    }

    public void goToMainActivity() {
//        hideProgress();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void signout() {
        FirebaseAuth.getInstance().signOut();
    }


}

