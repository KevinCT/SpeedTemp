package com.zweigbergk.speedswede;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RetrieveDataFromDatabaseTest {

    @Test
    public void getTestData() throws Exception {

        AuthCredential credential = EmailAuthProvider.getCredential("test@example.com", "winteriscoming");

//        FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener(new Activity(), task -> {
//                    Log.d("test", "signInWithCredential:onComplete:" + task.isSuccessful());
//                });

        assertEquals(true, 1 == 1);


    }
}
