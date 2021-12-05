package id.ac.umn.if570.titik.koma.klikdarurat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalEmergencyContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PersonalEmergencyContactFragment extends Fragment implements View.OnClickListener {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FloatingActionButton fabAddContact;
    private RecyclerView rvPersonalEmergencyContact;
    private PersonalEmergencyContactAdapter adapter;
    private PersonalEmergencyContact selectedContact;
    private ProgressDialog progressDialog;
    private ArrayList<PersonalEmergencyContact> personalEmergencyContacts = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalEmergencyContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalEmergencyContactFragment newInstance(String param1, String param2) {
        PersonalEmergencyContactFragment fragment = new PersonalEmergencyContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PersonalEmergencyContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DocumentReference docRef = firestore.collection("users").document(currentUser.getUid());

        docRef.collection("contacts").orderBy("name", Query.Direction.ASCENDING).whereNotEqualTo("name", "EMPTY_RESERVED").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    Log.e("PersonalContacts", error.getMessage());
                    return;
                }

                if (value.isEmpty()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    return;
                }

                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        PersonalEmergencyContact contact = documentChange.getDocument().toObject(PersonalEmergencyContact.class);
                        personalEmergencyContacts.add(contact);
                    }

                    adapter.notifyDataSetChanged();
                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_emergency_contact, container, false);

        fabAddContact = view.findViewById(R.id.fab_personal_emergency_contact_add);
        rvPersonalEmergencyContact = view.findViewById(R.id.rv_personal_emergency_contact);

        fabAddContact.setOnClickListener(this);
        rvPersonalEmergencyContact.setHasFixedSize(true);
        rvPersonalEmergencyContact.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new PersonalEmergencyContactAdapter(view.getContext(), personalEmergencyContacts);
        rvPersonalEmergencyContact.setAdapter(adapter);

        registerForContextMenu(rvPersonalEmergencyContact);
        adapter.setOnItemClickCallback(new PersonalEmergencyContactAdapter.OnItemClickCallback() {
            @Override
            public void OnItemClicked(PersonalEmergencyContact contact) {
                Uri phoneNumber = Uri.parse("tel:" + contact.getPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                startActivity(intent);
            }
        });

        adapter.setOnLongItemClickCallback(new PersonalEmergencyContactAdapter.OnLongItemClickCallback() {
            @Override
            public void OnLongItemClicked(PersonalEmergencyContact contact) {
                selectedContact = contact;
            }
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_menu_personal_emergency_contact, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.context_menu_personal_emergency_contact_edit) {
            editContact();
        } else if (itemId == R.id.context_menu_personal_emergency_contact_delete) {
            deleteContact();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == fabAddContact.getId()) {
            startActivity(new Intent(getContext(), AddPersonalEmergencyContactActivity.class));
        }
    }

    private void editContact() {
        Intent intent = new Intent(getContext(), EditPersonalEmergencyContactActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("SELECTED_CONTACT", selectedContact);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void deleteContact() {
        DocumentReference docRef = firestore.collection("users").document(currentUser.getUid());

        docRef.collection("contacts").document(selectedContact.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), selectedContact.getName() + " deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete contact!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}