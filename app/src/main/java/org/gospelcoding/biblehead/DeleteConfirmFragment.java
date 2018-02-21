package org.gospelcoding.biblehead;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DeleteConfirmFragment extends DialogFragment {

    DeleteConfirmFragmentListener listener;
    Verse verse;

    public void setVerse(Verse v){
        verse = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm_delete, verse.getReference()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDeleteConfirm(DeleteConfirmFragment.this, verse);
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    public interface DeleteConfirmFragmentListener {
        void onDeleteConfirm(DialogFragment dialog, Verse verse);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            listener = (DeleteConfirmFragmentListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement DeleteConfirmFramentListener");
        }
    }

}

