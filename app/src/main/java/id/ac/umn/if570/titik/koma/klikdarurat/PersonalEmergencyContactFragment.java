package id.ac.umn.if570.titik.koma.klikdarurat;

import android.app.Person;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalEmergencyContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PersonalEmergencyContactFragment extends Fragment implements View.OnClickListener {
    private TextView tvPersonalEmergencyContactEmpty;
    private FloatingActionButton fabAddContact;
    private RecyclerView rvPersonalEmergencyContact;
    private FirestoreRecyclerAdapter adapter;
    private PersonalEmergencyContact selectedContact;
    private ProgressDialog progressDialog;

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

    private class PersonalEmergencyContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public PersonalEmergencyContactViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_personal_emergency_contact_name);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (!FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(getContext(), LoginActivity.class));

            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
            return;
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String userId = FirebaseHelper.instance.getCurrentUser().getUid();
        FirestoreRecyclerOptions<PersonalEmergencyContact> options = new FirestoreRecyclerOptions.Builder<PersonalEmergencyContact>()
                .setQuery(FirebaseHelper.instance.getContactsDocument(userId), PersonalEmergencyContact.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PersonalEmergencyContact, PersonalEmergencyContactViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (getItemCount() != 0) {
                    rvPersonalEmergencyContact.setVisibility(View.VISIBLE);
                    tvPersonalEmergencyContactEmpty.setVisibility(View.INVISIBLE);
                } else {
                    rvPersonalEmergencyContact.setVisibility(View.INVISIBLE);
                    tvPersonalEmergencyContactEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                super.onError(e);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getContext(), "Gagal memuat daftar kontak.", Toast.LENGTH_SHORT).show();

                Log.e("PersonalContacts", e.getMessage());
            }

            @NonNull
            @Override
            public PersonalEmergencyContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_personal_emergency_contact, parent, false);

                return new PersonalEmergencyContactViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PersonalEmergencyContactViewHolder holder, int position, @NonNull PersonalEmergencyContact contact) {
                holder.tvName.setText(contact.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri phoneNumber = Uri.parse("tel:" + contact.getPhoneNumber());
                        Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedContact = contact;
                        return false;
                    }
                });
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_emergency_contact, container, false);

        fabAddContact = view.findViewById(R.id.fab_personal_emergency_contact_add);
        rvPersonalEmergencyContact = view.findViewById(R.id.rv_personal_emergency_contact);
        tvPersonalEmergencyContactEmpty = view.findViewById(R.id.tv_personal_emergency_contact_empty);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabAddContact.setOnClickListener(this);
        rvPersonalEmergencyContact.setHasFixedSize(true);
        rvPersonalEmergencyContact.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPersonalEmergencyContact.setAdapter(adapter);

        registerForContextMenu(rvPersonalEmergencyContact);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
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

        bundle.putSerializable(EditPersonalEmergencyContactActivity.EXTRA_SELECTED_CONTACT, selectedContact);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void deleteContact() {
        String currentUserId = FirebaseHelper.instance.getCurrentUser().getUid();

        FirebaseHelper.instance.deleteContactDocument(currentUserId, selectedContact.getId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(),"Kontak berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Gagal menghapus kontak.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}