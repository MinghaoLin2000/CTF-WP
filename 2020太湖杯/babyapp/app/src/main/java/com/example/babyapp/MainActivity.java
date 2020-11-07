package com.example.babyapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private EditText mUser;
    private EditText mCode;
    private Button mRegisterButton;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mUser = (EditText) findViewById(R.id.login_edit_account);
            mCode = (EditText) findViewById(R.id.login_edit_code);
            mRegisterButton = (Button) findViewById(R.id.login_btn_register);
            mRegisterButton.setOnClickListener(mListener);
    }
    private int intcmp(int a[],int b[]){
        int c =1;
        for(int i = 0; i<a.length; i++){
            if(a[i]!=b[i]){
                c=0;
            }
        }
        return c;
    }

    private int strcmp(String a,String b){
        int c =1;
        for(int i = 0; i<a.length(); i++){
            if(a.charAt(i)!=b.charAt(i)){
                c=0;
            }
        }
        return c;
    }

    OnClickListener mListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:
                    String userName = mUser.getText().toString().trim();
                    String userCode = mCode.getText().toString().trim();
                    Base64Encode b64User = new Base64Encode(userName);
                    Base64Encode b64Code = new Base64Encode(userCode);
                    b64User.encode();
                    b64Code.encode();
                    String cipherUser = b64User.getCipher();
                    String cipherCode = b64Code.getCipher();
                    if(userCode.length()!=32){
                        Toast.makeText(getApplicationContext(),"error,you length is error",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(strcmp(cipherUser,"#czl$A==")==0){
                        Toast.makeText(getApplicationContext(),"error,you username is error",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    int l=cipherCode.length();
                    int s[]=new int[l];
                    if(Encrypto.encrypto2(cipherUser,cipherCode)==1){
                        Toast.makeText(getApplicationContext(),"sucess,your flag is flag{your input code}",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"error,you code is error",Toast.LENGTH_SHORT).show();
                        break;
                    }


            }
        }
    };
}

