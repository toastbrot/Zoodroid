package com.unicorntoast.android.zoodroid.ui.dialog;

import com.unicorntoast.android.zoodroid.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class ErrorDialog extends Dialog {

	public ErrorDialog(Context context, String title, String text) {
		super(context);
		setContentView(R.layout.error_dialog);
		setTitle(title);
		
		TextView.class.cast(findViewById(R.id.error_dialog_text)).setText(text);
	}

	public static ErrorDialog show(Context context, String title, String text) {
		ErrorDialog errorDialog = new ErrorDialog(context, title, text);
		errorDialog.show();
		return errorDialog;
	}
}
