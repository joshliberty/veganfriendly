package com.joshliberty.veganfriendly.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by caligula on 28/06/14.
 * This file is part of VeganFriendly.
 */
public class DialogUtil {

    public static void showDialog(Context ctx,
                                  int title,
                                  int content,
                                  int confirm,
                                  int cancel,
                                  DialogInterface.OnClickListener positiveListener,
                                  DialogInterface.OnCancelListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title).setMessage(content);
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        builder.setPositiveButton(confirm, positiveListener);
        AlertDialog dialog =  builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
