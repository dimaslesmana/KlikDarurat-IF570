package id.ac.umn.if570.titik.koma.klikdarurat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
        this.instance = null;
    }

    public Task<AuthResult> registerUser(String email, String password) {
        return this.auth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> addUserDocument(String userId, Map<String, Object> user) {
        return this.usersCollection.document(userId).set(user);
    }

    /**
     * Get user profile data
     */
    public DocumentReference getUserDocument(String userId) {
        return this.usersCollection.document(userId);
    }

    /**
     * Update user profile data
     */
    public Task<Void> updateUserDocument(String userId, Map<String, Object> editedUser) {
        return this.usersCollection.document(userId).update(editedUser);
    }

    /**
     * Create personal contacts collection inside user document (create contacts sub-collection)
     */
    public Task<Void> createContactsCollection(String userId, Map<String, Object> contact) {
       return this.usersCollection.document(userId).collection("contacts").document("EMPTY_RESERVED").set(contact);
    }

    /**
     * Get personal contacts document from contacts collection inside user document
     */
    public Query getContactsDocument(String userId, boolean sortAscending) {
        if (sortAscending) {
            return this.usersCollection.document(userId).collection("contacts").orderBy("_nameOrder", Query.Direction.ASCENDING).whereNotEqualTo("_nameOrder", "EMPTY_RESERVED");
        } else {
            return this.usersCollection.document(userId).collection("contacts").orderBy("_nameOrder", Query.Direction.DESCENDING).whereNotEqualTo("_nameOrder", "EMPTY_RESERVED");
        }
    }

    /**
     * Search personal contacts document from contacts collection inside user document
     */
    public Query searchContactsDocument(String userId, String searchText) {
        return this.usersCollection.document(userId).collection("contacts").orderBy("_nameSearch").startAt(searchText).endAt(searchText + "\uf8ff");
    }

    /**
     * Add personal contact document
     */
    public Task<DocumentReference> addContactDocument(String userId, Map<String, Object> newContact) {
        return this.usersCollection.document(userId).collection("contacts").add(newContact);
    }

    /**
     * Update personal contact document
     */
    public Task<Void> updateContactDocument(String currentUserId, String contactId, Map<String, Object> updatedContact) {
        return this.usersCollection.document(currentUserId).collection("contacts").document(contactId).update(updatedContact);
    }

    /**
     * Delete personal contact document
     */
    public Task<Void> deleteContactDocument(String currentUserId, String contactId) {
        return this.usersCollection.document(currentUserId).collection("contacts").document(contactId).delete();
    }
}
