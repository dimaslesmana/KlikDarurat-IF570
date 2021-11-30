package id.ac.umn.if570.titik.koma.klikdarurat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PersonalEmergencyContactAdapter extends RecyclerView.Adapter<PersonalEmergencyContactAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PersonalEmergencyContact> personalEmergencyContacts;
    private OnItemClickCallback onItemClickCallback;
    private OnLongItemClickCallback onLongItemClickCallback;
    public interface OnItemClickCallback {
        void OnItemClicked(PersonalEmergencyContact contact);
    }

    public interface OnLongItemClickCallback {
        void OnLongItemClicked(PersonalEmergencyContact contact);
    }

    public PersonalEmergencyContactAdapter(Context context, ArrayList<PersonalEmergencyContact> list) {
        this.context = context;
        this.personalEmergencyContacts = list;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setOnLongItemClickCallback(OnLongItemClickCallback onLongItemClickCallback) {
        this.onLongItemClickCallback = onLongItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_personal_emergency_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalEmergencyContact contact = personalEmergencyContacts.get(position);

        holder.tvName.setText(contact.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.OnItemClicked(contact);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongItemClickCallback.OnLongItemClicked(contact);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return personalEmergencyContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_personal_emergency_contact_name);
        }
    }
}
