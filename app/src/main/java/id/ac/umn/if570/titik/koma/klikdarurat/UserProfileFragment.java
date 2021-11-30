package id.ac.umn.if570.titik.koma.klikdarurat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    private TextView fullName, phoneNumber, email, address;
    private String userID;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser currentUser;
    private LinearLayout linearlayoutEdit;
    private Button btnLogout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            //getActivity().getSupportFragmentManager().popBackStack();
            //getActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        fullName = view.findViewById(R.id.tv_profile_full_name);
        phoneNumber = view.findViewById(R.id.tv_profile_phone_number);
        email = view.findViewById(R.id.tv_profile_email);
        address = view.findViewById(R.id.tv_profile_address);

        userID = currentUser.getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                fullName.setText(documentSnapshot.getString("fullName"));
                phoneNumber.setText(documentSnapshot.getString("phoneNumber"));
                email.setText(documentSnapshot.getString("email"));
                address.setText(documentSnapshot.getString("address"));
            }
        });

        linearlayoutEdit = view.findViewById(R.id.linearlayout_profile_edit);
        btnLogout = view.findViewById(R.id.btn_profile_logout);

        linearlayoutEdit.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), EditUserProfileActivity.class);
            intent.putExtra("fullName", fullName.getText().toString());
            intent.putExtra("phoneNumber", phoneNumber.getText().toString());
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("address", address.getText().toString());
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(view.getContext(), LoginActivity.class));
        });

        return view;
    }
}