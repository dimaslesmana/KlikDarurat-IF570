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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    private LinearLayout linearlayoutEdit;
    private TextView tvFullName;
    private TextView tvPhoneNumber;
    private TextView tvEmail;
    private TextView tvAddress;
    private Button btnLogout;
    private String userID;
    private ListenerRegistration userDocumentListenerRegistration;

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

        if (!FirebaseHelper.instance.isAuthenticated()) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        linearlayoutEdit = view.findViewById(R.id.linearlayout_profile_edit);
        tvFullName = view.findViewById(R.id.tv_profile_full_name);
        tvPhoneNumber = view.findViewById(R.id.tv_profile_phone_number);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvAddress = view.findViewById(R.id.tv_profile_address);
        btnLogout = view.findViewById(R.id.btn_profile_logout);

        linearlayoutEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditUserProfileActivity.class);
            intent.putExtra("fullName", tvFullName.getText().toString());
            intent.putExtra("phoneNumber", tvPhoneNumber.getText().toString());
            intent.putExtra("email", tvEmail.getText().toString());
            intent.putExtra("address", tvAddress.getText().toString());
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            if (userDocumentListenerRegistration != null) {
                userDocumentListenerRegistration.remove();
            }

            FirebaseHelper.instance.logoutUser();
            startActivity(new Intent(getContext(), MainActivity.class));
        });

        userID = FirebaseHelper.instance.getCurrentUser().getUid();

        if (userDocumentListenerRegistration == null) {
            userDocumentListenerRegistration = FirebaseHelper.instance.getUserDocument(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value != null && value.exists()) {
                        tvFullName.setText(value.getString("fullName"));
                        tvPhoneNumber.setText(value.getString("phoneNumber"));
                        tvEmail.setText(value.getString("email"));
                        tvAddress.setText(value.getString("address"));
                    }
                }
            });
        }

        return view;
    }
}