package id.ac.umn.if570.titik.koma.klikdarurat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class EmergencyServiceFragment extends Fragment {

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
     * @return A new instance of fragment EmergencyServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyServiceFragment newInstance(String param1, String param2) {
        EmergencyServiceFragment fragment = new EmergencyServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EmergencyServiceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_emergency_service, container, false);

        // Load emergency service image using glide
        ImageView ivCallCenter = view.findViewById(R.id.iv_emergency_service_callcenter);
        ImageView ivAmbulans = view.findViewById(R.id.iv_emergency_service_ambulans);
        ImageView ivPolisi = view.findViewById(R.id.iv_emergency_service_polisi);
        ImageView ivDamkar = view.findViewById(R.id.iv_emergency_service_damkar);
        ImageView ivBasarnas = view.findViewById(R.id.iv_emergency_service_basarnas);
        ImageView ivBnpb = view.findViewById(R.id.iv_emergency_service_bnpb);
        ImageView ivPln = view.findViewById(R.id.iv_emergency_service_pln);
        ImageView ivJasaMarga = view.findViewById(R.id.iv_emergency_service_jasamarga);

        Glide.with(this).load(R.drawable.ic_callcenter).into(ivCallCenter);
        Glide.with(this).load(R.drawable.ic_ambulans).into(ivAmbulans);
        Glide.with(this).load(R.drawable.ic_polisi).into(ivPolisi);
        Glide.with(this).load(R.drawable.ic_damkar).into(ivDamkar);
        Glide.with(this).load(R.drawable.ic_basarnas).into(ivBasarnas);
        Glide.with(this).load(R.drawable.ic_bnpb).into(ivBnpb);
        Glide.with(this).load(R.drawable.ic_pln).into(ivPln);
        Glide.with(this).load(R.drawable.ic_jasamarga).into(ivJasaMarga);

        // Handle emergency service card when clicked
        MaterialCardView cvCallCenter = view.findViewById(R.id.card_emergency_service_callcenter);
        MaterialCardView cvAmbulans = view.findViewById(R.id.card_emergency_service_ambulans);
        MaterialCardView cvPolisi = view.findViewById(R.id.card_emergency_service_polisi);
        MaterialCardView cvDamkar = view.findViewById(R.id.card_emergency_service_damkar);
        MaterialCardView cvBasarnas = view.findViewById(R.id.card_emergency_service_basarnas);
        MaterialCardView cvBnpb = view.findViewById(R.id.card_emergency_service_bnpb);
        MaterialCardView cvPln = view.findViewById(R.id.card_emergency_service_pln);
        MaterialCardView cvJasaMarga = view.findViewById(R.id.card_emergency_service_jasamarga);

        cvCallCenter.setOnClickListener(v -> {
            Uri callCenterNumber = Uri.parse("tel:112");
            Intent callCenterIntent = new Intent(Intent.ACTION_DIAL, callCenterNumber);
            startActivity(callCenterIntent);
        });

        cvAmbulans.setOnClickListener(v -> {
            Uri ambulansNumber = Uri.parse("tel:119");
            Intent callCenterIntent = new Intent(Intent.ACTION_DIAL, ambulansNumber);
            startActivity(callCenterIntent);
        });

        cvPolisi.setOnClickListener(v -> {
            Uri polisiNumber = Uri.parse("tel:110");
            Intent polisiIntent = new Intent(Intent.ACTION_DIAL, polisiNumber);
            startActivity(polisiIntent);
        });

        cvDamkar.setOnClickListener(v -> {
            Uri damkarNumber = Uri.parse("tel:113");
            Intent damkarIntent = new Intent(Intent.ACTION_DIAL, damkarNumber);
            startActivity(damkarIntent);
        });

        cvBasarnas.setOnClickListener(v -> {
            Uri basarnasNumber = Uri.parse("tel:115");
            Intent basarnasIntent = new Intent(Intent.ACTION_DIAL, basarnasNumber);
            startActivity(basarnasIntent);
        });

        cvBnpb.setOnClickListener(v -> {
            Uri bnpbNumber = Uri.parse("tel:117");
            Intent bnpbIntent = new Intent(Intent.ACTION_DIAL, bnpbNumber);
            startActivity(bnpbIntent);
        });

        cvPln.setOnClickListener(v -> {
            Uri plnNumber = Uri.parse("tel:123");
            Intent plnIntent = new Intent(Intent.ACTION_DIAL, plnNumber);
            startActivity(plnIntent);
        });

        cvJasaMarga.setOnClickListener(v -> {
            Uri jasaMargaNumber = Uri.parse("tel:14080");
            Intent jasaMargaIntent = new Intent(Intent.ACTION_DIAL, jasaMargaNumber);
            startActivity(jasaMargaIntent);
        });

        return view;
    }
}