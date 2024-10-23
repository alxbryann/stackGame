package org.example;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Firebase {

    Firestore firestore;

    public void createConnection(){
        try{
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream("firebaseCon.json");
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(is);
            @SuppressWarnings("deprecation")
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(googleCredentials).build();
            FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore();

        } catch (RuntimeException | IOException e) {

        }
    }

    public void insertData(String collection, Map<String, Object> data) {
        try {
            if (firestore != null) {
                ApiFuture<DocumentReference> result = firestore.collection(collection).add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insertData(String colection, String document, Map<String, Object> data){
        try{
            if(firestore != null){
                DocumentReference docref = firestore.collection(colection).document(document);
                ApiFuture<WriteResult> result = docref.set(data);
                System.out.println("" + result.get().getUpdateTime());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    public String getBestScore(){
        String bestScore = " ";
        try {
            if (firestore != null) {
                ApiFuture<QuerySnapshot> result = firestore.collection("bestScore")
                        .whereEqualTo("bestScore", "0").get();

                List<QueryDocumentSnapshot> documents = result.get().getDocuments();
                if (!documents.isEmpty()) {
                    for (DocumentSnapshot document : documents) {
                        bestScore = String.valueOf(document.get("bestScore"));
                    }
                } else {
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bestScore;
    }
}
