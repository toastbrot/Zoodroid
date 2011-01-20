package com.unicorntoast.android.zoodroid.ui.dialog;

import com.unicorntoast.android.zoodroid.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class ErrorDialog extends Dialog {

	public ErrorDialog(Context context, String text) {
		super(context);
		setContentView(R.layout.error_dialog);
		setTitle(context.getString(R.string.title_exception_dialog));
		TextView.class.cast(findViewById(R.id.error_dialog_text)).setText(text);
	}

	public static ErrorDialog show(Context context, String text) {
		ErrorDialog errorDialog = new ErrorDialog(context, text);
		errorDialog.show();
		return errorDialog;
	}
}
