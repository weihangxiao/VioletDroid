package com.github.dilyar85.violetdroid;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login Activity to allow users log in with their accounts
 */
public class LoginActivity extends Activity {

    static final  String LOG_TAG = LoginActivity.class.getSimpleName();

    public static final String EXTRA_USER_NAME = "userName";
    public static final String EXTRA_USER_PASSWORD = "userPassword";


    @BindView(R.id.log_in_username_editText)
    EditText mUserNameEditText;
    @BindView(R.id.log_in_password_editText)
    EditText mPasswordEditText;

    public String inputUserName;
    public String inputPassword;

    private AlertDialog verifiedResultDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.log_in_button)
    public void clickLogInButton() {

        if(checkEditTextBlank()) {
            showLoadingDialog();
            verifyAccount();
        }

    }



    private boolean checkEditTextBlank() {

        inputUserName = mUserNameEditText.getText().toString();
        inputPassword = mPasswordEditText.getText().toString();

        if (inputUserName.length() <=0) {
            showToastShort(R.string.toast_username_blank);
            return false;
        }
        if (inputPassword.length()<=0) {
            showToastShort(R.string.toast_password_blank);
            return false;
        }
        return true;
    }



    @OnClick(R.id.log_in_sign_up_textView)
    public void clickSignUpTextView() {

        startActivity(new Intent(this, SignUpActivity.class));
    }






    private void verifyAccount() {

        AVUser.logInInBackground(inputUserName, inputPassword, new LogInCallback<AVUser>() {

            @Override
            public void done(AVUser avUser, AVException e) {
                verifiedResultDialog.dismiss();
                if (e == null) {
                    showToastShort(R.string.toast_login_successfully);
                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1000);
                } else {

                    String originalMessage = e.getMessage();
                    int startIndexOfError = originalMessage.lastIndexOf("error") + 7;
                    String errorMessage = originalMessage.substring(startIndexOfError, originalMessage.length() - 1);
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Fail to Log In. Full Message from Cloud: " + e.getMessage());

                }
            }
        });

    }



    private void showLoadingDialog() {

        verifiedResultDialog = new AlertDialog.Builder(this).create();
        ImageView loadingImageview = new ImageView(this);
        loadingImageview.setBackgroundResource(R.drawable.loading_anim);
        verifiedResultDialog.setView(loadingImageview);
        verifiedResultDialog.setCancelable(false);
        verifiedResultDialog.show();
        AnimationDrawable loadingAnimationDrawable = (AnimationDrawable) loadingImageview.getBackground();
        loadingAnimationDrawable.start();
        WindowManager.LayoutParams lp = verifiedResultDialog.getWindow().getAttributes();
        lp.height = 500;
        lp.width = 500;
        verifiedResultDialog.getWindow().setAttributes(lp);
    }



    private void showToastShort(int stringResource) {
        Toast.makeText(this, getString(stringResource), Toast.LENGTH_SHORT).show();
    }



}
