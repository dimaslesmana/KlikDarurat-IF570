package id.ac.umn.if570.titik.koma.klikdarurat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirebaseHelper {
    public static FirebaseHelper instance = null;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CollectionReference usersCollection;

    public FirebaseHelper() {
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.currentUser = this.auth.getCurrentUser();
        this.usersCollection = this.firestore.collection("users");
    }

    public FirebaseAuth getAuth() {
        return this.auth;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public FirebaseUser getCurrentUser() {
        return this.currentUser;
    }

    public boolean isAuthenticated() {
        return (this.currentUser != null) ? true : false;
    }

    public void sendVerificationEmail() {
        this.currentUser.sendEmailVerification();
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return this.auth.signInWithEmailAndPassword(email, password);
    }

    public void logoutUser() {
        this.auth.signOut();
        FirebaseHelper.instance = null;
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return this.auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> addUserDocument(String userId, Map<String, Object> user) {
        return this.usersCollection.document(userId).set(user);
    }

    /**
     * Get user data
     */
    public DocumentReference getUserDocument(String userId) {
        return this.usersCollection.document(userId);
    }

    /**
     * Update user data
     * @return
     */
    public Task<Void> updateUserDocument(String userId, Map<String, Object> editedUser) {
        return this.usersCollection.document(userId).update(editedUser);
    }

    /**
     * Create contacts collection inside user document (create contacts sub-collection)
     */
    public Task<Void> createContactsCollection(String userId, Map<String, Object> contact) {
       return this.usersCollection.document(userId).collection("contacts").document("EMPTY_RESERVED").set(contact);
    }
}
