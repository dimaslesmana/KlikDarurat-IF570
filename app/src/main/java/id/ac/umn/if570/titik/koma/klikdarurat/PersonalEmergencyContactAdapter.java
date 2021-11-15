package id.ac.umn.if570.titik.koma.klikdarurat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonalEmergencyContactAdapter extends RecyclerView.Adapter<PersonalEmergencyContactAdapter.ViewHolder> {
    private ArrayList<PersonalEmergencyContact> listPersonalEmergencyContact;

    public PersonalEmergencyContactAdapter(ArrayList<PersonalEmergencyContact> list) {
        this.listPersonalEmergencyContact = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_personal_emergency_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalEmergencyContact contact = listPersonalEmergencyContact.get(position);

        holder.tvName.setText(contact.getName());
    }

    @Override
    public int getItemCount() {
        return listPersonalEmergencyContact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_personal_emergency_contact_name);
        }
    }
}
