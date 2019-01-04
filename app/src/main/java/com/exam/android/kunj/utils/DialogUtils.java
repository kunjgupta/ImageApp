package com.exam.android.kunj.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.exam.android.kunj.R;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class DialogUtils {
    public static void showNoConnectionDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.connection_alert_dialog_title));
        builder.setMessage(R.string.connection_alert_dialog_message)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.settings),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            }
                        })
                .setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
