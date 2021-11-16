package id.ac.umn.if570.titik.koma.klikdarurat;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalEmergencyContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PersonalEmergencyContactFragment extends Fragment {
    private RecyclerView rvPersonalEmergencyContact;
    private ArrayList<PersonalEmergencyContact> listPersonalEmergencyContact = new ArrayList<>();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_emergency_contact, container, false);

        rvPersonalEmergencyContact = view.findViewById(R.id.rv_personal_emergency_contact);
        rvPersonalEmergencyContact.setHasFixedSize(true);

        listPersonalEmergencyContact.addAll(PersonalEmergencyContactData.getListData());

        PersonalEmergencyContactAdapter adapter = new PersonalEmergencyContactAdapter(listPersonalEmergencyContact);
        rvPersonalEmergencyContact.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPersonalEmergencyContact.setAdapter(adapter);

        return view;
    }
}