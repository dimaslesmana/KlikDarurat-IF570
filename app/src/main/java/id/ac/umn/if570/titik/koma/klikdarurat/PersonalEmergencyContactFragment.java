package id.ac.umn.if570.titik.koma.klikdarurat;

import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalEmergencyContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PersonalEmergencyContactFragment extends Fragment implements View.OnClickListener {
    private SearchView searchView;
    private LinearLayout linearLayoutSort;
    private TextView tvPersonalEmergencyContactEmpty;
    private TextView tvSortAz;
    private FloatingActionButton fabAddContact;
    private RecyclerView rvPersonalEmergencyContact;
    private FirestoreRecyclerAdapter adapter;
    private PersonalEmergencyContact selectedContact;
    private CircularProgressIndicator circularProgressIndicator;

    private boolean sortAscending = true;

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

        String userId = FirebaseHelper.instance.getCurrentUser().getUid();
        FirestoreRecyclerOptions<PersonalEmergencyContact> options = new FirestoreRecyclerOptions.Builder<PersonalEmergencyContact>()
                .setQuery(FirebaseHelper.instance.getContactsDocument(userId, sortAscending), PersonalEmergencyContact.class)
                .build();
        initializeFirestoreAdapter(options);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_emergency_contact, container, false);

        linearLayoutSort = view.findViewById(R.id.linearlayout_personal_emergency_contact_sort);
        searchView = view.findViewById(R.id.search_view_personal_emergency_contact);
        fabAddContact = view.findViewById(R.id.fab_personal_emergency_contact_add);
        rvPersonalEmergencyContact = view.findViewById(R.id.rv_personal_emergency_contact);
        tvPersonalEmergencyContactEmpty = view.findViewById(R.id.tv_personal_emergency_contact_empty);
        tvSortAz = view.findViewById(R.id.tv_personal_emergency_contact_a_z);
        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);

        registerForContextMenu(rvPersonalEmergencyContact);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        circularProgressIndicator.setVisibility(View.VISIBLE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPersonalEmergencyContact(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPersonalEmergencyContact(newText);
                return false;
            }
        });
        searchView.setOnClickListener(this);
        linearLayoutSort.setOnClickListener(this);
        fabAddContact.setOnClickListener(this);

        rvPersonalEmergencyContact.setHasFixedSize(true);
        rvPersonalEmergencyContact.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPersonalEmergencyContact.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == fabAddContact.getId()) {
            startActivity(new Intent(getContext(), AddPersonalEmergencyContactActivity.class));
        } else if (viewId == searchView.getId()) {
            searchView.onActionViewExpanded();
        } else if (viewId == linearLayoutSort.getId()) {
            sortAscending = !sortAscending;
            if (sortAscending) {
                tvSortAz.setText(R.string.personal_emergency_contact_tv_a_z);
            } else {
                tvSortAz.setText(R.string.personal_emergency_contact_tv_z_a);
            }

            String userId = FirebaseHelper.instance.getCurrentUser().getUid();
            FirestoreRecyclerOptions<PersonalEmergencyContact> options = new FirestoreRecyclerOptions.Builder<PersonalEmergencyContact>()
                    .setQuery(FirebaseHelper.instance.getContactsDocument(userId, sortAscending), PersonalEmergencyContact.class)
                    .build();

            this.adapter.updateOptions(options);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.adapter.stopListening();
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

    private void searchPersonalEmergencyContact(String searchText) {
        FirestoreRecyclerOptions<PersonalEmergencyContact> options;

        String userId = FirebaseHelper.instance.getCurrentUser().getUid();

        if (!searchText.equalsIgnoreCase("")) {
            options = new FirestoreRecyclerOptions.Builder<PersonalEmergencyContact>()
                    .setQuery(FirebaseHelper.instance.searchContactsDocument(userId, searchText.toLowerCase()), PersonalEmergencyContact.class)
                    .build();
        } else {
            options = new FirestoreRecyclerOptions.Builder<PersonalEmergencyContact>()
                    .setQuery(FirebaseHelper.instance.getContactsDocument(userId, sortAscending), PersonalEmergencyContact.class)
                    .build();
        }

        this.adapter.updateOptions(options);
    }

    private void initializeFirestoreAdapter(FirestoreRecyclerOptions<PersonalEmergencyContact> options) {
        this.adapter = new FirestoreRecyclerAdapter<PersonalEmergencyContact, PersonalEmergencyContactViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                adapter.notifyDataSetChanged();

                circularProgressIndicator.setVisibility(View.GONE);

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
                circularProgressIndicator.setVisibility(View.GONE);

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

    private void editContact() {
        Intent intent = new Intent(getContext(), EditPersonalEmergencyContactActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable(EditPersonalEmergencyContactActivity.EXTRA_SELECTED_CONTACT, selectedContact);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void deleteContact() {
        String currentUserId = FirebaseHelper.instance.getCurrentUser().getUid();

        MaterialAlertDialogBuilder confirmDialog = new MaterialAlertDialogBuilder(getContext());
        confirmDialog.setTitle("Hapus Kontak");
        confirmDialog.setMessage("Apakah anda yakin ingin menghapus kontak ini?");
        confirmDialog
                .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        circularProgressIndicator.setVisibility(View.VISIBLE);

                        FirebaseHelper.instance.deleteContactDocument(currentUserId, selectedContact.getId())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        circularProgressIndicator.setVisibility(View.GONE);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
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
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        confirmDialog.create().show();
    }
}